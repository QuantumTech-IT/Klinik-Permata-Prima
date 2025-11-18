package fungsi;

import jpos.Biometrics;
import jpos.events.DataEvent;
import jpos.events.DataListener;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;

/** Service JavaPOS untuk U.are.U (logicalName = UareU_FP di jpos.xml). */
public class JavaPOSFingerprintService implements FingerprintService {

    private static final String LOGICAL_NAME = "UareU_FP";

    @Override
    public byte[] captureProbeTemplate() throws Exception {
        Biometrics bio = new Biometrics();
        final Object lock = new Object();
        final byte[][] result = new byte[1][];
        final Exception[] err = new Exception[1];

        try {
            bio.open(LOGICAL_NAME);
            bio.claim(5000);
            bio.setDeviceEnabled(true);
            bio.setDataEventEnabled(true);
            bio.setAutoDisable(false);

            bio.addDataListener(new DataListener() {
                @Override public void dataOccurred(DataEvent de) {
                    try {
                        byte[] tmpl = null;
                        try { tmpl = (byte[]) Biometrics.class.getMethod("getTemplate").invoke(bio); }
                        catch (Throwable ignore1) {
                            try { tmpl = (byte[]) Biometrics.class.getMethod("getRawData").invoke(bio); }
                            catch (Throwable ignore2) {
                                try { tmpl = (byte[]) Biometrics.class.getMethod("getSensorData").invoke(bio); }
                                catch (Throwable ignore3) { /* tetap null */ }
                            }
                        }
                        result[0] = tmpl;
                    } catch (Throwable ex) {
                        err[0] = new Exception(ex);
                    } finally {
                        synchronized (lock) { lock.notifyAll(); }
                    }
                }
            });
            bio.addErrorListener(new ErrorListener() {
                @Override public void errorOccurred(ErrorEvent ee) {
                    err[0] = new Exception("JavaPOS error: " + ee.getErrorCode() + "/" + ee.getErrorCodeExtended());
                    synchronized (lock) { lock.notifyAll(); }
                }
            });

            // mulai capture (metode bisa beda; pakai refleksi agar fleksibel)
            try { Biometrics.class.getMethod("beginEnrollCapture").invoke(bio); }
            catch (Throwable ignore) {
                try { Biometrics.class.getMethod("beginCapture").invoke(bio); }
                catch (Throwable ignore2) { /* biarkan event otomatis */ }
            }

            synchronized (lock) { lock.wait(15000); } // tunggu max 15s

            try { Biometrics.class.getMethod("endCapture").invoke(bio); } catch (Throwable ignore) {}

            if (err[0] != null) throw err[0];
            if (result[0] == null || result[0].length == 0)
                throw new Exception("Tidak ada data template dari perangkat.");

            return result[0];
        } finally {
            try { bio.setDeviceEnabled(false); } catch (Exception ignored) {}
            try { bio.release(); } catch (Exception ignored) {}
            try { bio.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    public String getDeviceSerial() {
        return "UareU-JavaPOS";
    }
}
