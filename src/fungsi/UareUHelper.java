package fungsi;

import jpos.Biometrics;
import jpos.BiometricsConst;
import jpos.JposException;
import jpos.events.DataEvent;
import jpos.events.DataListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Helper kecil untuk operasi fingerprint U.are.U via JavaPOS.
 * Default logicalName = "DPFingerprintReader" (sesuai jposUareU.xml sample).
 *
 * Pastikan sekali di awal aplikasi kamu sudah panggil:
 *   JposBootstrap.ensure();
 * atau biarkan helper mencoba memanggilnya sendiri (best-effort).
 */
public final class UareUHelper {
    /** Bisa dioverride lewat -Duareu.logicalName=NamaService */
    private static final String LOGICAL_NAME =
            System.getProperty("uareu.logicalName", "DPFingerprintReader");

    public UareUHelper() {}

    /* ====== PUBLIC API ====== */

    /** Capture 1x (verify capture). Return template BIR (byte[]) atau throw Exception on timeout/error. */
    public static byte[] captureOnce(int timeoutSec) throws Exception {
        ensureBootstrap();
        Biometrics bio = null;
        try {
            bio = openClaimEnable();
            final Biometrics bioRef = bio;
            final byte[][] out = new byte[1][];
            final CountDownLatch done = new CountDownLatch(1);

            DataListener listener = (DataEvent e) -> {
                try {
                    // Untuk verify, status event biasanya BIO_DATA_VERIFY; ambil BIR apapun statusnya
                    out[0] = bioRef.getBIR();
                } catch (Exception ignore) {}
                done.countDown();
            };
            bio.addDataListener(listener);
            bio.setDataEventEnabled(true);

            // mulai capture sample (sekali sentuh)
            bio.beginVerifyCapture();

            if (!done.await(timeoutSec, TimeUnit.SECONDS)) {
                throw new Exception("Timeout capture fingerprint");
            }

            // beres
            bio.setDataEventEnabled(false);
            bio.removeDataListener(listener);
            return out[0];
        } finally {
            safeClose(bio);
        }
    }

    /** Enroll (driver akan minta beberapa kali sentuhan). Return template enroll final (BIR). */
    public static byte[] enrollOnce(int timeoutSec) throws Exception {
        ensureBootstrap();
        Biometrics bio = null;
        try {
            bio = openClaimEnable();
            final Biometrics bioRef = bio; 
            final byte[][] out = new byte[1][];
            final CountDownLatch done = new CountDownLatch(1);

            DataListener listener = (DataEvent e) -> {
                try {
                    if (e.getStatus() == BiometricsConst.BIO_DATA_ENROLL) {
                        out[0] = bioRef.getBIR();
                        done.countDown();
                    }
                } catch (Exception ignore) {}
            };
            bio.addDataListener(listener);
            bio.setDataEventEnabled(true);

            // mulai proses enroll; refBir & payload boleh kosong
            bio.beginEnrollCapture(new byte[0], new byte[0]);

            if (!done.await(timeoutSec, TimeUnit.SECONDS)) {
                throw new Exception("Timeout enroll fingerprint");
            }

            bio.setDataEventEnabled(false);
            bio.removeDataListener(listener);
            return out[0];
        } finally {
            safeClose(bio);
        }
    }

    /**
     * Pastikan subjek sudah punya template di tabel biometrik_subjek.
     * Jika belum ada → jalankan ENROLL, simpan via BiometrikDao, lalu kembalikan templatenya.
     */
    public static byte[] ensureEnrollmentOrGetExisting(
            String jenisSubjek,   // "pasien" | "keluarga" | "petugas"
            String kunciSubjek,   // no_rkm_medis / NIK / NIP
            BiometrikDao biomDao,
            int timeoutSec
    ) throws Exception {
        byte[] tpl = biomDao.getTemplateSubjek(jenisSubjek, kunciSubjek);
        if (tpl != null && tpl.length > 0) return tpl;

        // belum ada → enroll sekarang
        tpl = enrollOnce(timeoutSec);
        // Simpan; siapkan method berikut di BiometrikDao (lihat catatan di bawah)
        biomDao.upsertTemplateSubjek(jenisSubjek, kunciSubjek, tpl, "DPJPOS", "UareU JavaPOS");
        return tpl;
    }

