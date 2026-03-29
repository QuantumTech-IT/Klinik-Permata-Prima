package toko;


import fungsi.WarnaTable2;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import keuangan.Jurnal;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TokoPemesanan extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private riwayattoko Trackbarang=new riwayattoko();
    private Jurnal jur=new Jurnal();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private TokoCariPemesanan form=new TokoCariPemesanan(null,false);
    private double ttl=0,y=0,w=0,ttldisk=0,sbttl=0,ppn=0,meterai=0,hargadiskon,hargappn;
    private int jml=0,i=0,row=0,index=0;
    private String[] nobatch;
private String[] tglexp;
    private boolean[] ganti;
    private String[] kodebarang,namabarang,satuan;
    private double[] h_beli,jumlah,subtotal,diskon,besardiskon,jmltotal,dasar,distributor,grosir,retail;
    private WarnaTable2 warna=new WarnaTable2();
    public boolean tampikan=true;
    private boolean sukses=true;
    private String Penerimaan_Toko=Sequel.cariIsi("select set_akun.Penerimaan_Toko from set_akun"),Kontra_Penerimaan_Toko=Sequel.cariIsi("select set_akun.Kontra_Penerimaan_Toko from set_akun"),
            PPN_Masukan=Sequel.cariIsi("select set_akun.PPN_Masukan from set_akun");

    /** Creates new form DlgProgramStudi
     * @param parent
     * @param modal */
    public TokoPemesanan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Object[] judul={"Jml","Kode Barang","Nama Barang","Satuan","G","Harga(Rp)","Subtotal(Rp)","Disk(%)","Diskon(Rp)","Total","Dasar","Distributor","Grosir","Retail", "No. Batch", "Expired Date"};
        tabMode=new DefaultTableModel(null,judul){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==4)||(colIndex==5)||(colIndex==7)||(colIndex==8)||(colIndex==11)||(colIndex==12)||(colIndex==13)||(colIndex==14)||(colIndex==15)) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                java.lang.Boolean.class,java.lang.Double.class,java.lang.Double.class,java.lang.Double.class,
                java.lang.Double.class,java.lang.Double.class,java.lang.Double.class,java.lang.Double.class,
                java.lang.Double.class,java.lang.Double.class,java.lang.String.class,java.lang.String.class 
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbDokter.setModel(tabMode);

        tbDokter.setPreferredScrollableViewportSize(new Dimension(800,800));
        tbDokter.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

       for (int i = 0; i < 16; i++) {
    TableColumn column = tbDokter.getColumnModel().getColumn(i);
    if(i==0){ column.setPreferredWidth(42); }         // Jml
    else if(i==1){ column.setPreferredWidth(90); }    // Kode
    else if(i==2){ column.setPreferredWidth(330); }   // Nama
    else if(i==3){ column.setPreferredWidth(50); }    // Satuan
    else if(i==4){ column.setPreferredWidth(22); }    // G (checkbox)
    else if(i==5){ column.setPreferredWidth(90); }    // Harga(Rp)
    else if(i==6){ column.setPreferredWidth(90); }    // Subtotal
    else if(i==7){ column.setPreferredWidth(60); }    // Disk(%)
    else if(i==8){ column.setPreferredWidth(90); }    // Diskon(Rp)
    else if(i==9){ column.setPreferredWidth(100);}    // Total
    else if(i==10){ column.setPreferredWidth(90);}    // Dasar
    else if(i==11){ column.setPreferredWidth(90);}    // Distributor
    else if(i==12){ column.setPreferredWidth(90);}    // Grosir
    else if(i==13){ column.setPreferredWidth(90);} // Retail
    else if(i==14){ column.setPreferredWidth(90);}// No. Batch
    else if(i==15){ column.setPreferredWidth(110);}// exp
}
        warna.kolom=0;
        tbDokter.setDefaultRenderer(Object.class,warna);

        NoFaktur.setDocument(new batasInput((byte)20).getKata(NoFaktur));
        NoOrder.setDocument(new batasInput((byte)20).getKata(NoOrder));
        kdsup.setDocument(new batasInput((byte)5).getKata(kdsup));
        kdptg.setDocument(new batasInput((byte)25).getKata(kdptg)); 
        Meterai.setDocument(new batasInput((byte)15).getOnlyAngka(Meterai));        
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
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
        }  
        
        form.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                autoNomor();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        }); 
        
        form.suplier.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("TokoPemesanan")){
                    if(form.suplier.getTable().getSelectedRow()!= -1){                   
                        kdsup.setText(form.suplier.getTable().getValueAt(form.suplier.getTable().getSelectedRow(),0).toString());                    
                        nmsup.setText(form.suplier.getTable().getValueAt(form.suplier.getTable().getSelectedRow(),1).toString());
                    } 
                    kdsup.requestFocus();
                }
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        form.suplier.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(akses.getform().equals("TokoPemesanan")){
                    if(e.getKeyCode()==KeyEvent.VK_SPACE){
                        form.suplier.dispose();
                    }                
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });            
        
        form.petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("TokoPemesanan")){
                    if(form.petugas.getTable().getSelectedRow()!= -1){
                        kdptg.setText(form.petugas.getTable().getValueAt(form.petugas.getTable().getSelectedRow(),0).toString());
                        nmptg.setText(form.petugas.getTable().getValueAt(form.petugas.getTable().getSelectedRow(),1).toString());
                    }
                    kdptg.requestFocus();
                }
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });

        initListenerHargaEnter(); // aktifkan listener konversi harga & G-flag
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Kd2 = new widget.TextBox();
        Popup = new javax.swing.JPopupMenu();
        ppBersihkan = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        scrollPane1 = new widget.ScrollPane();
        tbDokter = new widget.Table();
        panelisi1 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        label10 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari1 = new widget.Button();
        BtnCari = new widget.Button();
        BtnKeluar = new widget.Button();
        BtnTambah = new widget.Button();
        label12 = new widget.Label();
        LSubtotal = new widget.Label();
        label9 = new widget.Label();
        LPotongan = new widget.Label();
        label20 = new widget.Label();
        LTotal2 = new widget.Label();
        label17 = new widget.Label();
        tppn = new widget.TextBox();
        LPpn = new widget.Label();
        label24 = new widget.Label();
        Meterai = new widget.TextBox();
        label19 = new widget.Label();
        LTagiha = new widget.Label();
        label21 = new widget.Label();
        panelisi3 = new widget.panelisi();
        label15 = new widget.Label();
        NoFaktur = new widget.TextBox();
        label13 = new widget.Label();
        kdsup = new widget.TextBox();
        label16 = new widget.Label();
        kdptg = new widget.TextBox();
        nmsup = new widget.TextBox();
        nmptg = new widget.TextBox();
        btnSuplier = new widget.Button();
        btnPetugas = new widget.Button();
        label11 = new widget.Label();
        TglPesan = new widget.Tanggal();
        label22 = new widget.Label();
        TglFaktur = new widget.Tanggal();
        TglTempo = new widget.Tanggal();
        label18 = new widget.Label();
        NoOrder = new widget.TextBox();
        label23 = new widget.Label();

        Kd2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        Kd2.setName("Kd2"); // NOI18N
        Kd2.setPreferredSize(new java.awt.Dimension(207, 23));

        Popup.setName("Popup"); // NOI18N

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Bersihkan Jumlah");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(200, 25));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        Popup.add(ppBersihkan);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Transaksi Penerimaan Barang Toko / Minimarket / Koperasi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        scrollPane1.setComponentPopupMenu(Popup);
        scrollPane1.setName("scrollPane1"); // NOI18N
        scrollPane1.setOpaque(true);

        tbDokter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbDokter.setToolTipText("Masukkan jumlah pengajuan di ujung paling kiri pada warna biru kemudian geser kanan");
        tbDokter.setComponentPopupMenu(Popup);
        tbDokter.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tbDokter.setGridColor(new java.awt.Color(0, 0, 0));
        tbDokter.setName("tbDokter"); // NOI18N
        tbDokter.setRowHeight(25);
        tbDokter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDokterMouseClicked(evt);
            }
        });
        tbDokter.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbDokterPropertyChange(evt);
            }
        });
        tbDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbDokterKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(tbDokter);

        internalFrame1.add(scrollPane1, java.awt.BorderLayout.CENTER);

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(100, 107));
        panelisi1.setWarnaAtas(new java.awt.Color(153, 153, 153));
        panelisi1.setWarnaBawah(new java.awt.Color(153, 153, 153));
        panelisi1.setLayout(null);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelisi1.add(BtnSimpan);
        BtnSimpan.setBounds(10, 62, 100, 30);

        label10.setText("Key Word :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(75, 23));
        panelisi1.add(label10);
        label10.setBounds(110, 65, 75, 23);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(150, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi1.add(TCari);
        TCari.setBounds(190, 65, 290, 23);

        BtnCari1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari1.setMnemonic('1');
        BtnCari1.setToolTipText("Alt+1");
        BtnCari1.setName("BtnCari1"); // NOI18N
        BtnCari1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCari1ActionPerformed(evt);
            }
        });
        BtnCari1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCari1KeyPressed(evt);
            }
        });
        panelisi1.add(BtnCari1);
        BtnCari1.setBounds(482, 65, 28, 23);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnCari.setMnemonic('C');
        BtnCari.setText("Cari");
        BtnCari.setToolTipText("Alt+C");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelisi1.add(BtnCari);
        BtnCari.setBounds(560, 62, 100, 30);

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
        panelisi1.add(BtnKeluar);
        BtnKeluar.setBounds(670, 62, 100, 30);

        BtnTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        BtnTambah.setMnemonic('3');
        BtnTambah.setToolTipText("Alt+3");
        BtnTambah.setName("BtnTambah"); // NOI18N
        BtnTambah.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahActionPerformed(evt);
            }
        });
        panelisi1.add(BtnTambah);
        BtnTambah.setBounds(510, 65, 28, 23);

        label12.setForeground(new java.awt.Color(255, 255, 255));
        label12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label12.setText("Total 1 :");
        label12.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(60, 30));
        panelisi1.add(label12);
        label12.setBounds(10, 0, 60, 30);

        LSubtotal.setForeground(new java.awt.Color(255, 255, 255));
        LSubtotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LSubtotal.setText("0");
        LSubtotal.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        LSubtotal.setName("LSubtotal"); // NOI18N
        LSubtotal.setPreferredSize(new java.awt.Dimension(110, 30));
        panelisi1.add(LSubtotal);
        LSubtotal.setBounds(10, 20, 100, 30);

        label9.setForeground(new java.awt.Color(255, 255, 255));
        label9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label9.setText("Potongan :");
        label9.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(60, 30));
        panelisi1.add(label9);
        label9.setBounds(120, 0, 90, 30);

        LPotongan.setForeground(new java.awt.Color(255, 255, 255));
        LPotongan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LPotongan.setText("0");
        LPotongan.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        LPotongan.setName("LPotongan"); // NOI18N
        LPotongan.setPreferredSize(new java.awt.Dimension(110, 30));
        panelisi1.add(LPotongan);
        LPotongan.setBounds(120, 20, 100, 30);

        label20.setForeground(new java.awt.Color(255, 255, 255));
        label20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label20.setText("Total 2 :");
        label20.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label20.setName("label20"); // NOI18N
        label20.setPreferredSize(new java.awt.Dimension(60, 30));
        panelisi1.add(label20);
        label20.setBounds(230, 0, 90, 30);

        LTotal2.setForeground(new java.awt.Color(255, 255, 255));
        LTotal2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LTotal2.setText("0");
        LTotal2.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        LTotal2.setName("LTotal2"); // NOI18N
        LTotal2.setPreferredSize(new java.awt.Dimension(110, 30));
        panelisi1.add(LTotal2);
        LTotal2.setBounds(230, 20, 100, 30);

        label17.setForeground(new java.awt.Color(255, 255, 255));
        label17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label17.setText("PPN :");
        label17.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(60, 30));
        panelisi1.add(label17);
        label17.setBounds(340, 0, 40, 30);

        tppn.setForeground(new java.awt.Color(255, 255, 255));
        tppn.setText("0");
        tppn.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        tppn.setName("tppn"); // NOI18N
        tppn.setPreferredSize(new java.awt.Dimension(80, 23));
        tppn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tppnKeyPressed(evt);
            }
        });
        panelisi1.add(tppn);
        tppn.setBounds(340, 26, 45, 23);

        LPpn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LPpn.setText("0");
        LPpn.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        LPpn.setName("LPpn"); // NOI18N
        LPpn.setPreferredSize(new java.awt.Dimension(110, 30));
        panelisi1.add(LPpn);
        LPpn.setBounds(410, 20, 100, 30);

        label24.setForeground(new java.awt.Color(255, 255, 255));
        label24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label24.setText("Meterai :");
        label24.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label24.setName("label24"); // NOI18N
        label24.setPreferredSize(new java.awt.Dimension(60, 30));
        panelisi1.add(label24);
        label24.setBounds(520, 0, 90, 30);

        Meterai.setForeground(new java.awt.Color(255, 255, 255));
        Meterai.setText("0");
        Meterai.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        Meterai.setName("Meterai"); // NOI18N
        Meterai.setPreferredSize(new java.awt.Dimension(80, 23));
        Meterai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MeteraiKeyPressed(evt);
            }
        });
        panelisi1.add(Meterai);
        Meterai.setBounds(520, 26, 100, 23);

        label19.setForeground(new java.awt.Color(255, 255, 255));
        label19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label19.setText("Jumlah Tagihan :");
        label19.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(60, 30));
        panelisi1.add(label19);
        label19.setBounds(630, 0, 130, 30);

        LTagiha.setForeground(new java.awt.Color(255, 255, 255));
        LTagiha.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LTagiha.setText("0");
        LTagiha.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        LTagiha.setName("LTagiha"); // NOI18N
        LTagiha.setPreferredSize(new java.awt.Dimension(110, 30));
        panelisi1.add(LTagiha);
        LTagiha.setBounds(630, 20, 150, 30);

        label21.setForeground(new java.awt.Color(255, 255, 255));
        label21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label21.setText("%");
        label21.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label21.setName("label21"); // NOI18N
        label21.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi1.add(label21);
        label21.setBounds(387, 26, 30, 23);

        internalFrame1.add(panelisi1, java.awt.BorderLayout.PAGE_END);

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 103));
        panelisi3.setWarnaAtas(new java.awt.Color(153, 153, 153));
        panelisi3.setWarnaBawah(new java.awt.Color(102, 102, 102));
        panelisi3.setLayout(null);

        label15.setForeground(new java.awt.Color(255, 255, 255));
        label15.setText("No.Faktur :");
        label15.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label15);
        label15.setBounds(0, 10, 75, 23);

        NoFaktur.setBackground(new java.awt.Color(255, 0, 204));
        NoFaktur.setName("NoFaktur"); // NOI18N
        NoFaktur.setOpaque(true);
        NoFaktur.setPreferredSize(new java.awt.Dimension(207, 23));
        NoFaktur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoFakturKeyPressed(evt);
            }
        });
        panelisi3.add(NoFaktur);
        NoFaktur.setBounds(79, 10, 210, 23);

        label13.setForeground(new java.awt.Color(255, 255, 255));
        label13.setText("Petugas :");
        label13.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label13);
        label13.setBounds(305, 40, 100, 23);

        kdsup.setBackground(new java.awt.Color(255, 0, 204));
        kdsup.setName("kdsup"); // NOI18N
        kdsup.setOpaque(true);
        kdsup.setPreferredSize(new java.awt.Dimension(80, 23));
        kdsup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdsupKeyPressed(evt);
            }
        });
        panelisi3.add(kdsup);
        kdsup.setBounds(409, 10, 80, 23);

        label16.setForeground(new java.awt.Color(255, 255, 255));
        label16.setText("Supplier :");
        label16.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label16);
        label16.setBounds(305, 10, 100, 23);

        kdptg.setBackground(new java.awt.Color(255, 0, 204));
        kdptg.setName("kdptg"); // NOI18N
        kdptg.setOpaque(true);
        kdptg.setPreferredSize(new java.awt.Dimension(80, 23));
        kdptg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdptgKeyPressed(evt);
            }
        });
        panelisi3.add(kdptg);
        kdptg.setBounds(409, 40, 80, 23);

        nmsup.setEditable(false);
        nmsup.setBackground(new java.awt.Color(255, 0, 204));
        nmsup.setName("nmsup"); // NOI18N
        nmsup.setOpaque(true);
        nmsup.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi3.add(nmsup);
        nmsup.setBounds(491, 10, 240, 23);

        nmptg.setEditable(false);
        nmptg.setBackground(new java.awt.Color(255, 0, 204));
        nmptg.setName("nmptg"); // NOI18N
        nmptg.setOpaque(true);
        nmptg.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi3.add(nmptg);
        nmptg.setBounds(491, 40, 240, 23);

        btnSuplier.setBackground(new java.awt.Color(255, 0, 204));
        btnSuplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnSuplier.setMnemonic('1');
        btnSuplier.setToolTipText("Alt+1");
        btnSuplier.setName("btnSuplier"); // NOI18N
        btnSuplier.setOpaque(true);
        btnSuplier.setPreferredSize(new java.awt.Dimension(28, 23));
        btnSuplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuplierActionPerformed(evt);
            }
        });
        panelisi3.add(btnSuplier);
        btnSuplier.setBounds(734, 10, 28, 23);

        btnPetugas.setBackground(new java.awt.Color(255, 0, 204));
        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('2');
        btnPetugas.setToolTipText("Alt+2");
        btnPetugas.setName("btnPetugas"); // NOI18N
        btnPetugas.setOpaque(true);
        btnPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasActionPerformed(evt);
            }
        });
        panelisi3.add(btnPetugas);
        btnPetugas.setBounds(734, 40, 28, 23);

        label11.setForeground(new java.awt.Color(255, 255, 255));
        label11.setText("Tgl.Datang :");
        label11.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label11);
        label11.setBounds(0, 40, 75, 23);

        TglPesan.setBackground(new java.awt.Color(255, 51, 204));
        TglPesan.setDisplayFormat("dd-MM-yyyy");
        TglPesan.setName("TglPesan"); // NOI18N
        TglPesan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TglPesanItemStateChanged(evt);
            }
        });
        TglPesan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglPesanKeyPressed(evt);
            }
        });
        panelisi3.add(TglPesan);
        TglPesan.setBounds(79, 40, 95, 23);

        label22.setForeground(new java.awt.Color(255, 255, 255));
        label22.setText("Tgl.Faktur :");
        label22.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label22.setName("label22"); // NOI18N
        label22.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label22);
        label22.setBounds(180, 40, 60, 23);

        TglFaktur.setBackground(new java.awt.Color(255, 51, 204));
        TglFaktur.setDisplayFormat("dd-MM-yyyy");
        TglFaktur.setName("TglFaktur"); // NOI18N
        TglFaktur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglFakturKeyPressed(evt);
            }
        });
        panelisi3.add(TglFaktur);
        TglFaktur.setBounds(243, 40, 95, 23);

        TglTempo.setBackground(new java.awt.Color(255, 51, 204));
        TglTempo.setDisplayFormat("dd-MM-yyyy");
        TglTempo.setName("TglTempo"); // NOI18N
        TglTempo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglTempoKeyPressed(evt);
            }
        });
        panelisi3.add(TglTempo);
        TglTempo.setBounds(243, 70, 95, 23);

        label18.setForeground(new java.awt.Color(255, 255, 255));
        label18.setText("Jth.Tempo :");
        label18.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label18);
        label18.setBounds(180, 70, 60, 23);

        NoOrder.setBackground(new java.awt.Color(255, 0, 204));
        NoOrder.setName("NoOrder"); // NOI18N
        NoOrder.setOpaque(true);
        NoOrder.setPreferredSize(new java.awt.Dimension(207, 23));
        NoOrder.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoOrderKeyPressed(evt);
            }
        });
        panelisi3.add(NoOrder);
        NoOrder.setBounds(79, 70, 95, 23);

        label23.setForeground(new java.awt.Color(255, 255, 255));
        label23.setText("SP/Order :");
        label23.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label23.setName("label23"); // NOI18N
        label23.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label23);
        label23.setBounds(0, 70, 75, 23);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        form.emptTeks();    
        form.isCek();
        form.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        form.setLocationRelativeTo(internalFrame1);
        form.setAlwaysOnTop(false);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
            dispose();  
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){            
            dispose();              
        }else{Valid.pindah(evt,BtnSimpan,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed
/*
private void KdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdKeyPressed
    Valid.pindah(evt,BtnCari,Nm);
}//GEN-LAST:event_TKdKeyPressed
*/

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
       if (NoFaktur.getText().trim().equals("")) {
    Valid.textKosong(NoFaktur, "No.Faktur");
} else if (nmsup.getText().trim().equals("")) {
    Valid.textKosong(kdsup, "Supplier");
} else if (nmptg.getText().trim().equals("")) {
    Valid.textKosong(kdptg, "Petugas");
} else if (NoOrder.getText().trim().equals("")) {
    Valid.textKosong(NoOrder, "No.Order");
} else if (Meterai.getText().trim().equals("")) {
    Valid.textKosong(Meterai, "meterai");
} else if (tbDokter.getRowCount() == 0) {
    JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
    TCari.requestFocus();
} else if (ttl <= 0) {
    JOptionPane.showMessageDialog(null, "Maaf, Silahkan masukkan pembelian...!!!!");
    tbDokter.requestFocus();
} else {

    int reply = JOptionPane.showConfirmDialog(
            rootPane,
            "Eeiiiiiits, udah bener belum data yang mau disimpan..??",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION
    );

    if (reply == JOptionPane.YES_OPTION) {
        writeLog(); // tulis log sebelum proses simpan
        Sequel.AutoComitFalse();
        sukses = true;

        if (Sequel.menyimpantf2(
                "tokopemesanan",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                "No.Faktur",
                14,
                new String[]{
                        NoFaktur.getText(),
                        NoOrder.getText(),
                        kdsup.getText(),
                        kdptg.getText(),
                        Valid.SetTgl(TglPesan.getSelectedItem() + ""),
                        Valid.SetTgl(TglFaktur.getSelectedItem() + ""),
                        Valid.SetTgl(TglTempo.getSelectedItem() + ""),
                        "" + sbttl,
                        "" + ttldisk,
                        "" + ttl,
                        "" + ppn,
                        "" + meterai,
                        "" + (ttl + ppn + meterai),
                        "Belum Dibayar"
                }
        )) {

            jml = tbDokter.getRowCount();

            for (i = 0; i < jml; i++) {
                if (Valid.SetAngka(tbDokter.getValueAt(i, 0).toString()) > 0) {

                    boolean cekG_debug = Boolean.TRUE.equals(tbDokter.getValueAt(i, 4));
                    logSimpan("=== SIMPAN BARIS [" + i + "] | Faktur: " + NoFaktur.getText() + " ===");
                    logSimpan("  Kode     : " + tbDokter.getValueAt(i, 1));
                    logSimpan("  Nama     : " + tbDokter.getValueAt(i, 2));
                    logSimpan("  Satuan   : " + tbDokter.getValueAt(i, 3));
                    logSimpan("  G        : " + cekG_debug);
                    logSimpan("  Qty      : " + tbDokter.getValueAt(i, 0));
                    logSimpan("  H.Beli   : " + tbDokter.getValueAt(i, 5));
                    logSimpan("  Subtotal : " + tbDokter.getValueAt(i, 6));
                    logSimpan("  Disk%    : " + tbDokter.getValueAt(i, 7));
                    logSimpan("  Diskon   : " + tbDokter.getValueAt(i, 8));
                    logSimpan("  Total    : " + tbDokter.getValueAt(i, 9));
                    logSimpan("  Dasar    : " + tbDokter.getValueAt(i, 10));
                    logSimpan("  Dist     : " + tbDokter.getValueAt(i, 11));
                    logSimpan("  Grosir   : " + tbDokter.getValueAt(i, 12));
                    logSimpan("  Retail   : " + tbDokter.getValueAt(i, 13));
                    logSimpan("  Batch    : " + tbDokter.getValueAt(i, 14));
                    logSimpan("  Expire   : " + tbDokter.getValueAt(i, 15));
                    if (cekG_debug) {
                        double hBeliSimpan = Double.parseDouble(tbDokter.getValueAt(i, 5).toString());
                        double ppnPct      = Double.parseDouble(tppn.getText().isEmpty() ? "0" : tppn.getText());
                        logSimpan("  [G=true] h_beli→DB : " + (hBeliSimpan + (ppnPct/100)*hBeliSimpan));
                        logSimpan("  [G=true] dasar→DB  : " + tbDokter.getValueAt(i, 10));
                        logSimpan("  [G=true] grosir→DB : " + tbDokter.getValueAt(i, 12));
                        logSimpan("  [G=true] retail→DB : " + tbDokter.getValueAt(i, 13));
                    }

                    // ✅ INSERT detail pesan (tgl_expire aman NULL)
                    if (insertDetailPesan(i)) {

                        Trackbarang.catatRiwayat(
                                tbDokter.getValueAt(i, 1).toString(),
                                Valid.SetAngka(tbDokter.getValueAt(i, 0).toString()),
                                0,
                                "Penerimaan",
                                akses.getkode(),
                                "Simpan"
                        );

                        Sequel.mengedit(
                                "tokobarang",
                                "kode_brng=?",
                                "stok=stok+?",
                                2,
                                new String[]{
                                        tbDokter.getValueAt(i, 0).toString(),
                                        tbDokter.getValueAt(i, 1).toString()
                                }
                        );

                        // ✅ Update expire tokobarang hanya kalau tanggal valid
                        String expParam = normalizeMysqlDate(tbDokter.getValueAt(i, 15));
                        if (expParam != null) {
                            Sequel.mengedit(
                                    "tokobarang",
                                    "kode_brng=?",
                                    "expire=?",
                                    2,
                                    new String[]{
                                            expParam,
                                            tbDokter.getValueAt(i, 1).toString()
                                    }
                            );
                        }

                        // ✅ checkbox G aman dari NULL
                        boolean cekG = Boolean.TRUE.equals(tbDokter.getValueAt(i, 4));

                        if (cekG && akses.gettoko_barang()) {
                            Sequel.mengedit(
                                    "tokobarang",
                                    "kode_brng=?",
                                    "dasar=?,h_beli=?,distributor=?,grosir=?,retail=?",
                                    6,
                                    new String[]{
                                            tbDokter.getValueAt(i, 10).toString(),
                                            (Double.parseDouble(tbDokter.getValueAt(i, 5).toString())
                                                    + ((Double.parseDouble(tppn.getText()) / 100)
                                                    * Double.parseDouble(tbDokter.getValueAt(i, 5).toString()))) + "",
                                            tbDokter.getValueAt(i, 11).toString(),
                                            tbDokter.getValueAt(i, 12).toString(),
                                            tbDokter.getValueAt(i, 13).toString(),
                                            tbDokter.getValueAt(i, 1).toString()
                                    }
                            );
                        }

                    } else {
                        sukses = false;
                    }
                }
            }

        } else {
            sukses = false;
        }

        if (sukses) {
            Sequel.queryu("delete from tampjurnal");
            Sequel.menyimpan("tampjurnal", "?,?,?,?", 4, new String[]{Penerimaan_Toko, "PERSEDIAAN BARANG TOKO", "" + (ttl + meterai), "0"});
            if (ppn > 0) {
                Sequel.menyimpan2("tampjurnal", "?,?,?,?", 4, new String[]{PPN_Masukan, "PPN Masukan Toko", "" + ppn, "0"});
            }
            Sequel.menyimpan("tampjurnal", "?,?,?,?", 4, new String[]{Kontra_Penerimaan_Toko, "HUTANG BARANG TOKO", "0", "" + (ttl + ppn + meterai)});
            sukses = jur.simpanJurnal(NoFaktur.getText(), "U", "PENERIMAAN BARANG TOKO" + ", OLEH " + akses.getkode());
        }

        if (sukses) {
            Sequel.Commit();

            // Gunakan variabel lokal + flag agar listener tidak terpicu
            // dan global i/jml tidak ditimpa oleh getData() di dalam listener
            int rowCount = tabMode.getRowCount();
            sedangUpdateHarga = true;
            try {
                for (int r = 0; r < rowCount; r++) {
                    tabMode.setValueAt("", r, 0);
                    tabMode.setValueAt(false, r, 4);
                    tabMode.setValueAt(0, r, 6);
                    tabMode.setValueAt(0, r, 7);
                    tabMode.setValueAt(0, r, 8);
                    tabMode.setValueAt(0, r, 9);
                    tabMode.setValueAt(0, r, 10);
                    tabMode.setValueAt(0, r, 11);
                    tabMode.setValueAt(0, r, 12);
                    tabMode.setValueAt(0, r, 13);
                    tabMode.setValueAt("", r, 14);
                    tabMode.setValueAt("", r, 15);
                }
            } finally {
                sedangUpdateHarga = false;
            }

            Meterai.setText("0");
            getData();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\n" +
                    "Periksa kembali data sebelum melanjutkan menyimpan..!!"
            );
            Sequel.RollBack();
        }

        Sequel.AutoComitTrue();
        autoNomor();
    }
}
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,BtnKeluar,TCari);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnSimpan,BtnKeluar);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            tampil();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari1.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kdsup.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbDokter.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

