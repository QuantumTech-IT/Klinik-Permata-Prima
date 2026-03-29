package toko;

import fungsi.koneksiDB;
import fungsi.validasi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.table.DefaultTableModel;

public class DlgPreviewBillingToko extends JDialog {

    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private validasi Valid = new validasi();

    private DefaultTableModel tabMode;

    private widget.InternalFrame internalFrame1 = new widget.InternalFrame();
    private widget.ScrollPane scrollPane1 = new widget.ScrollPane();
    private widget.Table tbPreview = new widget.Table();

    private widget.Label LTotal = new widget.Label();
    private widget.Button BtnOK = new widget.Button();
    private widget.Button BtnPrint = new widget.Button();
    private widget.Button BtnKeluar = new widget.Button();

    // parameter
    private String noRawat = "";
    private String nota = "";
    private boolean disetujui = false;

    public DlgPreviewBillingToko(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(900, 600);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setTitle("Preview Billing (Toko)");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tabMode = new DefaultTableModel(null, new String[]{
            "P", "Kode", "Uraian", ":", "Tarif", "Jml", "Tamb", "Subtotal", "Jenis"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 0) return Boolean.class;
                return Object.class;
            }
        };

        tbPreview.setModel(tabMode);
        scrollPane1.setViewportView(tbPreview);

        BtnOK.setText("OK");
        BtnPrint.setText("Print");
        BtnKeluar.setText("Keluar");

        BtnOK.addActionListener(e -> {
            disetujui = true;
            dispose();
        });

        BtnPrint.addActionListener(e -> cetak());
        BtnKeluar.addActionListener(e -> {
            disetujui = false;
            dispose();
        });

        internalFrame1.setLayout(new java.awt.BorderLayout());
        internalFrame1.add(scrollPane1, java.awt.BorderLayout.CENTER);

        widget.PanelBiasa south = new widget.PanelBiasa();
        south.setLayout(new java.awt.BorderLayout());

        widget.PanelBiasa left = new widget.PanelBiasa();
        left.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 8));
        widget.Label lblTotal = new widget.Label();
lblTotal.setText("Total :");

left.add(lblTotal);
left.add(LTotal);
        left.add(LTotal);

        widget.PanelBiasa right = new widget.PanelBiasa();
        right.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 8));
        right.add(BtnOK);
        right.add(BtnPrint);
        right.add(BtnKeluar);

        south.add(left, java.awt.BorderLayout.WEST);
        south.add(right, java.awt.BorderLayout.EAST);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);
    }

    public void setNoRawat(String noRawat) {
        this.noRawat = (noRawat == null) ? "" : noRawat.trim();
    }

    public void setNota(String nota) {
        this.nota = (nota == null) ? "" : nota.trim();
    }

    public boolean isDisetujui() {
        return disetujui;
    }

    // === isi data preview (contoh: ambil dari billing / atau dari tabel lain sesuai kebutuhan) ===
   public void tampil() {
    Valid.tabelKosong(tabMode);
    double total = 0;

    System.out.println("DEBUG preview: nota=" + nota + " | noRawat=" + noRawat);

    // =========================
    // 1) OBAT TOKO (dari toko_detail_jual)
    // =========================
    if (nota != null && !nota.trim().equals("")) {
        String sqlObat =
            "SELECT tdj.kode_brng, tb.nama_brng, ks.satuan, " +
            "       tdj.h_jual, tdj.jumlah, tdj.tambahan, tdj.total " +
            "FROM toko_detail_jual tdj " +
            "JOIN tokobarang tb ON tb.kode_brng = tdj.kode_brng " +
            "JOIN kodesatuan ks ON ks.kode_sat = tdj.kode_sat " +
            "WHERE tdj.nota_jual = ? " +
            "ORDER BY tb.nama_brng";

        try {
            ps = koneksi.prepareStatement(sqlObat);
            ps.setString(1, nota);
            rs = ps.executeQuery();

            while (rs.next()) {
                String kode   = rs.getString("kode_brng");
                String uraian = rs.getString("nama_brng") + " (" + rs.getString("satuan") + ")";
                double tarif  = rs.getDouble("h_jual");
                double jml    = rs.getDouble("jumlah");
                double tamb   = rs.getDouble("tambahan");
                double sub    = rs.getDouble("total");   // sudah termasuk diskon/tambahan sesuai struktur kamu

                tabMode.addRow(new Object[]{
                    true, kode, uraian, ":",
                    tarif, jml, tamb, sub,
                    "Obat Toko"
                });

                total += sub;
            }
        } catch (Exception e) {
            System.out.println("Notif tampil obat toko: " + e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }

    // =========================
    // 2) TINDAKAN DOKTER (rawat_jl_dr) - kalau no_rawat ada
    // =========================
    if (noRawat != null && !noRawat.trim().equals("")) {

        String sqlDr =
            "SELECT jp.nm_perawatan, SUM(r.jml) AS jml, SUM(r.biaya_rawat) AS biaya " +
            "FROM rawat_jl_dr r " +
            "JOIN jns_perawatan jp ON jp.kd_jenis_prw = r.kd_jenis_prw " +
            "WHERE r.no_rawat = ? " +
            "GROUP BY r.kd_jenis_prw, jp.nm_perawatan " +
            "ORDER BY jp.nm_perawatan";

        try {
            ps = koneksi.prepareStatement(sqlDr);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();

            while (rs.next()) {
                String uraian = rs.getString("nm_perawatan");
                double jml    = rs.getDouble("jml");
                double biaya  = rs.getDouble("biaya");
                double tarif  = (jml > 0) ? (biaya / jml) : biaya;

                tabMode.addRow(new Object[]{
                    true, "", uraian, ":",
                    tarif, jml, 0, biaya,
                    "Ralan Dokter"
                });

                total += biaya;
            }
        } catch (Exception e) {
            System.out.println("Notif tampil tindakan dokter: " + e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }

    // TOTAL tampil Rupiah
    LTotal.setText("Rp" + Valid.SetAngka(total));
}

    private void cetak() {
        try {
            tbPreview.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal cetak: " + e.getMessage());
        }
    }
}
