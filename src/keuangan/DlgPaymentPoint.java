/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgLhtBiaya.java
 *
 * Created on 12 Jul 10, 16:21:34
 */

package keuangan;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JScrollPane;
import toko.TuslahTokopenjualanService;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author perpustakaan
 */
public final class DlgPaymentPoint extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps,psjamshift;
    private ResultSet rs,rsjamshift;
    //private double all=0,pagi=0,siang=0,sore=0,malam=0;
    private int i;
    private String shift="",tanggal2="",nonota="",petugas="";
    private DlgLapKeuanganHarianToko dlgKeuangan = new DlgLapKeuanganHarianToko(null, false);

    /** Creates new form DlgLhtBiaya
     * @param parent
     * @param modal */
    public DlgPaymentPoint(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(885,674);
        //ModalAwal.setText(Sequel.cariIsi("select modal_awal from toko_kasir_shift"));  
        BtnSeek4.setVisible(false);

        Object[] rowRwJlDr={"No.","Tanggal","Shift","No.Rawat/No.Nota","Nama Pasien","Pembayaran","Petugas"};
        tabMode=new DefaultTableModel(null,rowRwJlDr){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
             Class[] types = new Class[] {
                java.lang.String.class,java.lang.String.class,java.lang.String.class,
                java.lang.String.class,java.lang.String.class,java.lang.Double.class,
                java.lang.String.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        Tabel.setModel(tabMode);
        //tbBangsal.setDefaultRenderer(Object.class, new WarnaTable(jPanel2.getBackground(),tbBangsal.getBackground()));
        // untuk format nominal
class MoneyRenderer extends javax.swing.table.DefaultTableCellRenderer {
    private final java.text.NumberFormat rupiah = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));
    private final java.awt.Font boldFont;

    public MoneyRenderer(javax.swing.JTable table) {
        setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        boldFont = table.getFont().deriveFont(java.awt.Font.BOLD);
    }

    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
        java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // default reset warna/font
        c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        c.setFont(table.getFont());

        // ambil label di kolom 1 (index 1)
        Object labelObj = table.getValueAt(row, 1);
        String label = labelObj == null ? "" : labelObj.toString();

        // format rupiah
        if (value != null) {
            try {
                double d = Double.parseDouble(value.toString());
                setText(rupiah.format(d));
            } catch (Exception e) {
                // biarkan apa adanya
            }
        }

        // >> Total ditebalkan
        if (">> Total".equalsIgnoreCase(label)) {
            c.setFont(boldFont);
        }

        // Selisih → merah kalau minus, hijau kalau plus
        if ("Selisih".equalsIgnoreCase(label)) {
            double angka = 0;
            try { angka = Double.parseDouble(value.toString()); } catch (Exception ignored) {}
            if (angka < 0) {
                c.setForeground(new java.awt.Color(200, 0, 0));
            } else {
                c.setForeground(new java.awt.Color(0, 128, 0));
            }
            c.setFont(boldFont);
        }

        return c;
    }
}
class SummaryLabelRenderer extends DefaultTableCellRenderer {
    private final Font bold;
    public SummaryLabelRenderer(JTable table) { bold = table.getFont().deriveFont(Font.BOLD); }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String label = value == null ? "" : value.toString();
        if ("Modal Awal".equalsIgnoreCase(label) ||
            "Uang Masuk".equalsIgnoreCase(label) ||
            ">> Total".equalsIgnoreCase(label) ||
            "Tutup Kasir".equalsIgnoreCase(label) ||
            "Selisih".equalsIgnoreCase(label)) {
            c.setFont(bold);
        }
        return c;
    }
}
        Tabel.getColumnModel().getColumn(5).setCellRenderer(new MoneyRenderer(Tabel));
        Tabel.getColumnModel().getColumn(1).setCellRenderer(new SummaryLabelRenderer(Tabel));
        Tabel.setPreferredScrollableViewportSize(new Dimension(500,500));
        Tabel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 7; i++) {
            TableColumn column = Tabel.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(30);
            }else if(i==1){
                column.setPreferredWidth(120);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(125);
            }else if(i==4){
                column.setPreferredWidth(300);
            }else if(i==5){
                column.setPreferredWidth(110);
            }else if(i==6){
                column.setPreferredWidth(100);
            }
        }
        Tabel.setDefaultRenderer(Object.class, new WarnaTable());
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        User.setDocument(new batasInput((byte)100).getKata(User));
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
            User.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(User.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(User.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(User.getText().length()>2){
                        tampil();
                    }
                }
            });
        }  
        InputModalAwal.setDocument(new batasInput((byte)16).getOnlyAngka(InputModalAwal));
        Sequel.cariIsiAngka(
    "SELECT COALESCE(modal_awal,0) " +
    "FROM toko_kasir_shift " +
    "WHERE tgl=CURRENT_DATE() AND modal_awal>0 " +
    "ORDER BY jam_buka ASC, id ASC " +
    "LIMIT 1",
    ModalAwal
);
        
        
    }    
    
    
     

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        WindowModalAwal = new javax.swing.JDialog();
        internalFrame2 = new widget.InternalFrame();
        InputModalAwal = new widget.TextBox();
        jLabel8 = new widget.Label();
        BtnCloseIn = new widget.Button();
        BtnSimpan2 = new widget.Button();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        Tabel = new widget.Table();
        panelGlass5 = new widget.panelisi();
        label17 = new widget.Label();
        TCari = new widget.TextBox();
        label19 = new widget.Label();
        User = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel11 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass6 = new widget.panelisi();
        label11 = new widget.Label();
        Tgl1 = new widget.Tanggal();
        jLabel9 = new widget.Label();
        CmbStatus = new widget.ComboBox();
        label18 = new widget.Label();
        ModalAwal = new widget.TextBox();
        BtnSeek4 = new widget.Button();
        label21 = new widget.Label();
        pengeluaran1 = new widget.TextBox();
        label22 = new widget.Label();
        qris = new widget.TextBox();
        label20 = new widget.Label();
        TTutupKasir = new widget.TextBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        WindowModalAwal.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowModalAwal.setName("WindowModalAwal"); // NOI18N
        WindowModalAwal.setUndecorated(true);
        WindowModalAwal.setResizable(false);

        internalFrame2.setBackground(new java.awt.Color(255, 255, 255));
        internalFrame2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Input Modal Awal ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(null);

        InputModalAwal.setHighlighter(null);
        InputModalAwal.setName("InputModalAwal"); // NOI18N
        InputModalAwal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                InputModalAwalKeyPressed(evt);
            }
        });
        internalFrame2.add(InputModalAwal);
        InputModalAwal.setBounds(84, 27, 170, 23);

        jLabel8.setText("Modal Awal :");
        jLabel8.setName("jLabel8"); // NOI18N
        internalFrame2.add(jLabel8);
        jLabel8.setBounds(0, 27, 80, 23);

        BtnCloseIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnCloseIn.setMnemonic('U');
        BtnCloseIn.setText("Tutup");
        BtnCloseIn.setToolTipText("Alt+U");
        BtnCloseIn.setName("BtnCloseIn"); // NOI18N
        BtnCloseIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCloseInActionPerformed(evt);
            }
        });
        BtnCloseIn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCloseInKeyPressed(evt);
            }
        });
        internalFrame2.add(BtnCloseIn);
        BtnCloseIn.setBounds(380, 25, 100, 30);

        BtnSimpan2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan2.setMnemonic('S');
        BtnSimpan2.setText("Simpan");
        BtnSimpan2.setToolTipText("Alt+S");
        BtnSimpan2.setName("BtnSimpan2"); // NOI18N
        BtnSimpan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan2ActionPerformed(evt);
            }
        });
        BtnSimpan2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpan2KeyPressed(evt);
            }
        });
        internalFrame2.add(BtnSimpan2);
        BtnSimpan2.setBounds(275, 25, 100, 30);

        WindowModalAwal.getContentPane().add(internalFrame2, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Payment Point ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        Tabel.setName("Tabel"); // NOI18N
        Scroll.setViewportView(Tabel);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass5.setName("panelGlass5"); // NOI18N
        panelGlass5.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label17.setText("Key Word :");
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass5.add(label17);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(150, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass5.add(TCari);

        label19.setText("User :");
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(40, 23));
        panelGlass5.add(label19);

        User.setName("User"); // NOI18N
        User.setPreferredSize(new java.awt.Dimension(150, 23));
        User.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UserKeyPressed(evt);
            }
        });
        panelGlass5.add(User);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelGlass5.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass5.add(BtnAll);

        jLabel11.setForeground(new java.awt.Color(50, 50, 50));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(30, 23));
        panelGlass5.add(jLabel11);

        jButton3.setText("Laporan Keuntungan Harian");
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        panelGlass5.add(jButton3);

        jButton4.setText("Rekap Tuslah");
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        panelGlass5.add(jButton4);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelGlass5.add(BtnPrint);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass5.add(BtnKeluar);

        internalFrame1.add(panelGlass5, java.awt.BorderLayout.PAGE_END);

        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(55, 45));
        panelGlass6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label11.setText("Tanggal Bayar :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass6.add(label11);

        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setName("Tgl1"); // NOI18N
        Tgl1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass6.add(Tgl1);

        jLabel9.setText("Shift :");
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass6.add(jLabel9);

        CmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Pagi", "Siang", "Sore", "Malam" }));
        CmbStatus.setName("CmbStatus"); // NOI18N
        CmbStatus.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass6.add(CmbStatus);

        label18.setText("Modal Awal :");
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(130, 23));
        panelGlass6.add(label18);

        ModalAwal.setEditable(false);
        ModalAwal.setName("ModalAwal"); // NOI18N
        ModalAwal.setPreferredSize(new java.awt.Dimension(150, 23));
        panelGlass6.add(ModalAwal);

        BtnSeek4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeek4.setMnemonic('5');
        BtnSeek4.setToolTipText("ALt+5");
        BtnSeek4.setName("BtnSeek4"); // NOI18N
        BtnSeek4.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSeek4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeek4ActionPerformed(evt);
            }
        });
        panelGlass6.add(BtnSeek4);

        label21.setText("Pengeluaran :");
        label21.setName("label21"); // NOI18N
        label21.setPreferredSize(new java.awt.Dimension(130, 23));
        panelGlass6.add(label21);

        pengeluaran1.setName("pengeluaran1"); // NOI18N
        pengeluaran1.setPreferredSize(new java.awt.Dimension(150, 23));
        pengeluaran1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pengeluaran1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pengeluaran1KeyReleased(evt);
            }
        });
        panelGlass6.add(pengeluaran1);

        label22.setText("Nominal QRIS :");
        label22.setName("label22"); // NOI18N
        label22.setPreferredSize(new java.awt.Dimension(130, 23));
        panelGlass6.add(label22);

        qris.setName("qris"); // NOI18N
        qris.setPreferredSize(new java.awt.Dimension(150, 23));
        qris.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                qrisKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                qrisKeyReleased(evt);
            }
        });
        panelGlass6.add(qris);

        label20.setText("Tutup Kasir :");
        label20.setName("label20"); // NOI18N
        label20.setPreferredSize(new java.awt.Dimension(130, 23));
        panelGlass6.add(label20);

        TTutupKasir.setName("TTutupKasir"); // NOI18N
        TTutupKasir.setPreferredSize(new java.awt.Dimension(150, 23));
        TTutupKasir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TTutupKasirKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TTutupKasirKeyReleased(evt);
            }
        });
        panelGlass6.add(TTutupKasir);

        jButton1.setText("SIMPAN");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        panelGlass6.add(jButton1);

        jButton2.setText("LIHAT REKAP LAPORAN");
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        panelGlass6.add(jButton2);

        internalFrame1.add(panelGlass6, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            //TCari.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            
            Sequel.queryu("delete from temporary_payment");
            for(int r=0;r<tabMode.getRowCount();r++){  
                Sequel.menyimpan("temporary_payment","'0',?,?,?,?,?,?,?,'','','','','','','','','','','','','','','','','','','','','','','','','','','','','',''",7,new String[]{
                    tabMode.getValueAt(r,0).toString(),tabMode.getValueAt(r,1).toString(),
                    tabMode.getValueAt(r,2).toString(),tabMode.getValueAt(r,3).toString(),
                    tabMode.getValueAt(r,4).toString(),Valid.SetAngka(Double.parseDouble(tabMode.getValueAt(r,5).toString())),
                    tabMode.getValueAt(r,6).toString()                    
                });
            }
            
            Map<String, Object> param = new HashMap<>();                 
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("shift",CmbStatus.getSelectedItem().toString());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            Valid.MyReport("rptPaymentPoint.jasper","report","::[ Payment Point ]::",param);
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, Tgl1,BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnKeluar,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        User.setText("");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnPrint);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            tampil();
            this.setCursor(Cursor.getDefaultCursor());
        }else{
            Valid.pindah(evt,TCari, BtnPrint);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnSeek4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek4ActionPerformed
        InputModalAwal.setText(Sequel.cariIsi("select modal_awal from toko_kasir_shift"));  
        WindowModalAwal.setSize(500,80);
        WindowModalAwal.setLocationRelativeTo(ModalAwal);
        InputModalAwal.requestFocus();
        WindowModalAwal.setAlwaysOnTop(false);
        WindowModalAwal.setVisible(true);
    }//GEN-LAST:event_BtnSeek4ActionPerformed

    private void InputModalAwalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InputModalAwalKeyPressed
        Valid.pindah(evt,BtnCloseIn,TCari);
    }//GEN-LAST:event_InputModalAwalKeyPressed

    private void BtnCloseInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCloseInActionPerformed
        WindowModalAwal.dispose();
    }//GEN-LAST:event_BtnCloseInActionPerformed

    private void BtnCloseInKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCloseInKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            WindowModalAwal.dispose();
        }else{Valid.pindah(evt, BtnSimpan2, InputModalAwal);}
    }//GEN-LAST:event_BtnCloseInKeyPressed

    private void BtnSimpan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpan2ActionPerformed
        if(InputModalAwal.getText().trim().equals("")){
            Valid.textKosong(InputModalAwal,"Modal Awal");
        }else{
            Sequel.queryu("delete from set_modal_payment");
            Sequel.menyimpan("set_modal_payment","'"+InputModalAwal.getText()+"'","Modal Awal");
            WindowModalAwal.setVisible(false);
            ModalAwal.setText(InputModalAwal.getText());
        }
    }//GEN-LAST:event_BtnSimpan2ActionPerformed

    private void BtnSimpan2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpan2KeyPressed
        Valid.pindah(evt,InputModalAwal,BtnCloseIn);
    }//GEN-LAST:event_BtnSimpan2KeyPressed

    private void UserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UserKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            TCari.requestFocus();
        }
    }//GEN-LAST:event_UserKeyPressed

    private void TTutupKasirKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TTutupKasirKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_TTutupKasirKeyReleased

    private void TTutupKasirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TTutupKasirKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
           tampil();
        }
    }//GEN-LAST:event_TTutupKasirKeyPressed

    private void pengeluaran1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pengeluaran1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pengeluaran1KeyPressed

    private void pengeluaran1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pengeluaran1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_pengeluaran1KeyReleased

    private void qrisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_qrisKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_qrisKeyPressed

    private void qrisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_qrisKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_qrisKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        previewDanSimpanTutupKasir();// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
      lihatLaporanTersimpan();  // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String tgl = Valid.SetTgl(Tgl1.getSelectedItem()+"");  // sesuaikan nama komponen
    String shift = CmbStatus.getSelectedItem().toString();            // "Semua"/"1"/"2" dst
    String user = User.getText().trim();                         // field user di bawah (kalau ada)

    double modal = Valid.SetAngka(ModalAwal.getText());
    double keluar = Valid.SetAngka(pengeluaran1.getText());
    double Qris = Valid.SetAngka(qris.getText());

    dlgKeuangan.setParameter(tgl, shift, user, modal, keluar, Qris);
    dlgKeuangan.setVisible(true);
    dlgKeuangan.tampil(); // auto preview saat dibuka
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
     tampilPopupTuslahPerNip();
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgPaymentPoint dialog = new DlgPaymentPoint(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnCloseIn;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSeek4;
    private widget.Button BtnSimpan2;
    private widget.ComboBox CmbStatus;
    private widget.TextBox InputModalAwal;
    private widget.TextBox ModalAwal;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TTutupKasir;
    private widget.Table Tabel;
    private widget.Tanggal Tgl1;
    private widget.TextBox User;
    private javax.swing.JDialog WindowModalAwal;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel11;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.Label label11;
    private widget.Label label17;
    private widget.Label label18;
    private widget.Label label19;
    private widget.Label label20;
    private widget.Label label21;
    private widget.Label label22;
    private widget.panelisi panelGlass5;
    private widget.panelisi panelGlass6;
    private widget.TextBox pengeluaran1;
    private widget.TextBox qris;
    // End of variables declaration//GEN-END:variables

//   public void tampil(){
//    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//    Valid.tabelKosong(tabMode);
//
   // =========================
// TAMBAH VARIABEL INI DI CLASS (global field)
// =========================
//private long all=0, pagi=0, siang=0, sore=0, malam=0;

private long all=0L, pagi=0L, siang=0L, sore=0L, malam=0L;
private long cashAll=0L, cashPagi=0L, cashSiang=0L, cashSore=0L, cashMalam=0L;
private long nonCashAll=0L, nonCashPagi=0L, nonCashSiang=0L, nonCashSore=0L, nonCashMalam=0L;


//private int i=1;

// =========================
// METHOD UTAMA
// =========================
public void tampil() {
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Valid.tabelKosong(tabMode);

    // reset akumulasi
    all=0; pagi=0; siang=0; sore=0; malam=0;

    cashAll=0; cashPagi=0; cashSiang=0; cashSore=0; cashMalam=0;
    nonCashAll=0; nonCashPagi=0; nonCashSiang=0; nonCashSore=0; nonCashMalam=0;

    i=1;

    String tgl  = Valid.SetTgl(Tgl1.getSelectedItem()+""); // yyyy-MM-dd
    String cari = TCari.getText().trim();
    String st   = CmbStatus.getSelectedItem().toString();  // Semua/Pagi/Siang/Sore/Malam

    // DEBUG
    System.out.println("PAYMENT POINT tgl=" + tgl + " cari=" + cari + " status=" + st);

    // =========================
    // 1) TAGIHAN SADEWA (anggap CASH)
    // =========================
    PreparedStatement psSadewa=null;
    ResultSet rsSadewa=null;
    try{
        psSadewa = koneksi.prepareStatement(
            "SELECT no_nota, tgl_bayar, nama_pasien, jumlah_bayar, petugas " +
            "FROM tagihan_sadewa " +
            "WHERE DATE(tgl_bayar)=? " +
            "AND (nama_pasien LIKE ? OR no_nota LIKE ?) " +
            "ORDER BY tgl_bayar, no_nota"
        );
        psSadewa.setString(1, tgl);
        psSadewa.setString(2, "%"+cari+"%");
        psSadewa.setString(3, "%"+cari+"%");

        rsSadewa = psSadewa.executeQuery();
        while(rsSadewa.next()){
            String waktu = rsSadewa.getString("tgl_bayar");   // datetime
            String shift = shiftDariDateTime(waktu);

            // filter tampilan berdasarkan status (kumulatif)
            if(!bolehTampilShift(shift, st)) continue;

            // mapping nota dari no_rawat (jika memang no_nota berisi no_rawat)
            String nonota = Sequel.cariIsi(
                "SELECT no_nota FROM nota_inap WHERE no_rawat=?",
                rsSadewa.getString("no_nota")
            );
            if(nonota.equals("")){
                nonota = Sequel.cariIsi(
                    "SELECT no_nota FROM nota_jalan WHERE no_rawat=?",
                    rsSadewa.getString("no_nota")
                );
                if(nonota.equals("")) nonota = rsSadewa.getString("no_nota");
            }

            String petugas = rsSadewa.getString("petugas") + " " +
                Sequel.cariIsi("SELECT pegawai.nama FROM pegawai WHERE pegawai.nik=?",
                    rsSadewa.getString("petugas"));

            long jml = Math.round(rsSadewa.getDouble("jumlah_bayar"));

            // sadewa diasumsikan CASH
            akumulasi(shift, jml, true);

            tabMode.addRow(new Object[]{
                i,
                waktu,
                shift,
                nonota,
                rsSadewa.getString("nama_pasien"),
                jml,
                petugas
            });
            i++;
        }
    }catch(Exception e){
        System.out.println("Notifikasi (sadewa): "+e);
    }finally{
        try{ if(rsSadewa!=null) rsSadewa.close(); }catch(Exception e){}
        try{ if(psSadewa!=null) psSadewa.close(); }catch(Exception e){}
    }

    // =========================
    // 2) TOKO PENJUALAN (CASH/NONCASH pakai nama_bayar)
    // =========================
    PreparedStatement psToko=null;
    ResultSet rsToko=null;
    try{
        psToko = koneksi.prepareStatement(
            "SELECT tp.nota_jual, tp.tgl_jual, tp.nm_member, tp.nama_bayar, tp.nip, " +
            "       COALESCE(pg.nama,'') AS nama, " +
            "       (tp.total + tp.ongkir + tp.ppn) AS grand " +
            "FROM tokopenjualan tp " +
            "LEFT JOIN petugas pg ON tp.nip = pg.nip " +
            "WHERE DATE(tp.tgl_jual)=? " +
            (cari.isEmpty() ? "" :
                "AND (tp.nip LIKE ? OR pg.nama LIKE ? OR tp.nm_member LIKE ? OR tp.nota_jual LIKE ?) ") +
            "ORDER BY tp.tgl_jual, tp.nota_jual"
        );

        int p=1;
        psToko.setString(p++, tgl);
        if(!cari.isEmpty()){
            String like = "%"+cari+"%";
            psToko.setString(p++, like);
            psToko.setString(p++, like);
            psToko.setString(p++, like);
            psToko.setString(p++, like);
        }

        rsToko = psToko.executeQuery();
        while(rsToko.next()){
            String waktu = rsToko.getString("tgl_jual");
            String shift = shiftDariDateTime(waktu);

            if(!bolehTampilShift(shift, st)) continue;

            String petugas = rsToko.getString("nip")+" "+rsToko.getString("nama");
            String bayar   = rsToko.getString("nama_bayar");
            long jml       = Math.round(rsToko.getDouble("grand"));

            boolean isCash = isCashBayar(bayar);
            akumulasi(shift, jml, isCash);

            tabMode.addRow(new Object[]{
                i,
                waktu,
                shift,
                "Toko (" + (bayar==null ? "-" : bayar) + ")",
                rsToko.getString("nm_member"),
                jml,
                petugas
            });
            i++;
        }
    }catch(Exception e){
        System.out.println("Notifikasi (toko): "+e);
    }finally{
        try{ if(rsToko!=null) rsToko.close(); }catch(Exception e){}
        try{ if(psToko!=null) psToko.close(); }catch(Exception e){}
    }

    // ringkasan tutup kasir
    appendRingkasan();

    this.setCursor(Cursor.getDefaultCursor());
}

// =========================
// RINGKASAN TUTUP KASIR
// =========================
private void appendRingkasan() {
    String st = CmbStatus.getSelectedItem().toString();

    long modal     = parseLongSafe(ModalAwal.getText());
    long cashFisik = parseLongSafe(TTutupKasir.getText());     // uang cash di laci
    long keluar    = parseLongSafe(pengeluaran1.getText());    // cash out dari laci
    long nonCashAktual = parseLongSafe(qris.getText());        // isi dari mutasi/settlement (kalau kosong -> default sistem)

    long uangMasukDipakai   = getUangMasukUpTo(st);
    long cashSistemDipakai  = getCashUpTo(st);
    long nonCashSistemDipakai = getNonCashUpTo(st);

    long expectedCash    = modal + cashSistemDipakai;
    long expectedNonCash = nonCashSistemDipakai;
    long expectedTotal   = expectedCash + expectedNonCash;

    // kalau user tidak isi noncash aktual, default = noncash sistem (biar tetap jalan)
    if(qris.getText()==null || qris.getText().trim().isEmpty()){
        nonCashAktual = expectedNonCash;
    }

    long actualCash   = cashFisik + keluar;
    long actualTotal  = actualCash + nonCashAktual;

    long selisihCash    = actualCash - expectedCash;
    long selisihNonCash = nonCashAktual - expectedNonCash;
    long selisihTotal   = actualTotal - expectedTotal;

    tabMode.addRow(new Object[]{"","Modal Awal",":","","", modal, ""});
    tabMode.addRow(new Object[]{"","Uang Masuk Sistem",":","","", uangMasukDipakai, ""});

    tabMode.addRow(new Object[]{"","Masuk CASH (Sistem)",":","","", cashSistemDipakai, ""});
    tabMode.addRow(new Object[]{"","Masuk NONCASH (Sistem)",":","","", nonCashSistemDipakai, ""});

    tabMode.addRow(new Object[]{"",">> Total Sistem",":","","", expectedTotal, ""});

    tabMode.addRow(new Object[]{"","Cash Fisik",":","","", cashFisik, ""});
    tabMode.addRow(new Object[]{"","Pengeluaran (Cash Out)",":","","", keluar, ""});
    tabMode.addRow(new Object[]{"","NONCASH Aktual",":","","", nonCashAktual, ""});

    tabMode.addRow(new Object[]{"","Selisih CASH",":","","", selisihCash, ""});
    tabMode.addRow(new Object[]{"","Selisih NONCASH",":","","", selisihNonCash, ""});
    tabMode.addRow(new Object[]{"","Selisih TOTAL",":","","", selisihTotal, ""});
}

// =========================
// AKUMULATOR
// =========================
private void akumulasi(String shift, long jml, boolean isCash){
    // total per shift (semua jenis bayar)
    all += jml;
    if ("Pagi".equals(shift)) pagi += jml;
    else if ("Siang".equals(shift)) siang += jml;
    else if ("Sore".equals(shift)) sore += jml;
    else if ("Malam".equals(shift)) malam += jml;

    // cash/noncash per shift
    if(isCash){
        cashAll += jml;
        if ("Pagi".equals(shift)) cashPagi += jml;
        else if ("Siang".equals(shift)) cashSiang += jml;
        else if ("Sore".equals(shift)) cashSore += jml;
        else if ("Malam".equals(shift)) cashMalam += jml;
    }else{
        nonCashAll += jml;
        if ("Pagi".equals(shift)) nonCashPagi += jml;
        else if ("Siang".equals(shift)) nonCashSiang += jml;
        else if ("Sore".equals(shift)) nonCashSore += jml;
        else if ("Malam".equals(shift)) nonCashMalam += jml;
    }
}

// =========================
// SHIFT & FILTER (KUMULATIF)
// =========================
private boolean bolehTampilShift(String shiftRow, String statusDipilih){
    if(statusDipilih == null || "Semua".equalsIgnoreCase(statusDipilih)) return true;

    // kumulatif: Siang = tampil Pagi+Siang, Sore = Pagi+Siang+Sore, dst
    return urutanShift(shiftRow) <= urutanShift(statusDipilih);
}

private int urutanShift(String s){
    if(s == null) return 99;
    if("Pagi".equalsIgnoreCase(s)) return 1;
    if("Siang".equalsIgnoreCase(s)) return 2;
    if("Sore".equalsIgnoreCase(s)) return 3;
    if("Malam".equalsIgnoreCase(s)) return 4;
    if("Semua".equalsIgnoreCase(s)) return 4;
    return 99;
}

private long getUangMasukUpTo(String st){
    if(st == null || "Semua".equalsIgnoreCase(st)) return all;
    if("Pagi".equalsIgnoreCase(st))  return pagi;
    if("Siang".equalsIgnoreCase(st)) return pagi + siang;
    if("Sore".equalsIgnoreCase(st))  return pagi + siang + sore;
    if("Malam".equalsIgnoreCase(st)) return pagi + siang + sore + malam;
    return all;
}

private long getCashUpTo(String st){
    if(st == null || "Semua".equalsIgnoreCase(st)) return cashAll;
    if("Pagi".equalsIgnoreCase(st))  return cashPagi;
    if("Siang".equalsIgnoreCase(st)) return cashPagi + cashSiang;
    if("Sore".equalsIgnoreCase(st))  return cashPagi + cashSiang + cashSore;
    if("Malam".equalsIgnoreCase(st)) return cashPagi + cashSiang + cashSore + cashMalam;
    return cashAll;
}

private long getNonCashUpTo(String st){
    if(st == null || "Semua".equalsIgnoreCase(st)) return nonCashAll;
    if("Pagi".equalsIgnoreCase(st))  return nonCashPagi;
    if("Siang".equalsIgnoreCase(st)) return nonCashPagi + nonCashSiang;
    if("Sore".equalsIgnoreCase(st))  return nonCashPagi + nonCashSiang + nonCashSore;
    if("Malam".equalsIgnoreCase(st)) return nonCashPagi + nonCashSiang + nonCashSore + nonCashMalam;
    return nonCashAll;
}

// =========================
// DETEKSI CASH / NONCASH (nama_bayar)
// =========================
private boolean isCashBayar(String namaBayar){
    if(namaBayar == null) return false;
    String s = namaBayar.toLowerCase();
    return s.contains("cash") || s.contains("tunai");
}

// =========================
// SHIFT DARI DATETIME
// (atur jam sesuai kebutuhanmu kalau beda definisi shift)
// =========================
private String shiftDariDateTime(String dt){
    String jam = dt;
    int sp = dt == null ? -1 : dt.indexOf(' ');
    if(sp > -1 && sp + 1 < dt.length()){
        jam = dt.substring(sp + 1);
    }
    jam = normJam(jam); // pastikan HH:mm:ss

    java.time.LocalTime t;
    try{
        t = java.time.LocalTime.parse(jam);
    }catch(Exception e){
        return "Pagi";
    }

    // contoh batas shift (silakan ubah)
    if(t.isBefore(java.time.LocalTime.of(12, 0))) return "Pagi";
    if(t.isBefore(java.time.LocalTime.of(15, 0))) return "Siang";
    if(t.isBefore(java.time.LocalTime.of(18, 0))) return "Sore";
    return "Malam";
}

// =========================
// PARSER RUPIAH AMAN
// =========================
private long parseLongSafe(String s){
    if(s==null) return 0L;
    s=s.trim().replace("Rp","").replace("rp","")
         .replace(".","").replace(",","").replace(" ","");
    if(s.isEmpty()) return 0L;
    try{
        double d = Double.parseDouble(s);
        return Math.round(d);
    }catch(Exception e){
        return 0L;
    }
}

private String normJam(String jam){
    if(jam == null) return "00:00:00";
    jam = jam.trim();
    if(jam.isEmpty()) return "00:00:00";
    if(jam.length() == 5) return jam + ":00"; // HH:mm -> HH:mm:00
    return jam; // HH:mm:ss
}
// =========================
// 1) DATA REKAP (POJO kecil)
// =========================
private static class RekapTutupKasir {
    String tgl;          // yyyy-MM-dd
    String statusShift;  // Semua / Pagi / Siang / Sore / Malam
    String nip;
    String catatan;
    String tglInput;

    long modal;
    long cashSistem;
    long nonCashSistem;
    long totalSistem;

    long cashFisik;
    long pengeluaran;
    long nonCashAktual;

    long selisihCash;
    long selisihNonCash;
    long selisihTotal;

    boolean nonCashAutoDariSistem; // true kalau field noncash kosong -> auto
}

// =========================
// 2) PANGGIL INI SAAT KLIK "Simpan Tutup Kasir"
// =========================
private void previewDanSimpanTutupKasir() {
    // pastikan sudah dihitung (minimal pernah klik tampil)
    if (tabMode.getRowCount() <= 0) {
        JOptionPane.showMessageDialog(null, "Data masih kosong. Klik TAMPIL dulu ya.");
        return;
    }

    RekapTutupKasir r = hitungRekapSekarang();

    // tampilkan preview -> OK = Simpan
    boolean ok = tampilkanPreviewKonfirmasi(r);
    if (!ok) return;

    if (simpanRekapKeDB(r)) {
        tutupShiftGlobal(r.tgl);
        JOptionPane.showMessageDialog(null, "✅ Laporan tutup kasir berhasil disimpan.");
        
    } else {
        JOptionPane.showMessageDialog(null, "❌ Gagal menyimpan laporan tutup kasir. Cek console log.");
    }
}

// =========================
// 3) HITUNG REKAP DARI AKUMULASI + INPUT USER
// =========================
private RekapTutupKasir hitungRekapSekarang() {
    RekapTutupKasir r = new RekapTutupKasir();

    r.tgl = Valid.SetTgl(Tgl1.getSelectedItem() + ""); // yyyy-MM-dd
    r.statusShift = CmbStatus.getSelectedItem().toString();

    // ambil nip login (sesuaikan dengan project kamu)
    // kalau di Khanza biasanya: akses.getkode()
    String nipLogin = "";
    try { nipLogin = akses.getkode(); } catch (Exception e) { nipLogin = ""; }
    r.nip = nipLogin;

    r.modal = parseLongSafe(ModalAwal.getText());
    r.cashFisik = parseLongSafe(TTutupKasir.getText());
    r.pengeluaran = parseLongSafe(pengeluaran1.getText());

    // noncash aktual: kalau kosong -> auto pakai noncash sistem (biar tidak bikin selisih noncash)
    String txtNonCash = (qris.getText() == null ? "" : qris.getText().trim());
    if (txtNonCash.isEmpty()) {
        r.nonCashAutoDariSistem = true;
    }
    r.nonCashAktual = parseLongSafe(txtNonCash);

    // ambil sistem sesuai status (kumulatif)
    r.cashSistem = getCashUpTo(r.statusShift);
    r.nonCashSistem = getNonCashUpTo(r.statusShift);

    // total sistem = modal + semua pemasukan (cash + noncash)
    r.totalSistem = r.modal + r.cashSistem + r.nonCashSistem;

    if (r.nonCashAutoDariSistem) {
        r.nonCashAktual = r.nonCashSistem;
    }

    // rekonsiliasi:
    // CASH: (cash fisik + pengeluaran) vs (modal + cash sistem)
    long expectedCash = r.modal + r.cashSistem;
    long actualCash = r.cashFisik + r.pengeluaran;
    r.selisihCash = actualCash - expectedCash;

    // NONCASH: noncash aktual vs noncash sistem
    r.selisihNonCash = r.nonCashAktual - r.nonCashSistem;

    // TOTAL: (cash fisik + pengeluaran + noncash aktual) vs (modal + cash sistem + noncash sistem)
    long actualTotal = actualCash + r.nonCashAktual;
    r.selisihTotal = actualTotal - r.totalSistem;

    return r;
}

// =========================
// 4) PREVIEW HTML + KONFIRMASI SIMPAN
// =========================
private boolean tampilkanPreviewKonfirmasi(RekapTutupKasir r) {
    java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));
    nf.setMaximumFractionDigits(0);
    nf.setMinimumFractionDigits(0);

    String badgeNonCash = r.nonCashAutoDariSistem ? " <span style='color:#666'>(auto dari sistem)</span>" : "";

    String html =
        "<html><body style='font-family:Tahoma; font-size:12px;'>" +
        "<h3 style='margin:0;'>Preview Tutup Kasir - Payment Point</h3>" +
        "<div style='margin-top:6px;'>Tanggal: <b>" + r.tgl + "</b> &nbsp; | &nbsp; Shift: <b>" + r.statusShift + "</b></div>" +
        "<div style='margin-top:2px;'>Petugas: <b>" + (r.nip==null?"":r.nip) + "</b></div>" +

        "<hr/>" +

        "<table width='100%' cellspacing='0' cellpadding='6' style='border-collapse:collapse;'>" +
        "<tr><td style='border:1px solid #ccc;'>Modal Awal</td><td align='right' style='border:1px solid #ccc;'><b>" + nf.format(r.modal) + "</b></td></tr>" +
        "<tr><td style='border:1px solid #ccc;'>Masuk CASH (Sistem)</td><td align='right' style='border:1px solid #ccc;'>" + nf.format(r.cashSistem) + "</td></tr>" +
        "<tr><td style='border:1px solid #ccc;'>Masuk NONCASH (Sistem)</td><td align='right' style='border:1px solid #ccc;'>" + nf.format(r.nonCashSistem) + "</td></tr>" +
        "<tr><td style='border:1px solid #ccc;'><b>> Total Sistem</b></td><td align='right' style='border:1px solid #ccc;'><b>" + nf.format(r.totalSistem) + "</b></td></tr>" +

        "<tr><td colspan='2' style='height:6px;'></td></tr>" +

        "<tr><td style='border:1px solid #ccc;'>Cash Fisik</td><td align='right' style='border:1px solid #ccc;'>" + nf.format(r.cashFisik) + "</td></tr>" +
        "<tr><td style='border:1px solid #ccc;'>Pengeluaran (Cash Out)</td><td align='right' style='border:1px solid #ccc;'>" + nf.format(r.pengeluaran) + "</td></tr>" +
        "<tr><td style='border:1px solid #ccc;'>NONCASH Aktual" + badgeNonCash + "</td><td align='right' style='border:1px solid #ccc;'>" + nf.format(r.nonCashAktual) + "</td></tr>" +

        "<tr><td colspan='2' style='height:6px;'></td></tr>" +

        "<tr><td style='border:1px solid #ccc;'><b>Selisih CASH</b></td><td align='right' style='border:1px solid #ccc;'><b style='color:" + (r.selisihCash<0?"#c00":"#080") + ";'>" + nf.format(r.selisihCash) + "</b></td></tr>" +
        "<tr><td style='border:1px solid #ccc;'><b>Selisih NONCASH</b></td><td align='right' style='border:1px solid #ccc;'><b style='color:" + (r.selisihNonCash<0?"#c00":"#080") + ";'>" + nf.format(r.selisihNonCash) + "</b></td></tr>" +
        "<tr><td style='border:1px solid #ccc;'><b>Selisih TOTAL</b></td><td align='right' style='border:1px solid #ccc;'><b style='color:" + (r.selisihTotal<0?"#c00":"#080") + ";'>" + nf.format(r.selisihTotal) + "</b></td></tr>" +
        "</table>" +

        "<hr/>" +
        "<div style='color:#555;'>Klik <b>OK</b> untuk simpan laporan ini.</div>" +
        "</body></html>";

    javax.swing.JEditorPane pane = new javax.swing.JEditorPane("text/html", html);
    pane.setEditable(false);
    pane.putClientProperty(javax.swing.JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    pane.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 12));

    javax.swing.JScrollPane sp = new javax.swing.JScrollPane(pane);
    sp.setPreferredSize(new java.awt.Dimension(560, 520));

    int res = JOptionPane.showConfirmDialog(
        null,
        sp,
        "Preview Tutup Kasir",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
    );

    return res == JOptionPane.OK_OPTION;
}