private void BtnCari1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCari1ActionPerformed
        tampil();
}//GEN-LAST:event_BtnCari1ActionPerformed

private void BtnCari1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCari1KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            tampil();
        }else{
            Valid.pindah(evt, BtnSimpan, BtnKeluar);
        }
}//GEN-LAST:event_BtnCari1KeyPressed

private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        int rowCount = tabMode.getRowCount();
        sedangUpdateHarga = true;
        try {
            for (int r = 0; r < rowCount; r++) {
                tabMode.setValueAt("",    r, 0);
                tabMode.setValueAt(false, r, 4);
                tabMode.setValueAt(0,     r, 6);
                tabMode.setValueAt(0,     r, 7);
                tabMode.setValueAt(0,     r, 8);
                tabMode.setValueAt(0,     r, 9);
                tabMode.setValueAt(0,     r, 10);
                tabMode.setValueAt(0,     r, 11);
                tabMode.setValueAt(0,     r, 12);
                tabMode.setValueAt(0,     r, 13);
            }
        } finally {
            sedangUpdateHarga = false;
        }
}//GEN-LAST:event_ppBersihkanActionPerformed

private void tbDokterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDokterMouseClicked
        if(tbDokter.getRowCount()!=0){
            try {
                   int selCol = tbDokter.getSelectedColumn();
                   int selRow = tbDokter.getSelectedRow();
                   if((selCol==1)||(selCol==5)||(selCol==6)||(selCol==8)){
                        getData();
                   }else if(selCol==7){
                       tbDokter.setValueAt(Math.round(Double.parseDouble(tbDokter.getValueAt(selRow,6).toString())*
                               (Double.parseDouble(tbDokter.getValueAt(selRow,7).toString())/100)),selRow,8);
                       recalcRowByG(selRow);
                       getData();
                   }
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbDokterMouseClicked

private void tbDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbDokterKeyPressed
        if(tbDokter.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                try {
                   int selCol = tbDokter.getSelectedColumn();
                   int selRow = tbDokter.getSelectedRow();
                   if (selCol == COL_HARGA) {
                       // Enter di kolom harga: update dasar, recalc grosir & retail
                       konversiHargaBaru(selRow);
                       getData();
                   } else if (selCol == COL_DIST) {
                       // Enter di kolom distributor: hitung grosir & retail dari distributor
                       konversiDariDistributor(selRow);
                       getData();
                   } else if((selCol==1)||(selCol==2)||(selCol==6)||(selCol==8)){
                        getData();
                        TCari.setText("");
                        TCari.requestFocus();
                   } else if(selCol==7){
                       if(Double.parseDouble(tbDokter.getValueAt(selRow,7).toString())>0){
                           tbDokter.setValueAt(Math.round(Double.parseDouble(tbDokter.getValueAt(selRow,6).toString())*
                               (Double.parseDouble(tbDokter.getValueAt(selRow,7).toString())/100)),selRow,8);
                       }
                       recalcRowByG(selRow);
                       getData();
                   }
                } catch (java.lang.NullPointerException e) {
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_DELETE){
                int delRow = tbDokter.getSelectedRow();
                if(delRow != -1){
                    tbDokter.setValueAt("", delRow, 0);
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                TCari.setText("");
                TCari.requestFocus();
            }else if(evt.getKeyCode()==KeyEvent.VK_RIGHT){
                   if((tbDokter.getSelectedColumn()==1)||(tbDokter.getSelectedColumn()==5)||(tbDokter.getSelectedColumn()==6)){  
                        setKonversi(tbDokter.getSelectedRow());
                        getData();  
                   }else if((tbDokter.getSelectedColumn()==7)||(tbDokter.getSelectedColumn()==8)){
                       setKonversi(tbDokter.getSelectedRow());
                       if(Double.parseDouble(tbDokter.getValueAt(tbDokter.getSelectedRow(),7).toString())>0){
                        tbDokter.setValueAt(Math.round(Double.parseDouble(tbDokter.getValueAt(tbDokter.getSelectedRow(),6).toString())*
                               (Double.parseDouble(tbDokter.getValueAt(tbDokter.getSelectedRow(),7).toString())/100)),tbDokter.getSelectedRow(),8);    
                       }
                       getData();
                   }
            }
        }
}//GEN-LAST:event_tbDokterKeyPressed

private void NoFakturKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoFakturKeyPressed
        Valid.pindah(evt, BtnSimpan, kdsup);
}//GEN-LAST:event_NoFakturKeyPressed

private void kdsupKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdsupKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            Sequel.cariIsi("select nama_suplier from tokosuplier where kode_suplier=?", nmsup,kdsup.getText());           
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            Sequel.cariIsi("select nama_suplier from tokosuplier where kode_suplier=?", nmsup,kdsup.getText());
            NoFaktur.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            Sequel.cariIsi("select nama_suplier from tokosuplier where kode_suplier=?", nmsup,kdsup.getText());
            kdptg.requestFocus(); 
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnSuplierActionPerformed(null);
        }
}//GEN-LAST:event_kdsupKeyPressed

