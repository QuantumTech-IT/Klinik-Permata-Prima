/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toko;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author ALI-PC
 */
public class DlgRekapReturPenjualanToko extends JDialog {

    private final Connection koneksi;
    private final String nipLogin; // opsional (kalau mau dipakai)

    private final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    // UI
    private JTabbedPane tabs;

    private JTable tbDetail;
    private DefaultTableModel tmDetail;

    private JTable tbRekapPetugas;
    private DefaultTableModel tmRekapPetugas;

    private JSpinner spTgl1, spTgl2;
    private JTextField TCari;

    private JLabel LTotalDetail, LTotalRekap;
    private JButton BtnCari, BtnPrint, BtnKeluar, BtnRefresh;

    public DlgRekapReturPenjualanToko(Frame parent, boolean modal, Connection conn, String nip) {
        super(parent, modal);
        this.koneksi = conn;
        this.nipLogin = nip;

        setTitle("Rekap Retur Penjualan Toko");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(980, 600));

        buildUI();
        setLocationRelativeTo(parent);

        // ESC tutup
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // default: range hari ini
        Date now = new Date();
        spTgl1.setValue(now);
        spTgl2.setValue(now);

        tampil(); // auto load
    }

    private void buildUI() {
        getContentPane().setLayout(new BorderLayout(8, 8));

        // ===== Top Filter =====
        JPanel pTop = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        spTgl1 = new JSpinner(new SpinnerDateModel());
        spTgl1.setEditor(new JSpinner.DateEditor(spTgl1, "yyyy-MM-dd"));

        spTgl2 = new JSpinner(new SpinnerDateModel());
        spTgl2.setEditor(new JSpinner.DateEditor(spTgl2, "yyyy-MM-dd"));

        TCari = new JTextField(28);

        BtnCari = new JButton("Cari");
        BtnRefresh = new JButton("Refresh");
        BtnPrint = new JButton("Print");
        BtnKeluar = new JButton("Keluar");

        BtnCari.addActionListener(e -> tampil());
        BtnRefresh.addActionListener(e -> {
            TCari.setText("");
            tampil();
        });

        BtnPrint.addActionListener(e -> printAktif());
        BtnKeluar.addActionListener(e -> dispose());

        // Enter di TCari -> cari
        TCari.addActionListener(e -> tampil());

        int col = 0;
        g.gridx = col++; g.gridy = 0; pTop.add(new JLabel("Tgl1"), g);
        g.gridx = col++; pTop.add(spTgl1, g);
        g.gridx = col++; pTop.add(new JLabel("Tgl2"), g);
        g.gridx = col++; pTop.add(spTgl2, g);

        g.gridx = col++; pTop.add(new JLabel("Cari"), g);
        g.gridx = col++; g.weightx = 1; pTop.add(TCari, g);
        g.weightx = 0;

        g.gridx = col++; pTop.add(BtnCari, g);
        g.gridx = col++; pTop.add(BtnRefresh, g);
        g.gridx = col++; pTop.add(BtnPrint, g);
        g.gridx = col++; pTop.add(BtnKeluar, g);

        getContentPane().add(pTop, BorderLayout.NORTH);

        // ===== Tabs =====
        tabs = new JTabbedPane();

        // --- TAB 1: Detail Retur ---
        tmDetail = new DefaultTableModel(null, new Object[]{
                "Tgl", "No Retur", "No Nota", "Kode", "Nama Barang",
                "Qty", "Sat", "Harga", "Subtotal", "NIP", "Nama Petugas"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 5 || c == 7 || c == 8) return Double.class;
                return String.class;
            }
        };
        tbDetail = new JTable(tmDetail);
        tbDetail.setAutoCreateRowSorter(true);
        setupNumericRenderer(tbDetail, new int[]{5,7,8});

        JPanel tab1 = new JPanel(new BorderLayout());
        tab1.add(new JScrollPane(tbDetail), BorderLayout.CENTER);

        JPanel foot1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        LTotalDetail = new JLabel("Total: 0");
        foot1.add(LTotalDetail);
        tab1.add(foot1, BorderLayout.SOUTH);

        tabs.addTab("Detail Retur", tab1);

        // --- TAB 2: Rekap per Petugas ---
        tmRekapPetugas = new DefaultTableModel(null, new Object[]{
                "NIP", "Nama Petugas", "Jumlah Retur", "Total Subtotal"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 2) return Long.class;
                if (c == 3) return Double.class;
                return String.class;
            }
        };
        tbRekapPetugas = new JTable(tmRekapPetugas);
        tbRekapPetugas.setAutoCreateRowSorter(true);
        setupNumericRenderer(tbRekapPetugas, new int[]{3});

        JPanel tab2 = new JPanel(new BorderLayout());
        tab2.add(new JScrollPane(tbRekapPetugas), BorderLayout.CENTER);

        JPanel foot2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        LTotalRekap = new JLabel("Total: 0");
        foot2.add(LTotalRekap);
        tab2.add(foot2, BorderLayout.SOUTH);

        tabs.addTab("Rekap per Petugas", tab2);

        getContentPane().add(tabs, BorderLayout.CENTER);

        pack();
    }

    private void setupNumericRenderer(JTable tb, int[] cols) {
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        for (int c : cols) {
            tb.getColumnModel().getColumn(c).setCellRenderer(new DefaultTableCellRenderer() {
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
                        // tampilkan kosong kalau 0
                        setText(Math.abs(v) < 0.0000001 ? "" : formatAngka(v));
                    } else {
                        setText(value.toString());
                    }
                    return this;
                }
            });
        }
    }

    private String formatAngka(double v) {
        // format simple (kalau kamu mau pakai Valid.SetAngka, ganti di sini)
        // tanpa desimal biar mirip rupiah
        return String.format("%,.0f", v);
    }

    // ====== Load Data ======
    public void tampil() {
        Date d1 = (Date) spTgl1.getValue();
        Date d2 = (Date) spTgl2.getValue();

        String tgl1 = ymd.format(d1);
        String tgl2 = ymd.format(d2);

        String key = TCari.getText().trim();

        tampilDetail(tgl1, tgl2, key);
        tampilRekapPetugas(tgl1, tgl2, key);
    }

    private void tampilDetail(String tgl1, String tgl2, String key) {
        kosongkan(tmDetail);

        final String sql =
                "SELECT r.tgl_retur, r.no_retur, r.no_nota, r.kode_brng, b.nama_brng, " +
                "       r.jml_retur, r.satuan, r.harga, r.subtotal, r.nip_petugas, p.nama AS nama_petugas " +
                "FROM toko_retur_penjualan r " +
                "LEFT JOIN tokobarang b ON b.kode_brng=r.kode_brng " +
                "LEFT JOIN petugas p ON p.nip=r.nip_petugas " +
                "WHERE DATE(r.tgl_retur) BETWEEN ? AND ? " +
                (key.isEmpty() ? "" :
                 "AND (r.no_retur LIKE ? OR r.no_nota LIKE ? OR r.kode_brng LIKE ? OR " +
                 "     b.nama_brng LIKE ? OR r.nip_petugas LIKE ? OR p.nama LIKE ?) ") +
                "ORDER BY r.tgl_retur, r.no_retur";

        double total = 0.0;

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            int idx = 1;
            ps.setString(idx++, tgl1);
            ps.setString(idx++, tgl2);

            if (!key.isEmpty()) {
                String k = "%" + key + "%";
                ps.setString(idx++, k);
                ps.setString(idx++, k);
                ps.setString(idx++, k);
                ps.setString(idx++, k);
                ps.setString(idx++, k);
                ps.setString(idx++, k);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double subtotal = rs.getDouble("subtotal");
                    total += subtotal;

                    tmDetail.addRow(new Object[]{
                            rs.getString("tgl_retur"),
                            rs.getString("no_retur"),
                            rs.getString("no_nota"),
                            rs.getString("kode_brng"),
                            rs.getString("nama_brng"),
                            rs.getDouble("jml_retur"),
                            rs.getString("satuan"),
                            rs.getDouble("harga"),
                            subtotal,
                            rs.getString("nip_petugas"),
                            rs.getString("nama_petugas")
                    });
                }
            }

            LTotalDetail.setText("Total: " + formatAngka(total));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load detail retur: " + e.getMessage());
        }
    }

    private void tampilRekapPetugas(String tgl1, String tgl2, String key) {
        kosongkan(tmRekapPetugas);

        final String sql =
                "SELECT r.nip_petugas, p.nama AS nama_petugas, COUNT(*) AS jml, SUM(r.subtotal) AS total " +
                "FROM toko_retur_penjualan r " +
                "LEFT JOIN petugas p ON p.nip=r.nip_petugas " +
                "LEFT JOIN tokobarang b ON b.kode_brng=r.kode_brng " +
                "WHERE DATE(r.tgl_retur) BETWEEN ? AND ? " +
                (key.isEmpty() ? "" :
                 "AND (r.no_retur LIKE ? OR r.no_nota LIKE ? OR r.kode_brng LIKE ? OR " +
                 "     b.nama_brng LIKE ? OR r.nip_petugas LIKE ? OR p.nama LIKE ?) ") +
                "GROUP BY r.nip_petugas, p.nama " +
                "ORDER BY total DESC";

        double totalAll = 0.0;

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            int idx = 1;
            ps.setString(idx++, tgl1);
            ps.setString(idx++, tgl2);

            if (!key.isEmpty()) {
                String k = "%" + key + "%";
                ps.setString(idx++, k);
                ps.setString(idx++, k);
                ps.setString(idx++, k);
                ps.setString(idx++, k);
                ps.setString(idx++, k);
                ps.setString(idx++, k);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long jml = rs.getLong("jml");
                    double tot = rs.getDouble("total");
                    totalAll += tot;

                    tmRekapPetugas.addRow(new Object[]{
                            rs.getString("nip_petugas"),
                            rs.getString("nama_petugas"),
                            jml,
                            tot
                    });
                }
            }

            LTotalRekap.setText("Total: " + formatAngka(totalAll));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load rekap petugas: " + e.getMessage());
        }
    }

    private void kosongkan(DefaultTableModel tm) {
        while (tm.getRowCount() > 0) tm.removeRow(0);
    }

    // ===== Print =====
    private void printAktif() {
        try {
            JTable aktif = (tabs.getSelectedIndex() == 0) ? tbDetail : tbRekapPetugas;
            String judul = (tabs.getSelectedIndex() == 0) ? "Rekap Detail Retur" : "Rekap Retur per Petugas";

            MessageFormat header = new MessageFormat(judul + "  (" +
                    ymd.format((Date) spTgl1.getValue()) + " s/d " +
                    ymd.format((Date) spTgl2.getValue()) + ")");
            MessageFormat footer = new MessageFormat("Halaman {0}");

            aktif.print(JTable.PrintMode.FIT_WIDTH, header, footer);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal print: " + e.getMessage());
        }
    }
}