// =========================
// 5) SIMPAN KE DB (UPSERT)
// =========================
private boolean simpanRekapKeDB(RekapTutupKasir r) {
    String sql =
        "INSERT INTO closing_kasir_payment_point " +
        "(tgl, status_shift, nip, modal_awal, cash_sistem, noncash_sistem, total_sistem, " +
        " cash_fisik, pengeluaran, noncash_aktual, selisih_cash, selisih_noncash, selisih_total, catatan) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
        " modal_awal=VALUES(modal_awal), cash_sistem=VALUES(cash_sistem), noncash_sistem=VALUES(noncash_sistem), total_sistem=VALUES(total_sistem), " +
        " cash_fisik=VALUES(cash_fisik), pengeluaran=VALUES(pengeluaran), noncash_aktual=VALUES(noncash_aktual), " +
        " selisih_cash=VALUES(selisih_cash), selisih_noncash=VALUES(selisih_noncash), selisih_total=VALUES(selisih_total), " +
        " catatan=VALUES(catatan), tgl_input=NOW()";

    // catatan optional (kalau kamu punya field catatan, ambil dari form; kalau tidak, kosongkan)
    String catatan = "";

    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        int p = 1;
        ps.setString(p++, r.tgl);
        ps.setString(p++, r.statusShift);
        ps.setString(p++, r.nip == null ? "" : r.nip);

        ps.setLong(p++, r.modal);
        ps.setLong(p++, r.cashSistem);
        ps.setLong(p++, r.nonCashSistem);
        ps.setLong(p++, r.totalSistem);

        ps.setLong(p++, r.cashFisik);
        ps.setLong(p++, r.pengeluaran);
        ps.setLong(p++, r.nonCashAktual);

        ps.setLong(p++, r.selisihCash);
        ps.setLong(p++, r.selisihNonCash);
        ps.setLong(p++, r.selisihTotal);

        ps.setString(p++, catatan);

        ps.executeUpdate();
        return true;
    } catch (Exception e) {
        System.out.println("Notifikasi simpan tutup kasir: " + e);
        return false;
    }
}