private void kdptgKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdptgKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            nmptg.setText(form.petugas.tampil3(kdptg.getText()));          
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            nmptg.setText(form.petugas.tampil3(kdptg.getText()));
            kdsup.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            nmptg.setText(form.petugas.tampil3(kdptg.getText()));
            BtnSimpan.requestFocus();  
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnPetugasActionPerformed(null);
        }
}//GEN-LAST:event_kdptgKeyPressed

private void btnSuplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuplierActionPerformed
        akses.setform("TokoPemesanan");
        form.suplier.emptTeks();
        form.suplier.isCek();
        form.suplier.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        form.suplier.setLocationRelativeTo(internalFrame1);
        form.suplier.setAlwaysOnTop(false);
        form.suplier.setVisible(true);
}//GEN-LAST:event_btnSuplierActionPerformed

private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        akses.setform("TokoPemesanan");
        form.petugas.emptTeks();
        form.petugas.isCek();
        form.petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        form.petugas.setLocationRelativeTo(internalFrame1);
        form.petugas.setAlwaysOnTop(false);
        form.petugas.setVisible(true);
}//GEN-LAST:event_btnPetugasActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if(tampikan==true){
            tampil();
        }
    }//GEN-LAST:event_formWindowOpened

    private void BtnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        akses.setform("TokoPemesanan");
        form.barang.emptTeks();
        form.barang.isCek();
        form.barang.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        form.barang.setLocationRelativeTo(internalFrame1);
        form.barang.setAlwaysOnTop(false);
        form.barang.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnTambahActionPerformed

    private void tppnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tppnKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            getData();
        }
    }//GEN-LAST:event_tppnKeyPressed

    private void MeteraiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MeteraiKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            getData();
        }
    }//GEN-LAST:event_MeteraiKeyPressed

    private void TglPesanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TglPesanItemStateChanged
        try {
            autoNomor();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_TglPesanItemStateChanged

    private void TglPesanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglPesanKeyPressed
        Valid.pindah(evt,NoFaktur,kdsup);
    }//GEN-LAST:event_TglPesanKeyPressed

    private void TglFakturKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglFakturKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TglFakturKeyPressed

    private void TglTempoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglTempoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TglTempoKeyPressed

    private void NoOrderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoOrderKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoOrderKeyPressed

    private void tbDokterPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbDokterPropertyChange
        if(this.isVisible()==true){
            getData();
        }
    }//GEN-LAST:event_tbDokterPropertyChange

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            TokoPemesanan dialog = new TokoPemesanan(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCari;
    private widget.Button BtnCari1;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.Button BtnTambah;
    private widget.TextBox Kd2;
    private widget.Label LPotongan;
    private widget.Label LPpn;
    private widget.Label LSubtotal;
    private widget.Label LTagiha;
    private widget.Label LTotal2;
    private widget.TextBox Meterai;
    private widget.TextBox NoFaktur;
    private widget.TextBox NoOrder;
    private javax.swing.JPopupMenu Popup;
    private widget.TextBox TCari;
    private widget.Tanggal TglFaktur;
    private widget.Tanggal TglPesan;
    private widget.Tanggal TglTempo;
    private widget.Button btnPetugas;
    private widget.Button btnSuplier;
    private widget.InternalFrame internalFrame1;
    private widget.TextBox kdptg;
    private widget.TextBox kdsup;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label15;
    private widget.Label label16;
    private widget.Label label17;
    private widget.Label label18;
    private widget.Label label19;
    private widget.Label label20;
    private widget.Label label21;
    private widget.Label label22;
    private widget.Label label23;
    private widget.Label label24;
    private widget.Label label9;
    private widget.TextBox nmptg;
    private widget.TextBox nmsup;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi3;
    private javax.swing.JMenuItem ppBersihkan;
    private widget.ScrollPane scrollPane1;
    private widget.Table tbDokter;
    private widget.TextBox tppn;
    // End of variables declaration//GEN-END:variables

    
    private boolean sedangUpdateHarga = false;
    private void initListenerHargaEnter() {
    tabMode.addTableModelListener(e -> {
        if (e.getType() != javax.swing.event.TableModelEvent.UPDATE) return;
        if (sedangUpdateHarga) return;

        int r = e.getFirstRow();
        int c = e.getColumn();

        if (c == COL_GFLAG) {
            // G dicentang → langsung konversi harga dengan h_beli yang ada
            sedangUpdateHarga = true;
            try {
                if (Boolean.TRUE.equals(tabMode.getValueAt(r, COL_GFLAG))) {
                    konversiHargaBaru(r);
                    getData();
                }
            } finally {
                sedangUpdateHarga = false;
            }
        } else if (c == 0 || c == 1) {
            // kolom 0 = jumlah, kolom 1 = kode → ambil harga dari master
            sedangUpdateHarga = true;
            try {
                boolean force = (c == 1);
                ambilHargaMasterTokobarang(r, force);
                getData();
            } finally {
                sedangUpdateHarga = false;
            }
        }
    });
}
    private String s(Object v) {
    return (v == null) ? "" : v.toString().trim();
}

private double d(Object v) {
    try {
        String x = s(v);
        if (x.equals("")) return 0.0;
        return Double.parseDouble(x);
    } catch (Exception e) {
        return 0.0;
    }
}

private void ambilHargaMasterTokobarang(int row, boolean force) {
    if (row < 0) return;

    String kode = s(tbDokter.getValueAt(row, 1));
    if (kode.equals("")) return;

    // kalau checkbox G = true, biasanya user mau ubah harga manual → jangan ditimpa
    boolean cekG = Boolean.TRUE.equals(tbDokter.getValueAt(row, 4));
    if (!force && cekG) return;

    PreparedStatement psHarga = null;
    ResultSet rsHarga = null;

    try {
        psHarga = koneksi.prepareStatement(
            "SELECT kode_sat, h_beli, dasar, distributor, grosir, retail, expire " +
            "FROM tokobarang WHERE kode_brng=? LIMIT 1"
        );
        psHarga.setString(1, kode);
        rsHarga = psHarga.executeQuery();

        if (rsHarga.next()) {
            // satuan: isi kalau kosong
            if (s(tbDokter.getValueAt(row, 3)).equals("")) {
                tbDokter.setValueAt(rsHarga.getString("kode_sat"), row, 3);
            }

            tbDokter.setValueAt(rsHarga.getDouble("h_beli"), row, 5);
            tbDokter.setValueAt(rsHarga.getDouble("dasar"), row, 10);
            tbDokter.setValueAt(rsHarga.getDouble("distributor"), row, 11);
            tbDokter.setValueAt(rsHarga.getDouble("grosir"), row, 12);
            tbDokter.setValueAt(rsHarga.getDouble("retail"), row, 13);

            String exp = rsHarga.getString("expire");
            tbDokter.setValueAt(exp == null ? "" : exp, row, 15);
        }
    } catch (Exception e) {
        System.out.println("ambilHargaMasterTokobarang(): " + e);
    } finally {
        try { if (rsHarga != null) rsHarga.close(); } catch (Exception ex) {}
        try { if (psHarga != null) psHarga.close(); } catch (Exception ex) {}
    }
}

    private void tampil() {
    row = tbDokter.getRowCount();
    jml = 0;

    // hitung baris yg qty > 0
    for (i = 0; i < row; i++) {
        try {
            if (d(tbDokter.getValueAt(i, 0)) > 0) {
                jml++;
            }
        } catch (Exception e) {
            // skip
        }
    }

    kodebarang = new String[jml];
    namabarang = new String[jml];
    satuan     = new String[jml];
    h_beli     = new double[jml];
    jumlah     = new double[jml];
    subtotal   = new double[jml];
    diskon     = new double[jml];
    besardiskon= new double[jml];
    jmltotal   = new double[jml];
    ganti      = new boolean[jml];
    dasar      = new double[jml];
    distributor= new double[jml];
    grosir     = new double[jml];
    retail     = new double[jml];
    nobatch    = new String[jml];
    tglexp     = new String[jml];

    index = 0;

    // simpan dulu item yg sudah diisi qty
    for (i = 0; i < row; i++) {
        try {
            if (d(tbDokter.getValueAt(i, 0)) > 0) {
                jumlah[index]      = d(tbDokter.getValueAt(i, 0));
                kodebarang[index]  = s(tbDokter.getValueAt(i, 1));
                namabarang[index]  = s(tbDokter.getValueAt(i, 2));
                satuan[index]      = s(tbDokter.getValueAt(i, 3));

                // ✅ aman dari null
                ganti[index]       = Boolean.TRUE.equals(tbDokter.getValueAt(i, 4));

                h_beli[index]      = d(tbDokter.getValueAt(i, 5));
                subtotal[index]    = d(tbDokter.getValueAt(i, 6));
                diskon[index]      = d(tbDokter.getValueAt(i, 7));
                besardiskon[index] = d(tbDokter.getValueAt(i, 8));
                jmltotal[index]    = d(tbDokter.getValueAt(i, 9));
                dasar[index]       = d(tbDokter.getValueAt(i, 10));
                distributor[index] = d(tbDokter.getValueAt(i, 11));
                grosir[index]      = d(tbDokter.getValueAt(i, 12));
                retail[index]      = d(tbDokter.getValueAt(i, 13));

                nobatch[index]     = s(tbDokter.getValueAt(i, 14));
                tglexp[index]      = s(tbDokter.getValueAt(i, 15));

                index++;
            }
        } catch (Exception e) {
            // skip
        }
    }

    // kosongkan tabel tampilan
    Valid.tabelKosong(tabMode);

    // masukkan lagi item yg sudah dipilih
    for (i = 0; i < jml; i++) {
        tabMode.addRow(new Object[]{
            jumlah[i],      // 0
            kodebarang[i],  // 1
            namabarang[i],  // 2
            satuan[i],      // 3
            ganti[i],       // 4
            h_beli[i],      // 5
            subtotal[i],    // 6
            diskon[i],      // 7
            besardiskon[i], // 8
            jmltotal[i],    // 9
            dasar[i],       // 10
            distributor[i], // 11
            grosir[i],      // 12
            retail[i],      // 13
            nobatch[i],     // 14
            tglexp[i]       // 15
        });
    }

    // tampilkan master barang (harga lengkap)
    try {
        ps = koneksi.prepareStatement(
            "SELECT kode_brng, nama_brng, kode_sat, " +
            "       h_beli, dasar, distributor, grosir, retail, expire " +
            "FROM tokobarang " +
            "WHERE status='1' AND (kode_brng LIKE ? OR nama_brng LIKE ? OR jenis LIKE ?) " +
            "ORDER BY nama_brng"
        );

        try {
            ps.setString(1, "%" + TCari.getText().trim() + "%");
            ps.setString(2, "%" + TCari.getText().trim() + "%");
            ps.setString(3, "%" + TCari.getText().trim() + "%");

            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    "",                          // 0 Jml (kosong)
                    rs.getString("kode_brng"),   // 1
                    rs.getString("nama_brng"),   // 2
                    rs.getString("kode_sat"),    // 3
                    false,                       // 4 G
                    rs.getDouble("h_beli"),      // 5 Harga beli
                    0.0,                         // 6 Subtotal
                    0.0,                         // 7 Disk(%)
                    0.0,                         // 8 Disk(Rp)
                    0.0,                         // 9 Total
                    rs.getDouble("dasar"),       // 10 Dasar
                    rs.getDouble("distributor"), // 11 Distributor
                    rs.getDouble("grosir"),      // 12 Grosir
                    rs.getDouble("retail"),      // 13 Retail
                    "",                          // 14 No Batch
                    rs.getString("expire") == null ? "" : rs.getString("expire") // 15 Exp
                });
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    } catch (SQLException e) {
        System.out.println("Notifikasi : " + e);
    }
}
    
    private void getData() {
    row = tbDokter.getSelectedRow();
    if (row != -1) {

        String qtyStr = s(tbDokter.getValueAt(row, 0));

        if (!qtyStr.equals("")) {
            try {
                if (Valid.SetAngka(qtyStr) > 0) {
                    double qty   = d(tbDokter.getValueAt(row, 0));
                    double harga = d(tbDokter.getValueAt(row, 5));
                    double sub   = qty * harga;

                    tbDokter.setValueAt(sub, row, 6);
                    tbDokter.setValueAt(sub - d(tbDokter.getValueAt(row, 8)), row, 9);
                }
            } catch (Exception e) {
                tbDokter.setValueAt("", row, 0);
                tbDokter.setValueAt(0, row, 6);
                tbDokter.setValueAt(0, row, 7);
                tbDokter.setValueAt(0, row, 8);
                tbDokter.setValueAt(0, row, 9);
                tbDokter.setValueAt("", row, 14);
                // expire biar aman
                tbDokter.setValueAt("", row, 15);
            }
        } else {
            tbDokter.setValueAt(0, row, 6);
            tbDokter.setValueAt(0, row, 7);
            tbDokter.setValueAt(0, row, 8);
            tbDokter.setValueAt(0, row, 9);
            tbDokter.setValueAt("", row, 14);
            tbDokter.setValueAt("", row, 15);
        }
    }

    ttl = 0;
    sbttl = 0;
    ttldisk = 0;
    y = 0;
    w = 0;

    meterai = 0;
    if (!Meterai.getText().equals("")) {
        meterai = Double.parseDouble(Meterai.getText());
    }

    jml = tbDokter.getRowCount();
    for (i = 0; i < jml; i++) {
        w = d(tbDokter.getValueAt(i, 6));
        sbttl = sbttl + w;

        y = d(tbDokter.getValueAt(i, 8));
        ttldisk = ttldisk + y;
    }

    LSubtotal.setText(Valid.SetAngka(sbttl));
    LPotongan.setText(Valid.SetAngka(ttldisk));

    ttl = sbttl - ttldisk;
    LTotal2.setText(Valid.SetAngka(ttl));

    ppn = 0;
    if (!tppn.getText().equals("")) {
        ppn = Math.round((Double.parseDouble(tppn.getText()) / 100) * (ttl));
        LPpn.setText(Valid.SetAngka(ppn));
        LTagiha.setText(Valid.SetAngka(ttl + ppn + meterai));
    }
}
//    private void tampil() {
//        row=tbDokter.getRowCount();
//        jml=0;
//        for(i=0;i<row;i++){
//            try {
//                if(Double.parseDouble(tbDokter.getValueAt(i,0).toString())>0){
//                    jml++;
//                }
//            } catch (Exception e) {
//                jml=jml+0;
//            } 
//        }
//        
//        kodebarang=new String[jml];
//        namabarang=new String[jml];
//        satuan=new String[jml];
//        h_beli=new double[jml];
//        jumlah=new double[jml];
//        subtotal=new double[jml];
//        diskon=new double[jml];
//        besardiskon=new double[jml];
//        jmltotal=new double[jml];
//        ganti=new boolean[jml];
//        dasar=new double[jml];
//        distributor=new double[jml];
//        grosir=new double[jml];
//        retail=new double[jml];
//        nobatch = new String[jml];
//        tglexp  = new String[jml];
//        
//        index=0;        
//        for(i=0;i<row;i++){
//            try {
//                if(Double.parseDouble(tbDokter.getValueAt(i,0).toString())>0){
//                    jumlah[index]=Double.parseDouble(tbDokter.getValueAt(i,0).toString());
//                    kodebarang[index]=tbDokter.getValueAt(i,1).toString();
//                    namabarang[index]=tbDokter.getValueAt(i,2).toString();
//                    satuan[index]=tbDokter.getValueAt(i,3).toString();
//                    ganti[index]=Boolean.parseBoolean(tbDokter.getValueAt(i,4).toString());
//                    h_beli[index]=Double.parseDouble(tbDokter.getValueAt(i,5).toString());
//                    subtotal[index]=Double.parseDouble(tbDokter.getValueAt(i,6).toString());
//                    diskon[index]=Double.parseDouble(tbDokter.getValueAt(i,7).toString());
//                    besardiskon[index]=Double.parseDouble(tbDokter.getValueAt(i,8).toString());
//                    jmltotal[index]=Double.parseDouble(tbDokter.getValueAt(i,9).toString());
//                    dasar[index]=Double.parseDouble(tbDokter.getValueAt(i,10).toString());
//                    distributor[index]=Double.parseDouble(tbDokter.getValueAt(i,11).toString());
//                    grosir[index]=Double.parseDouble(tbDokter.getValueAt(i,12).toString());
//                    retail[index]=Double.parseDouble(tbDokter.getValueAt(i,13).toString());
//                    nobatch[index] = tbDokter.getValueAt(i,14) == null ? "" : tbDokter.getValueAt(i,14).toString();
//
//            // kolom 15 = Tgl. Exp (kolom baru yg tadi kamu tambah di constructor)
//                    tglexp[index]  = (tbDokter.getValueAt(i,15) == null ? "" : tbDokter.getValueAt(i,15).toString());
//                    index++;
//                }
//            } catch (Exception e) {
//            }
//        }
//        Valid.tabelKosong(tabMode);
//        for(i=0;i<jml;i++){
//            //tabMode.addRow(new Object[]{jumlah[i],kodebarang[i],namabarang[i],satuan[i],ganti[i],h_beli[i],subtotal[i],diskon[i],besardiskon[i],jmltotal[i],dasar[i],distributor[i],grosir[i],retail[i]});
//        tabMode.addRow(new Object[]{
//        jumlah[i],      // 0
//        kodebarang[i],  // 1
//        namabarang[i],  // 2
//        satuan[i],      // 3
//        ganti[i],       // 4
//        h_beli[i],      // 5
//        subtotal[i],    // 6
//        diskon[i],      // 7
//        besardiskon[i], // 8
//        jmltotal[i],    // 9
//        dasar[i],       // 10
//        distributor[i], // 11
//        grosir[i],      // 12
//        retail[i],      // 13
//        nobatch[i],     // 14 - No. Batch
//        tglexp[i]       // 15 - Tgl. Exp
//    });
//        }
//        try{
//            ps = koneksi.prepareStatement(
//                "select tokobarang.kode_brng, tokobarang.nama_brng, tokobarang.kode_sat, " +
//                "       tokobarang.h_beli, tokobarang.expire " +   // <-- tambah expire
//                "from tokobarang " +
//                "where tokobarang.status='1' and tokobarang.kode_brng like ? or " +
//                "      tokobarang.status='1' and tokobarang.nama_brng like ? or " +
//                "      tokobarang.status='1' and tokobarang.jenis like ? " +
//                "order by tokobarang.nama_brng"
//            );
//            try{   
//                ps.setString(1,"%"+TCari.getText().trim()+"%");
//                ps.setString(2,"%"+TCari.getText().trim()+"%");
//                ps.setString(3,"%"+TCari.getText().trim()+"%");
//                rs=ps.executeQuery();
//                while(rs.next()){
//                    //tabMode.addRow(new Object[]{"",rs.getString("kode_brng"),rs.getString("nama_brng"),rs.getString("kode_sat"),false,rs.getDouble("h_beli"),0,0,0,0,0,0,0,0});
//               tabMode.addRow(new Object[]{
//        "",                          // 0 Jml
//        rs.getString("kode_brng"),   // 1
//        rs.getString("nama_brng"),   // 2
//        rs.getString("kode_sat"),    // 3
//        false,                       // 4 G
//        rs.getDouble("h_beli"),      // 5 Harga
//        0.0,                         // 6 Subtotal
//        0.0,                         // 7 Disk(%)
//        0.0,                         // 8 Diskon(Rp)
//        0.0,                         // 9 Total
//        0.0,                         // 10 Dasar
//        0.0,                         // 11 Distributor
//        0.0,                         // 12 Grosir
//        0.0,                         // 13 Retail
//        "",                          // 14 No. Batch (kosong, diisi manual)
//        rs.getString("expire")       // 15 Tgl. Exp dari master
//    });
//                }   
//            }catch(Exception e){
//                System.out.println(e);
//            }finally{
//                if(rs!=null){
//                    rs.close();
//                }
//                if(ps!=null){
//                    ps.close();
//                }
//            }              
//        }catch(SQLException e){
//            System.out.println("Notifikasi : "+e);
//        }
//        
//    }
//
//    private void getData(){
//        row=tbDokter.getSelectedRow();
//        if(row!= -1){
//            if(!tbDokter.getValueAt(row,0).toString().equals("")){
//                try {
//                    if(Valid.SetAngka(tbDokter.getValueAt(row,0).toString())>0){
//                        tbDokter.setValueAt(Double.parseDouble(tbDokter.getValueAt(row,0).toString())*Double.parseDouble(tbDokter.getValueAt(row,5).toString()), row,6);                
//                        tbDokter.setValueAt(Double.parseDouble(tbDokter.getValueAt(row,6).toString())-Double.parseDouble(tbDokter.getValueAt(row,8).toString()), row,9);           
//                    }
//                } catch (Exception e) {
//                    tbDokter.setValueAt("",row,0);
//                    tbDokter.setValueAt(0,row,6);   
//                    tbDokter.setValueAt(0,row,7);   
//                    tbDokter.setValueAt(0,row,8);                
//                    tbDokter.setValueAt(0,row,9); 
//                    tbDokter.setValueAt("",row,14);
//                }    
//            }else{
//                tbDokter.setValueAt(0,row,6);   
//                tbDokter.setValueAt(0,row,7);   
//                tbDokter.setValueAt(0,row,8);                
//                tbDokter.setValueAt(0,row,9); 
//                tbDokter.setValueAt("",row,14); 
//            }             
//        }
//        ttl=0;sbttl=0;ttldisk=0;
//        y=0;w=0;
//        meterai=0;
//        if(!Meterai.getText().equals("")){
//            meterai=Double.parseDouble(Meterai.getText());
//        }
//        
//        jml=tbDokter.getRowCount();
//        for(i=0;i<jml;i++){                 
//            try {
//                w=Double.parseDouble(tbDokter.getValueAt(i,6).toString());                
//            }catch (Exception e) {
//                w=0;                
//            }
//            sbttl=sbttl+w;                
//            try {
//                y=Double.parseDouble(tbDokter.getValueAt(i,8).toString());                
//            }catch (Exception e) {
//                y=0;                
//            }
//            ttldisk=ttldisk+y;
//        }
//        LSubtotal.setText(Valid.SetAngka(sbttl));
//        LPotongan.setText(Valid.SetAngka(ttldisk));
//        ttl=sbttl-ttldisk;
//        LTotal2.setText(Valid.SetAngka(ttl));
//        ppn=0;
//        if(!tppn.getText().equals("")){
//            ppn=Math.round((Double.parseDouble(tppn.getText())/100) *(ttl));
//            LPpn.setText(Valid.SetAngka(ppn));
//            LTagiha.setText(Valid.SetAngka(ttl+ppn+meterai));
//        }
//        
//    }
    
    public void isCek(){
        autoNomor();
        TCari.requestFocus();
        tppn.setText("0");
        Meterai.setText("0");
        if(akses.getjml2()>=1){
            kdptg.setEditable(false);
            btnPetugas.setEnabled(false);
            kdptg.setText(akses.getkode());
            BtnSimpan.setEnabled(akses.gettoko_penerimaan_barang());
            BtnTambah.setEnabled(akses.gettoko_barang());
            nmptg.setText(form.petugas.tampil3(kdptg.getText()));
        }        
    }
    
    private void autoNomor() {
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(tokopemesanan.no_faktur,3),signed)),0) from tokopemesanan where tokopemesanan.tgl_pesan='"+Valid.SetTgl(TglPesan.getSelectedItem()+"")+"'","PNT"+TglPesan.getSelectedItem().toString().substring(6,10)+TglPesan.getSelectedItem().toString().substring(3,5)+TglPesan.getSelectedItem().toString().substring(0,2),3,NoFaktur); 
    }

    public void tampil(String noorder) {
        NoOrder.setText(noorder);
        kdsup.setText(Sequel.cariIsi("select toko_surat_pemesanan.kode_suplier from toko_surat_pemesanan where toko_surat_pemesanan.no_pemesanan=?",noorder));
        nmsup.setText(Sequel.cariIsi("select tokosuplier.nama_suplier from tokosuplier where tokosuplier.kode_suplier=?",kdsup.getText()));
        meterai=Sequel.cariIsiAngka("select toko_surat_pemesanan.meterai from toko_surat_pemesanan where toko_surat_pemesanan.no_pemesanan=?",noorder);
        ppn=Sequel.cariIsiAngka("select toko_surat_pemesanan.ppn from toko_surat_pemesanan where toko_surat_pemesanan.no_pemesanan=?",noorder);
        Meterai.setText(Valid.SetAngka2(meterai));
        try{
            Valid.tabelKosong(tabMode);
            ps=koneksi.prepareStatement(
                "select toko_detail_surat_pemesanan.kode_brng,tokobarang.nama_brng,(toko_detail_surat_pemesanan.total/toko_detail_surat_pemesanan.jumlah) as dasar, "+
                "toko_detail_surat_pemesanan.kode_sat,toko_detail_surat_pemesanan.jumlah,toko_detail_surat_pemesanan.h_pesan, "+
                "toko_detail_surat_pemesanan.subtotal,toko_detail_surat_pemesanan.dis,toko_detail_surat_pemesanan.besardis,toko_detail_surat_pemesanan.total "+
                "from toko_detail_surat_pemesanan inner join tokobarang "+
                " on toko_detail_surat_pemesanan.kode_brng=tokobarang.kode_brng "+
                " where toko_detail_surat_pemesanan.no_pemesanan=? order by tokobarang.nama_brng");
            try {
                ps.setString(1,noorder);
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("jumlah"),rs.getString("kode_brng"),rs.getString("nama_brng"),rs.getString("kode_sat"),true,rs.getDouble("h_pesan"),
                        rs.getDouble("subtotal"),rs.getDouble("dis"),rs.getDouble("besardis"),rs.getDouble("total"),rs.getString("dasar"),0,0,0
                    });
                } 
                getData();
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }         
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }
    
    private void setKonversi(int baris){
        try{
            if(Valid.SetAngka(tbDokter.getValueAt(baris,0).toString())>0){
                // FIX BUG #1: gunakan Boolean.TRUE.equals() untuk hindari NullPointerException
                if(Boolean.TRUE.equals(tbDokter.getValueAt(baris,4))){
                    try {
                        rs=koneksi.prepareStatement("select * from tokosetharga").executeQuery();
                        if(rs.next()){
                            hargappn=0;
                            try {
                                hargappn=Double.parseDouble(tbDokter.getValueAt(baris,5).toString())+((Double.parseDouble(tppn.getText())/100)*Double.parseDouble(tbDokter.getValueAt(baris,5).toString()));
                            } catch (Exception e) {
                                hargappn=0;
                            }

                            try {
                                hargadiskon=(Double.parseDouble(tbDokter.getValueAt(baris,9).toString())/Double.parseDouble(tbDokter.getValueAt(baris,0).toString()))+((Double.parseDouble(tppn.getText())/100)*(Double.parseDouble(tbDokter.getValueAt(baris,9).toString())/Double.parseDouble(tbDokter.getValueAt(baris,0).toString())));
                            } catch (Exception e) {
                                hargadiskon=0;
                            }
                            tbDokter.setValueAt(Math.round(hargadiskon),baris,10);
                            tbDokter.setValueAt(Valid.roundUp(hargappn+(hargappn*(rs.getDouble("distributor")/100)),100),baris,11);
                            tbDokter.setValueAt(Valid.roundUp(hargappn+(hargappn*(rs.getDouble("grosir")/100)),100),baris,12);
                            tbDokter.setValueAt(Valid.roundUp(hargappn+(hargappn*(rs.getDouble("retail")/100)),100),baris,13);
                        }else{
                            tbDokter.setValueAt(false,baris,4);
                            JOptionPane.showMessageDialog(null,
                                "Pengaturan harga umum masih kosong!\n\n" +
                                "Langkah yang harus dilakukan:\n" +
                                "1. Buka menu Master → Pengaturan Harga Toko\n" +
                                "2. Isi persentase markup Distributor, Grosir, dan Retail\n" +
                                "3. Simpan pengaturan, lalu coba lagi.");
                            TCari.requestFocus();
                            fungsi.TelegramNotifier.sendError(
                                "Pemesanan Toko", akses.getkode(),
                                "tokosetharga kosong - konversi harga gagal",
                                "Baris ke-" + (baris+1) + " | Kode: " + s(tbDokter.getValueAt(baris,1))
                            );
                        }
                    } catch (Exception e) {
                        System.out.println("setKonversi error: " + e);
                        fungsi.TelegramNotifier.sendError(
                            "Pemesanan Toko", akses.getkode(),
                            "Exception di setKonversi: " + e.getMessage(),
                            "Kode: " + s(tbDokter.getValueAt(baris,1))
                        );
                    } finally{
                        if(rs!=null){
                            try { rs.close(); } catch(Exception ex){}
                        }
                    }
                }
            }
        }catch(Exception e){
            System.out.println("setKonversi outer error: " + e);
        }
    }