    /** Capture sample lalu bandingkan offline dengan template enroll yang diberikan. */
    public static boolean verifyMatch(byte[] enrollmentTemplate, int timeoutSec) throws Exception {
        ensureBootstrap();
        Biometrics bio = null;
        try {
            bio = openClaimEnable();
final Biometrics bioRef = bio; 
            // 1) capture sample
            final byte[][] sample = new byte[1][];
            final CountDownLatch done = new CountDownLatch(1);
            DataListener listener = (DataEvent e) -> {
                try { sample[0] = bioRef.getBIR(); } catch (Exception ignore) {}
                done.countDown();
            };
            bio.addDataListener(listener);
            bio.setDataEventEnabled(true);
            bio.beginVerifyCapture();

            if (!done.await(timeoutSec, TimeUnit.SECONDS)) {
                throw new Exception("Timeout capture fingerprint");
            }
            bio.setDataEventEnabled(false);
            bio.removeDataListener(listener);

            // 2) offline match
            boolean[] result = new boolean[1];
            int maxFARRequested = 2147483; // angka default dari sample DP
            int maxFRRRequested = 1;
            boolean FARPrecedence = true;
            int[] FARAchieved = new int[1];
            int[] FRRAchieved = new int[1];
            byte[][] adaptedBIR = new byte[1][];
            byte[][] payload = new byte[1][];

            bio.verifyMatch(maxFARRequested, maxFRRRequested, FARPrecedence,
                    sample[0], enrollmentTemplate, adaptedBIR,
                    result, FARAchieved, FRRAchieved, payload);

            return result[0];
        } finally {
            safeClose(bio);
        }
    }

    /* ====== INTERNAL ====== */

    private static Biometrics openClaimEnable() throws JposException {
        Biometrics bio = new Biometrics();
        bio.open(LOGICAL_NAME);
        bio.claim(5000);
        bio.setDeviceEnabled(true);
        return bio;
    }

    private static void safeClose(Biometrics bio) {
        if (bio == null) return;
        try { bio.setDeviceEnabled(false); } catch (Exception ignore) {}
        try { bio.release(); }              catch (Exception ignore) {}
        try { bio.close(); }                catch (Exception ignore) {}
    }

    /** Best-effort memanggil bootstrap agar JCL/registry sudah siap. */
    private static void ensureBootstrap() {
        try {
            // Jika JposBootstrap tersedia di project, panggil. Jika tidak, abaikan.
            Class<?> c = Class.forName("fungsi.JposBootstrap");
            c.getMethod("ensure").invoke(null);
        } catch (Throwable ignore) { /* no-op */ }
    }
    
    public void setupJposOnce() {
    // kalau kamu mau pakai file sample yang sudah terbukti jalan:
    String jposPath = "C:\\Program Files\\DigitalPersona\\U.are.U SDK\\Windows\\Samples\\UareUSampleJavaPOS\\config\\jpos\\res\\jposUareU.xml";
    System.setProperty("jpos.config.populator.class","jpos.config.simple.xml.SimpleXmlRegPopulator");
    System.setProperty("jpos.config.populator.file", jposPath);

    // (opsional) injeksi entry ke registry JPOS kalau kosong
    try {
        jpos.loader.simple.SimpleServiceManager mgr =
            (jpos.loader.simple.SimpleServiceManager) jpos.loader.JposServiceLoader.getManager();
        jpos.config.simple.SimpleEntryRegistry reg =
            (jpos.config.simple.SimpleEntryRegistry) mgr.getEntryRegistry();

        boolean has = false;
        java.util.Enumeration<?> en = reg.getEntries();
        while (en.hasMoreElements()) {
            jpos.config.JposEntry e = (jpos.config.JposEntry) en.nextElement();
            if ("DPFingerprintReader".equals(e.getLogicalName())) { has = true; break; }
        }
        if (!has) {
            jpos.config.simple.xml.SimpleXmlRegPopulator pop = new jpos.config.simple.xml.SimpleXmlRegPopulator();
            pop.load(jposPath);
            java.util.Enumeration<?> en2 = pop.getEntries();
            while (en2.hasMoreElements()) {
                jpos.config.JposEntry e = (jpos.config.JposEntry) en2.nextElement();
                reg.addJposEntry(e);
            }
        }
    } catch (Exception ignore) {}
}
    
}