private void lihatLaporanTersimpan() {
    String tgl   = Valid.SetTgl(Tgl1.getSelectedItem() + "");     // yyyy-MM-dd
    String shift = CmbStatus.getSelectedItem().toString();        // Semua/Pagi/Siang/Sore/Malam
    String nip   = "";
    try { nip = akses.getkode(); } catch (Exception e) { nip = ""; }

    RekapTutupKasir r = ambilRekapDariDB(tgl, shift, nip);

    // Kalau tidak ketemu persis (tgl+shift+nip), tampilkan daftar untuk dipilih
    if (r == null) {
        boolean dipilih = pilihLaporanDariDaftar(tgl);
        if (!dipilih) return; // user cancel
        return;
    }

    tampilkanPreviewTersimpan(r);
}

private RekapTutupKasir ambilRekapDariDB(String tgl, String shift, String nip) {
    String sql =
        "SELECT tgl, status_shift, nip, modal_awal, cash_sistem, noncash_sistem, total_sistem, " +
        "       cash_fisik, pengeluaran, noncash_aktual, selisih_cash, selisih_noncash, selisih_total, " +
        "       catatan, tgl_input " +
        "FROM closing_kasir_payment_point " +
        "WHERE tgl=? AND status_shift=? AND nip=? " +
        "LIMIT 1";

    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setString(1, tgl);
        ps.setString(2, shift);
        ps.setString(3, nip);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                RekapTutupKasir r = new RekapTutupKasir();
                r.tgl = rs.getString("tgl");
                r.statusShift = rs.getString("status_shift");
                r.nip = rs.getString("nip");

                r.modal = rs.getLong("modal_awal");
                r.cashSistem = rs.getLong("cash_sistem");
                r.nonCashSistem = rs.getLong("noncash_sistem");
                r.totalSistem = rs.getLong("total_sistem");

                r.cashFisik = rs.getLong("cash_fisik");
                r.pengeluaran = rs.getLong("pengeluaran");
                r.nonCashAktual = rs.getLong("noncash_aktual");

                r.selisihCash = rs.getLong("selisih_cash");
                r.selisihNonCash = rs.getLong("selisih_noncash");
                r.selisihTotal = rs.getLong("selisih_total");

                r.catatan = rs.getString("catatan");
                r.tglInput = rs.getString("tgl_input");
                return r;
            }
        }
    } catch (Exception e) {
        System.out.println("Notifikasi ambilRekapDariDB: " + e);
    }
    return null;
}