//    private double safeNum(JTextField tf){ 
//    try { return Double.parseDouble(tf.getText().replace(",","").trim()); } 
//    catch(Exception e){ return 0; } 
//}
//private double roundUp(double v, double step){
//    if (step <= 0) return v;
//    return Math.ceil(v/step)*step;
//}
//
//private void hitungDariHBeli() {
//    double hBeliBox  = safeNum(beli);        // kolom G: harga beli per BOX
//    double isiBox    = Math.max(1, safeNum(Isi));        // strip per BOX
//    double kapas     = Math.max(1, safeNum(Kapasitas));  // tablet per STRIP
//    double pctStrip  = safeNum(percensatuanbesar);       // % grosir (per strip)
//    double pctTablet = safeNum(percensatuankecil);       // % retail (per tablet)
//
//    if (hBeliBox <= 0){ setNum(grosir,0); setNum(retail,0); return; }
//
//    double beliStrip  = hBeliBox / isiBox;                 // → harus ~ratus ribu
//    double beliTablet = hBeliBox / (isiBox * kapas);

//    final double STEP = 100;  // atau 500/1000 sesuai kebijakan
//
//    double jualGrosir  = roundUp(beliStrip  * (1 + pctStrip/100.0), STEP);
//    double jualRetail  = roundUp(beliTablet * (1 + pctTablet/100.0), STEP);
//
//    setNum(grosir, jualGrosir);
//    setNum(retail, jualRetail);
//}
// ==== index kolom (0-based) ====
private static final int COL_JML   = 0;
private static final int COL_KODE  = 1;
private static final int COL_NAMA  = 2;
private static final int COL_SAT   = 3;
private static final int COL_GFLAG = 4;
private static final int COL_HARGA = 5;
private static final int COL_SUB   = 6;
private static final int COL_DISCP = 7;
private static final int COL_DISC  = 8;
private static final int COL_TOTAL = 9;
private static final int COL_DASAR = 10;  // harga beli per BOX
private static final int COL_DIST  = 11;  // harga BOX (packaging)
private static final int COL_GROS  = 12;  // harga STRIP
private static final int COL_RETL  = 13;  // harga PCS/Tablet

