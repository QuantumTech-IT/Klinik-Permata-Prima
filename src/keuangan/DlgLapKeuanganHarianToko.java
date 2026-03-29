package keuangan;

import fungsi.koneksiDB;
import fungsi.validasi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class DlgLapKeuanganHarianToko extends JDialog {

    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private validasi Valid = new validasi();

    private DefaultTableModel tabMode;

    // ======= komponen =======
    private widget.InternalFrame internalFrame1 = new widget.InternalFrame();
    private widget.ScrollPane scrollPane1 = new widget.ScrollPane();
    private widget.Table tbLaporan = new widget.Table();

    // Highlight atas
    private widget.Label LKeuntunganHarian = new widget.Label();

    // Footer
    private widget.Label LSubtotal = new widget.Label();
    private widget.Label LDiskon   = new widget.Label();
    private widget.Label LTuslah   = new widget.Label();
    private widget.Label LTotal    = new widget.Label();
    private widget.Label LHPP      = new widget.Label();
    private widget.Label LUntung   = new widget.Label();

    private widget.Button BtnPreview = new widget.Button();
    private widget.Button BtnPrint   = new widget.Button();
    private widget.Button BtnKeluar  = new widget.Button();

    // parameter dari PaymentPoint
    private String tgl = "";
    private String shift = "Semua";
    private String user = "";

    // optional untuk header laporan
    private double modalAwal = 0;
    private double pengeluaran = 0;
    private double qris = 0;

    // ====== warna modern ======
    private final Color ROW_ODD  = new Color(250, 251, 253);
    private final Color ROW_EVEN = Color.WHITE;
    private final Color SEL_BG   = new Color(225, 236, 255);
    private final Color GRID     = new Color(230, 233, 238);

    public DlgLapKeuanganHarianToko(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1020, 600);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setTitle("Laporan Keuangan Harian (Toko)");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Model: kolom nominal bertipe Double, plus Untung % (Double)
        tabMode = new DefaultTableModel(null, new String[]{
            "Tgl", "Nota", "Kasir", "Metode",
            "Subtotal", "Diskon", "Tuslah", "Total", "HPP", "Untung", "Untung %"
        }) {
            Class[] types = new Class[]{
                String.class, String.class, String.class, String.class,
                Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class
            };

            @Override public Class<?> getColumnClass(int columnIndex) { return types[columnIndex]; }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tbLaporan.setModel(tabMode);
        scrollPane1.setViewportView(tbLaporan);

        // Tombol
        BtnPreview.setText("Preview");
        BtnPrint.setText("Print");
        BtnKeluar.setText("Keluar");

        BtnPreview.addActionListener(e -> tampil());
        BtnPrint.addActionListener(e -> cetak());
        BtnKeluar.addActionListener(e -> dispose());

        // ===== Header highlight (Total keuntungan harian) =====
        widget.PanelBiasa topInfo = new widget.PanelBiasa();
        topInfo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 8));

        widget.Label lbl = makeLabel("Total Keuntungan Harian :");
        lbl.setFont(new Font("Tahoma", Font.BOLD, 12));

        LKeuntunganHarian.setText("Rp0 (0.00%)");
        LKeuntunganHarian.setFont(new Font("Tahoma", Font.BOLD, 14));

        topInfo.add(lbl);
        topInfo.add(LKeuntunganHarian);

        // Layout internal frame
        internalFrame1.setLayout(new java.awt.BorderLayout());
        internalFrame1.add(topInfo, java.awt.BorderLayout.NORTH);
        internalFrame1.add(scrollPane1, java.awt.BorderLayout.CENTER);

        // Footer total
        widget.PanelBiasa footer = new widget.PanelBiasa();
        footer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 8));

        footer.add(makeLabel("Subtotal :")); footer.add(LSubtotal);
        footer.add(makeLabel("Diskon :"));   footer.add(LDiskon);
        footer.add(makeLabel("Tuslah :"));   footer.add(LTuslah);
        footer.add(makeLabel("Total :"));    footer.add(LTotal);
        footer.add(makeLabel("HPP :"));      footer.add(LHPP);
        footer.add(makeLabel("Untung :"));   footer.add(LUntung);

        widget.PanelBiasa actions = new widget.PanelBiasa();
        actions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 8));
        actions.add(BtnPreview);
        actions.add(BtnPrint);
        actions.add(BtnKeluar);

        widget.PanelBiasa south = new widget.PanelBiasa();
        south.setLayout(new java.awt.BorderLayout());
        south.add(footer, java.awt.BorderLayout.CENTER);
        south.add(actions, java.awt.BorderLayout.EAST);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        // Modern style
        applyModernTableStyle();

        // Enter = preview
        getRootPane().setDefaultButton(BtnPreview);
    }

    // dipanggil dari PaymentPoint
    public void setParameter(String tgl, String shift, String user,
                             double modalAwal, double pengeluaran, double qris) {
        this.tgl = tgl;
        this.shift = (shift == null || shift.equals("")) ? "Semua" : shift;
        this.user = (user == null) ? "" : user;
        this.modalAwal = modalAwal;
        this.pengeluaran = pengeluaran;
        this.qris = qris;
    }

    // ======= TAMPIL =======
    public void tampil() {
        Valid.tabelKosong(tabMode);

        double subtotalAll = 0, diskonAll = 0, tuslahAll = 0, totalAll = 0, hppAll = 0, untungAll = 0;

        String sql =
          "SELECT " +
          " tp.tgl_jual, tp.nota_jual, " +
          " SUM(tdj.subtotal) subtotal, SUM(tdj.bsr_dis) diskon, SUM(tdj.tambahan) tuslah, SUM(tdj.total) total, " +
          " SUM( " +
          "   CASE " +
          "     WHEN tdj.kode_sat = tb.kode_sat  THEN IFNULL(tb.h_beli,0) * tdj.jumlah " + // BOX
          "     WHEN tdj.kode_sat = tb.kode_sat2 THEN (IFNULL(tb.h_beli,0) / NULLIF(tb.kapasitas,0)) * tdj.jumlah " + // STR -> /kapasitas
          "     WHEN tdj.kode_sat = tb.kode_sat1 THEN (IFNULL(tb.h_beli,0) / NULLIF(tb.isi,0)) * tdj.jumlah " + // level lain -> /isi
          "     ELSE IFNULL(tb.h_beli,0) * tdj.jumlah " +
          "   END " +
          " ) AS hpp, " +
          " (SUM(tdj.total) - SUM( " +
          "   CASE " +
          "     WHEN tdj.kode_sat = tb.kode_sat  THEN IFNULL(tb.h_beli,0) * tdj.jumlah " +
          "     WHEN tdj.kode_sat = tb.kode_sat2 THEN (IFNULL(tb.h_beli,0) / NULLIF(tb.kapasitas,0)) * tdj.jumlah " +
          "     WHEN tdj.kode_sat = tb.kode_sat1 THEN (IFNULL(tb.h_beli,0) / NULLIF(tb.isi,0)) * tdj.jumlah " +
          "     ELSE IFNULL(tb.h_beli,0) * tdj.jumlah " +
          "   END " +
          " )) AS untung " +
          "FROM tokopenjualan tp " +
          "JOIN toko_detail_jual tdj ON tp.nota_jual = tdj.nota_jual " +
          "JOIN tokobarang tb ON tb.kode_brng = tdj.kode_brng " +
          "WHERE tp.tgl_jual = ? " +
          "GROUP BY tp.tgl_jual, tp.nota_jual " +
          "ORDER BY tp.nota_jual";

        try {
            ps = koneksi.prepareStatement(sql);
            ps.setString(1, tgl); // yyyy-MM-dd

            rs = ps.executeQuery();
            while (rs.next()) {
                double sub = rs.getDouble("subtotal");
                double dis = rs.getDouble("diskon");
                double tus = rs.getDouble("tuslah");
                double tot = rs.getDouble("total");
                double hpp = rs.getDouble("hpp");
                double unt = rs.getDouble("untung");

                double persenUntung = persenMarkup(unt, hpp); // (unt/hpp)*100

                subtotalAll += sub;
                diskonAll   += dis;
                tuslahAll   += tus;
                totalAll    += tot;
                hppAll      += hpp;
                untungAll   += unt;

                tabMode.addRow(new Object[]{
                    rs.getString("tgl_jual"),
                    rs.getString("nota_jual"),
                    "", // kasir (isi nanti jika field sudah jelas)
                    "", // metode
                    sub, dis, tus, tot, hpp, unt,
                    persenUntung
                });
            }
        } catch (Exception e) {
            System.out.println("Notif tampil LapKeu: " + e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }

        // Footer tampil Rupiah + persen
        LSubtotal.setText(rupiah(subtotalAll));
        LDiskon.setText(rupiah(diskonAll));
        LTuslah.setText(rupiah(tuslahAll));
        LTotal.setText(rupiah(totalAll));
        LHPP.setText(rupiah(hppAll));
        LUntung.setText(rupiah(untungAll) + " (" + persenStr(untungAll, hppAll) + ")");

        // Highlight total keuntungan harian (Rp + %)
        LKeuntunganHarian.setText(rupiah(untungAll) + " (" + persenStr(untungAll, hppAll) + ")");
        if (untungAll < 0) {
            LKeuntunganHarian.setForeground(new Color(180, 0, 0));
        } else {
            LKeuntunganHarian.setForeground(new Color(0, 120, 0));
        }
    }

    // ======= CETAK =======
    private void cetak() {
        try {
            HashMap<String, Object> param = new HashMap<>();
            param.put("tanggal", tgl);
            param.put("shift", shift);
            param.put("user", user);
            param.put("modal_awal", modalAwal);
            param.put("pengeluaran", pengeluaran);
            param.put("qris", qris);

            // kalau sudah punya jasper:
            // Valid.MyReport("rptKeuanganTokoHarian.jasper","report","::[ Laporan Keuangan Toko Harian ]::",param);

            // fallback cepat: print tabel
            tbLaporan.print();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal cetak: " + e.getMessage());
        }
    }

    // ======= Helper label widget =======
    private widget.Label makeLabel(String text){
        widget.Label l = new widget.Label();
        l.setText(text);
        return l;
    }

    // ======= Rupiah (footer/highlight) =======
    private String rupiah(double v){
        return "Rp" + Valid.SetAngka(v);
    }

    // ======= Persen Untung (markup vs HPP) =======
    private double persenMarkup(double unt, double hpp){
        return (hpp > 0) ? (unt / hpp) * 100.0 : 0.0;
    }

    private String persenStr(double unt, double hpp){
        double p = persenMarkup(unt, hpp);

        DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id", "ID"));
        sym.setGroupingSeparator('.');
        sym.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.00", sym);

        return df.format(p) + "%";
    }

    // ======= Modern Table Style + Renderer =======
    private void applyModernTableStyle() {
        tbLaporan.setRowHeight(28);
        tbLaporan.setShowHorizontalLines(true);
        tbLaporan.setShowVerticalLines(false);
        tbLaporan.setGridColor(GRID);
        tbLaporan.setIntercellSpacing(new java.awt.Dimension(0, 1));
        tbLaporan.setSelectionBackground(SEL_BG);
        tbLaporan.setSelectionForeground(Color.BLACK);
        tbLaporan.setFont(new Font("Tahoma", Font.PLAIN, 12));

        // Header modern
        JTableHeader h = tbLaporan.getTableHeader();
        h.setFont(new Font("Tahoma", Font.BOLD, 12));
        h.setOpaque(true);
        h.setBackground(new Color(245, 247, 250));
        h.setForeground(new Color(40, 40, 40));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID));

        // Zebra renderer default (untuk kolom teks)
        DefaultTableCellRenderer zebraText = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSelected) c.setBackground((row % 2 == 0) ? ROW_EVEN : ROW_ODD);
                c.setHorizontalAlignment(SwingConstants.LEFT);
                c.setForeground(new Color(30, 30, 30));
                return c;
            }
        };

        tbLaporan.setDefaultRenderer(Object.class, zebraText);
        tbLaporan.setDefaultRenderer(String.class, zebraText);

        // Rupiah renderer untuk kolom nominal (index 4..9)
        RupiahRenderer rr = new RupiahRenderer();
        for (int c = 4; c <= 9; c++) {
            tbLaporan.getColumnModel().getColumn(c).setCellRenderer(rr);
        }

        // Percent renderer untuk kolom Untung % (index 10)
        tbLaporan.getColumnModel().getColumn(10).setCellRenderer(new PercentRenderer());

        // Lebar kolom
        tbLaporan.getColumnModel().getColumn(0).setPreferredWidth(90);   // tgl
        tbLaporan.getColumnModel().getColumn(1).setPreferredWidth(170);  // nota
        tbLaporan.getColumnModel().getColumn(2).setPreferredWidth(140);  // kasir
        tbLaporan.getColumnModel().getColumn(3).setPreferredWidth(110);  // metode
        for (int c = 4; c <= 9; c++) {
            tbLaporan.getColumnModel().getColumn(c).setPreferredWidth(120);
        }
        tbLaporan.getColumnModel().getColumn(10).setPreferredWidth(90);  // untung %
    }

    // Renderer rupiah + zebra + rata kanan + untung minus merah (kolom untung = index 9)
    private class RupiahRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat df;

        RupiahRenderer() {
            DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id", "ID"));
            sym.setGroupingSeparator('.');
            sym.setDecimalSeparator(',');
            df = new DecimalFormat("#,##0", sym);
            setHorizontalAlignment(SwingConstants.RIGHT);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            JLabel c = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            double v = 0;
            if (value instanceof Number) {
                v = ((Number) value).doubleValue();
            } else if (value != null) {
                try { v = Double.parseDouble(value.toString()); } catch (Exception e) { v = 0; }
            }

            c.setText("Rp" + df.format(v));
            c.setHorizontalAlignment(SwingConstants.RIGHT);

            if (!isSelected) c.setBackground((row % 2 == 0) ? ROW_EVEN : ROW_ODD);

            // Untung (kolom index 9) minus jadi merah
            if (column == 9 && v < 0) {
                c.setForeground(new Color(180, 0, 0));
            } else {
                c.setForeground(new Color(30, 30, 30));
            }
            return c;
        }
    }

    // Renderer persen (Untung %) + zebra + minus merah
    private class PercentRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat df;

        PercentRenderer() {
            DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id", "ID"));
            sym.setGroupingSeparator('.');
            sym.setDecimalSeparator(',');
            df = new DecimalFormat("#,##0.00", sym);
            setHorizontalAlignment(SwingConstants.RIGHT);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            JLabel c = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            double v = 0;
            if (value instanceof Number) v = ((Number) value).doubleValue();

            c.setText(df.format(v) + "%");
            c.setHorizontalAlignment(SwingConstants.RIGHT);

            if (!isSelected) c.setBackground((row % 2 == 0) ? ROW_EVEN : ROW_ODD);

            c.setForeground(v < 0 ? new Color(180, 0, 0) : new Color(30, 30, 30));
            return c;
        }
    }
}