/**
 * Kalau record spesifik tidak ketemu, tampilkan daftar laporan pada tanggal itu,
 * user pilih salah satu -> tampilkan preview.
 */
private boolean pilihLaporanDariDaftar(String tgl) {
    String sql =
        "SELECT status_shift, nip, total_sistem, selisih_total, tgl_input " +
        "FROM closing_kasir_payment_point " +
        "WHERE tgl=? " +
        "ORDER BY tgl_input DESC";

    javax.swing.table.DefaultTableModel dm = new javax.swing.table.DefaultTableModel(
        new Object[]{"Shift", "NIP", "Total Sistem", "Selisih Total", "Tgl Input"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };

    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setString(1, tgl);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dm.addRow(new Object[]{
                    rs.getString("status_shift"),
                    rs.getString("nip"),
                    rs.getLong("total_sistem"),
                    rs.getLong("selisih_total"),
                    rs.getString("tgl_input")
                });
            }
        }
    } catch (Exception e) {
        System.out.println("Notifikasi pilihLaporanDariDaftar: " + e);
        return false;
    }

    if (dm.getRowCount() == 0) {
        JOptionPane.showMessageDialog(null, "Belum ada laporan tersimpan untuk tanggal " + tgl);
        return false;
    }
javax.swing.table.DefaultTableCellRenderer money = new javax.swing.table.DefaultTableCellRenderer() {
    final java.text.NumberFormat nf =
        java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id","ID"));
    {
        setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
    }
    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        long v = 0;
        try { if(value != null) v = Long.parseLong(value.toString()); } catch(Exception ignored) {}
        setText(nf.format(v));
        return this;
    }
};
    JTable tb = new JTable(dm);
    tb.setRowHeight(24);
    tb.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    tb.getColumnModel().getColumn(2).setCellRenderer(money);
    tb.getColumnModel().getColumn(3).setCellRenderer(money);

    JScrollPane sp = new JScrollPane(tb);
    sp.setPreferredSize(new java.awt.Dimension(720, 280));

    int res = JOptionPane.showConfirmDialog(
        null, sp, "Pilih Laporan (" + tgl + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
    );
    if (res != JOptionPane.OK_OPTION) return false;

    int row = tb.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(null, "Pilih salah satu baris dulu.");
        return false;
    }

    String shift = String.valueOf(tb.getValueAt(row, 0));
    String nip   = String.valueOf(tb.getValueAt(row, 1));

    RekapTutupKasir r = ambilRekapDariDB(tgl, shift, nip);
    if (r == null) {
        JOptionPane.showMessageDialog(null, "Data laporan tidak ditemukan lagi (mungkin terhapus/berubah).");
        return false;
    }

    tampilkanPreviewTersimpan(r);
    return true;
}