private double numObj(Object v){
    if (v==null) return 0;
    try { return Double.parseDouble(v.toString().replace(",","").trim()); }
    catch(Exception e){ return 0; }
}
private double numTF(javax.swing.text.JTextComponent tf){
    if (tf==null) return 0;
    return numObj(tf.getText());
}
private void setVal(int row,int col,double v){ tabMode.setValueAt(v,row,col); }
private boolean boolAt(int row,int col){
    Object v = tabMode.getValueAt(row,col);
    return (v instanceof Boolean) ? (Boolean)v : "true".equalsIgnoreCase(String.valueOf(v));
}
private double roundUp(double v, double step){
    if (step<=0) return v;
    return Math.ceil(v/step)*step;
}

/** Hitung & pilih harga sesuai satuan jika G dicentang, lalu hitung subtotal/diskon/total */
private void recalcRowByG(int row){
    if (row < 0 || row >= tabMode.getRowCount()) return;

    final double STEP = 100;

    String kodeBrg  = String.valueOf(tabMode.getValueAt(row, COL_KODE));
    double hBeliBox = numObj(tabMode.getValueAt(row, COL_DASAR)); // atau tarik lagi dari DB bila perlu
    int isi   = Math.max(1, cariI("SELECT isi FROM tokobarang WHERE kode_brng=?", kodeBrg));
    int kap   = Math.max(1, cariI("SELECT kapasitas FROM tokobarang WHERE kode_brng=?", kodeBrg));

    // infer persen dari harga yg tersimpan di tokobarang
    PercentSet ps = inferPercentsFromTokobarang(kodeBrg);

    if (boolAt(row, COL_GFLAG) && hBeliBox > 0){
        double beliStrip  = hBeliBox / isi;
        double beliPcs    = hBeliBox / ((double)isi * kap);

        double hargaBox   = roundUp(beliStrip * isi * (1 + ps.pPackaging/100.0), STEP); // sama dengan hBeliBox*(1+pPackaging)
        double hargaStrip = roundUp(beliStrip       * (1 + ps.pStrip    /100.0), STEP);
        double hargaPcs   = roundUp(beliPcs         * (1 + ps.pTablet   /100.0), STEP);

        setVal(row, COL_DIST, hargaBox);
        setVal(row, COL_GROS, hargaStrip);
        setVal(row, COL_RETL, hargaPcs);

        // pilih harga transaksi berdasar Satuan
        String sat = String.valueOf(tabMode.getValueAt(row, COL_SAT)).toUpperCase();
        double harga =
            (sat.contains("BOX") || sat.contains("PACK") || sat.contains("PAK")) ? hargaBox :
            (sat.contains("STR") || sat.contains("LBR")  || sat.contains("SAC")) ? hargaStrip :
                                                                                   hargaPcs;
        setVal(row, COL_HARGA, harga);
    }

    // subtotal/diskon/total
    double qty   = numObj(tabMode.getValueAt(row, COL_JML));
    double harga = numObj(tabMode.getValueAt(row, COL_HARGA));
    double dpct  = numObj(tabMode.getValueAt(row, COL_DISCP));
    double sub   = qty * harga;
    double drp   = sub * (dpct/100.0);
    setVal(row, COL_SUB,  sub);
    setVal(row, COL_DISC, drp);
    setVal(row, COL_TOTAL, sub - drp);
}

