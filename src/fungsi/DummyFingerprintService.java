package fungsi;

import javax.swing.JFileChooser;
import java.nio.file.Files;
import java.nio.file.Path;

/** Implementasi dummy untuk uji alur tanpa alat fingerprint. */
public class DummyFingerprintService implements FingerprintService {

    @Override
    public byte[] captureProbeTemplate() throws Exception {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Pilih file template fingerprint (ISO/ANSI) untuk uji");
        int r = fc.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            Path p = fc.getSelectedFile().toPath();
            return Files.readAllBytes(p);
        }
        throw new Exception("Dibatalkan oleh pengguna");
    }

    @Override
    public String getDeviceSerial() {
        return "DUMMY-SN";
    }
}