/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toko;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.YearMonth;
import java.time.LocalDate;

/**
 *
 * @author ALI-PC
 */
public class TuslahTokopenjualanService {

    private final Connection conn;

    public TuslahTokopenjualanService(Connection conn) {
        this.conn = conn;
    }

    // =========================
    // DTO
    // =========================
    public static class Summary {
        public long jumlahNota;
        public double totalTransaksi;
        public double totalTuslah;
    }

    public static class RowNota {
        public String notaJual;
        public Date tglJual;
        public String nip;
        public String jnsJual;
        public double totalNota;
        public double tuslah;
        public String namaBayar;
    }

    public static class RowNip {
        public String nip;
        public String nama;      // dari petugas.nama
        public long jumlahNota;
        public double totalTransaksi;
        public double totalTuslah;
    }

    // =========================
    // Helper tanggal bulanan
    // =========================
    private static java.sql.Date firstDayOfMonth(YearMonth ym) {
        LocalDate d = ym.atDay(1);
        return java.sql.Date.valueOf(d);
    }

    private static java.sql.Date lastDayOfMonth(YearMonth ym) {
        LocalDate d = ym.atEndOfMonth();
        return java.sql.Date.valueOf(d);
    }

    // =========================================================
    // SUMMARY by NIP (range)
    // =========================================================
    public Summary getSummaryByNip(String nip, java.sql.Date tgl1, java.sql.Date tgl2) throws SQLException {
        return getSummaryByNip(nip, tgl1, tgl2, "%"); // default: semua jenis jual
    }