/**
 * Saat G dicentang atau Enter di kolom Harga (col 5):
 * - Update dasar = h_beli baru
 * - Panggil konversiDariDistributor untuk hitung grosir & retail
 */
private void konversiHargaBaru(int baris) {
    if (baris < 0 || baris >= tabMode.getRowCount()) return;
    if (!Boolean.TRUE.equals(tbDokter.getValueAt(baris, COL_GFLAG))) return;

    double hBeli = numObj(tbDokter.getValueAt(baris, COL_HARGA));
    if (hBeli <= 0) return;

    // Update dasar = h_beli baru
    sedangUpdateHarga = true;
    try {
        setVal(baris, COL_DASAR, hBeli);
    } finally {
        sedangUpdateHarga = false;
    }

    // Hitung grosir & retail dari distributor yang ada di col 11
    konversiDariDistributor(baris);
}

/**
 * Hitung grosir & retail dari harga distributor (harga per BOX).
 * Dipanggil saat Enter di kolom Distributor (col 11) atau setelah konversiHargaBaru.
 *
 *   grosir = roundUp(distributor / isi,       100)  → per STRIP
 *   retail = roundUp(distributor / kapasitas, 100)  → per TABLET
 *
 * isi & kapasitas diambil dari tokobarang.
 */
private void konversiDariDistributor(int baris) {
    if (baris < 0 || baris >= tabMode.getRowCount()) return;
    if (!Boolean.TRUE.equals(tbDokter.getValueAt(baris, COL_GFLAG))) return;

    double dist = numObj(tbDokter.getValueAt(baris, COL_DIST));
    if (dist <= 0) return;

    String kode = s(tbDokter.getValueAt(baris, COL_KODE));
    if (kode.isEmpty()) return;

    PreparedStatement psK = null;
    ResultSet rsK = null;
    try {
        psK = koneksi.prepareStatement(
            "SELECT isi, kapasitas FROM tokobarang WHERE kode_brng=? LIMIT 1"
        );
        psK.setString(1, kode);
        rsK = psK.executeQuery();
        if (!rsK.next()) return;

        int isi = Math.max(1, rsK.getInt("isi"));
        int kap = Math.max(1, rsK.getInt("kapasitas")); // TOTAL tablet per BOX

        double hargaGros = roundUp(dist / isi, 100); // per STRIP
        double hargaRetl = roundUp(dist / kap, 100); // per TABLET

        sedangUpdateHarga = true;
        try {
            setVal(baris, COL_GROS, hargaGros);
            setVal(baris, COL_RETL, hargaRetl);
        } finally {
            sedangUpdateHarga = false;
        }

    } catch (Exception e) {
        System.out.println("konversiDariDistributor: " + e);
        fungsi.TelegramNotifier.sendError(
            "Pemesanan Toko", akses.getkode(),
            "Exception di konversiDariDistributor: " + e.getMessage(),
            "Kode: " + kode
        );
    } finally {
        try { if (rsK != null) rsK.close(); } catch (Exception ex) {}
        try { if (psK != null) psK.close(); } catch (Exception ex) {}
    }
}

