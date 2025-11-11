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
    private double all=0,pagi=0,siang=0,sore=0,malam=0;
    private int i;
    private String shift="",tanggal2="",nonota="",petugas="";

    /** Creates new form DlgLhtBiaya
     * @param parent
     * @param modal */
    public DlgPaymentPoint(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(885,674);

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
        Sequel.cariIsiAngka("select modal_awal from set_modal_payment",ModalAwal);
        
        
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
        label20 = new widget.Label();
        TTutupKasir = new widget.TextBox();
        BtnSeek5 = new widget.Button();
        label21 = new widget.Label();
        pengeluaran1 = new widget.TextBox();
        label22 = new widget.Label();
        qris = new widget.TextBox();

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

        BtnSeek5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeek5.setMnemonic('5');
        BtnSeek5.setToolTipText("ALt+5");
        BtnSeek5.setName("BtnSeek5"); // NOI18N
        BtnSeek5.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSeek5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeek5ActionPerformed(evt);
            }
        });
        BtnSeek5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSeek5KeyPressed(evt);
            }
        });
        panelGlass6.add(BtnSeek5);

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
        InputModalAwal.setText(Sequel.cariIsi("select modal_awal from set_modal_payment"));  
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

    private void BtnSeek5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnSeek5ActionPerformed

    private void BtnSeek5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeek5KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnSeek5KeyPressed

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
    private widget.Button BtnSeek5;
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

    public void tampil(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); 
        Valid.tabelKosong(tabMode);
        try{        
            psjamshift=koneksi.prepareStatement("select * from closing_kasir");
            try {
                rsjamshift=psjamshift.executeQuery();
                all=0;
                pagi=0;
                siang=0;
                sore=0;
                malam=0;
                i = 1; // running number global (boleh taruh di atas sebelum loop shift)
while (rsjamshift.next()) {
    final String shift = rsjamshift.getString("shift");

    // hitung awal/akhir rentang waktu shift
    String awal  = Valid.SetTgl(Tgl1.getSelectedItem()+"")+" "+rsjamshift.getString("jam_masuk");
    String akhir;
    if ("Malam".equals(shift)) {
        akhir = Sequel.cariIsi(
            "SELECT DATE_ADD(?, INTERVAL 1 DAY)",
            Valid.SetTgl(Tgl1.getSelectedItem()+"")+" "+rsjamshift.getString("jam_pulang")
        );
    } else {
        akhir = Valid.SetTgl(Tgl1.getSelectedItem()+"")+" "+rsjamshift.getString("jam_pulang");
    }

    // ------------------------------------------------------------------
    // 1) SUMBER: TAGIHAN SADEWA (RAWAT INAP/JALAN) — versi aslimu, kurung diperjelas
    // ------------------------------------------------------------------
    PreparedStatement psSadewa = null;
    ResultSet rsSadewa = null;
    try {
        psSadewa = koneksi.prepareStatement(
            "SELECT no_nota, tgl_bayar, nama_pasien, jumlah_bayar, petugas " +
            "FROM tagihan_sadewa " +
            "WHERE (tgl_bayar BETWEEN ? AND ? AND nama_pasien LIKE ?) " +
            "   OR (tgl_bayar BETWEEN ? AND ? AND no_nota      LIKE ?) " +
            "ORDER BY tgl_bayar, no_nota"
        );
        psSadewa.setString(1, awal);
        psSadewa.setString(2, akhir);
        psSadewa.setString(3, "%"+TCari.getText().trim()+"%");
        psSadewa.setString(4, awal);
        psSadewa.setString(5, akhir);
        psSadewa.setString(6, "%"+TCari.getText().trim()+"%");

        rsSadewa = psSadewa.executeQuery();
        while (rsSadewa.next()) {
            // cari nomor nota final (no_nota rawat inap/jalan bila ada)
            String nonota = Sequel.cariIsi(
                "SELECT no_nota FROM nota_inap WHERE no_rawat=?",
                rsSadewa.getString("no_nota")
            );
            if (nonota.equals("")) {
                nonota = Sequel.cariIsi(
                    "SELECT no_nota FROM nota_jalan WHERE no_rawat=?",
                    rsSadewa.getString("no_nota")
                );
                if (nonota.equals("")) nonota = rsSadewa.getString("no_nota");
            }

            String petugas = rsSadewa.getString("petugas")+" "+
                    Sequel.cariIsi("SELECT pegawai.nama FROM pegawai WHERE pegawai.nik=?",
                                   rsSadewa.getString("petugas"));

            // filter user (nik/nama mengandung teks User)
            boolean lolosUser = petugas.toLowerCase().contains(User.getText().trim().toLowerCase());

            if (CmbStatus.getSelectedItem().toString().equals("Semua") || shift.equals(CmbStatus.getSelectedItem().toString())) {
                if (lolosUser) {
                    long jml = Math.round(rsSadewa.getDouble("jumlah_bayar"));

                    if      ("Pagi".equals(shift))  pagi  += jml;
                    else if ("Siang".equals(shift)) siang += jml;
                    else if ("Sore".equals(shift))  sore  += jml;
                    else if ("Malam".equals(shift)) malam += jml;
                    all += jml;

                    tabMode.addRow(new Object[]{
                        i, rsSadewa.getString("tgl_bayar"), shift,
                        nonota, rsSadewa.getString("nama_pasien"),
                        jml, petugas
                    });
                    i++;
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Notifikasi (sadewa): "+e);
    } finally {
        if (rsSadewa != null) try { rsSadewa.close(); } catch (Exception ig) {}
        if (psSadewa != null) try { psSadewa.close(); } catch (Exception ig) {}
    }

    // ------------------------------------------------------------------
    // 2) SUMBER: TOKO PENJUALAN — DITAMBAHKAN
    //     Catatan: kalau hanya mau total tanpa ongkir/ppn, ganti jml = total saja.
    // ------------------------------------------------------------------
    PreparedStatement psToko = null;
    ResultSet rsToko = null;
    try {
        psToko = koneksi.prepareStatement(
            "SELECT tp.tgl_jual, tp.nm_member, tp.nip , pg.nama, " +
            "       SUM(tp.ongkir) AS ongkir, SUM(tp.total) AS total, SUM(tp.ppn) AS ppn " +
            "FROM tokopenjualan tp " +
            "INNER JOIN petugas pg ON tp.nip = pg.nip " +
            "WHERE tp.tgl_jual BETWEEN ? AND ? " +
            (TCari.getText().trim().isEmpty() ? "" : "AND (tp.nip LIKE ? OR pg.nama LIKE ?) ") +
            "GROUP BY tp.tgl_jual, tp.nip " +
            "ORDER BY tp.tgl_jual, tp.nip"
        );
        int p = 1;
        psToko.setString(p++, awal);
        psToko.setString(p++, akhir);
        if (!TCari.getText().trim().isEmpty()) {
            psToko.setString(p++, "%"+TCari.getText().trim()+"%");
            psToko.setString(p++, "%"+TCari.getText().trim()+"%");
        }

        rsToko = psToko.executeQuery();
        while (rsToko.next()) {
            // label petugas dari toko
            String petugas = rsToko.getString("nip")+" "+rsToko.getString("nama");
            

            // Payment Point boleh tetap filter user yang sama:
            boolean lolosUser = petugas.toLowerCase().contains(User.getText().trim().toLowerCase());

            if (CmbStatus.getSelectedItem().toString().equals("Semua") || shift.equals(CmbStatus.getSelectedItem().toString())) {
                if (lolosUser) {
                    // ambil total toko: total + ongkir + ppn (ubah sesuai kebijakanmu)
                    long jml = Math.round(
                        rsToko.getDouble("total") + rsToko.getDouble("ongkir") + rsToko.getDouble("ppn")
                    );

                    if      ("Pagi".equals(shift))  pagi  += jml;
                    else if ("Siang".equals(shift)) siang += jml;
                    else if ("Sore".equals(shift))  sore  += jml;
                    else if ("Malam".equals(shift)) malam += jml;
                    all += jml;

                    // tampilkan baris ringkas sumber "Toko" (kolom no_nota diisi label sumber)
                    tabMode.addRow(new Object[]{
                        i, rsToko.getString("tgl_jual"), shift,
                        "Toko Penjualan", rsToko.getString("nm_member"),
                        jml, petugas
                    });
                    i++;
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Notifikasi (toko): "+e);
    } finally {
        if (rsToko != null) try { rsToko.close(); } catch (Exception ig) {}
        if (psToko != null) try { psToko.close(); } catch (Exception ig) {}
    }
}
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rsjamshift!=null){
                    rsjamshift.close();
                }
                if(psjamshift!=null){
                    psjamshift.close();
                }
            }
//            if(CmbStatus.getSelectedItem().toString().equals("Semua")){
//                tabMode.addRow(new Object[]{
//                        "","Modal Awal",":","","",Double.parseDouble(ModalAwal.getText()),""
//                });
//                tabMode.addRow(new Object[]{
//                        "","Uang Masuk",":","","",all,""
//                });
//                tabMode.addRow(new Object[]{
//                        "",">> Total",":","","",(all+Double.parseDouble(ModalAwal.getText())),""
//                });
//            }else if(CmbStatus.getSelectedItem().toString().equals("Pagi")){
//                tabMode.addRow(new Object[]{
//                        "","Modal Awal",":","","",Double.parseDouble(ModalAwal.getText()),""
//                });
//                tabMode.addRow(new Object[]{
//                        "","Uang Masuk",":","","",pagi,""
//                });
//                tabMode.addRow(new Object[]{
//                        "",">> Total",":","","",(pagi+Double.parseDouble(ModalAwal.getText())),""
//                });
//            }else if(CmbStatus.getSelectedItem().toString().equals("Siang")){
//                tabMode.addRow(new Object[]{
//                        "","Modal Awal",":","","",(Double.parseDouble(ModalAwal.getText())+pagi),""
//                });
//                tabMode.addRow(new Object[]{
//                        "","Uang Masuk",":","","",siang,""
//                });
//                tabMode.addRow(new Object[]{
//                        "",">> Total",":","","",(pagi+siang+Double.parseDouble(ModalAwal.getText())),""
//                });
//            }else if(CmbStatus.getSelectedItem().toString().equals("Sore")){
//                tabMode.addRow(new Object[]{
//                        "","Modal Awal",":","","",(Double.parseDouble(ModalAwal.getText())+pagi+siang),""
//                });
//                tabMode.addRow(new Object[]{
//                        "","Uang Masuk",":","","",sore,""
//                });
//                tabMode.addRow(new Object[]{
//                        "",">> Total",":","","",(pagi+siang+sore+Double.parseDouble(ModalAwal.getText())),""
//                });
//            }else if(CmbStatus.getSelectedItem().toString().equals("Malam")){
//                tabMode.addRow(new Object[]{
//                        "","Modal Awal",":","","",(Double.parseDouble(ModalAwal.getText())+pagi+siang+sore),""
//                });
//                tabMode.addRow(new Object[]{
//                        "","Uang Masuk",":","","",malam,""
//                });
//                tabMode.addRow(new Object[]{
//                        "",">> Total",":","","",(pagi+siang+sore+malam+Double.parseDouble(ModalAwal.getText())),""
//                });
//            }                
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        this.setCursor(Cursor.getDefaultCursor());
        appendRingkasan();
    }    
    private void appendRingkasan() {
    double modal   = parseDoubleSafe(ModalAwal.getText());
double setoran = parseDoubleSafe(TTutupKasir.getText());
double nonCash = (Valid.SetAngka(pengeluaran1.getText()) + Valid.SetAngka(qris.getText()));
//double NQris = parseDoubleSafe(qris.getText());
double totalSistem = 0.0;

    String st = CmbStatus.getSelectedItem().toString();

    if ("Semua".equals(st)) {
        totalSistem = all + modal;
        tabMode.addRow(new Object[]{"","Modal Awal",":","","", modal, ""});
        tabMode.addRow(new Object[]{"","Uang Masuk",":","","", all, ""});
        tabMode.addRow(new Object[]{"",">> Total",":","","", totalSistem, ""});
    } else if ("Pagi".equals(st)) {
        totalSistem = pagi + modal;
        tabMode.addRow(new Object[]{"","Modal Awal",":","","", modal, ""});
        tabMode.addRow(new Object[]{"","Uang Masuk",":","","", pagi, ""});
        tabMode.addRow(new Object[]{"",">> Total",":","","", totalSistem, ""});
    } else if ("Siang".equals(st)) {
        totalSistem = pagi + siang + modal;
        tabMode.addRow(new Object[]{"","Modal Awal",":","","", modal + pagi, ""});
        tabMode.addRow(new Object[]{"","Uang Masuk",":","","", siang, ""});
        tabMode.addRow(new Object[]{"",">> Total",":","","", totalSistem, ""});
    } else if ("Sore".equals(st)) {
        totalSistem = pagi + siang + sore + modal;
        tabMode.addRow(new Object[]{"","Modal Awal",":","","", modal + pagi + siang, ""});
        tabMode.addRow(new Object[]{"","Uang Masuk",":","","", sore, ""});
        tabMode.addRow(new Object[]{"",">> Total",":","","", totalSistem, ""});
    } else if ("Malam".equals(st)) {
        totalSistem = pagi + siang + sore + malam + modal;
        tabMode.addRow(new Object[]{"","Modal Awal",":","","", modal + pagi + siang + sore, ""});
        tabMode.addRow(new Object[]{"","Uang Masuk",":","","", malam, ""});
        tabMode.addRow(new Object[]{"",">> Total",":","","", totalSistem, ""});
    }

    // Baris baru: Tutup Kasir & Selisih
    tabMode.addRow(new Object[]{"","Tutup Kasir",":","","", setoran, ""});
    tabMode.addRow(new Object[]{"","Selisih",":","","", ((setoran + nonCash) - totalSistem ), ""});
}
private long parseLongSafe(String s){
    if(s==null) return 0L;
    s=s.trim().replace(".", "").replace(",", "").replace("Rp", "").replace("rp", "");
    if(s.isEmpty()) return 0L;
    try { 
        // dukung desimal, tapi kita bulatkan ke rupiah
        double d = Double.parseDouble(s);
        return Math.round(d);
    } catch(Exception e){ 
        return 0L; 
    }
}
//    private void hitungTutupKasir() {
//    double modal   = Valid.SetAngka(ModalAwal.getText());
//    double total   = Valid.SetAngka(TTotalSistem.getText());
//    double setoran = Valid.SetAngka(TTutupKasir.getText());
//
//    double expected = modal + total;                // seharusnya disetor
//    double selisih  = setoran - expected;
//
//    TExpected.setText(Valid.SetAngka2(expected));   // jika Anda sediakan field “Seharusnya”
//    TSelisih.setText(Valid.SetAngka2(selisih));     // field “Selisih”
//
//    if (Math.abs(selisih) < 0.0001) {
//        LStatus.setText("✅ SESUAI");
//        LStatus.setForeground(new java.awt.Color(0,128,0));
//    } else if (selisih > 0) {
//        LStatus.setText("LEBIH " + Valid.SetAngka2(selisih));
//        LStatus.setForeground(new java.awt.Color(0,102,204));
//    } else {
//        LStatus.setText("KURANG " + Valid.SetAngka2(Math.abs(selisih)));
//        LStatus.setForeground(java.awt.Color.RED);
//    }
//}
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
}
private double parseDoubleSafe(String s){
    if(s == null) return 0.0;
    s = s.trim()
         .replace("Rp","")
         .replace("rp","")
         .replace(".","")
         .replace(",","");
    if(s.isEmpty()) return 0.0;
    try {
        return Double.parseDouble(s);
    } catch(Exception e){
        return 0.0;
    }
}
}
