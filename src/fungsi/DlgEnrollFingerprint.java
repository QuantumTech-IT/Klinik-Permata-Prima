package fungsi;

import jpos.Biometrics;
import jpos.BiometricsConst;
import jpos.JposConst;
import jpos.JposException;
import jpos.events.DataEvent;
import jpos.events.DataListener;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

import com.digitalpersona.javapos.services.biometrics.DPFPConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DlgEnrollFingerprint extends JDialog {
    // UI
    private final JLabel lblInfo = new JLabel("Siapkan jari…", SwingConstants.CENTER);
    private final JLabel lblStep = new JLabel("0/4", SwingConstants.CENTER);
    private final PreviewPanel canvas = new PreviewPanel();
    private final JButton btnStart = new JButton("Mulai Enroll");
    private final JButton btnBatal = new JButton("Batal");

    // Device
    private Biometrics bio;
    private int captured = 0;
    private byte[] resultBir = null;
    private CountDownLatch done;

    // Config
    private static final int ENROLL_NEED = 4;               // jumlah sentuhan
    private static final int OPEN_TIMEOUT_MS = 5000;        // claim timeout
    private static final int WAIT_AFTER_SEC = 2;            // tunggu setelah complete

    public DlgEnrollFingerprint(Window owner) {
        super(owner, "Enroll Fingerprint", ModalityType.APPLICATION_MODAL);
        buildUI();
        pack();
        setMinimumSize(new Dimension(420, 560));
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        lblInfo.setFont(lblInfo.getFont().deriveFont(Font.BOLD, 14f));
        lblStep.setFont(lblStep.getFont().deriveFont(Font.PLAIN, 13f));

        JPanel top = new JPanel(new GridLayout(2,1,0,6));
        top.add(lblInfo);
        top.add(lblStep);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        buttons.add(btnStart);
        buttons.add(btnBatal);

        setLayout(new BorderLayout(8,8));
        add(top, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        btnStart.addActionListener(e -> new Thread(this::doEnroll).start());
        btnBatal.addActionListener(e -> dispose());
    }

    /** panggil ini dari form kamu; return null kalau batal/gagal */
    public byte[] showAndEnroll() {
        setVisible(true);
        return resultBir;
    }

    // ====== LOGIKA ENROLL ======

    private void doEnroll() {
        btnStart.setEnabled(false);
        try {
            captured = 0;
            resultBir = null;
            done = new CountDownLatch(ENROLL_NEED);

            openClaimEnable();

            // listener update preview + step
            bio.addStatusUpdateListener(statusListener);
            bio.addErrorListener(errorListener);
            bio.addDataListener(dataListener);
            bio.setDataEventEnabled(true);

            // mulai enroll
            setInfo("Tempelkan jari (butuh " + ENROLL_NEED + "x sentuh) …");
            bio.beginEnrollCapture(new byte[0], new byte[0]);

            // tunggu sampai 4 kali capture (atau dialog ditutup)
            if (!done.await(60, TimeUnit.SECONDS)) {
                throw new Exception("Timeout enroll");
            }

            // selesai, akhiri capture dan sedikit jeda
            safe(() -> bio.endCapture());
            setInfo("Enroll lengkap. Menyimpan template …");
            Thread.sleep(WAIT_AFTER_SEC * 500L);

        } catch (Exception ex) {
            showErr(ex.getMessage());
        } finally {
            // bersih-bersih device
            safe(() -> bio.setDataEventEnabled(false));
            safe(() -> bio.removeStatusUpdateListener(statusListener));
            safe(() -> bio.removeErrorListener(errorListener));
            safe(() -> bio.removeDataListener(dataListener));
            safe(() -> { bio.setDeviceEnabled(false); bio.release(); bio.close(); });
            btnStart.setEnabled(true);
            if (resultBir != null) dispose();
        }
    }

    private void openClaimEnable() throws Exception {
        bio = new Biometrics();
        // Pastikan JCL (jpos.xml) sudah dipasang sebelumnya oleh helper kamu
        bio.open("DPFingerprintReader");          // logicalName dari jposUareU.xml
        bio.claim(OPEN_TIMEOUT_MS);
        bio.setDeviceEnabled(true);
    }

    // listener data: tiap sentuhan sukses saat ENROLL -> getBIR() berisi template
    private final DataListener dataListener = new DataListener() {
        @Override public void dataOccurred(DataEvent event) {
            if (event.getStatus() == BiometricsConst.BIO_DATA_ENROLL) {
                try {
                    byte[] bir = bio.getBIR();
                    if (bir != null && bir.length > 0) {
                        resultBir = bir.clone();   // ambil yang terakhir
                        captured++;
                        setStep(captured, ENROLL_NEED);
                        if (captured >= ENROLL_NEED) {
                            done.countDown(); // habiskan latch
                            done.countDown();
                            done.countDown();
                            done.countDown();
                        } else {
                            done.countDown();
                        }
                    }
                } catch (JposException ignore) {}
            }
        }
    };

    // listener status: update preview dan instruksi
    private final StatusUpdateListener statusListener = new StatusUpdateListener() {
        @Override public void statusUpdateOccurred(StatusUpdateEvent e) {
            int code = e.getStatus();
            switch (code) {
                case DPFPConstants.DP_EVENT_FINGER_TOUCHED:
                    setInfo("Jari terdeteksi… tahan sebentar");
                    break;
                case DPFPConstants.DP_EVENT_FINGER_GONE:
                    setInfo("Angkat jari…");
                    break;
                case DPFPConstants.DP_EVENT_DISCONNECT:
                    setInfo("Perangkat terlepas.");
                    break;
                case DPFPConstants.DP_EVENT_RECONNECT:
                    setInfo("Perangkat tersambung.");
                    break;
                default:
                    // kemungkinan raw sensor data tersedia -> coba render
                    byte[] raw = tryGetRawSensorData();
                    if (raw != null && raw.length > 0) {
                        canvas.updateImage(raw);
                    }
            }
        }
    };

    private final ErrorListener errorListener = new ErrorListener() {
        @Override public void errorOccurred(ErrorEvent e) {
            setInfo("Error: " + e.getErrorCode() + "/" + e.getErrorCodeExtended());
        }
    };

    // --- util UI ---
    private void setInfo(String s) { SwingUtilities.invokeLater(() -> lblInfo.setText(s)); }
    private void setStep(int cur, int need) {
        SwingUtilities.invokeLater(() -> lblStep.setText(cur + " / " + need));
    }
    private void showErr(String s) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(DlgEnrollFingerprint.this, s, "Gagal", JOptionPane.ERROR_MESSAGE));
    }
    private void safe(ThrowingRunnable r) { try { r.run(); } catch (Exception ignore) {} }
    @FunctionalInterface private interface ThrowingRunnable { void run() throws Exception; }

    // --- ambil raw sensor data (DigitalPersona menaruh sebagai byte[] image) ---
    private byte[] tryGetRawSensorData() {
        try {
            // langsung (kalau API ada)
            try {
                Method m = bio.getClass().getMethod("getRawSensorData");
                Object o = m.invoke(bio);
                return (o instanceof byte[]) ? (byte[]) o : null;
            } catch (NoSuchMethodException ns) {
                return null;
            }
        } catch (Exception ignore) {
            return null;
        }
    }

    // panel preview
    static class PreviewPanel extends JPanel {
        private BufferedImage img;

        void updateImage(byte[] bytes) {
            BufferedImage bi = decode(bytes);
            if (bi != null) {
                this.img = bi;
                repaint();
            }
        }

        private BufferedImage decode(byte[] bytes) {
            try {
                // kalau driver kirim PNG/JPEG
                return ImageIO.read(new ByteArrayInputStream(bytes));
            } catch (Exception ignore) { /* lanjut raw grayscale */ }

            // fallback raw grayscale – coba deteksi ukuran lazim
            int[][] whCandidates = new int[][] {
                {252,324}, {324,252}, {256,360}, {360,256}, {300,400}, {400,300}
            };
            for (int[] wh : whCandidates) {
                int w = wh[0], h = wh[1];
                if (w*h == bytes.length) {
                    BufferedImage gray = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                    gray.getRaster().setDataElements(0, 0, w, h, bytes);
                    return gray;
                }
            }
            return null;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(245,247,250));
            g.fillRect(0,0,getWidth(),getHeight());
            if (img != null) {
                // fit center
                double sx = getWidth() / (double) img.getWidth();
                double sy = getHeight() / (double) img.getHeight();
                double s = Math.min(sx, sy);
                int w = (int)(img.getWidth() * s);
                int h = (int)(img.getHeight() * s);
                int x = (getWidth()-w)/2, y=(getHeight()-h)/2;
                g.drawImage(img, x, y, w, h, null);
            } else {
                g.setColor(Color.GRAY);
                g.drawString("Preview akan muncul di sini…", 10, getHeight()/2);
            }
        }

        @Override public Dimension getPreferredSize() { return new Dimension(360, 400); }
    }
}