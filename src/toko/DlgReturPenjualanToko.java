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
    private validasi Valid=new validasi();

    public DlgReturPenjualanToko(Frame parent, boolean modal, Connection conn, String nip) {
        super(parent, modal);
        this.koneksi = conn;
        this.nipPetugas = nip;

        setTitle("Retur Penjualan");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(780, 520));

        buildUI();
        setLocationRelativeTo(parent);

        // ESC tutup, ENTER simpan
        getRootPane().registerKeyboardAction(e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().setDefaultButton(btnSimpan);
    }

    /* ---------------- UI ---------------- */

    private JButton btnSimpan;

    private void buildUI() {
       tm = new DefaultTableModel(
    null, new Object[]{"Pilih","Kode","Nama Barang","Jml Jual","Sisa Retur","Jml Retur","Harga","Subtotal","SatuanJual"}
){
    @Override public Class<?> getColumnClass(int c){
        return (c==0) ? Boolean.class :
               (c==3||c==4||c==5||c==6||c==7) ? Double.class : String.class;
    }
    @Override public boolean isCellEditable(int r,int c){ return c==0 || c==5; }
};
tbItem = new JTable(tm);
// Sembunyikan kolom 8 (SatuanJual)
tbItem.getColumnModel().getColumn(8).setMinWidth(0);
tbItem.getColumnModel().getColumn(8).setMaxWidth(0);
tbItem.getColumnModel().getColumn(8).setPreferredWidth(0);
        tbItem.setAutoCreateRowSorter(true);
        tbItem.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Renderer angka rata kanan
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        int[] numCols = {3,4,5,6,7};
        for (int c : numCols) {
            tbItem.getColumnModel().getColumn(c).setCellRenderer(right);
        }
        tbItem.getColumnModel().getColumn(0).setPreferredWidth(60);
        tbItem.getColumnModel().getColumn(2).setPreferredWidth(260);

        // hitung subtotal realtime saat Jml Retur berubah
        tm.addTableModelListener(e -> {
            if (e.getColumn()==5 || e.getColumn()==6) hitungSubtotalBaris(e.getFirstRow());
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

    private void hitungSubtotalBaris(int rView){
        if (rView<0) return;
        int r = tbItem.convertRowIndexToModel(rView);
        Object oQty = tm.getValueAt(r, 5);
        Object oHarga = tm.getValueAt(r, 6);
        double q = (oQty instanceof Number) ? ((Number)oQty).doubleValue() : 0.0;
        double h = (oHarga instanceof Number) ? ((Number)oHarga).doubleValue() : 0.0;
        tm.setValueAt(q*h, r, 7);
    }

    /* ---------------- API ---------------- */

    public void setNoNota(String noNota){
        this.noNota = noNota;
        setTitle("Retur Penjualan - " + noNota);
        loadItemNota();
    }

    public String getNoNota(){ return noNota; }

    /* ---------------- DB load ---------------- */

    private void loadItemNota(){
        clearTable();
        final String sql =
    "SELECT d.kode_brng, b.nama_brng, d.jumlah AS jml_jual, d.kode_sat, d.h_jual, " +
    "       (d.jumlah - IFNULL((SELECT SUM(r.jml_retur) " +
    "         FROM toko_retur_penjualan r " +
    "         WHERE r.no_nota=d.nota_jual AND r.kode_brng=d.kode_brng),0)) AS sisa_retur " +
    "FROM toko_detail_jual d " +
    "JOIN tokobarang b ON b.kode_brng=d.kode_brng " +
    "WHERE d.nota_jual=? ORDER BY b.nama_brng";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noNota);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double jmlJual   = rs.getDouble("jml_jual");
                    double sisaRetur = rs.getDouble("sisa_retur");
                    double harga     = rs.getDouble("h_jual");
                    tm.addRow(new Object[]{
    false,
    rs.getString("kode_brng"),
    rs.getString("nama_brng"),
    rs.getDouble("jml_jual"),
    Math.max(0.0, rs.getDouble("sisa_retur")),
    0.0,                       // Jml Retur
    rs.getDouble("h_jual"),
    0.0,                       // Subtotal
    rs.getString("kode_sat")   // << simpan satuan jual (hidden)
});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal load item: " + ex.getMessage());
        }
    }

    private void clearTable(){
        while (tm.getRowCount()>0) tm.removeRow(0);
    }

    /* ---------------- Save ---------------- */

//    private void simpanRetur(){
//        if (noNota==null || noNota.isEmpty()){
//            JOptionPane.showMessageDialog(this, "Nomor nota kosong.");
//            return;
//        }
//
//        // validasi JmlRetur
//        for (int i=0;i<tm.getRowCount();i++){
//            boolean pilih   = Boolean.TRUE.equals(tm.getValueAt(i,0));
//            double sisa     = ((Number)tm.getValueAt(i,4)).doubleValue();
//            double jmlRetur = ((Number)tm.getValueAt(i,5)).doubleValue();
//            if (pilih && (jmlRetur<=0 || jmlRetur> sisa)){
//                JOptionPane.showMessageDialog(this,
//                    "Jumlah retur tidak valid untuk item: " + tm.getValueAt(i,2) +
//                    "\nMaksimal: " + sisa);
//                return;
//            }
//        }
//
//        String noRetur = "RT" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        Date now = new Date();
//        java.sql.Date tglRetur = new java.sql.Date(now.getTime());
//
//        final String ins =
//            "INSERT INTO toko_retur_penjualan " +
//            "(no_retur, no_nota, kode_brng, jml_retur, satuan, harga, subtotal, tgl_retur, nip_petugas) " +
//            "VALUES (?,?,?,?,?,?,?,?,?)";
//
//        try (PreparedStatement ps = koneksi.prepareStatement(ins)) {
//            int cnt = 0;
//            for (int i=0;i<tm.getRowCount();i++){
//                boolean pilih = Boolean.TRUE.equals(tm.getValueAt(i,0));
//                if (!pilih) continue;
//
//                String kode   = String.valueOf(tm.getValueAt(i,1));
//                double qty    = ((Number)tm.getValueAt(i,5)).doubleValue();
//                double harga  = ((Number)tm.getValueAt(i,6)).doubleValue();
//
//                ps.setString(1, noRetur);
//                ps.setString(2, noNota);
//                ps.setString(3, kode);
//                ps.setDouble(4, qty);
//                ps.setString(5, "PCS");                     // atau ambil dari d.kode_sat
//                ps.setDouble(6, harga);
//                ps.setDouble(7, qty*harga);
//                ps.setDate(8, tglRetur);
//                ps.setString(9, nipPetugas);
//                ps.addBatch();
//                cnt++;
//
//                // --- OPSIONAL: update stok kembali ke gudang toko ---
//                // try (PreparedStatement psu = koneksi.prepareStatement(
//                //     "UPDATE tokobarang SET stok = stok + ? WHERE kode_brng=?")){
//                //     psu.setDouble(1, qty);
//                //     psu.setString(2, kode);
//                //     psu.executeUpdate();
//                // }
//            }
//
//            if (cnt==0){
//                JOptionPane.showMessageDialog(this, "Tidak ada item dipilih.");
//                return;
//            }
//
//            ps.executeBatch();
//            JOptionPane.showMessageDialog(this, "Retur tersimpan. No.Retur: "+noRetur);
//            dispose();
//
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Gagal simpan retur: " + ex.getMessage());
//        }
//    }
    private void simpanRetur() {
    if (noNota == null || noNota.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nomor nota kosong.");
        return;
    }

    String noRetur = "RT" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
    java.sql.Date tglRetur = new java.sql.Date(System.currentTimeMillis());

    final String INS =
        "INSERT INTO toko_retur_penjualan " +
        "(no_retur, no_nota, kode_brng, jml_retur, satuan, harga, subtotal, tgl_retur, nip_petugas) " +
        "VALUES (?,?,?,?,?,?,?,?,?)";

    final String UPD_STOK_BESAR =
        "UPDATE tokobarang SET stok = stok + ? WHERE kode_brng=?";

    // === tambahan: SQL untuk koreksi detail & header ===
    final String GET_DETAIL_FOR_UPDATE =
        "SELECT jumlah, h_jual, subtotal, dis, bsr_dis, tambahan " +
        "FROM toko_detail_jual WHERE nota_jual=? AND kode_brng=? FOR UPDATE";

    final String UPD_DETAIL_AFTER_RETUR =
        "UPDATE toko_detail_jual SET jumlah=?, subtotal=?, dis=?, bsr_dis=?, tambahan=?, total=? " +
        "WHERE nota_jual=? AND kode_brng=?";

    final String RECALC_HDR =
        "SELECT COALESCE(SUM(total),0) AS totalbaru FROM toko_detail_jual WHERE nota_jual=?";

    final String UPD_HDR =
        "UPDATE tokopenjualan SET total=? WHERE nota_jual=?";

    // ====== VALIDASI UI (punyamu) ======
    int picked = 0;
    for (int i = 0; i < tm.getRowCount(); i++) {
        if (Boolean.TRUE.equals(tm.getValueAt(i, 0))) {
            double sisa = ((Number) tm.getValueAt(i, 4)).doubleValue();
            Double jml  = (Double) tm.getValueAt(i, 5);
            if (jml == null || jml <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah retur belum diisi pada baris: " + (i+1));
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

    try {
        koneksi.setAutoCommit(false);

        try (PreparedStatement psIns  = koneksi.prepareStatement(INS);
             PreparedStatement psStok = koneksi.prepareStatement(UPD_STOK_BESAR);
             PreparedStatement psGet  = koneksi.prepareStatement(GET_DETAIL_FOR_UPDATE);
             PreparedStatement psUpdD = koneksi.prepareStatement(UPD_DETAIL_AFTER_RETUR);
             PreparedStatement psRe   = koneksi.prepareStatement(RECALC_HDR);
             PreparedStatement psUpdH = koneksi.prepareStatement(UPD_HDR)) {

            double totalNominalRetur = 0.0;

            for (int i = 0; i < tm.getRowCount(); i++) {
                if (!Boolean.TRUE.equals(tm.getValueAt(i, 0))) continue;

                final String kode = String.valueOf(tm.getValueAt(i, 1));
                final String satuanJualRaw = String.valueOf(tm.getValueAt(i, 8)); // bisa "RSEP"
                final String satuanJual = "RSEP".equalsIgnoreCase(satuanJualRaw)
                        ? satuanKecilDariBarang(kode)
                        : satuanJualRaw;

                final double qtyRetur = ((Number) tm.getValueAt(i, 5)).doubleValue();
                final double hargaUI  = ((Number) tm.getValueAt(i, 6)).doubleValue();

                // --- Insert retur (gunakan harga dari UI; kalau mau pakai dari DB, ganti setelah GET_DETAIL) ---
                psIns.setString(1, noRetur);
                psIns.setString(2, noNota);
                psIns.setString(3, kode);
                psIns.setDouble(4, qtyRetur);
                psIns.setString(5, satuanJual);
                psIns.setDouble(6, hargaUI);
                psIns.setDouble(7, qtyRetur * hargaUI);
                psIns.setDate(8, tglRetur);
                psIns.setString(9, nipPetugas); // pastikan variabel ada
                psIns.addBatch();

                // --- Kembalikan stok ke satuan terbesar ---
                Konversi konv = bacaKonversiDanSatuan(kode); // satBesar, satTengah, satKecil, isi, kapasitas
                double deltaStokBesar;
                if (satuanJual.equalsIgnoreCase(konv.satBesar)) {
                    deltaStokBesar = qtyRetur;
                } else if (satuanJual.equalsIgnoreCase(konv.satTengah)) {
                    deltaStokBesar = qtyRetur / konv.isi;
                } else if (satuanJual.equalsIgnoreCase(konv.satKecil)) {
                    deltaStokBesar = qtyRetur / (konv.isi * konv.kapasitas);
                } else {
                    deltaStokBesar = qtyRetur / (konv.isi * konv.kapasitas);
                }
                psStok.setDouble(1, deltaStokBesar);
                psStok.setString(2, kode);
                psStok.addBatch();

                // --- Ambil current detail & KOREKSI nominal sesuai retur ---
                psGet.setString(1, noNota);
                psGet.setString(2, kode);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("Detail jual tidak ditemukan: " + kode);

                    double oldJumlah   = rs.getDouble("jumlah");
                    double hJual       = rs.getDouble("h_jual");
                    double oldSubtotal = rs.getDouble("subtotal");
                    double disPct      = rs.getDouble("dis");      // persen
                    double oldBsrDis   = rs.getDouble("bsr_dis");  // nominal
                    double oldTambahan = rs.getDouble("tambahan"); // nominal (anggap per-item → diproporsikan)

                    double newJumlah   = oldJumlah - qtyRetur;
                    if (newJumlah < 0) throw new IllegalArgumentException("Retur melebihi jumlah jual untuk " + kode);

                    double newSubtotal = newJumlah * hJual;

                    double newBsrDis;
                    if (disPct > 0) {
                        newBsrDis = Math.round(newSubtotal * disPct / 100.0);
                    } else if (oldSubtotal > 0) {
                        newBsrDis = Math.round(oldBsrDis * (newSubtotal / oldSubtotal));
                    } else {
                        newBsrDis = 0;
                    }

                    double newTambahan = (oldJumlah > 0) ? Math.round(oldTambahan * (newJumlah / oldJumlah)) : 0;
                    double newTotal    = newSubtotal - newBsrDis + newTambahan;

                    // akumulasi nominal retur untuk info (pakai harga jual)
                    totalNominalRetur += (oldSubtotal - newSubtotal) - (oldBsrDis - newBsrDis) + (oldTambahan - newTambahan);

                    psUpdD.setDouble(1, newJumlah);
                    psUpdD.setDouble(2, newSubtotal);
                    psUpdD.setDouble(3, disPct);
                    psUpdD.setDouble(4, newBsrDis);
                    psUpdD.setDouble(5, newTambahan);
                    psUpdD.setDouble(6, newTotal);
                    psUpdD.setString(7, noNota);
                    psUpdD.setString(8, kode);
                    psUpdD.addBatch();
                }
            }

            // Eksekusi batch simpan & update
            psIns.executeBatch();
            psStok.executeBatch();
            psUpdD.executeBatch();

            // Recalc header dari jumlah total baris detail
            psRe.setString(1, noNota);
            double totalBaru = 0.0;
            try (ResultSet rs = psRe.executeQuery()) {
                if (rs.next()) totalBaru = rs.getDouble("totalbaru");
            }

            psUpdH.setDouble(1, totalBaru);
            psUpdH.setString(2, noNota);
            psUpdH.executeUpdate();

            koneksi.commit();

            JOptionPane.showMessageDialog(this,
                "Retur berhasil.\nNo. Retur: " + noRetur +
                "\nNominal retur (±): " + Valid.SetAngka(totalNominalRetur) +
                "\nTotal penjualan baru: " + Valid.SetAngka(totalBaru));
            dispose();
        }

    } catch (Exception ex) {
        try { koneksi.rollback(); } catch (Exception ignore) {}
        JOptionPane.showMessageDialog(this, "Gagal simpan retur: " + ex.getMessage());
    } finally {
        try { koneksi.setAutoCommit(true); } catch (Exception ignore) {}
    }
}


/* ===== helper: ambil satuan kecil untuk 'RSEP' ===== */
private String satuanKecilDariBarang(String kodeBrg) {
    try (PreparedStatement ps = koneksi.prepareStatement(
            "SELECT kode_sat2 FROM tokobarang WHERE kode_brng=?")) {
        ps.setString(1, kodeBrg);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString(1);
        }
    } catch (Exception ignored) {}
    return ""; // fallback aman
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
                k.satBesar   = rs.getString("kode_sat");
                k.satTengah  = rs.getString("kode_sat1");
                k.satKecil   = rs.getString("kode_sat2");
                k.isi        = rs.getDouble("isi");        if (k.isi <= 0) k.isi = 1;
                k.kapasitas  = rs.getDouble("kapasitas");  if (k.kapasitas <= 0) k.kapasitas = 1;
            } else {
                // default aman
                k.satBesar=""; k.satTengah=""; k.satKecil=""; k.isi=1; k.kapasitas=1;
            }
        }
    }
    return k;
}
}
    