private void tampilkanPreviewTersimpan(RekapTutupKasir r) {
    java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));
    nf.setMaximumFractionDigits(0);
    nf.setMinimumFractionDigits(0);

    String cat = (r.catatan == null || r.catatan.trim().isEmpty()) ? "-" : r.catatan;

    String html =
        "<html><body style='font-family:Tahoma; font-size:12px;'>" +
        "<h3 style='margin:0;'>Laporan Tutup Kasir (Tersimpan)</h3>" +
        "<div style='margin-top:6px;'>Tanggal: <b>" + r.tgl + "</b> &nbsp; | &nbsp; Shift: <b>" + r.statusShift + "</b></div>" +
        "<div style='margin-top:2px;'>Petugas: <b>" + (r.nip==null?"":r.nip) + "</b></div>" +
        "<div style='margin-top:2px; color:#555;'>Tgl Input: <b>" + (r.tglInput==null?"":r.tglInput) + "</b></div>" +
        "<hr/>" +

        "<table width='100%' cellspacing='0' cellpadding='6' style='border-collapse:collapse;'>" +
        "<tr><td style='border:1px solid #00f;'>Modal Awal</td><td align='right' style='border:1px solid #00f;'><b>" + nf.format(r.modal) + "</b></td></tr>" +
        "<tr><td style='border:1px solid #00f;'>Masuk CASH (Sistem)</td><td align='right' style='border:1px solid #00f;'>" + nf.format(r.cashSistem) + "</td></tr>" +
        "<tr><td style='border:1px solid #00f;'>Masuk NONCASH (Sistem)</td><td align='right' style='border:1px solid #00f;'>" + nf.format(r.nonCashSistem) + "</td></tr>" +
        "<tr><td style='border:1px solid #00f;'><b>> Total Sistem</b></td><td align='right' style='border:1px solid #00f;'><b>" + nf.format(r.totalSistem) + "</b></td></tr>" +

        "<tr><td colspan='2' style='height:10px;'></td></tr>" +

        "<tr><td style='border:1px solid #00f;'>Cash Fisik</td><td align='right' style='border:1px solid #00f;'>" + nf.format(r.cashFisik) + "</td></tr>" +
        "<tr><td style='border:1px solid #00f;'>Pengeluaran (Cash Out)</td><td align='right' style='border:1px solid #00f;'>" + nf.format(r.pengeluaran) + "</td></tr>" +
        "<tr><td style='border:1px solid #00f;'>NONCASH Aktual</td><td align='right' style='border:1px solid #00f;'>" + nf.format(r.nonCashAktual) + "</td></tr>" +

        "<tr><td colspan='2' style='height:10px;'></td></tr>" +

        "<tr><td style='border:1px solid #00f;'><b>Selisih CASH</b></td><td align='right' style='border:1px solid #00f;'><b>" + nf.format(r.selisihCash) + "</b></td></tr>" +
        "<tr><td style='border:1px solid #00f;'><b>Selisih NONCASH</b></td><td align='right' style='border:1px solid #00f;'><b>" + nf.format(r.selisihNonCash) + "</b></td></tr>" +
        "<tr><td style='border:1px solid #00f;'><b>Selisih TOTAL</b></td><td align='right' style='border:1px solid #00f;'><b>" + nf.format(r.selisihTotal) + "</b></td></tr>" +
        "</table>" +

        "<hr/>" +
        "<div><b>Catatan:</b> " + cat + "</div>" +
        "</body></html>";

    javax.swing.JEditorPane pane = new javax.swing.JEditorPane("text/html", html);
    pane.setEditable(false);
    pane.putClientProperty(javax.swing.JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    pane.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 12));

    javax.swing.JScrollPane sp = new javax.swing.JScrollPane(pane);
    sp.setPreferredSize(new java.awt.Dimension(560, 520));

    Object[] options = {"🖨 Print", "Tutup"};
    int res = javax.swing.JOptionPane.showOptionDialog(
        null,
        sp,
        "Laporan Tutup Kasir",
        javax.swing.JOptionPane.DEFAULT_OPTION,
        javax.swing.JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        options[1]
    );

    if (res == 0) { // klik Print
        cetakPane(pane);
    }
}

