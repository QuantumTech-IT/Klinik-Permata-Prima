/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toko;

import fungsi.validasi;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 *
 * @author USER
 */
public class DlgReturPenjualanToko extends JDialog {

    private JTable tbItem;
    private DefaultTableModel tm;
    private String noNota;
    private final Connection koneksi;       // kirim dari caller
    private final String nipPetugas;        // kirim dari caller (akses.getkode())
    private validasi Valid = new validasi();

    private JButton btnSimpan;

    public DlgReturPenjualanToko(Frame parent, boolean modal, Connection conn, String nip) {
        super(parent, modal);
        this.koneksi = conn;
        this.nipPetugas = nip;

        setTitle("Retur Penjualan");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(780, 520));

        buildUI();
        setLocationRelativeTo(parent);

        // ESC tutup
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // ENTER simpan
        getRootPane().setDefaultButton(btnSimpan);
    }

    /* ---------------- UI ---------------- */

    private void buildUI() {
        tm = new DefaultTableModel(
                null,
                new Object[]{"Pilih", "Kode", "Nama Barang", "Jml Jual", "Sisa Retur", "Jml Retur", "Harga", "Subtotal", "SatuanJual"}
        ) {
            @Override
            public Class<?> getColumnClass(int c) {
                return (c == 0) ? Boolean.class
                        : (c == 3 || c == 4 || c == 5 || c == 6 || c == 7) ? Double.class
                        : String.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 0 || c == 5;
            }
        };

        tbItem = new JTable(tm);
        tbItem.getColumnModel().getColumn(5).setCellEditor(new ZeroBlankDoubleEditor());

        // Sembunyikan kolom 8 (SatuanJual)
        tbItem.getColumnModel().getColumn(8).setMinWidth(0);
        tbItem.getColumnModel().getColumn(8).setMaxWidth(0);
        tbItem.getColumnModel().getColumn(8).setPreferredWidth(0);

        tbItem.setAutoCreateRowSorter(true);
        tbItem.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Renderer angka rata kanan
        DefaultTableCellRenderer rightZeroBlank = new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(SwingConstants.RIGHT);

        if (value == null) {
            setText("");
            return this;
        }

        if (value instanceof Number) {
            double v = ((Number) value).doubleValue();
            setText(Math.abs(v) < 0.0000001 ? "" : value.toString());
        } else {
            String s = value.toString();
            setText(("0".equals(s) || "0.0".equals(s)) ? "" : s);
        }

        return this;
    }
};

