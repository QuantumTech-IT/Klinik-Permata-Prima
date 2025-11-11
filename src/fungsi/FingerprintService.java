package fungsi;

/** Antarmuka layanan fingerprint (minimal, tanpa SDK). */
public interface FingerprintService {
    /** Capture template (ISO/ANSI) dari alat. */
    byte[] captureProbeTemplate() throws Exception;

    /** Opsional: serial number alat. */
    default String getDeviceSerial() { return null; }
}