private boolean tutupShiftOpen(String tgl, String nip){
    try (PreparedStatement ps = koneksi.prepareStatement(
        "UPDATE toko_kasir_shift " +
        "SET jam_tutup = CURTIME() " +
        "WHERE tgl=? AND nip=? AND jam_tutup IS NULL"
    )) {
        ps.setString(1, tgl);
        ps.setString(2, nip);
        ps.executeUpdate();
        return true;
    } catch (Exception e) {
        System.out.println("Notifikasi tutupShiftOpen: " + e);
        return false;
    }
}
private void cetakPane(javax.swing.JEditorPane pane) {
    try {
        // ini akan munculin dialog printer bawaan Windows
        boolean done = pane.print();
        if (!done) {
            System.out.println("Print dibatalkan user.");
        }
    } catch (java.awt.print.PrinterException e) {
        System.out.println("Notifikasi print: " + e);
        javax.swing.JOptionPane.showMessageDialog(null, "Gagal print: " + e.getMessage());
    }
}

private boolean tutupShiftGlobal(String tgl) {
    String sql =
        "UPDATE toko_kasir_shift " +
        "SET jam_tutup = CURTIME() " +
        "WHERE tgl = ? AND jam_tutup IS NULL " +
        "ORDER BY id DESC LIMIT 1";
    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setString(1, tgl);
        int aff = ps.executeUpdate();
        System.out.println("tutupShiftGlobal affected_rows=" + aff);
        return aff > 0;
    } catch (Exception e) {
        System.out.println("Gagal tutupShiftGlobal: " + e);
        return false;
    }
}

