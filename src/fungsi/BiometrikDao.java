package fungsi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** DAO untuk simpan & ambil data biometrik (fingerprint) terkait bukti pernyataan. */
public class BiometrikDao {
    private final Connection conn;

    public BiometrikDao(Connection conn) {
        this.conn = conn;
    }

    /** UPSERT + format template (JPOS/DP/ISO). */
    public void upsertBukti(
        String noPernyataan, String tipePihak, String nik, String nama,
        Integer fingerIndex, byte[] templateBytes, String templateFormat,
        byte[] imgPreview, Integer scoreMatch, String deviceSn
    ) throws Exception {
        String sql = "REPLACE INTO bukti_persetujuan_biometrik " +
                     "(no_pernyataan, tipe_pihak, nik, nama, finger_index, " +
                     " template_iso, template_format, img_preview, score_match, device_sn) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noPernyataan);
            ps.setString(2, tipePihak);
            ps.setString(3, nik);
            ps.setString(4, nama);
            if (fingerIndex == null) ps.setNull(5, java.sql.Types.TINYINT); else ps.setInt(5, fingerIndex);
            ps.setBytes(6, templateBytes);
            ps.setString(7, templateFormat != null ? templateFormat : "JPOS");
            if (imgPreview == null) ps.setNull(8, java.sql.Types.LONGVARBINARY); else ps.setBytes(8, imgPreview);
            if (scoreMatch == null) ps.setNull(9, java.sql.Types.INTEGER); else ps.setInt(9, scoreMatch);
            ps.setString(10, deviceSn);
            ps.executeUpdate();
        }
    }
    
    // simpan / replace template enrolment subjek
public void upsertTemplateSubjek(String jenisSubjek, String kunciSubjek,
                                 byte[] templateBytes, String format, String device)
        throws Exception {
    String sql = "REPLACE INTO biometrik_subjek " +
                 "(jenis_subjek,kunci_subjek,template_iso,template_format,diagnosa"
            + ",updated_at) " +
                 "VALUES (?,?,?,?,?,NOW())";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, jenisSubjek);
        ps.setString(2, kunciSubjek);
        ps.setBytes(3, templateBytes);
        ps.setString(4, format);     // "DPJPOS"
        ps.setString(5, device);     // "UareU JavaPOS"
        ps.executeUpdate();
    }
}


    /** Overload lama (default format = JPOS). */
    public void upsertBukti(
        String noPernyataan, String tipePihak, String nik, String nama,
        Integer fingerIndex, byte[] templateBytes, byte[] imgPreview,
        Integer scoreMatch, String deviceSn
    ) throws Exception {
        upsertBukti(noPernyataan, tipePihak, nik, nama, fingerIndex, templateBytes, "JPOS",
                    imgPreview, scoreMatch, deviceSn);
    }

    /** Cek apakah sudah ada bukti fingerprint untuk pihak tertentu. */
    public boolean hasBukti(String noPernyataan, String tipePihak) throws Exception {
        String sql = "SELECT 1 FROM bukti_persetujuan_biometrik " +
                     "WHERE no_pernyataan=? AND tipe_pihak=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noPernyataan);
            ps.setString(2, tipePihak);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** Hapus fingerprint pihak tertentu. */
    public void deleteBukti(String noPernyataan, String tipePihak) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM bukti_persetujuan_biometrik WHERE no_pernyataan=? AND tipe_pihak=?")) {
            ps.setString(1, noPernyataan);
            ps.setString(2, tipePihak);
            ps.executeUpdate();
        }
    }

    /** Ambil template enrolment untuk verifikasi (opsional). */
    public byte[] getTemplateSubjek(String jenisSubjek, String kunciSubjek) throws Exception {
        String sql = "SELECT template_iso FROM biometrik_subjek " +
                     "WHERE jenis_subjek=? AND kunci_subjek=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, jenisSubjek);
            ps.setString(2, kunciSubjek);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBytes(1) : null;
            }
        }
    }
}
