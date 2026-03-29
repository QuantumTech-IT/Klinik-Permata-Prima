/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toko;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 *
 * @author ALI-PC
 */
public class TokobarangStokMinDao {
     private final Connection conn;

    public TokobarangStokMinDao(Connection conn) {
        this.conn = Objects.requireNonNull(conn, "Connection tidak boleh null");
    }

    public static class StokMinRow {
        public final String kodeBrng;
        public final String namaBrng;
        public final String satuan;
        public final BigDecimal stok;
        public final BigDecimal stokMin;
        public final BigDecimal kurangExact;
        public final long saranOrder;
        public final LocalDate expire;
        public final String kondisi;

        public StokMinRow(String kodeBrng, String namaBrng, String satuan,
                          BigDecimal stok, BigDecimal stokMin, BigDecimal kurangExact,
                          long saranOrder, LocalDate expire, String kondisi) {
            this.kodeBrng = kodeBrng;
            this.namaBrng = namaBrng;
            this.satuan = satuan;
            this.stok = stok;
            this.stokMin = stokMin;
            this.kurangExact = kurangExact;
            this.saranOrder = saranOrder;
            this.expire = expire;
            this.kondisi = kondisi;
        }
    }

    public List<StokMinRow> getStokMinAktif() throws SQLException {
        final String sql =
            "SELECT " +
            "  tb.kode_brng, tb.nama_brng, ks.satuan, " +
            "  ROUND(tb.stok, 2) AS stok, tb.stok_min, " +
            "  GREATEST(0, tb.stok_min - ROUND(tb.stok, 2)) AS kurang_exact, " +
            "  CEIL(GREATEST(0, tb.stok_min - ROUND(tb.stok, 2))) AS saran_order, " +
            "  tb.expire, " +
            "  CASE WHEN ROUND(tb.stok,2) <= 0 THEN 'HABIS' ELSE 'MINIM' END AS kondisi " +
            "FROM tokobarang tb " +
            "JOIN kodesatuan ks ON ks.kode_sat = tb.kode_sat " +
            "WHERE tb.status = ? " +
            "  AND tb.stok_min > 0 " +
            "  AND ROUND(tb.stok,2) <= tb.stok_min " +
            "ORDER BY kondisi DESC, saran_order DESC, kurang_exact DESC, tb.nama_brng ASC";

        List<StokMinRow> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "1");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String kode = rs.getString("kode_brng");
                    String nama = rs.getString("nama_brng");
                    String satuan = rs.getString("satuan");

                    BigDecimal stok = rs.getBigDecimal("stok");
                    BigDecimal stokMin = rs.getBigDecimal("stok_min");
                    BigDecimal kurangExact = rs.getBigDecimal("kurang_exact");
                    long saranOrder = rs.getLong("saran_order");

                    Date exp = rs.getDate("expire");
                    LocalDate expire = (exp != null) ? exp.toLocalDate() : null;

                    String kondisi = rs.getString("kondisi");

                    out.add(new StokMinRow(kode, nama, satuan, stok, stokMin, kurangExact, saranOrder, expire, kondisi));
                }
            }
        }
        return out;
    }

    public int updateStokMin(String kodeBrng, BigDecimal stokMin) throws SQLException {
        final String sql = "UPDATE tokobarang SET stok_min = ? WHERE kode_brng = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, stokMin);
            ps.setString(2, kodeBrng);
            return ps.executeUpdate();
        }
    }
}