private void tampilPopupTuslahPerNip() {
    try {
        final toko.TuslahTokopenjualanService svc = new toko.TuslahTokopenjualanService(koneksi);

        // formatter Rp
        java.text.DecimalFormatSymbols sym = new java.text.DecimalFormatSymbols();
        sym.setGroupingSeparator('.');
        sym.setDecimalSeparator(',');
        final java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0", sym);

        final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        // ===== dialog
        final JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Rekap Tuslah Per NIP",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlg.setSize(940, 600);
        dlg.setLocationRelativeTo(this);

        JPanel root = new JPanel(new java.awt.BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ===== komponen tanggal (dibuat DI DALAM POPUP)
        final widget.Tanggal dt1 = new widget.Tanggal();
        final widget.Tanggal dt2 = new widget.Tanggal();

        java.util.Date now = new java.util.Date();
        dt1.setDate(now);
        dt2.setDate(now);

        JButton btnTampil   = new JButton("Tampilkan");
        JButton btnHariIni  = new JButton("Hari Ini");
        JButton btnBulanIni = new JButton("Bulan Ini");

        JPanel pFilter = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 2));
        pFilter.add(new JLabel("Periode:"));
        dt1.setPreferredSize(new java.awt.Dimension(110, 26));
        dt2.setPreferredSize(new java.awt.Dimension(110, 26));
        pFilter.add(dt1);
        pFilter.add(new JLabel(" s/d "));
        pFilter.add(dt2);
        pFilter.add(btnTampil);
        pFilter.add(btnHariIni);
        pFilter.add(btnBulanIni);

        // ===== judul + ringkasan
        JLabel lTitle = new JLabel("REKAP TUSLAH PER NIP");
        lTitle.setFont(lTitle.getFont().deriveFont(java.awt.Font.BOLD, 16f));

        final JLabel lPrd = new JLabel("Periode: -");
        final JLabel lJml = new JLabel("Total Nota: 0");
        final JLabel lTot = new JLabel("Total Transaksi: Rp0");
        final JLabel lTus = new JLabel("Total Tuslah: Rp0");
        lTus.setFont(lTus.getFont().deriveFont(java.awt.Font.BOLD, 12f));

        JPanel info = new JPanel(new java.awt.GridLayout(0, 2, 12, 6));
        info.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Ringkasan"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        info.add(lPrd); info.add(new JLabel(""));
        info.add(lJml); info.add(new JLabel(""));
        info.add(lTot); info.add(new JLabel(""));
        info.add(lTus); info.add(new JLabel(""));

        JPanel top = new JPanel(new java.awt.BorderLayout(10, 10));
        top.add(lTitle, java.awt.BorderLayout.NORTH);
        top.add(pFilter, java.awt.BorderLayout.CENTER);
        top.add(info, java.awt.BorderLayout.SOUTH);

        root.add(top, java.awt.BorderLayout.NORTH);

        // ===== tabel
        final DefaultTableModel model = new DefaultTableModel(
                new Object[]{"NIP", "Nama", "Jml Nota", "Total Transaksi", "Total Tuslah"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 2) return Long.class;
                if (c == 3 || c == 4) return Double.class;
                return String.class;
            }
        };

        final JTable tb = new JTable(model);
        tb.setAutoCreateRowSorter(true);
        tb.setRowHeight(24);
        tb.setShowVerticalLines(false);
        tb.setShowHorizontalLines(true);
        tb.getTableHeader().setFont(tb.getTableHeader().getFont().deriveFont(java.awt.Font.BOLD, 12f));
        tb.getTableHeader().setReorderingAllowed(false);

        // zebra + align + format Rp
        final javax.swing.table.DefaultTableCellRenderer rLeft = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) setBackground((row % 2 == 0) ? new java.awt.Color(250,250,250) : java.awt.Color.WHITE);
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        };

        final javax.swing.table.DefaultTableCellRenderer rCenter = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) setBackground((row % 2 == 0) ? new java.awt.Color(250,250,250) : java.awt.Color.WHITE);
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        };

        final javax.swing.table.DefaultTableCellRenderer rRightNum = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) setBackground((row % 2 == 0) ? new java.awt.Color(250,250,250) : java.awt.Color.WHITE);
                setHorizontalAlignment(SwingConstants.RIGHT);
                return this;
            }
        };

        final javax.swing.table.DefaultTableCellRenderer rRightRp = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) setBackground((row % 2 == 0) ? new java.awt.Color(250,250,250) : java.awt.Color.WHITE);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (value instanceof Number) setText("Rp" + df.format(((Number) value).doubleValue()));
                if (column == 4) setFont(getFont().deriveFont(java.awt.Font.BOLD));
                return this;
            }
        };

        tb.getColumnModel().getColumn(0).setCellRenderer(rCenter);    // NIP
        tb.getColumnModel().getColumn(1).setCellRenderer(rLeft);      // Nama
        tb.getColumnModel().getColumn(2).setCellRenderer(rRightNum);  // Jml
        tb.getColumnModel().getColumn(3).setCellRenderer(rRightRp);   // Total
        tb.getColumnModel().getColumn(4).setCellRenderer(rRightRp);   // Tuslah

        tb.getColumnModel().getColumn(0).setPreferredWidth(70);
        tb.getColumnModel().getColumn(1).setPreferredWidth(260);
        tb.getColumnModel().getColumn(2).setPreferredWidth(90);
        tb.getColumnModel().getColumn(3).setPreferredWidth(140);
        tb.getColumnModel().getColumn(4).setPreferredWidth(140);

        root.add(new JScrollPane(tb), java.awt.BorderLayout.CENTER);

        // ===== tombol bawah
        JButton btnPrint = new JButton("Print");
        JButton btnClose = new JButton("Tutup");

        // biar print pakai total terakhir yang tampil
        final double[] grandTuslahHolder = new double[]{0.0};
        final String[] periodeHolder = new String[]{"-"};

        btnPrint.addActionListener(e -> {
            try {
                java.text.MessageFormat header = new java.text.MessageFormat(
                        "REKAP TUSLAH PER NIP • Periode: " + periodeHolder[0] +
                        " • Total Tuslah: Rp" + df.format(grandTuslahHolder[0])
                );
                java.text.MessageFormat footer = new java.text.MessageFormat("Halaman {0}");
                tb.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Gagal print: " + ex.getMessage());
            }
        });

        btnClose.addActionListener(e -> dlg.dispose());

        JPanel bottom = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        bottom.add(btnPrint);
        bottom.add(btnClose);
        root.add(bottom, java.awt.BorderLayout.SOUTH);

        dlg.setContentPane(root);

        // ===== fungsi reload data (dipanggil tombol Tampilkan/HariIni/BulanIni)
        Runnable reload = () -> {
            try {
                java.util.Date dA = dt1.getDate();
                java.util.Date dB = dt2.getDate();

                if (dA == null || dB == null) {
                    JOptionPane.showMessageDialog(dlg, "Tanggal periode masih kosong.");
                    return;
                }

                java.sql.Date tgl1 = new java.sql.Date(dA.getTime());
                java.sql.Date tgl2 = new java.sql.Date(dB.getTime());

                if (tgl2.before(tgl1)) {
                    JOptionPane.showMessageDialog(dlg, "Tanggal akhir tidak boleh lebih kecil dari tanggal awal.");
                    return;
                }

                java.util.List<toko.TuslahTokopenjualanService.RowNip> rows =
                        svc.listRekapPerNip(tgl1, tgl2, 5000);

                long grandNota = 0;
                double grandTrans = 0;
                double grandTuslah = 0;

                // reset tabel
                model.setRowCount(0);

                for (toko.TuslahTokopenjualanService.RowNip r : rows) {
                    grandNota += r.jumlahNota;
                    grandTrans += r.totalTransaksi;
                    grandTuslah += r.totalTuslah;

                    model.addRow(new Object[]{
                            r.nip,
                            (r.nama == null ? "-" : r.nama),
                            r.jumlahNota,
                            r.totalTransaksi,
                            r.totalTuslah
                    });
                }

                String periode = sdf.format(tgl1) + " s/d " + sdf.format(tgl2);
                periodeHolder[0] = periode;
                grandTuslahHolder[0] = grandTuslah;

                lPrd.setText("Periode: " + periode);
                lJml.setText("Total Nota: " + grandNota);
                lTot.setText("Total Transaksi: Rp" + df.format(grandTrans));
                lTus.setText("Total Tuslah: Rp" + df.format(grandTuslah));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Gagal load data: " + ex.getMessage());
            }
        };

        // tombol aksi
        btnTampil.addActionListener(e -> reload.run());

        btnHariIni.addActionListener(e -> {
            java.util.Date x = new java.util.Date();
            dt1.setDate(x);
            dt2.setDate(x);
            reload.run();
        });

        btnBulanIni.addActionListener(e -> {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate first = today.withDayOfMonth(1);
            dt1.setDate(java.sql.Date.valueOf(first));
            dt2.setDate(java.sql.Date.valueOf(today));
            reload.run();
        });

        // load awal
        reload.run();

        dlg.setVisible(true);

    } catch (Exception e) {
        System.out.println("Notifikasi Popup Tuslah Per NIP: " + e);
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}




}