/**
 * Tulis log transaksi ke file lokal sebelum simpan.
 * File: logs/pemesanan_toko_YYYYMMDD.txt
 */
private void writeLog() {
    try {
        String tgl = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String jam = new SimpleDateFormat("HH:mm:ss").format(new Date());
        File dir = new File("logs");
        if (!dir.exists()) dir.mkdirs();
        File logFile = new File(dir, "pemesanan_toko_" + tgl + ".txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
            bw.write("==========================================================");
            bw.newLine();
            bw.write("[" + jam + "] SIMPAN PEMESANAN TOKO");
            bw.newLine();
            bw.write("No.Faktur : " + NoFaktur.getText());
            bw.newLine();
            bw.write("Supplier  : " + kdsup.getText() + " - " + nmsup.getText());
            bw.newLine();
            bw.write("Petugas   : " + kdptg.getText() + " - " + nmptg.getText());
            bw.newLine();
            bw.write("No.Order  : " + NoOrder.getText());
            bw.newLine();
            bw.write("PPN %     : " + tppn.getText());
            bw.newLine();
            bw.write("Meterai   : " + Meterai.getText());
            bw.newLine();
            bw.write("Total     : " + ttl);
            bw.newLine();
            bw.write("----------------------------------------------------------");
            bw.newLine();

            // Header tabel
            bw.write(String.format("%-3s %-8s %-30s %-5s %-5s %10s %10s %10s %10s %10s %10s",
                "G", "Kode", "Nama", "Sat", "Jml",
                "H.Beli", "Dasar", "Distributor", "Grosir", "Retail", "Total"));
            bw.newLine();

            int n = tabMode.getRowCount();
            for (int r = 0; r < n; r++) {
                double qty = numObj(tabMode.getValueAt(r, COL_JML));
                if (qty <= 0) continue;
                boolean cekG = Boolean.TRUE.equals(tabMode.getValueAt(r, COL_GFLAG));
                String gStr   = cekG ? "[G]" : "   ";
                String kode   = s(tabMode.getValueAt(r, COL_KODE));
                String nama   = s(tabMode.getValueAt(r, COL_NAMA));
                String sat    = s(tabMode.getValueAt(r, COL_SAT));
                double hbeli  = numObj(tabMode.getValueAt(r, COL_HARGA));
                double dasar  = numObj(tabMode.getValueAt(r, COL_DASAR));
                double dist   = numObj(tabMode.getValueAt(r, COL_DIST));
                double gros   = numObj(tabMode.getValueAt(r, COL_GROS));
                double retl   = numObj(tabMode.getValueAt(r, COL_RETL));
                double total  = numObj(tabMode.getValueAt(r, COL_TOTAL));

                System.out.printf("[LOG] %s %-8s %-30s %-5s qty=%.0f hbeli=%.0f dasar=%.0f dist=%.0f gros=%.0f retl=%.0f total=%.0f%n",
                    gStr, kode, nama.length()>30?nama.substring(0,30):nama,
                    sat, qty, hbeli, dasar, dist, gros, retl, total);

                bw.write(String.format("%-3s %-8s %-30s %-5s %5.0f %10.0f %10.0f %10.0f %10.0f %10.0f %10.0f",
                    gStr, kode, nama.length()>30?nama.substring(0,30):nama,
                    sat, qty, hbeli, dasar, dist, gros, retl, total));
                bw.newLine();
            }
            bw.write("==========================================================");
            bw.newLine();
            bw.newLine();
        }
        System.out.println("[LOG] Log tersimpan: " + logFile.getAbsolutePath());

        // Kirim ringkasan transaksi ke Telegram
        int totalG = 0;
        StringBuilder detailG = new StringBuilder();
        for (int r = 0; r < tabMode.getRowCount(); r++) {
            if (Boolean.TRUE.equals(tabMode.getValueAt(r, COL_GFLAG)) &&
                numObj(tabMode.getValueAt(r, COL_JML)) > 0) {
                totalG++;
                String nm   = s(tabMode.getValueAt(r, COL_NAMA));
                double dist = numObj(tabMode.getValueAt(r, COL_DIST));
                double gros = numObj(tabMode.getValueAt(r, COL_GROS));
                double retl = numObj(tabMode.getValueAt(r, COL_RETL));
                double dasr = numObj(tabMode.getValueAt(r, COL_DASAR));
                detailG.append("\n  • ").append(nm.length() > 25 ? nm.substring(0, 25) : nm)
                       .append("\n    Dasar: ").append(String.format("%,.0f", dasr))
                       .append(" | Dist: ").append(String.format("%,.0f", dist))
                       .append(" | Grosir: ").append(String.format("%,.0f", gros))
                       .append(" | Retail: ").append(String.format("%,.0f", retl));
            }
        }
        String infoMsg = "Faktur: " + NoFaktur.getText()
            + " | Supplier: " + nmsup.getText()
            + " | Total: Rp " + String.format("%,.0f", ttl)
            + " | Item update harga: " + totalG
            + (detailG.length() > 0 ? "\n\nHarga diupdate:" + detailG.toString() : "");
        fungsi.TelegramNotifier.sendInfo("Pemesanan Toko", akses.getkode(), infoMsg);

    } catch (Exception e) {
        System.out.println("[LOG ERROR] writeLog: " + e);
        fungsi.TelegramNotifier.sendError(
            "Pemesanan Toko", akses.getkode(),
            "Gagal menulis log transaksi: " + e.getMessage(),
            "Faktur: " + NoFaktur.getText()
        );
    }
}

