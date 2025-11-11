package fungsi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO untuk simpan & ambil data biometrik (fingerprint) terkait bukti pernyataan.
 * Catatan: Connection disuntik dari luar (koneksiDB.condb()) dan TIDAK ditutup di sini.
 */
public class BiometrikDao {
    private final Connection conn;

    public BiometrikDao(Connection conn) {
        this.conn = conn;
    }

    /** REPLACE/UPSERT ke tabel bukti_persetujuan_biometrik */
    public void upsertBukti(
            String noPernyataan,
            String tipePihak,       // 'pasien' | 'keluarga' | 'pendamping' | 'petugas'
            String nik,
            String nama,
            Integer fingerIndex,    // boleh null
            byte[] templateIso,     // WAJIB: template ISO/ANSI dari SDK
            byte[] imgPreview,      // boleh null (thumbnail PNG)
            Integer scoreMatch,     // boleh null (kalau ada verifikasi)
            String deviceSn         // boleh null
    ) throws Exception {
        String sql = "REPLACE INTO bukti_persetujuan_biometrik " +
                "(no_pernyataan, tipe_pihak, nik, nama, finger_index, template_iso, img_preview, score_match, device_sn) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noPernyataan);
            ps.setString(2, tipePihak);
            ps.setString(3, nik);
            ps.setString(4, nama);
            if (fingerIndex == null) ps.setNull(5, java.sql.Types.TINYINT); else ps.setInt(5, fingerIndex);
            ps.setBytes(6, templateIso);
            if (imgPreview == null) ps.setNull(7, java.sql.Types.LONGVARBINARY); else ps.setBytes(7, imgPreview);
            if (scoreMatch == null) ps.setNull(8, java.sql.Types.INTEGER); else ps.setInt(8, scoreMatch);
            ps.setString(9, deviceSn);
            ps.executeUpdate();
        }
    }

    /** Ambil template enrolment dari biometrik_subjek (kalau Anda pakai verifikasi) */
    public byte[] getTemplateSubjek(String jenisSubjek, String kunciSubjek) throws Exception {
        String sql = "SELECT template_iso FROM biometrik_subjek " +
                     "WHERE jenis_subjek=? AND kunci_subjek=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, jenisSubjek);   // 'pasien' | 'keluarga' | 'petugas'
            ps.setString(2, kunciSubjek);   // no_rkm_medis / NIK / nip
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBytes(1) : null;
            }
        }
    }
}