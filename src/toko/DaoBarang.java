package toko;

import java.sql.*;

public class DaoBarang {
    private final Connection conn;
    public DaoBarang(Connection conn) { this.conn = conn; }

    public BarangMeta getMeta(String kodeBrg) throws SQLException {
        String sql =
            "SELECT d.kode_sat, d.kode_sat1, d.kode_sat2, d.isi, d.kapasitas, " +
            "       d.distributor, d.grosir, d.retail, " +
            "       COALESCE(st.stok_pack,0) AS stok_pack " +
            "FROM databarang d " +
            "LEFT JOIN view_stok_pack st ON st.kode_brng = d.kode_brng " +
            "WHERE d.kode_brng = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodeBrg);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                BarangMeta m = new BarangMeta();
                m.kodeSatuan   = rs.getString("kode_sat");
                m.kodeSatuan1  = rs.getString("kode_sat1");
                m.kodeSatuan2  = rs.getString("kode_sat2");
                m.isi          = Math.max(1, rs.getInt("isi"));
                m.kapasitas    = Math.max(1, rs.getInt("kapasitas"));
                m.hargaDistributor = rs.getDouble("distributor");
                m.hargaGrosir      = rs.getDouble("grosir");
                m.hargaRetail      = rs.getDouble("retail");
                m.stokPack         = rs.getLong("stok_pack");
                return m;
            }
        }
    }

    public static long toPack(String jenis, long qty, int isi, int kapasitas) {
        switch (jenis) {
            case "Distributor": return qty;
            case "Grosir":      return (long)Math.ceil(qty / (double)isi);
            default:            return (long)Math.ceil(qty / (double)kapasitas);
        }
    }
}