private double safeDiv(double a, double b){ return (b==0)? 0 : a/b; }
private double clampPos(double v){ return Double.isFinite(v) ? v : 0; }

private double cariD(String sql, String p){
    // ganti ke helper double kamu (mis. Sequel.cariIsiAngka)
    String s = Sequel.cariIsi(sql, p);
    try { return Double.parseDouble(s); } catch(Exception e){ return 0; }
}

private int cariI(String sql, String p){
    return Sequel.cariInteger(sql, p);
}

/** Hitung persen dari DB tokobarang untuk kode tertentu. */
private PercentSet inferPercentsFromTokobarang(String kodeBrg){
    double hBeli = cariD("SELECT h_beli FROM tokobarang WHERE kode_brng=?", kodeBrg);
    double dist  = cariD("SELECT distributor FROM tokobarang WHERE kode_brng=?", kodeBrg);
    double gros  = cariD("SELECT grosir FROM tokobarang WHERE kode_brng=?", kodeBrg);
    double ret   = cariD("SELECT retail FROM tokobarang WHERE kode_brng=?", kodeBrg);
    int isi      = Math.max(1, cariI("SELECT isi FROM tokobarang WHERE kode_brng=?", kodeBrg));
    int kapas    = Math.max(1, cariI("SELECT kapasitas FROM tokobarang WHERE kode_brng=?", kodeBrg));

    double beliPerStrip = safeDiv(hBeli, isi);
    double beliPerPcs   = safeDiv(hBeli, (double)isi * kapas);

    double pPackaging = clampPos( (safeDiv(dist, hBeli)       - 1.0) * 100.0 );
    double pStrip     = clampPos( (safeDiv(gros, beliPerStrip) - 1.0) * 100.0 );
    double pTablet    = clampPos( (safeDiv(ret,  beliPerPcs)   - 1.0) * 100.0 );

    return new PercentSet(pPackaging, pStrip, pTablet, 0); // pResep kalau ada kolomnya
}

private static class PercentSet {
    final double pPackaging, pStrip, pTablet, pResep;
    PercentSet(double a,double b,double c,double d){ pPackaging=a; pStrip=b; pTablet=c; pResep=d; }
}

//private String s(Object v) {
//    return (v == null) ? "" : v.toString().trim();
//}

private void logSimpan(String msg) {
    System.out.println(msg);
    try {
        File dir = new File("logs");
        if (!dir.exists()) dir.mkdirs();
        String tgl = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File logFile = new File("logs/pemesanan_" + tgl + ".log");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
            String waktu = new SimpleDateFormat("HH:mm:ss").format(new Date());
            bw.write("[" + waktu + "] " + msg);
            bw.newLine();
        }
    } catch (Exception ex) {
        System.out.println("logSimpan error: " + ex.getMessage());
    }
}

private String normalizeMysqlDate(Object v) {
    if (v == null) return null;

    String x = v.toString().trim();
    if (x.equals("") || x.equalsIgnoreCase("null")) return null;

    // kalau kebawa timestamp "yyyy-MM-dd HH:mm:ss"
    if (x.length() >= 19 && x.charAt(4) == '-' && x.charAt(7) == '-') {
        x = x.substring(0, 10);
    } else if (x.length() >= 10 && x.charAt(4) == '-' && x.charAt(7) == '-') {
        x = x.substring(0, 10);
    }

    // dd/MM/yyyy -> yyyy-MM-dd
    if (x.matches("\\d{2}/\\d{2}/\\d{4}")) {
        String[] p = x.split("/");
        return p[2] + "-" + p[1] + "-" + p[0];
    }

    // yyyy-MM-dd
    if (x.matches("\\d{4}-\\d{2}-\\d{2}")) {
        return x;
    }

    // format lain dianggap kosong
    return null;
}

private boolean insertDetailPesan(int row) {
    PreparedStatement ps = null;
    try {
        String noFaktur = NoFaktur.getText().trim();
        String kodeBrg  = s(tbDokter.getValueAt(row, 1));
        String kodeSat  = s(tbDokter.getValueAt(row, 3));

        double jumlah   = Double.parseDouble(tbDokter.getValueAt(row, 0).toString());
        double harga    = Double.parseDouble(tbDokter.getValueAt(row, 5).toString());
        double subtotal = Double.parseDouble(tbDokter.getValueAt(row, 6).toString());
        double dis      = Double.parseDouble(tbDokter.getValueAt(row, 7).toString());
        double besardis = Double.parseDouble(tbDokter.getValueAt(row, 8).toString());
        double total    = Double.parseDouble(tbDokter.getValueAt(row, 9).toString());

        String batch    = s(tbDokter.getValueAt(row, 14));
        String expParam = normalizeMysqlDate(tbDokter.getValueAt(row, 15)); // ✅ bisa null

        ps = koneksi.prepareStatement(
            "INSERT INTO toko_detail_pesan " +
            "(no_faktur,kode_brng,kode_sat,jumlah,harga,subtotal,dis,besardis,total,no_batch,tgl_expire) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?)"
        );

        ps.setString(1, noFaktur);
        ps.setString(2, kodeBrg);
        ps.setString(3, kodeSat);
        ps.setDouble(4, jumlah);
        ps.setDouble(5, harga);
        ps.setDouble(6, subtotal);
        ps.setDouble(7, dis);
        ps.setDouble(8, besardis);
        ps.setDouble(9, total);
        ps.setString(10, batch);

        // ✅ ini inti fix: kosong -> NULL
        if (expParam == null) {
            ps.setNull(11, java.sql.Types.DATE);
        } else {
            ps.setDate(11, java.sql.Date.valueOf(expParam));
        }

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.out.println("Notifikasi : " + e);
        return false;
    } finally {
        try { if (ps != null) ps.close(); } catch (Exception ex) {}
    }
}
    
}