    // versi dengan filter jns_jual (misal "Resep%")
    public Summary getSummaryByNip(String nip, java.sql.Date tgl1, java.sql.Date tgl2, String jnsLike) throws SQLException {
        if (jnsLike == null || jnsLike.trim().isEmpty()) jnsLike = "%";

        String sql =
            "SELECT " +
            "  COUNT(DISTINCT p.nota_jual) AS jml_nota, " +
            "  COALESCE(SUM(COALESCE(p.total,0)),0) AS total_transaksi, " +
            "  COALESCE(SUM(COALESCE(d.tuslah,0)),0) AS total_tuslah " +
            "FROM tokopenjualan p " +
            "LEFT JOIN ( " +
            "   SELECT nota_jual, SUM(COALESCE(tambahan,0)) AS tuslah " +
            "   FROM toko_detail_jual " +
            "   GROUP BY nota_jual " +
            ") d ON d.nota_jual = p.nota_jual " +
            "WHERE p.nip = ? " +
            "  AND p.tgl_jual BETWEEN ? AND ? " +
            "  AND p.jns_jual LIKE ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, nip);
            ps.setDate(2, tgl1);
            ps.setDate(3, tgl2);
            ps.setString(4, jnsLike);

            rs = ps.executeQuery();
            Summary s = new Summary();
            if (rs.next()) {
                s.jumlahNota = rs.getLong("jml_nota");
                s.totalTransaksi = rs.getDouble("total_transaksi");
                s.totalTuslah = rs.getDouble("total_tuslah");
            }
            return s;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (ps != null) try { ps.close(); } catch (Exception e) {}
        }
    }

    // =========================================================
    // LIST NOTA by NIP (range)
    // =========================================================
    public List<RowNota> listNotaByNip(String nip, java.sql.Date tgl1, java.sql.Date tgl2, int limit) throws SQLException {
        return listNotaByNip(nip, tgl1, tgl2, limit, "%");
    }

    public List<RowNota> listNotaByNip(String nip, java.sql.Date tgl1, java.sql.Date tgl2, int limit, String jnsLike) throws SQLException {
        if (limit <= 0) limit = 200;
        if (jnsLike == null || jnsLike.trim().isEmpty()) jnsLike = "%";

        String sql =
            "SELECT " +
            "  p.nota_jual, p.tgl_jual, p.nip, p.jns_jual, " +
            "  COALESCE(p.total,0) AS total_nota, " +
            "  COALESCE(d.tuslah,0) AS tuslah, " +
            "  COALESCE(p.nama_bayar,'') AS nama_bayar " +
            "FROM tokopenjualan p " +
            "LEFT JOIN ( " +
            "   SELECT nota_jual, SUM(COALESCE(tambahan,0)) AS tuslah " +
            "   FROM toko_detail_jual " +
            "   GROUP BY nota_jual " +
            ") d ON d.nota_jual = p.nota_jual " +
            "WHERE p.nip = ? " +
            "  AND p.tgl_jual BETWEEN ? AND ? " +
            "  AND p.jns_jual LIKE ? " +
            "ORDER BY p.tgl_jual DESC, p.nota_jual DESC " +
            "LIMIT " + limit;

        List<RowNota> out = new ArrayList<RowNota>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, nip);
            ps.setDate(2, tgl1);
            ps.setDate(3, tgl2);
            ps.setString(4, jnsLike);

            rs = ps.executeQuery();
            while (rs.next()) {
                RowNota r = new RowNota();
                r.notaJual = rs.getString("nota_jual");
                r.tglJual = rs.getDate("tgl_jual");
                r.nip = rs.getString("nip");
                r.jnsJual = rs.getString("jns_jual");
                r.totalNota = rs.getDouble("total_nota");
                r.tuslah = rs.getDouble("tuslah");
                r.namaBayar = rs.getString("nama_bayar");
                out.add(r);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (ps != null) try { ps.close(); } catch (Exception e) {}
        }

        return out;
    }

    // =========================================================
    // TUSLAH per NOTA
    // =========================================================
    public double getTuslahByNota(String notaJual) throws SQLException {
        String sql = "SELECT COALESCE(SUM(COALESCE(tambahan,0)),0) AS tuslah " +
                     "FROM toko_detail_jual WHERE nota_jual = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, notaJual);

            rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("tuslah");
            return 0.0;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (ps != null) try { ps.close(); } catch (Exception e) {}
        }
    }

    // =========================================================
    // REKAP per NIP (range)  -> ini yang kamu pakai di tabel rekap
    // =========================================================
    public List<RowNip> listRekapPerNip(java.sql.Date tgl1, java.sql.Date tgl2, int limit) throws SQLException {
        return listRekapPerNip(tgl1, tgl2, limit, "%");
    }

    public List<RowNip> listRekapPerNip(java.sql.Date tgl1, java.sql.Date tgl2, int limit, String jnsLike) throws SQLException {
        if (limit <= 0) limit = 500;
        if (jnsLike == null || jnsLike.trim().isEmpty()) jnsLike = "%";

        String sql =
            "SELECT p.nip, COALESCE(ptg.nama,'-') AS nama, " +
            "       COUNT(DISTINCT p.nota_jual) AS jml_nota, " +
            "       COALESCE(SUM(COALESCE(p.total,0)),0) AS total_transaksi, " +
            "       COALESCE(SUM(COALESCE(d.tuslah,0)),0) AS total_tuslah " +
            "FROM tokopenjualan p " +
            "LEFT JOIN petugas ptg ON ptg.nip = p.nip " +
            "LEFT JOIN ( " +
            "   SELECT nota_jual, SUM(COALESCE(tambahan,0)) AS tuslah " +
            "   FROM toko_detail_jual " +
            "   GROUP BY nota_jual " +
            ") d ON d.nota_jual = p.nota_jual " +
            "WHERE p.tgl_jual BETWEEN ? AND ? " +
            "  AND p.jns_jual LIKE ? " +
            "GROUP BY p.nip, ptg.nama " +
            "ORDER BY total_tuslah DESC " +
            "LIMIT " + limit;

        List<RowNip> out = new ArrayList<RowNip>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, tgl1);
            ps.setDate(2, tgl2);
            ps.setString(3, jnsLike);

            rs = ps.executeQuery();
            while (rs.next()) {
                RowNip r = new RowNip();
                r.nip = rs.getString("nip");
                r.nama = rs.getString("nama");
                r.jumlahNota = rs.getLong("jml_nota");
                r.totalTransaksi = rs.getDouble("total_transaksi");
                r.totalTuslah = rs.getDouble("total_tuslah");
                out.add(r);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (ps != null) try { ps.close(); } catch (Exception e) {}
        }
        return out;
    }

    // =========================================================
    // === TAMBAHAN: REKAP BULANAN (yang kamu minta)
    // =========================================================
    public Summary getSummaryByNipBulan(String nip, YearMonth ym) throws SQLException {
        return getSummaryByNipBulan(nip, ym, "%");
    }

    public Summary getSummaryByNipBulan(String nip, YearMonth ym, String jnsLike) throws SQLException {
        java.sql.Date tgl1 = firstDayOfMonth(ym);
        java.sql.Date tgl2 = lastDayOfMonth(ym);
        return getSummaryByNip(nip, tgl1, tgl2, jnsLike);
    }

    public List<RowNota> listNotaByNipBulan(String nip, YearMonth ym, int limit) throws SQLException {
        return listNotaByNipBulan(nip, ym, limit, "%");
    }

    public List<RowNota> listNotaByNipBulan(String nip, YearMonth ym, int limit, String jnsLike) throws SQLException {
        java.sql.Date tgl1 = firstDayOfMonth(ym);
        java.sql.Date tgl2 = lastDayOfMonth(ym);
        return listNotaByNip(nip, tgl1, tgl2, limit, jnsLike);
    }

    public List<RowNip> listRekapPerNipBulan(YearMonth ym, int limit) throws SQLException {
        return listRekapPerNipBulan(ym, limit, "%");
    }

    public List<RowNip> listRekapPerNipBulan(YearMonth ym, int limit, String jnsLike) throws SQLException {
        java.sql.Date tgl1 = firstDayOfMonth(ym);
        java.sql.Date tgl2 = lastDayOfMonth(ym);
        return listRekapPerNip(tgl1, tgl2, limit, jnsLike);
    }
}