int[] numCols = {3,4,5,6,7}; // kolom angka
for (int c : numCols) {
    tbItem.getColumnModel().getColumn(c).setCellRenderer(rightZeroBlank);
}
        tbItem.getColumnModel().getColumn(0).setPreferredWidth(60);
        tbItem.getColumnModel().getColumn(2).setPreferredWidth(260);

        // hitung subtotal realtime saat Jml Retur berubah (event row = MODEL index)
        tm.addTableModelListener(e -> {
            if (e.getColumn() == 5 || e.getColumn() == 6) {
                hitungSubtotalBarisModel(e.getFirstRow());
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(tbItem), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSimpan = new JButton("Simpan Retur");
        btnSimpan.addActionListener(e -> simpanRetur());
        south.add(btnSimpan);
        getContentPane().add(south, BorderLayout.SOUTH);

        pack();
    }

    private void hitungSubtotalBarisModel(int r) {
        if (r < 0 || r >= tm.getRowCount()) return;
        Object oQty = tm.getValueAt(r, 5);
        Object oHarga = tm.getValueAt(r, 6);
        double q = (oQty instanceof Number) ? ((Number) oQty).doubleValue() : 0.0;
        double h = (oHarga instanceof Number) ? ((Number) oHarga).doubleValue() : 0.0;
        tm.setValueAt(q * h, r, 7);
    }

    /* ---------------- API ---------------- */

    public void setNoNota(String noNota) {
        this.noNota = noNota;
        setTitle("Retur Penjualan - " + noNota);
        loadItemNota();
    }

    public String getNoNota() {
        return noNota;
    }

    /* ---------------- DB load ---------------- */

    private void loadItemNota() {
        clearTable();

        // ✅ KONSISTEN dengan konsep "jumlah detail = NET setelah retur"
        // - sisa_retur cukup d.jumlah (net)
        // - jml_jual_awal = d.jumlah + sum(retur) (hanya untuk tampilan)
        final String sql =
                "SELECT d.kode_brng, b.nama_brng, d.kode_sat, d.h_jual, " +
                "       (d.jumlah + IFNULL((SELECT SUM(r.jml_retur) " +
                "           FROM toko_retur_penjualan r " +
                "           WHERE r.no_nota=d.nota_jual AND r.kode_brng=d.kode_brng AND r.satuan=d.kode_sat),0)) AS jml_jual_awal, " +
                "       d.jumlah AS sisa_retur " +
                "FROM toko_detail_jual d " +
                "JOIN tokobarang b ON b.kode_brng=d.kode_brng " +
                "WHERE d.nota_jual=? " +
                "ORDER BY b.nama_brng";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noNota);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tm.addRow(new Object[]{
                            false,
                            rs.getString("kode_brng"),
                            rs.getString("nama_brng"),
                            rs.getDouble("jml_jual_awal"),
                            Math.max(0.0, rs.getDouble("sisa_retur")),
                            0.0,
                            rs.getDouble("h_jual"),
                            0.0,
                            rs.getString("kode_sat") // hidden: satuan jual persis dari detail
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal load item: " + ex.getMessage());
        }
    }

    private void clearTable() {
        while (tm.getRowCount() > 0) tm.removeRow(0);
    }

    /* ---------------- Save ---------------- */

   private void simpanRetur() {
    if (noNota == null || noNota.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nomor nota kosong.");
        return;
    }

    // ===== VALIDASI UI =====
    int picked = 0;
    for (int i = 0; i < tm.getRowCount(); i++) {
        if (Boolean.TRUE.equals(tm.getValueAt(i, 0))) {
            double sisa = ((Number) tm.getValueAt(i, 4)).doubleValue();
            Double jml  = (Double) tm.getValueAt(i, 5);
            if (jml == null || jml <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah retur belum diisi pada baris: " + (i + 1));
                return;
            }
            if (jml > sisa) {
                JOptionPane.showMessageDialog(this, "Jumlah retur melebihi sisa pada item: " + tm.getValueAt(i, 2));
                return;
            }
            picked++;
        }
    }
    if (picked == 0) {
        JOptionPane.showMessageDialog(this, "Tidak ada item yang dipilih.");
        return;
    }

    String noRetur = "RT" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
    java.sql.Date tglRetur = new java.sql.Date(System.currentTimeMillis());

    final String INS_RETUR =
        "INSERT INTO toko_retur_penjualan " +
        "(no_retur, no_nota, kode_brng, jml_retur, satuan, harga, subtotal, tgl_retur, nip_petugas) " +
        "VALUES (?,?,?,?,?,?,?,?,?)";

    final String UPD_STOK_BESAR =
        "UPDATE tokobarang SET stok = stok + ? WHERE kode_brng=?";

    // lock + ambil detail
    final String GET_DETAIL_FOR_UPDATE =
        "SELECT jumlah, h_jual, subtotal, dis, bsr_dis, tambahan " +
        "FROM toko_detail_jual WHERE nota_jual=? AND kode_brng=? FOR UPDATE";

    // update detail setelah retur (tambahan kita set 0 dulu, karena akan dipasang 1x di akhir)
    final String UPD_DETAIL_AFTER_RETUR =
        "UPDATE toko_detail_jual SET jumlah=?, subtotal=?, dis=?, bsr_dis=?, tambahan=?, total=? " +
        "WHERE nota_jual=? AND kode_brng=?";

    // ambil total tuslah nota (1x per nota) = SUM(tambahan)
    final String GET_TUSLAH_NOTA =
        "SELECT COALESCE(SUM(tambahan),0) AS tuslah " +
        "FROM toko_detail_jual WHERE nota_jual=?";

    // normalisasi: nolkan tambahan di semua baris (biar gak dobel) + rapikan total = subtotal - bsr_dis
//    final String NORMALIZE_TAMBAHAN =
//        "UPDATE toko_detail_jual SET tambahan=0, total=(COALESCE(subtotal,0) - COALESCE(bsr_dis,0)) " +
//        "WHERE nota_jual=?";

    // hapus baris jumlah=0 (opsional biar bersih)
    final String DELETE_ZERO_ROWS =
        "DELETE FROM toko_detail_jual WHERE nota_jual=? AND COALESCE(jumlah,0) <= 0";

    // pilih 1 baris tersisa untuk “nempel” tuslah nota
    final String PICK_ONE_ROW =
        "SELECT kode_brng FROM toko_detail_jual " +
        "WHERE nota_jual=? ORDER BY kode_brng LIMIT 1 FOR UPDATE";

    // set tuslah ke 1 baris + update total baris tsb
//    final String APPLY_TUSLAH_ONE_ROW =
//        "UPDATE toko_detail_jual " +
//        "SET tambahan=?, total=(COALESCE(subtotal,0) - COALESCE(bsr_dis,0) + ?) " +
//        "WHERE nota_jual=? AND kode_brng=?";

    // sum total detail
    final String SUM_DETAIL_TOTAL =
        "SELECT COALESCE(SUM(total),0) AS total_detail " +
        "FROM toko_detail_jual WHERE nota_jual=?";

    // ambil ppn+ongkir dari header (lock)
    final String GET_HDR_FOR_UPDATE =
        "SELECT COALESCE(ppn,0) AS ppn, COALESCE(ongkir,0) AS ongkir " +
        "FROM tokopenjualan WHERE nota_jual=? FOR UPDATE";

    // update header total = total_detail + ppn + ongkir
    final String UPD_HDR_TOTAL =
        "UPDATE tokopenjualan SET total=? WHERE nota_jual=?";

    try {
        koneksi.setAutoCommit(false);

        try (PreparedStatement psGetTuslah = koneksi.prepareStatement(GET_TUSLAH_NOTA);
             //PreparedStatement psNormalize = koneksi.prepareStatement(NORMALIZE_TAMBAHAN);
             PreparedStatement psInsRetur  = koneksi.prepareStatement(INS_RETUR);
             PreparedStatement psStok      = koneksi.prepareStatement(UPD_STOK_BESAR);
             PreparedStatement psGetDetail = koneksi.prepareStatement(GET_DETAIL_FOR_UPDATE);
             PreparedStatement psUpdDetail = koneksi.prepareStatement(UPD_DETAIL_AFTER_RETUR);
             PreparedStatement psDelZero   = koneksi.prepareStatement(DELETE_ZERO_ROWS);
             PreparedStatement psPickOne   = koneksi.prepareStatement(PICK_ONE_ROW);
            // PreparedStatement psApplyOne  = koneksi.prepareStatement(APPLY_TUSLAH_ONE_ROW);
             PreparedStatement psSumDetail = koneksi.prepareStatement(SUM_DETAIL_TOTAL);
             PreparedStatement psGetHdr    = koneksi.prepareStatement(GET_HDR_FOR_UPDATE);
             PreparedStatement psUpdHdr    = koneksi.prepareStatement(UPD_HDR_TOTAL)) {

            // 1) ambil tuslah nota (1x per nota)
            double tuslahNota = 0.0;
            psGetTuslah.setString(1, noNota);
            try (ResultSet rs = psGetTuslah.executeQuery()) {
                if (rs.next()) tuslahNota = rs.getDouble("tuslah");
            }

            // 2) nolkan tambahan di semua detail (biar gak dobel)
//            psNormalize.setString(1, noNota);
//            psNormalize.executeUpdate();

            double totalNominalRetur = 0.0;

            // 3) proses tiap item yang diretur
            for (int i = 0; i < tm.getRowCount(); i++) {
                if (!Boolean.TRUE.equals(tm.getValueAt(i, 0))) continue;

                final String kode = String.valueOf(tm.getValueAt(i, 1));

                final String satuanJualRaw = String.valueOf(tm.getValueAt(i, 8)); // kode_sat jual (hidden)
                final String satuanJual = "RSEP".equalsIgnoreCase(satuanJualRaw)
                        ? satuanKecilDariBarang(kode)
                        : satuanJualRaw;

                final double qtyRetur = ((Number) tm.getValueAt(i, 5)).doubleValue();
                final double hargaUI  = ((Number) tm.getValueAt(i, 6)).doubleValue();

                // insert retur
                psInsRetur.setString(1, noRetur);
                psInsRetur.setString(2, noNota);
                psInsRetur.setString(3, kode);
                psInsRetur.setDouble(4, qtyRetur);
                psInsRetur.setString(5, satuanJual);
                psInsRetur.setDouble(6, hargaUI);
                psInsRetur.setDouble(7, qtyRetur * hargaUI);
                psInsRetur.setDate(8, tglRetur);
                psInsRetur.setString(9, nipPetugas);
                psInsRetur.addBatch();

                // kembalikan stok ke satuan terbesar
                Konversi konv = bacaKonversiDanSatuan(kode);
                double deltaStokBesar;
                if (satuanJual.equalsIgnoreCase(konv.satBesar)) {
                    deltaStokBesar = qtyRetur;
                } else if (satuanJual.equalsIgnoreCase(konv.satTengah)) {
                    deltaStokBesar = qtyRetur / konv.isi;
                } else { // kecil / default
                    deltaStokBesar = qtyRetur / (konv.isi * konv.kapasitas);
                }
                psStok.setDouble(1, deltaStokBesar);
                psStok.setString(2, kode);
                psStok.addBatch();

                // lock detail baris tsb
                psGetDetail.setString(1, noNota);
                psGetDetail.setString(2, kode);

                try (ResultSet rs = psGetDetail.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("Detail jual tidak ditemukan: " + kode);

                    double oldJumlah   = rs.getDouble("jumlah");
                    double hJual       = rs.getDouble("h_jual");
                    double oldSubtotal = rs.getDouble("subtotal");
                    double disPct      = rs.getDouble("dis");
                    double oldBsrDis   = rs.getDouble("bsr_dis");
                    // tambahan diabaikan di per-item (karena tuslah 1x per nota)
                     double oldTambahan = rs.getDouble("tambahan");

                    double newJumlah   = oldJumlah - qtyRetur;
                    double newSubtotal = newJumlah * hJual;

                    // diskon tetap dihitung dari subtotal baru
                    double newBsrDis;
                    if (disPct > 0) newBsrDis = Math.round(newSubtotal * disPct / 100.0);
                    else if (oldSubtotal > 0) newBsrDis = Math.round(oldBsrDis * (newSubtotal / oldSubtotal));
                    else newBsrDis = 0;

                    // ✅ tambahan TIDAK DIPROPORSIKAN (tetap per item)
                    double newTambahan = oldTambahan;

                    // ✅ total baris pakai tambahan asli
                    double newTotal = newSubtotal - newBsrDis + newTambahan;

                    // info nominal retur
                    totalNominalRetur += (oldSubtotal - newSubtotal) - (oldBsrDis - newBsrDis);

                    psUpdDetail.setDouble(1, newJumlah);
                    psUpdDetail.setDouble(2, newSubtotal);
                    psUpdDetail.setDouble(3, disPct);
                    psUpdDetail.setDouble(4, newBsrDis);
                    psUpdDetail.setDouble(5, newTambahan);
                    psUpdDetail.setDouble(6, newTotal);
                    psUpdDetail.setString(7, noNota);
                    psUpdDetail.setString(8, kode);
                    psUpdDetail.addBatch();
                }
            }

            // eksekusi batch
            psInsRetur.executeBatch();
            psStok.executeBatch();
            psUpdDetail.executeBatch();

            // 4) bersihkan baris qty=0 (biar rapi)
            psDelZero.setString(1, noNota);
            psDelZero.executeUpdate();

            // 5) pasang tuslah nota ke 1 baris tersisa (kalau masih ada item)
            String kodeTarget = null;
            psPickOne.setString(1, noNota);
            try (ResultSet rs = psPickOne.executeQuery()) {
                if (rs.next()) kodeTarget = rs.getString(1);
            }

//            if (kodeTarget != null && tuslahNota > 0) {
//                psApplyOne.setDouble(1, tuslahNota);
//                psApplyOne.setDouble(2, tuslahNota);
//                psApplyOne.setString(3, noNota);
//                psApplyOne.setString(4, kodeTarget);
//                psApplyOne.executeUpdate();
//            } else {
//                // kalau sudah tidak ada item sisa, tuslah jadi 0 (nota dianggap habis)
//                tuslahNota = 0;
//            }

            // 6) hitung total detail
            double totalDetail = 0.0;
            psSumDetail.setString(1, noNota);
            try (ResultSet rs = psSumDetail.executeQuery()) {
                if (rs.next()) totalDetail = rs.getDouble("total_detail");
            }

            // 7) ambil ppn+ongkir, update total header = detail + ppn + ongkir
            double ppn = 0.0, ongkir = 0.0;
            psGetHdr.setString(1, noNota);
            try (ResultSet rs = psGetHdr.executeQuery()) {
                if (rs.next()) {
                    ppn = rs.getDouble("ppn");
                    ongkir = rs.getDouble("ongkir");
                }
            }

            double totalBaruHeader = totalDetail + ppn + ongkir;

            psUpdHdr.setDouble(1, totalBaruHeader);
            psUpdHdr.setString(2, noNota);
            psUpdHdr.executeUpdate();

            koneksi.commit();

            JOptionPane.showMessageDialog(this,
                "Retur berhasil.\nNo. Retur: " + noRetur +
                "\nNominal retur (±): " + Valid.SetAngka(totalNominalRetur) +
                "\nTuslah (tetap 1x/nota): " + Valid.SetAngka(tuslahNota) +
                "\nTotal penjualan baru: " + Valid.SetAngka(totalBaruHeader)
            );
            dispose();
        }

    } catch (Exception ex) {
        try { koneksi.rollback(); } catch (Exception ignore) {}
        JOptionPane.showMessageDialog(this, "Gagal simpan retur: " + ex.getMessage());
    } finally {
        try { koneksi.setAutoCommit(true); } catch (Exception ignore) {}
    }
}


    private double num(Object o) {
        return (o instanceof Number) ? ((Number) o).doubleValue() : 0.0;
    }

    /* ===== helper: baca konversi & label satuan barang ===== */
    private static final class Konversi {
        String satBesar, satTengah, satKecil;
        double isi, kapasitas;
    }

    private Konversi bacaKonversiDanSatuan(String kodeBrg) throws SQLException {
        Konversi k = new Konversi();
        final String q =
                "SELECT kode_sat, kode_sat1, kode_sat2, IFNULL(isi,1) isi, IFNULL(kapasitas,1) kapasitas " +
                "FROM tokobarang WHERE kode_brng=?";

        try (PreparedStatement ps = koneksi.prepareStatement(q)) {
            ps.setString(1, kodeBrg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    k.satBesar = rs.getString("kode_sat");
                    k.satTengah = rs.getString("kode_sat1");
                    k.satKecil = rs.getString("kode_sat2");
                    k.isi = rs.getDouble("isi"); if (k.isi <= 0) k.isi = 1;
                    k.kapasitas = rs.getDouble("kapasitas"); if (k.kapasitas <= 0) k.kapasitas = 1;
                } else {
                    k.satBesar = "";
                    k.satTengah = "";
                    k.satKecil = "";
                    k.isi = 1;
                    k.kapasitas = 1;
                }
            }
        }
        return k;
    }
    // ===== helper: kalau satuan jual = "RSEP", ambil satuan kecil (kode_sat2)
private String satuanKecilDariBarang(String kodeBrg) {
    String sat = "";
    final String q = "SELECT kode_sat2 FROM tokobarang WHERE kode_brng=?";

    try (PreparedStatement ps = koneksi.prepareStatement(q)) {
        ps.setString(1, kodeBrg);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) sat = rs.getString(1);
        }
    } catch (Exception e) {
        // biarin kosong, nanti fallback
    }
    return (sat == null) ? "" : sat;
}
public class ZeroBlankNumberRenderer extends DefaultTableCellRenderer {
    private final DecimalFormat df = new DecimalFormat("#,##0");

    public ZeroBlankNumberRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value == null) {
            setText("");
            return this;
        }

        if (value instanceof Number) {
            double v = ((Number) value).doubleValue();
            if (Math.abs(v) < 0.0000001) {
                setText(""); // ✅ 0 jadi kosong
            } else {
                setText(df.format(v));
            }
        } else {
            setText(value.toString());
        }

        return this;
    }
}
private static class ZeroBlankDoubleEditor extends DefaultCellEditor {
    private final JTextField tf;

    ZeroBlankDoubleEditor() {
        super(new JTextField());
        tf = (JTextField) getComponent();
        tf.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        super.getTableCellEditorComponent(table, value, isSelected, row, column);

        if (value instanceof Number) {
            double v = ((Number) value).doubleValue();
            tf.setText(Math.abs(v) < 0.0000001 ? "" : String.valueOf(v));
        } else {
            tf.setText(value == null ? "" : value.toString());
        }
        return tf;
    }

    @Override
    public Object getCellEditorValue() {
        String s = tf.getText().trim();
        if (s.isEmpty()) return 0.0; // kosong dianggap 0 biar hitung aman
        try {
            return Double.parseDouble(s.replace(",", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }
}

}
