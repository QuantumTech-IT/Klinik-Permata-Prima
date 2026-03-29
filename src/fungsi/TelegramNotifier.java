package fungsi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Utilitas kirim notifikasi ke Telegram developer.
 * Konfigurasi disimpan di: config/telegram.properties
 *
 * Cara pakai:
 *   TelegramNotifier.sendError("Pemesanan Toko", akses.getkode(), "pesan error", "detail");
 *   TelegramNotifier.sendInfo("Pemesanan Toko", akses.getkode(), "transaksi berhasil");
 */
public class TelegramNotifier {

    private static final String CONFIG_FILE = "config/telegram.properties";

    private static String BOT_TOKEN   = "";
    private static String CHAT_ID     = "";
    private static String CLINIC_NAME = "Klinik";
    private static boolean ENABLED    = false;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(CONFIG_FILE));
            BOT_TOKEN   = props.getProperty("telegram.bot_token",   "").trim();
            CHAT_ID     = props.getProperty("telegram.chat_id",     "").trim();
            CLINIC_NAME = props.getProperty("telegram.clinic_name", "Klinik").trim();
            ENABLED     = Boolean.parseBoolean(props.getProperty("telegram.enabled", "false"));
            System.out.println("[Telegram] Config loaded. Enabled=" + ENABLED);
        } catch (Exception e) {
            ENABLED = false;
            System.out.println("[Telegram] Config tidak ditemukan, notifikasi dinonaktifkan.");
        }
    }

    /**
     * Kirim notifikasi error ke Telegram.
     * Berjalan di background thread — tidak memblokir UI.
     *
     * @param modul    Nama modul (misal: "Pemesanan Toko")
     * @param user     Kode petugas yang login
     * @param errorMsg Pesan error singkat
     * @param detail   Detail tambahan (nama barang, kode, dsb) — boleh kosong
     */
    public static void sendError(String modul, String user, String errorMsg, String detail) {
        if (!ENABLED || BOT_TOKEN.isEmpty() || CHAT_ID.isEmpty()) return;

        String waktu = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String text  = "\uD83D\uDEA8 *ERROR - " + escape(CLINIC_NAME) + "*\n"
                     + "\uD83D\uDCCB Modul  : " + escape(modul) + "\n"
                     + "\uD83D\uDC64 User   : " + escape(user) + "\n"
                     + "\u26A0\uFE0F Error  : " + escape(errorMsg) + "\n"
                     + (detail != null && !detail.trim().isEmpty()
                         ? "\uD83D\uDCE6 Detail : " + escape(detail) + "\n" : "")
                     + "\uD83D\uDD50 Waktu  : " + waktu;

        new Thread(() -> send(text), "TelegramNotif-Error").start();
    }

    /**
     * Kirim notifikasi informasi (non-error) ke Telegram.
     */
    public static void sendInfo(String modul, String user, String msg) {
        if (!ENABLED || BOT_TOKEN.isEmpty() || CHAT_ID.isEmpty()) return;

        String waktu = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String text  = "\u2139\uFE0F *INFO - " + escape(CLINIC_NAME) + "*\n"
                     + "\uD83D\uDCCB Modul  : " + escape(modul) + "\n"
                     + "\uD83D\uDC64 User   : " + escape(user) + "\n"
                     + "\uD83D\uDCDD Info   : " + escape(msg) + "\n"
                     + "\uD83D\uDD50 Waktu  : " + waktu;

        new Thread(() -> send(text), "TelegramNotif-Info").start();
    }

    private static void send(String text) {
        try {
            String urlStr = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Escape untuk JSON string
            String safeText = text.replace("\\", "\\\\")
                                  .replace("\"", "\\\"")
                                  .replace("\n", "\\n")
                                  .replace("\r", "");

            String json = "{\"chat_id\":\"" + CHAT_ID + "\","
                        + "\"text\":\"" + safeText + "\","
                        + "\"parse_mode\":\"Markdown\"}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes("UTF-8"));
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                System.out.println("[Telegram] Notifikasi terkirim.");
            } else {
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                System.out.println("[Telegram] HTTP " + code + " - " + sb.toString());
            }
            conn.disconnect();
        } catch (Exception e) {
            System.out.println("[Telegram] Gagal kirim notifikasi: " + e.getMessage());
        }
    }

    /** Escape karakter khusus Markdown Telegram */
    private static String escape(String s) {
        if (s == null) return "-";
        return s.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("`", "\\`");
    }
}
