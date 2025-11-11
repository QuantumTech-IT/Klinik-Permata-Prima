package toko;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import keuangan.Jurnal;
import inventory.DlgPeresepanDokter;
import java.awt.Component;
import java.awt.Dialog;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;


public class TokoPenjualan extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private riwayattoko Trackbarang=new riwayattoko();
    private Jurnal jur=new Jurnal();
    private Connection koneksi=koneksiDB.condb();
    private double ttl=0,ttlhpp=0,y=0,z=0,stokbarang=0,bayar=0,total=0,ppn=0,besarppn=0,tagihanppn=0,ongkir=0,nilaippn=0;
    private int jml=0,i=0,row,kolom=0,reply,index;
    private String Penjualan_Toko="",HPP_Barang_Toko="",Persediaan_Barang_Toko="";
    private PreparedStatement ps;
    private ResultSet rs;
    private String[] kodebarang,namabarang,kategori,satuan;
    private double[] harga,hbeli,jumlah,subtotal,diskon,besardiskon,totaljual,tambahan,stok;
    private WarnaTable2 warna=new WarnaTable2();
    private String notatoko="No",kode_akun_bayar="";
    private boolean sukses=true;
    private TokoCariPenjualan carijual=new TokoCariPenjualan(null,false);
    private boolean resepPanelInited = false;
    private javax.swing.Timer autoRefreshTimer;
private javax.swing.JCheckBox cbAutoRefresh;
private javax.swing.JComboBox<Integer> cbIntervalDetik; // opsional: pilih interval
private String currentNoResep; // sudah ada sebelumnya kalau pakai mode noResep
//private boolean resepPanelInited = false; // guard init sekali
//    DlgPeresepanDokter dlgResep = new DlgPeresepanDokter(null, false);
//    TokoPenjualan dlgToko = new TokoPenjualan(null, false);
    private String hpptoko="";
    private File file;
    private FileWriter fileWriter;
    private String iyem;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private FileReader myObj;
   private DaoBarang daoBarang;
   //private javax.swing.Timer autoRefreshTimer;
   // index kolom sesuai tabMode-mu
private static final int COL_JML     = 0;
private static final int COL_KODE    = 1;  // "Kode Barang"
private static final int COL_SATUAN  = 4;  // "Satuan"
private static final int COL_HARGA   = 5;  // "Harga(Rp)"
private static final int COL_STOK    = 11; // "Stok"

// === di deklarasi kelas DlgTokoPenjualan ===
private javax.swing.JPanel panelResepToko;
private javax.swing.JTable tbResepToko;
private javax.swing.table.DefaultTableModel tmResepToko;
private javax.swing.JScrollPane spResepToko;
private enum ResepMode { HEADER, DETAIL }
private ResepMode resepMode = ResepMode.HEADER;
private String noResepTerpilih = null;


//private String currentNoResep = null; // jika nanti butuh refresh detail

 // "Stok"
    private String String;
    //private String currentNoResep;

    

    /** Creates new form DlgProgramStudi
     * @param parent
     * @param modal */
    public TokoPenjualan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        
//loadResepHeader(new java.util.Date()); 
        initPanelResepToko();
        initResepHeaderTable();
        
        initPanelResepMasterDetail();
        java.lang.String noResep = null;
        loadResepToko(noResep);
        loadResepDetail(noResep);
       // loadResepTokoByTanggal(new java.util.Date());
        
loadResepHeaderByTanggal(new java.util.Date());
System.out.println("[TOKO] columns=" + tbResepToko.getModel().getColumnCount()); // harus 5
        
        
        
        assert tbResepToko.getModel() == tmResepToko : "[TOKO] Model JTable bukan tmResepToko";//loadResepToko(NoResep);
       
        
        DokterCombo.setVisible(false);
        label11.setVisible(false);
        daoBarang = new DaoBarang(koneksiDB.condb());
        
        
        tabMode=new DefaultTableModel(null,new Object[]{
                "Jml","Kode Barang","Nama Barang","Kategori","Satuan","Harga(Rp)",
                "Subtotal(Rp)","Ptg(%)","Ptg(Rp)","Tuslah(Rp)","Total(Rp)","Stok","H Beli"
        }){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==4)||(colIndex==7)||(colIndex==8)||(colIndex==9)) {
                    a=true;
                }
                return a;
            }
            
            Class[] types = new Class[] {
                java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                java.lang.Double.class,java.lang.Double.class,java.lang.Double.class,java.lang.Double.class,java.lang.Double.class,
                java.lang.Double.class,java.lang.Double.class,java.lang.Double.class
            };
            @Override
            public Class getColumnClass(int columnIndex) {
               return types [columnIndex];
            }
        };
        tbObat.setModel(tabMode);
        installSatuanEditor();

        tbObat.setPreferredScrollableViewportSize(new Dimension(800,800));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 13; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(42);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(160);
            }else if(i==3){
                column.setPreferredWidth(75);
            }else if(i==4){
                column.setPreferredWidth(45);
            }else if(i==5){
                column.setPreferredWidth(75);
            }else if(i==6){
                column.setPreferredWidth(75);
            }else if(i==7){
                column.setPreferredWidth(45);
            }else if(i==8){
                column.setPreferredWidth(60);
            }else if(i==9){
                column.setPreferredWidth(60);
            }else if(i==10){
                column.setPreferredWidth(80);
            }else if(i==11){
                column.setPreferredWidth(35);
            }else if(i==12){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        warna.kolom=0;
        tbObat.setDefaultRenderer(Object.class,warna);

        
        NoNota.setDocument(new batasInput((byte)15).getKata(NoNota));
        kdmem.setDocument(new batasInput((byte)10).getKata(kdmem));
        catatan.setDocument(new batasInput((byte)40).getKata(catatan));
        Bayar.setDocument(new batasInput((byte)14).getOnlyAngka(Bayar));
        Ongkir.setDocument(new batasInput((byte)14).getOnlyAngka(Ongkir));     
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
        tbResepHeader.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
    @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c){
        Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
        int mr = t.convertRowIndexToModel(r);
        String st = String.valueOf(tmResepHeader.getValueAt(mr, 5)); // kolom "Status"
        if ("selesai".equalsIgnoreCase(st)) {
            comp.setForeground(java.awt.Color.GRAY);
        } else if ("diproses".equalsIgnoreCase(st)) {
            comp.setForeground(new java.awt.Color(0,102,204));
        } else {
            comp.setForeground(java.awt.Color.BLACK);
        }
        return comp;
    }
});
        javax.swing.JButton btnRefresh = new javax.swing.JButton("Refresh");
        btnRefresh.addActionListener(e -> loadResepTokoByTanggal(new java.util.Date()));
        panelisi3.add(btnRefresh);
        Bayar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {isKembali();}
            @Override
            public void removeUpdate(DocumentEvent e) {isKembali();}
            @Override
            public void changedUpdate(DocumentEvent e) {isKembali();}
        });
        
        TCari.requestFocus();
        
        carijual.member.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("Penjualan")){
                    if(carijual.member.getTable().getSelectedRow()!= -1){                   
                        kdmem.setText(carijual.member.getTable().getValueAt(carijual.member.getTable().getSelectedRow(),0).toString());
                        nmmem.setText(carijual.member.getTable().getValueAt(carijual.member.getTable().getSelectedRow(),1).toString());
                    }  
                    kdmem.requestFocus();
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
        
        carijual.member.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(akses.getform().equals("Penjualan")){
                    if(e.getKeyCode()==KeyEvent.VK_SPACE){
                        carijual.member.dispose();
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });  
        
        tbResepHeader.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) { // double click buka popup
            int viewRow = tbResepHeader.getSelectedRow();
            if (viewRow < 0) return;
            int modelRow = tbResepHeader.convertRowIndexToModel(viewRow);
            String noResep = String.valueOf(tmResepHeader.getValueAt(modelRow, 0));
            showResepDetailDialog(noResep);
        }
    }
});
        carijual.petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("Penjualan")){
                    if(carijual.petugas.getTable().getSelectedRow()!= -1){                   
                        kdptg.setText(carijual.petugas.getTable().getValueAt(carijual.petugas.getTable().getSelectedRow(),0).toString());
                        nmptg.setText(carijual.petugas.getTable().getValueAt(carijual.petugas.getTable().getSelectedRow(),1).toString());
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
        
        try {
            notatoko=Sequel.cariIsi("select cetaknotasimpantoko from set_nota");
            if(notatoko.equals("")){
                notatoko="No";
            }
        } catch (Exception e) {
            notatoko="No"; 
        }
        
        try {
            hpptoko=koneksiDB.HPPTOKO();
        } catch (Exception e) {
            hpptoko="dasar";
        }
        
        try {
            ps=koneksi.prepareStatement("select Penjualan_Toko,HPP_Barang_Toko,Persediaan_Barang_Toko from set_akun");
            try {
                rs=ps.executeQuery();
                if(rs.next()){
                    Penjualan_Toko=rs.getString("Penjualan_Toko");
                    HPP_Barang_Toko=rs.getString("HPP_Barang_Toko");
                    Persediaan_Barang_Toko=rs.getString("Persediaan_Barang_Toko");
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }
        
        
//        Jenisjual.addActionListener(e -> {
//    int row = tbObat.getSelectedRow();
//    if (row >= 0) {
//        try {
//            applyJenisJualKeBaris(row);
//        } catch (SQLException ex) {
//            // log + info ke user
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this,
//                "Gagal menerapkan jenis jual:\n" + ex.getMessage(),
//                "Kesalahan DB", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//});
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
        jPanel1 = new javax.swing.JPanel();
        panelisi1 = new widget.panelisi();
        BtnNota = new widget.Button();
        BtnSimpan = new widget.Button();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari1 = new widget.Button();
        BtnTambah = new widget.Button();
        label22 = new widget.Label();
        BtnCari = new widget.Button();
        BtnKeluar = new widget.Button();
        BtnCari2 = new widget.Button();
        panelisi5 = new widget.panelisi();
        label10 = new widget.Label();
        LTotal = new widget.Label();
        label19 = new widget.Label();
        Bayar = new widget.TextBox();
        label20 = new widget.Label();
        LKembali = new widget.Label();
        jLabel11 = new widget.Label();
        jLabel12 = new widget.Label();
        TagihanPPn = new widget.Label();
        BesarPPN = new widget.TextBox();
        Persenppn = new widget.TextBox();
        label21 = new widget.Label();
        Ongkir = new widget.TextBox();
        panelisi3 = new widget.panelisi();
        label15 = new widget.Label();
        NoNota = new widget.TextBox();
        label14 = new widget.Label();
        kdmem = new widget.TextBox();
        kdptg = new widget.TextBox();
        label16 = new widget.Label();
        nmmem = new widget.TextBox();
        nmptg = new widget.TextBox();
        BtnMem = new widget.Button();
        BtnPtg = new widget.Button();
        label18 = new widget.Label();
        catatan = new widget.TextBox();
        label12 = new widget.Label();
        label11 = new widget.Label();
        Tgl = new widget.Tanggal();
        jLabel10 = new widget.Label();
        AkunBayar = new widget.ComboBox();
        Jenisjual = new widget.ComboBox();
        DokterCombo = new widget.ComboBox();
        label13 = new widget.Label();
        jPanel2 = new javax.swing.JPanel();
        label17 = new widget.Label();
        jPanel3 = new javax.swing.JPanel();
        label23 = new widget.Label();
        scrollPane1 = new widget.ScrollPane();
        tbObat = new widget.Table();

        Kd2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        Kd2.setName("Kd2"); // NOI18N
        Kd2.setPreferredSize(new java.awt.Dimension(207, 23));

        Popup.setName("Popup"); // NOI18N

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        ppBersihkan.setText("Bersihkan Jumlah");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(180, 25));
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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Transaksi Penjualan Barang Toko / Minimarket / Koperasi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(816, 132));
        jPanel1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(100, 56));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Agenda-1-16x16.png"))); // NOI18N
        BtnNota.setMnemonic('S');
        BtnNota.setText("Nota");
        BtnNota.setToolTipText("Alt+S");
        BtnNota.setName("BtnNota"); // NOI18N
        BtnNota.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnNotaActionPerformed(evt);
            }
        });
        BtnNota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnNotaKeyPressed(evt);
            }
        });
        panelisi1.add(BtnNota);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
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

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi1.add(label9);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(220, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi1.add(TCari);

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

        label22.setName("label22"); // NOI18N
        label22.setPreferredSize(new java.awt.Dimension(15, 23));
        panelisi1.add(label22);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnCari.setMnemonic('E');
        BtnCari.setText("Cari");
        BtnCari.setToolTipText("Alt+E");
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

        BtnCari2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari2.setMnemonic('1');
        BtnCari2.setToolTipText("Alt+1");
        BtnCari2.setName("BtnCari2"); // NOI18N
        BtnCari2.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCari2ActionPerformed(evt);
            }
        });
        BtnCari2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCari2KeyPressed(evt);
            }
        });
        panelisi1.add(BtnCari2);

        jPanel1.add(panelisi1, java.awt.BorderLayout.PAGE_END);

        panelisi5.setName("panelisi5"); // NOI18N
        panelisi5.setPreferredSize(new java.awt.Dimension(100, 54));
        panelisi5.setWarnaAtas(new java.awt.Color(153, 153, 153));
        panelisi5.setWarnaBawah(new java.awt.Color(102, 102, 102));
        panelisi5.setLayout(null);

        label10.setForeground(new java.awt.Color(255, 255, 255));
        label10.setText("Jumlah Total :");
        label10.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(85, 23));
        panelisi5.add(label10);
        label10.setBounds(0, 10, 90, 23);

        LTotal.setBackground(new java.awt.Color(255, 153, 0));
        LTotal.setForeground(new java.awt.Color(0, 0, 0));
        LTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LTotal.setText("0");
        LTotal.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        LTotal.setName("LTotal"); // NOI18N
        LTotal.setOpaque(true);
        LTotal.setPreferredSize(new java.awt.Dimension(200, 23));
        panelisi5.add(LTotal);
        LTotal.setBounds(94, 10, 160, 23);

        label19.setForeground(new java.awt.Color(255, 255, 255));
        label19.setText("Bayar :");
        label19.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(50, 23));
        panelisi5.add(label19);
        label19.setBounds(256, 40, 80, 23);

        Bayar.setBackground(new java.awt.Color(255, 153, 0));
        Bayar.setForeground(new java.awt.Color(0, 0, 0));
        Bayar.setText("0");
        Bayar.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        Bayar.setName("Bayar"); // NOI18N
        Bayar.setOpaque(true);
        Bayar.setPreferredSize(new java.awt.Dimension(150, 23));
        panelisi5.add(Bayar);
        Bayar.setBounds(340, 40, 200, 23);

        label20.setForeground(new java.awt.Color(255, 255, 255));
        label20.setText("Kembali :");
        label20.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label20.setName("label20"); // NOI18N
        label20.setPreferredSize(new java.awt.Dimension(130, 23));
        panelisi5.add(label20);
        label20.setBounds(556, 40, 80, 23);

        LKembali.setBackground(new java.awt.Color(255, 153, 0));
        LKembali.setForeground(new java.awt.Color(0, 0, 0));
        LKembali.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LKembali.setText("0");
        LKembali.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        LKembali.setName("LKembali"); // NOI18N
        LKembali.setOpaque(true);
        LKembali.setPreferredSize(new java.awt.Dimension(120, 23));
        panelisi5.add(LKembali);
        LKembali.setBounds(640, 40, 170, 23);

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("PPN(%) :");
        jLabel11.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(95, 23));
        panelisi5.add(jLabel11);
        jLabel11.setBounds(256, 10, 80, 23);

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Total Tagihan :");
        jLabel12.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(95, 23));
        panelisi5.add(jLabel12);
        jLabel12.setBounds(0, 40, 90, 23);

        TagihanPPn.setBackground(new java.awt.Color(255, 153, 0));
        TagihanPPn.setForeground(new java.awt.Color(0, 0, 0));
        TagihanPPn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TagihanPPn.setText("0");
        TagihanPPn.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        TagihanPPn.setName("TagihanPPn"); // NOI18N
        TagihanPPn.setOpaque(true);
        TagihanPPn.setPreferredSize(new java.awt.Dimension(200, 23));
        panelisi5.add(TagihanPPn);
        TagihanPPn.setBounds(94, 40, 160, 23);

        BesarPPN.setBackground(new java.awt.Color(255, 153, 0));
        BesarPPN.setForeground(new java.awt.Color(0, 0, 0));
        BesarPPN.setText("0");
        BesarPPN.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        BesarPPN.setName("BesarPPN"); // NOI18N
        BesarPPN.setOpaque(true);
        BesarPPN.setPreferredSize(new java.awt.Dimension(150, 23));
        panelisi5.add(BesarPPN);
        BesarPPN.setBounds(382, 10, 158, 23);

        Persenppn.setBackground(new java.awt.Color(255, 153, 0));
        Persenppn.setForeground(new java.awt.Color(0, 0, 0));
        Persenppn.setText("0");
        Persenppn.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        Persenppn.setName("Persenppn"); // NOI18N
        Persenppn.setOpaque(true);
        Persenppn.setPreferredSize(new java.awt.Dimension(150, 23));
        Persenppn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PersenppnKeyPressed(evt);
            }
        });
        panelisi5.add(Persenppn);
        Persenppn.setBounds(340, 10, 40, 23);

        label21.setForeground(new java.awt.Color(255, 255, 255));
        label21.setText("Ongkir :");
        label21.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label21.setName("label21"); // NOI18N
        label21.setPreferredSize(new java.awt.Dimension(50, 23));
        panelisi5.add(label21);
        label21.setBounds(556, 10, 80, 23);

        Ongkir.setBackground(new java.awt.Color(255, 153, 0));
        Ongkir.setForeground(new java.awt.Color(0, 0, 0));
        Ongkir.setText("0");
        Ongkir.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        Ongkir.setName("Ongkir"); // NOI18N
        Ongkir.setOpaque(true);
        Ongkir.setPreferredSize(new java.awt.Dimension(150, 23));
        Ongkir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                OngkirKeyPressed(evt);
            }
        });
        panelisi5.add(Ongkir);
        Ongkir.setBounds(640, 10, 157, 23);

        jPanel1.add(panelisi5, java.awt.BorderLayout.CENTER);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(300, 300));
        panelisi3.setWarnaAtas(new java.awt.Color(153, 153, 153));
        panelisi3.setWarnaBawah(new java.awt.Color(102, 102, 102));
        panelisi3.setLayout(null);

        label15.setForeground(new java.awt.Color(255, 255, 255));
        label15.setText("No.Nota :");
        label15.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label15);
        label15.setBounds(0, 10, 70, 23);

        NoNota.setBackground(new java.awt.Color(0, 204, 255));
        NoNota.setForeground(new java.awt.Color(102, 102, 102));
        NoNota.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        NoNota.setName("NoNota"); // NOI18N
        NoNota.setOpaque(true);
        NoNota.setPreferredSize(new java.awt.Dimension(207, 23));
        NoNota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoNotaKeyPressed(evt);
            }
        });
        panelisi3.add(NoNota);
        NoNota.setBounds(74, 10, 140, 23);

        label14.setForeground(new java.awt.Color(255, 255, 255));
        label14.setText("Petugas :");
        label14.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label14);
        label14.setBounds(365, 40, 80, 23);

        kdmem.setBackground(new java.awt.Color(0, 204, 255));
        kdmem.setForeground(new java.awt.Color(102, 102, 102));
        kdmem.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        kdmem.setName("kdmem"); // NOI18N
        kdmem.setOpaque(true);
        kdmem.setPreferredSize(new java.awt.Dimension(80, 23));
        kdmem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdmemKeyPressed(evt);
            }
        });
        panelisi3.add(kdmem);
        kdmem.setBounds(449, 10, 100, 23);

        kdptg.setBackground(new java.awt.Color(0, 204, 255));
        kdptg.setForeground(new java.awt.Color(102, 102, 102));
        kdptg.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        kdptg.setName("kdptg"); // NOI18N
        kdptg.setOpaque(true);
        kdptg.setPreferredSize(new java.awt.Dimension(80, 23));
        kdptg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdptgKeyPressed(evt);
            }
        });
        panelisi3.add(kdptg);
        kdptg.setBounds(449, 40, 100, 23);

        label16.setForeground(new java.awt.Color(255, 255, 255));
        label16.setText("Detail Resep  :");
        label16.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label16);
        label16.setBounds(20, 140, 130, 23);

        nmmem.setBackground(new java.awt.Color(0, 204, 255));
        nmmem.setForeground(new java.awt.Color(102, 102, 102));
        nmmem.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        nmmem.setName("nmmem"); // NOI18N
        nmmem.setOpaque(true);
        nmmem.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi3.add(nmmem);
        nmmem.setBounds(550, 10, 222, 23);

        nmptg.setEditable(false);
        nmptg.setBackground(new java.awt.Color(0, 204, 255));
        nmptg.setForeground(new java.awt.Color(102, 102, 102));
        nmptg.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        nmptg.setName("nmptg"); // NOI18N
        nmptg.setOpaque(true);
        nmptg.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi3.add(nmptg);
        nmptg.setBounds(550, 40, 222, 23);

        BtnMem.setBackground(new java.awt.Color(0, 204, 255));
        BtnMem.setForeground(new java.awt.Color(102, 102, 102));
        BtnMem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnMem.setMnemonic('1');
        BtnMem.setToolTipText("Alt+1");
        BtnMem.setName("BtnMem"); // NOI18N
        BtnMem.setOpaque(true);
        BtnMem.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnMem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnMemActionPerformed(evt);
            }
        });
        panelisi3.add(BtnMem);
        BtnMem.setBounds(774, 10, 28, 23);

        BtnPtg.setBackground(new java.awt.Color(0, 204, 255));
        BtnPtg.setForeground(new java.awt.Color(102, 102, 102));
        BtnPtg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPtg.setMnemonic('2');
        BtnPtg.setToolTipText("Alt+2");
        BtnPtg.setName("BtnPtg"); // NOI18N
        BtnPtg.setOpaque(true);
        BtnPtg.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPtg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPtgActionPerformed(evt);
            }
        });
        panelisi3.add(BtnPtg);
        BtnPtg.setBounds(774, 40, 28, 23);

        label18.setForeground(new java.awt.Color(255, 255, 255));
        label18.setText("Catatan :");
        label18.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label18);
        label18.setBounds(0, 40, 70, 23);

        catatan.setBackground(new java.awt.Color(0, 204, 255));
        catatan.setForeground(new java.awt.Color(102, 102, 102));
        catatan.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        catatan.setName("catatan"); // NOI18N
        catatan.setOpaque(true);
        catatan.setPreferredSize(new java.awt.Dimension(207, 23));
        catatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                catatanKeyPressed(evt);
            }
        });
        panelisi3.add(catatan);
        catatan.setBounds(74, 40, 275, 23);

        label12.setText("Jns.Jual :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label12);
        label12.setBounds(170, 70, 55, 23);

        label11.setForeground(new java.awt.Color(255, 255, 255));
        label11.setText("Dokter :");
        label11.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label11);
        label11.setBounds(0, 100, 70, 23);

        Tgl.setBackground(new java.awt.Color(0, 153, 255));
        Tgl.setDisplayFormat("dd-MM-yyyy");
        Tgl.setName("Tgl"); // NOI18N
        Tgl.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TglItemStateChanged(evt);
            }
        });
        Tgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglKeyPressed(evt);
            }
        });
        panelisi3.add(Tgl);
        Tgl.setBounds(74, 70, 95, 23);

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Akun Bayar :");
        jLabel10.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        panelisi3.add(jLabel10);
        jLabel10.setBounds(365, 70, 80, 23);

        AkunBayar.setBackground(new java.awt.Color(0, 153, 255));
        AkunBayar.setName("AkunBayar"); // NOI18N
        AkunBayar.setOpaque(false);
        AkunBayar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                AkunBayarItemStateChanged(evt);
            }
        });
        AkunBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AkunBayarKeyPressed(evt);
            }
        });
        panelisi3.add(AkunBayar);
        AkunBayar.setBounds(450, 70, 353, 23);

        Jenisjual.setBackground(new java.awt.Color(0, 153, 255));
        Jenisjual.setForeground(new java.awt.Color(255, 255, 255));
        Jenisjual.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Biasa", "Resep" }));
        Jenisjual.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        Jenisjual.setName("Jenisjual"); // NOI18N
        Jenisjual.setPreferredSize(new java.awt.Dimension(40, 23));
        Jenisjual.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                JenisjualItemStateChanged(evt);
            }
        });
        Jenisjual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JenisjualKeyPressed(evt);
            }
        });
        panelisi3.add(Jenisjual);
        Jenisjual.setBounds(229, 70, 120, 23);

        DokterCombo.setBackground(new java.awt.Color(0, 153, 255));
        DokterCombo.setName("DokterCombo"); // NOI18N
        DokterCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DokterComboItemStateChanged(evt);
            }
        });
        DokterCombo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DokterComboKeyPressed(evt);
            }
        });
        panelisi3.add(DokterCombo);
        DokterCombo.setBounds(70, 100, 353, 23);

        label13.setForeground(new java.awt.Color(255, 255, 255));
        label13.setText("Tanggal :");
        label13.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label13);
        label13.setBounds(0, 70, 70, 23);

        jPanel2.setName("jPanel2"); // NOI18N
        panelisi3.add(jPanel2);
        jPanel2.setBounds(840, 40, 530, 220);

        label17.setForeground(new java.awt.Color(255, 255, 255));
        label17.setText("Pasien :");
        label17.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label17);
        label17.setBounds(365, 10, 80, 23);

        jPanel3.setName("jPanel3"); // NOI18N
        panelisi3.add(jPanel3);
        jPanel3.setBounds(70, 160, 740, 120);

        label23.setForeground(new java.awt.Color(255, 255, 255));
        label23.setText("Daftar Resep Dokter :");
        label23.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label23.setName("label23"); // NOI18N
        label23.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label23);
        label23.setBounds(830, 10, 130, 23);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_START);

        scrollPane1.setComponentPopupMenu(Popup);
        scrollPane1.setName("scrollPane1"); // NOI18N
        scrollPane1.setOpaque(true);

        tbObat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbObat.setComponentPopupMenu(Popup);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbObatPropertyChange(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(tbObat);

        internalFrame1.add(scrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {                  
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tabMode.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                try {                                     
                    getData();                     
                    TCari.setText("");
                    TCari.requestFocus();
                } catch (java.lang.NullPointerException e) {
                }
            }
            
            if(evt.getKeyCode()==KeyEvent.VK_DELETE){
                try {
                    switch (tbObat.getSelectedColumn()) {
                        case 0:
                            tbObat.setValueAt("", tbObat.getSelectedRow(),0);
                            break;
                        case 7:
                            tbObat.setValueAt(0, tbObat.getSelectedRow(),7);
                            break;
                        case 8:
                            tbObat.setValueAt(0, tbObat.getSelectedRow(),8);
                            break;
                        case 9:
                            tbObat.setValueAt(0, tbObat.getSelectedRow(),9);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                } 
            }
            
            if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                TCari.setText("");
                TCari.requestFocus();
            }
            
            if(evt.getKeyCode()==KeyEvent.VK_BACK_SPACE){
                try {
                    switch (tbObat.getSelectedColumn()) {
                        case 0:
                            tbObat.setValueAt("", tbObat.getSelectedRow(),0);
                            break;
                        case 7:
                            tbObat.setValueAt(0, tbObat.getSelectedRow(),7);
                            break;
                        case 8:
                            tbObat.setValueAt(0, tbObat.getSelectedRow(),8);
                            break;
                        case 9:
                            tbObat.setValueAt(0, tbObat.getSelectedRow(),9);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                }     
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        carijual.emptTeks();  
        carijual.isCek();
        carijual.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        carijual.setLocationRelativeTo(internalFrame1);
        carijual.setAlwaysOnTop(false);
        carijual.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        carijual.barang.dispose();
        carijual.member.dispose();
        carijual.petugas.dispose();
        carijual.dispose();
        dispose(); 
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){            
            dispose();              
        }else{Valid.pindah(evt,BtnCari,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed
/*
private void KdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdKeyPressed
    Valid.pindah(evt,BtnCari,Nm);
}//GEN-LAST:event_TKdKeyPressed
*/

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
       if(NoNota.getText().trim().equals("")){
    Valid.textKosong(NoNota,"No.Nota");
}else if(nmmem.getText().trim().equals("")||kdmem.getText().trim().equals("")){
    Valid.textKosong(kdmem,"Member");
}else if(nmptg.getText().trim().equals("")||nmptg.getText().trim().equals("")){
    Valid.textKosong(kdptg,"Petugas");
}else if(AkunBayar.getSelectedItem().toString().trim().equals("")){
    Valid.textKosong(AkunBayar,"Akun Bayar");
}else if(tabMode.getRowCount()==0){
    JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
    tbObat.requestFocus();
}else if(ttl<=0){
    JOptionPane.showMessageDialog(null,"Maaf, Silahkan masukkan penjualan...!!!!");
    tbObat.requestFocus();
}else {
    reply = JOptionPane.showConfirmDialog(rootPane,"Eeiiiiiits, udah bener belum data yang mau disimpan..??","Konfirmasi",JOptionPane.YES_NO_OPTION);
    if (reply == JOptionPane.YES_OPTION) {
        Sequel.AutoComitFalse();
        sukses=true;   
        kode_akun_bayar="";
        try {
            myObj = new FileReader("./cache/akunbayar.iyem");
            root = mapper.readTree(myObj);
            response = root.path("akunbayar");
            if(response.isArray()){
               for(JsonNode list:response){
                   if(list.path("NamaAkun").asText().equals(AkunBayar.getSelectedItem().toString())){
                        kode_akun_bayar=list.path("KodeRek").asText();  
                   }
               }
            }
            myObj.close();
        } catch (Exception e) {
            sukses=false;
        } 
        
        // Tentukan jenis jual
        String jenis = Jenisjual.getSelectedItem().toString();
        boolean isResep = jenis.equalsIgnoreCase("Resep");

        String kdDokter = isResep ? ambilKdDokterDariCombo() : null;

        // Validasi kalau resep wajib dokter
        if (isResep && (kdDokter == null || kdDokter.isEmpty())) {
            Valid.textKosong(DokterCombo, "dokter");
            Sequel.AutoComitTrue();
            return;
        }
        
        // Ambil no_rawat dari header resep (kalau transaksi resep)
    String noRawat = null;
    if (isResep && noResepTerpilih != null && !noResepTerpilih.trim().isEmpty()) {
        noRawat = ambilNoRawatDariResep(noResepTerpilih);
    }

        // Insert header penjualan
        boolean ok = Sequel.menyimpantf2(
            "tokopenjualan",
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?",
            "nota_jual", 14,
            new String[]{
                NoNota.getText(),
                Valid.SetTgl(Tgl.getSelectedItem()+""),
                kdptg.getText(),
                kdmem.getText(),
                nmmem.getText(),
                catatan.getText(),
                jenis,
                String.valueOf(ongkir),
                String.valueOf(besarppn),
                kode_akun_bayar,
                String.valueOf(tagihanppn),
                AkunBayar.getSelectedItem().toString(),
                (kdDokter == null ? "" : kdDokter),
                (noRawat == null ? "" : noRawat)// 
            }
        );

        if (!ok) {
            autoNomor();
            // coba simpan lagi dengan NoNota baru
            ok = Sequel.menyimpantf2(
                "tokopenjualan",
        "?,?,?,?,?,?,?,?,?,?,?,?,?,?",
        "nota_jual", 14,
                new String[]{
                    NoNota.getText(),
                    Valid.SetTgl(Tgl.getSelectedItem()+""),
                    kdptg.getText(),
                    kdmem.getText(),
                    nmmem.getText(),
                    catatan.getText(),
                    jenis,
                    String.valueOf(ongkir),
                    String.valueOf(besarppn),
                    kode_akun_bayar,
                    String.valueOf(tagihanppn),
                    AkunBayar.getSelectedItem().toString(),
                    (kdDokter == null ? "" : kdDokter),
                    (noRawat == null ? "" : noRawat)
                }
            );
        }

       if (ok) {
    isSimpan();

    if (isResep) {
        if (noResepTerpilih != null && !noResepTerpilih.trim().isEmpty()) {
            boolean done = tandaiResepSelesai(
                noResepTerpilih,              // dari header
                NoNota.getText().trim(),      // nota penjualan
                kdptg.getText().trim()        // nip petugas
            );
            if (!done) {
                System.out.println("[RESEP] status resep tidak berubah (mungkin sudah selesai).");
            }
        } else {
            System.out.println("[RESEP] noResepTerpilih kosong, tidak bisa tandai selesai.");
        }
    }



        } else { 
            sukses=false; 
            autoNomor(); 
        }

        if(sukses==true){
            if(notatoko.equals("Yes")){
                BtnNotaActionPerformed(null);
            }
            Sequel.Commit();
            Valid.tabelKosong(tabMode);
            tampil();
            tagihanppn=0;
            ttl=0;
            ttlhpp=0;
            bayar=0;
            besarppn=0;
            total=0;
            ppn=0;
            ongkir=0;
            LTotal.setText("0");
            Bayar.setText("0");
            Ongkir.setText("0");

            // refresh resep header setelah commit
            
            loadResepHeader(Tgl.getDate());

        }else{
            JOptionPane.showMessageDialog(null,"Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
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
           Valid.pindah(evt,Bayar,BtnCari);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            tampil();
        }else{
            Valid.pindah(evt, BtnSimpan, BtnKeluar);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnNotaActionPerformed
        if(NoNota.getText().trim().equals("")){
            Valid.textKosong(NoNota,"No.Nota");
        }else if(nmmem.getText().trim().equals("")||kdmem.getText().trim().equals("")){
            Valid.textKosong(kdmem,"Member");
        }else if(nmptg.getText().trim().equals("")||nmptg.getText().trim().equals("")){
            Valid.textKosong(kdptg,"Petugas");
        }else if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            tbObat.requestFocus();
        }else if(ttl<=0){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan masukkan penjualan...!!!!");
            tbObat.requestFocus();
        }else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Sequel.queryu("delete from temporary_toko");
            for(i=0;i<tabMode.getRowCount();i++){  
                try {
                    if(Valid.SetAngka(tabMode.getValueAt(i,0).toString())>0){
                           Sequel.menyimpan2("temporary_toko","'0','"+
                                   tabMode.getValueAt(i,0).toString()+"','"+
                                   tabMode.getValueAt(i,1).toString()+"','"+
                                   tabMode.getValueAt(i,2).toString()+"','"+
                                   tabMode.getValueAt(i,3).toString()+"','"+
                                   tabMode.getValueAt(i,4).toString()+"','"+
                                   tabMode.getValueAt(i,5).toString()+"','"+
                                   tabMode.getValueAt(i,6).toString()+"','"+
                                   tabMode.getValueAt(i,8).toString()+"','"+
                                   tabMode.getValueAt(i,9).toString()+"','"+
                                   tabMode.getValueAt(i,10).toString()+"','"+
                                   tabMode.getValueAt(i,11).toString()+"','"+
                                   tabMode.getValueAt(i,12).toString()+"','','','','','','','','','','','','','','','','','','','','','','','','',''","Transaksi Penjualan"); 
                    }
                } catch (Exception e) {
                }                
            }
            this.setCursor(Cursor.getDefaultCursor());
            Valid.panggilUrl("billing/NotaToko.php?nonota="+NoNota.getText()+"&besarppn="+besarppn+"&bayar="+Bayar.getText()+"&ongkir="+Ongkir.getText()+"&tanggal="+Valid.SetTgl(Tgl.getSelectedItem()+"")+"&catatan="+catatan.getText().replaceAll(" ","_")+"&petugas="+nmptg.getText().replaceAll(" ","_")+"&member="+nmmem.getText().replaceAll(" ","_")+"&nomember="+kdmem.getText().replaceAll(" ","_")+"&usere="+koneksiDB.USERHYBRIDWEB()+"&passwordte="+koneksiDB.PASHYBRIDWEB());
        }
    }//GEN-LAST:event_BtnNotaActionPerformed

    private void BtnNotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnNotaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnNotaKeyPressed

private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
       if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCari1ActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari1.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbObat.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

private void BtnCari1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCari1ActionPerformed
    tampil();
}//GEN-LAST:event_BtnCari1ActionPerformed

private void BtnCari1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCari1KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCari1ActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, Bayar);
        }
}//GEN-LAST:event_BtnCari1KeyPressed

private void NoNotaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoNotaKeyPressed
        Valid.pindah(evt,TCari, Tgl);
}//GEN-LAST:event_NoNotaKeyPressed

private void kdmemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdmemKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_PAGE_DOWN:
                Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", nmmem,kdmem.getText());
                break;
            case KeyEvent.VK_PAGE_UP:
                Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", nmmem,kdmem.getText());
                Tgl.requestFocus();
                break;
            case KeyEvent.VK_ENTER:
                Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", nmmem,kdmem.getText());
                catatan.requestFocus();
                break;
            case KeyEvent.VK_UP:
                BtnMemActionPerformed(null);
                break;
            default:
                break;
        }
}//GEN-LAST:event_kdmemKeyPressed

private void kdptgKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdptgKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_PAGE_DOWN:
                nmptg.setText(carijual.petugas.tampil3(kdptg.getText()));
                break;
            case KeyEvent.VK_PAGE_UP:
                nmptg.setText(carijual.petugas.tampil3(kdptg.getText()));
                Jenisjual.requestFocus();
                break;
            case KeyEvent.VK_ENTER:
                nmptg.setText(carijual.petugas.tampil3(kdptg.getText()));
                TCari.requestFocus();
                break;
            case KeyEvent.VK_UP:
                BtnPtgActionPerformed(null);
                break;
            default:
                break;
        }
}//GEN-LAST:event_kdptgKeyPressed

private void BtnMemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnMemActionPerformed
        akses.setform("Penjualan");
        carijual.member.isCek();
        carijual.member.emptTeks();
        carijual.member.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        carijual.member.setLocationRelativeTo(internalFrame1);
        carijual.member.setAlwaysOnTop(false);
        carijual.member.setVisible(true);
}//GEN-LAST:event_BtnMemActionPerformed

private void BtnPtgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPtgActionPerformed
        akses.setform("Penjualan");
        carijual.petugas.emptTeks();
        carijual.petugas.isCek();
        carijual.petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        carijual.petugas.setLocationRelativeTo(internalFrame1);
        carijual.petugas.setAlwaysOnTop(false);
        carijual.petugas.setVisible(true);
}//GEN-LAST:event_BtnPtgActionPerformed

private void catatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_catatanKeyPressed
        Valid.pindah(evt, kdmem, Jenisjual);
}//GEN-LAST:event_catatanKeyPressed

private void TglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglKeyPressed
        Valid.pindah(evt,NoNota,kdmem);
}//GEN-LAST:event_TglKeyPressed

private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
            int row2=tabMode.getRowCount();
            for(int r=0;r<row2;r++){ 
                tabMode.setValueAt("",r,0);
                tabMode.setValueAt(0,r,6);
                tabMode.setValueAt(0,r,7);
                tabMode.setValueAt(0,r,8);
                tabMode.setValueAt(0,r,9);
                tabMode.setValueAt(0,r,10);
            }
}//GEN-LAST:event_ppBersihkanActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
        tampilAkunBayar();
        tampilDokter();
        cariPPN(); 
       // initPanelResepMasterDetail();
loadResepHeader(new java.util.Date()); 
    }//GEN-LAST:event_formWindowOpened

    private void BtnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        carijual.barang.emptTeks();
        carijual.barang.isCek();
        carijual.barang.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        carijual.barang.setLocationRelativeTo(internalFrame1);
        carijual.barang.setAlwaysOnTop(false);
        carijual.barang.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnTambahActionPerformed

    private void AkunBayarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AkunBayarKeyPressed
        Valid.pindah(evt, BtnPtg,BtnSimpan);
    }//GEN-LAST:event_AkunBayarKeyPressed

    private void PersenppnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PersenppnKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            isKembali();
            Ongkir.requestFocus();
        }
    }//GEN-LAST:event_PersenppnKeyPressed

    private void tbObatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbObatPropertyChange
        if(this.isVisible()==true){
              getData();
        }
    }//GEN-LAST:event_tbObatPropertyChange

    private void OngkirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_OngkirKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            isKembali();
            Bayar.requestFocus();
        }
    }//GEN-LAST:event_OngkirKeyPressed

    private void TglItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TglItemStateChanged
        try {
            autoNomor();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_TglItemStateChanged

    private void AkunBayarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_AkunBayarItemStateChanged
        if(this.isVisible()==true){
            cariPPN();
            isKembali();
        }
    }//GEN-LAST:event_AkunBayarItemStateChanged

    private void JenisjualItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_JenisjualItemStateChanged
        tampil();
        toggleDokter();
    }//GEN-LAST:event_JenisjualItemStateChanged

    private void JenisjualKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JenisjualKeyPressed
        Valid.pindah(evt, catatan, kdptg);
    }//GEN-LAST:event_JenisjualKeyPressed

    private void DokterComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DokterComboItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_DokterComboItemStateChanged

    private void DokterComboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DokterComboKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DokterComboKeyPressed

    private void BtnCari2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCari2ActionPerformed
     loadResepTokoByTanggal(new java.util.Date());   // TODO add your handling code here:
    }//GEN-LAST:event_BtnCari2ActionPerformed

    private void BtnCari2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCari2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnCari2KeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            TokoPenjualan dialog = new TokoPenjualan(new javax.swing.JFrame(), true);
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
    private widget.ComboBox AkunBayar;
    private widget.TextBox Bayar;
    private widget.TextBox BesarPPN;
    private widget.Button BtnCari;
    private widget.Button BtnCari1;
    private widget.Button BtnCari2;
    private widget.Button BtnKeluar;
    private widget.Button BtnMem;
    private widget.Button BtnNota;
    private widget.Button BtnPtg;
    private widget.Button BtnSimpan;
    private widget.Button BtnTambah;
    private widget.ComboBox DokterCombo;
    private widget.ComboBox Jenisjual;
    private widget.TextBox Kd2;
    private widget.Label LKembali;
    private widget.Label LTotal;
    private widget.TextBox NoNota;
    private widget.TextBox Ongkir;
    private widget.TextBox Persenppn;
    private javax.swing.JPopupMenu Popup;
    private widget.TextBox TCari;
    private widget.Label TagihanPPn;
    private widget.Tanggal Tgl;
    private widget.TextBox catatan;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox kdmem;
    private widget.TextBox kdptg;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label14;
    private widget.Label label15;
    private widget.Label label16;
    private widget.Label label17;
    private widget.Label label18;
    private widget.Label label19;
    private widget.Label label20;
    private widget.Label label21;
    private widget.Label label22;
    private widget.Label label23;
    private widget.Label label9;
    private widget.TextBox nmmem;
    private widget.TextBox nmptg;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi3;
    private widget.panelisi panelisi5;
    private javax.swing.JMenuItem ppBersihkan;
    private widget.ScrollPane scrollPane1;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

//    private void tampil() {
//       row=tabMode.getRowCount();
//        jml=0;
//        for(i=0;i<row;i++){
//            try {
//                if(Double.parseDouble(tbObat.getValueAt(i,0).toString())>0){
//                    jml++;
//                }
//            } catch (Exception e) {
//                jml=jml+0;
//            } 
//        }
//        
//        kodebarang=new String[jml];
//        namabarang=new String[jml];
//        kategori=new String[jml];
//        satuan=new String[jml];
//        harga=new double[jml];
//        jumlah=new double[jml];
//        subtotal=new double[jml];
//        diskon=new double[jml];
//        besardiskon=new double[jml];
//        tambahan=new double[jml];
//        totaljual=new double[jml];
//        stok=new double[jml];
//        hbeli=new double[jml];
//        
//        index=0;        
//        for(i=0;i<row;i++){
//            try {
//                if(Double.parseDouble(tbObat.getValueAt(i,0).toString())>0){
//                    jumlah[index]=Double.parseDouble(tabMode.getValueAt(i,0).toString());
//                    kodebarang[index]=tabMode.getValueAt(i,1).toString();
//                    namabarang[index]=tabMode.getValueAt(i,2).toString();
//                    kategori[index]=tabMode.getValueAt(i,3).toString();
//                    satuan[index]=tabMode.getValueAt(i,4).toString();
//                    harga[index]=Double.parseDouble(tabMode.getValueAt(i,5).toString());
//                    subtotal[index]=Double.parseDouble(tabMode.getValueAt(i,6).toString());
//                    diskon[index]=Double.parseDouble(tabMode.getValueAt(i,7).toString());
//                    besardiskon[index]=Double.parseDouble(tabMode.getValueAt(i,8).toString());
//                    tambahan[index]=Double.parseDouble(tabMode.getValueAt(i,9).toString());
//                    totaljual[index]=Double.parseDouble(tabMode.getValueAt(i,10).toString());
//                    stok[index]=Double.parseDouble(tabMode.getValueAt(i,11).toString());
//                    hbeli[index]=Double.parseDouble(tabMode.getValueAt(i,12).toString());
//                    index++;
//                }
//            } catch (Exception e) {
//            }                
//        }
//        
//        Valid.tabelKosong(tabMode);
//        
//        for(i=0;i<jml;i++){            
//            tabMode.addRow(new Object[]{jumlah[i],kodebarang[i],namabarang[i],kategori[i],satuan[i],harga[i],subtotal[i],diskon[i],besardiskon[i],tambahan[i],totaljual[i],stok[i],hbeli[i]});
//        }
//        
//        try{
//            ps=koneksi.prepareStatement(
//                "select tokobarang.kode_brng,tokobarang.nama_brng,tokojenisbarang.nm_jenis,tokobarang.stok, "+
//                "tokobarang.kode_sat,tokobarang.distributor,tokobarang.grosir,tokobarang.retail,tokobarang."+hpptoko+" as dasar "+
//                "from tokobarang inner join tokojenisbarang on tokobarang.jenis=tokojenisbarang.kd_jenis "+
//                "where tokobarang.stok>0 and tokobarang.status='1' and "+
//                "(tokobarang.kode_brng like ? or tokobarang.nama_brng like ? or tokojenisbarang.nm_jenis like ?) order by tokobarang.nama_brng");
//            try {
//                ps.setString(1,"%"+TCari.getText().trim()+"%");
//                ps.setString(2,"%"+TCari.getText().trim()+"%");
//                ps.setString(3,"%"+TCari.getText().trim()+"%");
//                rs=ps.executeQuery();
//                if(Jenisjual.getSelectedItem().equals("Distributor")){
//                    while(rs.next()){                              
//                        tabMode.addRow(new Object[]{
//                            "",rs.getString("kode_brng"),rs.getString("nama_brng"),rs.getString("nm_jenis"),rs.getString("kode_sat"),
//                            rs.getDouble("distributor"),0,0,0,0,0,rs.getDouble("stok"),rs.getDouble("dasar")
//                        });
//                    } 
//                }else if(Jenisjual.getSelectedItem().equals("Grosir")){
//                    while(rs.next()){                              
//                        tabMode.addRow(new Object[]{
//                            "",rs.getString("kode_brng"),rs.getString("nama_brng"),rs.getString("nm_jenis"),rs.getString("kode_sat"),
//                            rs.getDouble("grosir"),0,0,0,0,0,rs.getDouble("stok"),rs.getDouble("dasar")
//                        });
//                    } 
//                }else if(Jenisjual.getSelectedItem().equals("Retail")){
//                    while(rs.next()){                              
//                        tabMode.addRow(new Object[]{
//                            "",rs.getString("kode_brng"),rs.getString("nama_brng"),rs.getString("nm_jenis"),rs.getString("kode_sat"),
//                            rs.getDouble("retail"),0,0,0,0,0,rs.getDouble("stok"),rs.getDouble("dasar")
//                        });
//                    } 
//                }
//            } catch (Exception e) {
//                System.out.println("Notifikasi : "+e);
//            } finally{
//                if(rs!=null){
//                    rs.close();
//                }
//                if(ps!=null){
//                    ps.close();
//                }
//            }             
//        }catch(Exception e){
//            System.out.println("Notifikasi : "+e);
//        }  
//    }




    private void tampil() {
    final int C_JML=0,C_KODE=1,C_NAMA=2,C_KAT=3,C_SAT=4,C_HRG=5,
              C_SUB=6,C_DSC=7,C_DSCN=8,C_TMB=9,C_TOT=10,C_STK=11,C_HBELI=12;

    // 1) Kumpulkan baris qty>0
    List<Object[]> keep = new ArrayList<>();
    for (int r=0; r<tabMode.getRowCount(); r++) {
        if (d(tabMode.getValueAt(r, C_JML)) > 0) {
            keep.add(new Object[] {
                tabMode.getValueAt(r, C_JML),
                tabMode.getValueAt(r, C_KODE),
                tabMode.getValueAt(r, C_NAMA),
                tabMode.getValueAt(r, C_KAT),
                tabMode.getValueAt(r, C_SAT),
                tabMode.getValueAt(r, C_HRG),
                tabMode.getValueAt(r, C_SUB),
                tabMode.getValueAt(r, C_DSC),
                tabMode.getValueAt(r, C_DSCN),
                tabMode.getValueAt(r, C_TMB),
                tabMode.getValueAt(r, C_TOT),
                tabMode.getValueAt(r, C_STK),
                tabMode.getValueAt(r, C_HBELI)
            });
        }
    }

    // 2) Reset tabel & tulis ulang baris yang disimpan
    tabMode.setRowCount(0);
    for (Object[] rowObj : keep) tabMode.addRow(rowObj);
    

    // 3) Query DB & tambahkan baris baru sesuai jenis jual
//    String sql =
//    "SELECT b.kode_brng,b.nama_brng,j.nm_jenis,b.stok," +
//    "       b.kode_sat, b.kode_sat1, b.kode_sat2,b.isi,b.kapasitas,b.h_resep " +        // ambil semua satuan
//    "       b.distributor,b.grosir,b.retail,b." + hpptoko + " AS dasar " +
//    "FROM tokobarang b JOIN tokojenisbarang j ON b.jenis=j.kd_jenis " +
//    "WHERE b.stok>0 AND b.status='1' AND " +
//    " (b.kode_brng LIKE ? OR b.nama_brng LIKE ? OR j.nm_jenis LIKE ?) " +
//    "ORDER BY b.nama_brng";
//
//try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
//    String q = "%" + TCari.getText().trim() + "%";
//    ps.setString(1, q);
//    ps.setString(2, q);
//    ps.setString(3, q);
//
//    try (ResultSet rs = ps.executeQuery()) {
//        while (rs.next()) {
//            double harga;
//            String satuan;
//            double stok;
//            double stokPack   = rs.getDouble("stok");        // stok disimpan per PACK
//            double isi        = Math.max(1, rs.getDouble("isi"));        // grosir/unit per pack
//            double kapasitas  = Math.max(1, rs.getDouble("kapasitas"));
//            double h_resep      = Math.max(1, rs.getDouble("h_resep"));// retail/unit per pack
//
//            String pilihan = String.valueOf(Jenisjual.getSelectedItem());
//            if ("Distributor".equals(pilihan)) {
//                harga  = rs.getDouble("distributor");
//                satuan = rs.getString("kode_sat");
//                stok   = stokPack;// pack
//            } else if ("Grosir".equals(pilihan)) {
//                harga  = rs.getDouble("grosir");
//                satuan = rs.getString("kode_sat1");
//                stok   = stokPack * isi;// sesuai permintaan: pakai sat2
//            } else { // Retail
//                harga  = rs.getDouble("retail");
//                satuan = rs.getString("kode_sat2");
//                stok   = stokPack * isi * kapasitas;
//            }
//
//            tabMode.addRow(new Object[]{
//                0.0,
//                rs.getString("kode_brng"),
//                rs.getString("nama_brng"),
//                rs.getString("nm_jenis"),
//                satuan,        // kolom 4
//                harga,         // kolom 5
//                0.0, 0.0, 0.0, 0.0, 0.0,
//                stok,
//                rs.getDouble("dasar")
//            });
//        }
//    }
//} catch (SQLException ex) {
//    ex.printStackTrace();
//    JOptionPane.showMessageDialog(this, "Gagal load data: " + ex.getMessage(),
//                                  "Kesalahan DB", JOptionPane.ERROR_MESSAGE);
//}
    

   String sql =
    "SELECT b.kode_brng,b.nama_brng,j.nm_jenis,b.stok," +
    "       b.kode_sat, b.kode_sat1, b.kode_sat2,b.isi,b.kapasitas,b.h_resep, " +        // ambil semua satuan
    "       b.distributor,b.grosir,b.retail,b." + hpptoko + " AS dasar " +
    "FROM tokobarang b JOIN tokojenisbarang j ON b.jenis=j.kd_jenis " +
    "WHERE b.stok>0 AND b.status='1' AND " +
    " (b.kode_brng LIKE ? OR b.nama_brng LIKE ? OR j.nm_jenis LIKE ?) " +
    "ORDER BY b.nama_brng";

try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
    String q = "%" + TCari.getText().trim() + "%";
    ps.setString(1, q);
    ps.setString(2, q);
    ps.setString(3, q);

    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            double harga;
            String satuan;
            double stok;
            double stokPack   = rs.getDouble("stok");        // stok disimpan per PACK
            double isi        = Math.max(1, rs.getDouble("isi"));        // grosir/unit per pack
            double kapasitas  = Math.max(1, rs.getDouble("kapasitas"));
            double h_resep  = Math.max(1, rs.getDouble("h_resep"));// retail/unit per pack

            String pilihan = String.valueOf(Jenisjual.getSelectedItem());
            if ("Biasa".equals(pilihan)) {
                harga  = rs.getDouble("retail");
                satuan = rs.getString("kode_sat2");
                stok   = stokPack * isi * kapasitas;// pack
//            } else if ("Grosir".equals(pilihan)) {
//                harga  = rs.getDouble("grosir");
//                satuan = rs.getString("kode_sat1");
//                stok   = stokPack * isi;// sesuai permintaan: pakai sat2
//            } else if ("Biasa".equals(pilihan)){ // Retail
//                harga  = rs.getDouble("retail");
//                satuan = rs.getString("kode_sat2");
//                stok   = stokPack * isi * kapasitas;
            }else { // Retail
                harga  = rs.getDouble("h_resep");
                satuan = rs.getString("kode_sat2");
                stok   = stokPack * isi * kapasitas;
            }

            tabMode.addRow(new Object[]{
                0.0,
                rs.getString("kode_brng"),
                rs.getString("nama_brng"),
                rs.getString("nm_jenis"),
                satuan,        // kolom 4
                harga,         // kolom 5
                0.0, 0.0, 0.0, 0.0, 0.0,
                stok,
                rs.getDouble("dasar")
            });
        }
    }
} catch (SQLException ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "Gagal load data: " + ex.getMessage(),
                                  "Kesalahan DB", JOptionPane.ERROR_MESSAGE);
}
    
}

private static double d(Object v) {
    try { return v==null?0:Double.parseDouble(v.toString()); }
    catch (Exception e) { return 0; }
}
    
    
    
    private void getData(){        
        row=tbObat.getSelectedRow();
        if(row!= -1){ 
            if(!tbObat.getValueAt(row,0).toString().equals("")){
                kolom=tbObat.getSelectedColumn();
                if(Double.parseDouble(tabMode.getValueAt(row,0).toString())>0){
                    stokbarang=Double.parseDouble(tabMode.getValueAt(row,11).toString());
                    y=Valid.SetAngka(tbObat.getValueAt(row,0).toString());
                    if(stokbarang<y){
                        tabMode.setValueAt("",row,0);
                        JOptionPane.showMessageDialog(rootPane,"Maaf stok tidak mencukupi..!!");
                        tbObat.requestFocus();
                    }
                    if((kolom==0)||(kolom==1)||(kolom==2)||(kolom==3)||(kolom==4)||(kolom==5)){    
                        try {
                            tabMode.setValueAt(Double.parseDouble(tabMode.getValueAt(row,0).toString())*Double.parseDouble(tabMode.getValueAt(row,5).toString()), row,6);                   
                        } catch (Exception e) {
                            tabMode.setValueAt(0, row,6);                   
                        }

                        try {
                            tabMode.setValueAt(Double.parseDouble(tabMode.getValueAt(row,6).toString())-Double.parseDouble(tabMode.getValueAt(row,8).toString())+Double.parseDouble(tabMode.getValueAt(row,9).toString()), row,10);
                        } catch (Exception e) {
                            tabMode.setValueAt(0, row,10);                                                     
                        }              
                    }else if(kolom==7){ 
                        try {
                            tabMode.setValueAt(Double.parseDouble(tabMode.getValueAt(row,6).toString())*(Double.parseDouble(tabMode.getValueAt(row,7).toString())/100), row,8);                  
                        } catch (Exception e) {
                            tabMode.setValueAt(0, row,8);                   
                        }
                        
                        try {
                            tabMode.setValueAt(Double.parseDouble(tabMode.getValueAt(row,6).toString())-Double.parseDouble(tabMode.getValueAt(row,8).toString())+Double.parseDouble(tabMode.getValueAt(row,9).toString()), row,10);
                        } catch (Exception e) {
                            tabMode.setValueAt(0, row,10);                                                     
                        }
                    }else if((kolom==8)||(kolom==9)){ 
                        try {
                            tabMode.setValueAt(Double.parseDouble(tabMode.getValueAt(row,6).toString())-Double.parseDouble(tabMode.getValueAt(row,8).toString())+Double.parseDouble(tabMode.getValueAt(row,9).toString()), row,10);
                        } catch (Exception e) {
                            tabMode.setValueAt(0, row,10);                                                     
                        }
                    }
                }
            }else{
                tabMode.setValueAt(0, row,6);
                tabMode.setValueAt(0, row,7);
                tabMode.setValueAt(0, row,8);
                tabMode.setValueAt(0, row,9);
                tabMode.setValueAt(0, row,10);
            }
        }
        ttl=0;
        ttlhpp=0;
        y=0;
        z=0;
        
        for(int r=0;r<tabMode.getRowCount();r++){ 
            try {
                y=Double.parseDouble(tabMode.getValueAt(r,10).toString()); 
            } catch (Exception e) {
                y=0;
            }
            ttl=ttl+y;

            try {
                z=Double.parseDouble(tabMode.getValueAt(r,12).toString())*Double.parseDouble(tabMode.getValueAt(r,0).toString()); 
            } catch (Exception e) {
                z=0;
            }
            ttlhpp=ttlhpp+z;
        }
        
        LTotal.setText(Valid.SetAngka(ttl));
        isKembali();
    }
    
    
    private void isKembali(){
        if(!Bayar.getText().trim().equals("")) {
            bayar=Double.parseDouble(Bayar.getText()); 
        }
        if(ttl>0) {
            total=ttl; 
        }
        if(!Persenppn.getText().trim().equals("")) {
            ppn=Double.parseDouble(Persenppn.getText()); 
        }
        if(!Ongkir.getText().trim().equals("")) {
            ongkir=Double.parseDouble(Ongkir.getText()); 
        }
        if(ppn>0){
            besarppn=(ppn/100)*total;
            BesarPPN.setText(Valid.SetAngka(besarppn));
        }else{
            besarppn=0;
            BesarPPN.setText("0");
        }
        
        tagihanppn=besarppn+total+ongkir;
        TagihanPPn.setText(Valid.SetAngka(tagihanppn));        
        LKembali.setText(Valid.SetAngka(bayar-tagihanppn));     
    }
    
    public void isCek(){
        autoNomor();
        Ongkir.setText("0");
        TCari.requestFocus();
        if(akses.getjml2()>=1){
            kdptg.setEditable(false);
            BtnPtg.setEnabled(false);
            BtnSimpan.setEnabled(akses.gettoko_penjualan());
            BtnTambah.setEnabled(akses.gettoko_barang());
            kdptg.setText(akses.getkode());
            nmptg.setText(carijual.petugas.tampil3(kdptg.getText()));
        }    
        if(Sequel.cariIsi("select set_nota.tampilkan_tombol_nota_toko from set_nota").equals("Yes")){
            BtnNota.setVisible(true);
        }else{
            if(akses.getkode().equals("Admin Utama")){
                BtnNota.setVisible(true);
            }else{
                BtnNota.setVisible(false);
            }            
        }
    }
    
    public void autoNomor(){
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(tokopenjualan.nota_jual,5),signed)),0) from tokopenjualan where tokopenjualan.tgl_jual='"+Valid.SetTgl(Tgl.getSelectedItem()+"")+"' ",
                "TJ"+Tgl.getSelectedItem().toString().substring(6,10)+Tgl.getSelectedItem().toString().substring(3,5)+Tgl.getSelectedItem().toString().substring(0,2),5,NoNota); 
    }
private static double num(Object v) {
    try { return v == null ? 0 : Double.parseDouble(v.toString()); }
    catch (Exception e) { return 0; }
}

private static double toPack(double qty, double isi, double kapasitas, String jenis) {
    if (qty <= 0) return 0;
    if ("Distributor".equals(jenis)) return qty;                          // input sudah PACK
    if ("Grosir".equals(jenis))      return isi       > 0 ? qty / isi       : 0;  // grosir per PACK
    /* Retail / default */            return kapasitas > 0 ? qty / kapasitas : 0;  // retail per PACK
}
private double[] getIsiKapasitas(String kodeBrg) {
    try (PreparedStatement ps = koneksi.prepareStatement(
             "SELECT isi, kapasitas FROM tokobarang WHERE kode_brng=?")) {
        ps.setString(1, kodeBrg);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new double[] { rs.getDouble("isi"), rs.getDouble("kapasitas") };
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return new double[] {0, 0};
}
private void isSimpan() {
    String jenis = String.valueOf(Jenisjual.getSelectedItem());
    boolean okAll = true;

    for (int i = 0; i < tabMode.getRowCount(); i++) {
    double qtyInput = Valid.SetAngka(String.valueOf(tabMode.getValueAt(i, 0)));
    if (qtyInput <= 0) continue;

    String kode       = String.valueOf(tabMode.getValueAt(i, 1));
    String satuanRow  = String.valueOf(tabMode.getValueAt(i, 4)); // satuan yang terlihat (BOX/STRIP/TAB/PCS)
    String hargaShow  = String.valueOf(tabMode.getValueAt(i, 5));
    String dasar      = String.valueOf(tabMode.getValueAt(i, 12));

    // Ambil info satuan & konversi dari DB
    SatuanInfo si = getSatuanInfo(kode);

    // Tentukan potongan stok (selalu dalam PACK)
    double potongPack;
    if (satuanRow.equalsIgnoreCase(si.satPack)) {
        // Distributor / BOX
        potongPack = qtyInput;
    } else if (satuanRow.equalsIgnoreCase(si.satGrosir)) {
        // Grosir / STRIP
        potongPack = si.isi > 0 ? qtyInput / si.isi : 0;
    } else if (satuanRow.equalsIgnoreCase(si.satRetail)) {
        // Retail / PCS (TAB)
        double perPack = si.isi * si.kapasitas; // pcs per pack
        potongPack = perPack > 0 ? qtyInput / perPack : 0;
    } else {
        // fallback: asumsi pack
        potongPack = qtyInput;
    }

    if (potongPack <= 0) continue;

    boolean ok = Sequel.menyimpantf2(
        "toko_detail_jual","?,?,?,?,?,?,?,?,?,?,?","Barang",11,
        new String[]{
            NoNota.getText(), kode, satuanRow, hargaShow, dasar,
            String.valueOf(qtyInput),
            String.valueOf(tabMode.getValueAt(i,6)),
            String.valueOf(tabMode.getValueAt(i,7)),
            String.valueOf(tabMode.getValueAt(i,8)),
            String.valueOf(tabMode.getValueAt(i,9)),
            String.valueOf(tabMode.getValueAt(i,10))
        }
    );

    if (ok) {
        Trackbarang.catatRiwayat(kode, 0, qtyInput, "Penjualan", akses.getkode(), "Simpan");
        Sequel.mengedit("tokobarang", "kode_brng=?", "stok=stok-?", 2,
            new String[]{ String.valueOf(potongPack), kode });
    } else {
        okAll = false;
    }
}
sukses = okAll;

//    private void isSimpan() {
//        for(i=0;i<tabMode.getRowCount();i++){  
//            if(Valid.SetAngka(tabMode.getValueAt(i,0).toString())>0){
//                if(Sequel.menyimpantf2("toko_detail_jual","?,?,?,?,?,?,?,?,?,?,?","Barang",11,new String[]{
//                        NoNota.getText(),tabMode.getValueAt(i,1).toString(),tabMode.getValueAt(i,4).toString(),tabMode.getValueAt(i,5).toString(), 
//                        tabMode.getValueAt(i,12).toString(),tabMode.getValueAt(i,0).toString(),tabMode.getValueAt(i,6).toString(),
//                        tabMode.getValueAt(i,7).toString(),tabMode.getValueAt(i,8).toString(),tabMode.getValueAt(i,9).toString(),
//                        tabMode.getValueAt(i,10).toString()
//                    })==true){
//                    Trackbarang.catatRiwayat(tabMode.getValueAt(i,1).toString(),0,Valid.SetAngka(tabMode.getValueAt(i,0).toString()),"Penjualan", akses.getkode(),"Simpan");
//                    Sequel.mengedit("tokobarang","kode_brng=?","stok=stok-?",2,new String[]{
//                        tbObat.getValueAt(i,0).toString(),tbObat.getValueAt(i,1).toString()
//                    });
//                }else{
//                    sukses=false;
//                }
//            }
//        }
        if(sukses==true){
            Sequel.queryu("delete from tampjurnal");                    
            Sequel.menyimpan2("tampjurnal","'"+Penjualan_Toko+"','PENJUALAN TOKO','0','"+tagihanppn+"'","Rekening");    
            Sequel.menyimpan2("tampjurnal","'"+kode_akun_bayar+"','"+AkunBayar.getSelectedItem().toString()+"','"+tagihanppn+"','0'","Rekening"); 
            Sequel.menyimpan2("tampjurnal","'"+HPP_Barang_Toko+"','HPP Barang Toko','"+ttlhpp+"','0'","Rekening");    
            Sequel.menyimpan2("tampjurnal","'"+Persediaan_Barang_Toko+"','Persediaan Barang Toko','0','"+ttlhpp+"'","Rekening");                              
            sukses=jur.simpanJurnal(NoNota.getText(),"U","PENJUALAN TOKO / MINIMARKET / KOPERASI, OLEH "+akses.getkode());   
        }
    }
    
    private void tampilAkunBayar() {         
         try{      
             file=new File("./cache/akunbayar.iyem");
             file.createNewFile();
             fileWriter = new FileWriter(file);
             iyem="";
             ps=koneksi.prepareStatement("select * from akun_bayar order by nama_bayar");
             try{
                 rs=ps.executeQuery();
                 AkunBayar.removeAllItems();
                 while(rs.next()){    
                     AkunBayar.addItem(rs.getString(1).replaceAll("\"",""));
                     iyem=iyem+"{\"NamaAkun\":\""+rs.getString(1).replaceAll("\"","")+"\",\"KodeRek\":\""+rs.getString(2)+"\",\"PPN\":\""+rs.getDouble(3)+"\"},";
                 }
             }catch (Exception e) {
                 System.out.println("Notifikasi : "+e);
             } finally{
                 if(rs != null){
                     rs.close();
                 } 
                 if(ps != null){
                     ps.close();
                 } 
             }

             fileWriter.write("{\"akunbayar\":["+iyem.substring(0,iyem.length()-1)+"]}");
             fileWriter.flush();
             fileWriter.close();
             iyem=null;
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    private void tampilDokter() {
    File file = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    StringBuilder iyem = new StringBuilder();

    try {
        file = new File("./cache/dokter.iyem");
        file.getParentFile().mkdirs(); // jaga-jaga folder cache belum ada
        file.createNewFile();

        ps = koneksi.prepareStatement(
            "SELECT kd_dokter, nm_dokter, kd_sps FROM dokter where status = '1' ORDER BY nm_dokter"
        );
        rs = ps.executeQuery();

        DokterCombo.removeAllItems(); // ganti dengan nama combo kamu
        while (rs.next()) {
            String kd  = rs.getString(1) == null ? "" : rs.getString(1).replace("\"", "");
            String nama= rs.getString(2) == null ? "" : rs.getString(2).replace("\"", "");
            String sps = rs.getString(3) == null ? "" : rs.getString(3).replace("\"", "");

            // Tampilkan di combo (bebas: mau “kd - nama” atau cuma kode/nama)
            DokterCombo.addItem(kd + " - " + nama);

            // Susun JSON item
            iyem.append("{")
                .append("\"KodeDokter\":\"").append(kd).append("\",")
                .append("\"NamaDokter\":\"").append(nama).append("\",")
                .append("\"Spesialis\":\"").append(sps).append("\"")
                .append("},");
        }

        // Tutup trailing koma dan bungkus ke root "dokter"
        String payload = "{\"dokter\":[" + (iyem.length() > 0 ? iyem.substring(0, iyem.length() - 1) : "") + "]}";

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(payload);
        }
    } catch (Exception e) {
        System.out.println("Notifikasi tampilDokter : " + e);
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception ig) {}
        try { if (ps != null) ps.close(); } catch (Exception ig) {}
    }
}
   
    private String ambilKdDokterDariCombo() {
    Object sel = DokterCombo.getSelectedItem();
    if (sel == null) return null;
    String s = sel.toString();
    int p = s.indexOf(" - ");
    if (p <= 0) return null;
    return s.substring(0, p).trim(); // -> kd_dokter
}


    private void cariPPN() {
        try {
            myObj = new FileReader("./cache/akunbayar.iyem");
            root = mapper.readTree(myObj);
            response = root.path("akunbayar");
            if(response.isArray()){
               for(JsonNode list:response){
                   if(list.path("NamaAkun").asText().equals(AkunBayar.getSelectedItem().toString())){
                        Persenppn.setText(list.path("PPN").asText());
                   }
               }
            }
            myObj.close();
        } catch (Exception e) {
            Persenppn.setText("0");
        }
    }
    
   
    
    static class SatuanInfo {
    String satPack, satGrosir, satRetail;
    double isi, kapasitas;
}
@Override
public void dispose() {
    if (autoRefreshTimer != null && autoRefreshTimer.isRunning()) {
        autoRefreshTimer.stop();
    }
    super.dispose();
}


private SatuanInfo getSatuanInfo(String kodeBrg) {
    SatuanInfo si = new SatuanInfo();
    String sql = "SELECT kode_sat, kode_sat1, kode_sat2, isi, kapasitas FROM tokobarang WHERE kode_brng=?";
    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setString(1, kodeBrg);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                si.satPack    = rs.getString("kode_sat");
                si.satGrosir  = rs.getString("kode_sat1");
                si.satRetail  = rs.getString("kode_sat2");
                si.isi        = rs.getDouble("isi");
                si.kapasitas  = rs.getDouble("kapasitas");
            }
        }
    } catch (SQLException ex) { ex.printStackTrace(); }
    return si;
}
private static final int C_JML   = 0;
private static final int C_KODE  = 1;
private static final int C_NAMA  = 2;
private static final int C_KAT   = 3;
private static final int C_SAT   = 4;   // <-- kolom "Satuan" (editor dipasang di sini)
private static final int C_HRG   = 5;
private static final int C_SUB   = 6;
private static final int C_DSC   = 7;
private static final int C_DSCN  = 8;
private static final int C_TMB   = 9;
private static final int C_TOT   = 10;
private static final int C_STK   = 11;
private static final int C_HBELI = 12;
private void installSatuanEditor() {
    final javax.swing.JComboBox<String> comboSatuan = new javax.swing.JComboBox<String>();

    javax.swing.DefaultCellEditor editorSatuan = new javax.swing.DefaultCellEditor(comboSatuan) {
        @Override
        public java.awt.Component getTableCellEditorComponent(
                javax.swing.JTable table, Object value, boolean isSelected, int viewRow, int viewCol) {

            final int row = table.convertRowIndexToModel(viewRow);
            comboSatuan.removeAllItems();

            // ambil kode barang dari kolom "Kode Barang"
            Object kodeObj = table.getModel().getValueAt(row, COL_KODE);
            final String kodeBrg = (kodeObj == null) ? "" : kodeObj.toString();

            try {
                // opsi satuan dari tokobarang
                String sat0 = Sequel.cariIsi("SELECT kode_sat  FROM tokobarang WHERE kode_brng=?", kodeBrg);
                String sat1 = Sequel.cariIsi("SELECT kode_sat1 FROM tokobarang WHERE kode_brng=?", kodeBrg);
                String sat2 = Sequel.cariIsi("SELECT kode_sat2 FROM tokobarang WHERE kode_brng=?", kodeBrg);

                if (sat0 != null && !sat0.trim().isEmpty() && !"-".equals(sat0)) comboSatuan.addItem(sat0);
                if (sat1 != null && !sat1.trim().isEmpty() && !"-".equals(sat1)) comboSatuan.addItem(sat1);
                if (sat2 != null && !sat2.trim().isEmpty() && !"-".equals(sat2)) comboSatuan.addItem(sat2);
                double hResepAda = Valid.SetAngka(Sequel.cariIsi("SELECT h_resep FROM tokobarang WHERE kode_brng=?", kodeBrg));
                if (hResepAda > 0) {
                    comboSatuan.addItem("RSEP"); // label khusus
                        }
                if (comboSatuan.getItemCount() == 0) comboSatuan.addItem("PCS"); // fallback
            } catch (Exception ex) {
                System.out.println("Load satuan gagal: " + ex);
                if (comboSatuan.getItemCount() == 0) comboSatuan.addItem("PCS");
            }

            // set selected sesuai nilai lama cell (jika ada)
            if (value != null) comboSatuan.setSelectedItem(String.valueOf(value));

            // bersihkan listener lama → pasang baru
            java.awt.event.ItemListener[] all = comboSatuan.getItemListeners();
            for (int i = 0; i < all.length; i++) comboSatuan.removeItemListener(all[i]);

            comboSatuan.addItemListener(new java.awt.event.ItemListener() {
                @Override
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (e.getStateChange() != java.awt.event.ItemEvent.SELECTED) return;

                    String unitDipilih = (String) e.getItem();

                    // ambil data bantu
                    int isi        = Sequel.cariInteger("SELECT isi FROM tokobarang WHERE kode_brng=?", kodeBrg);
                    int kapasitas  = Sequel.cariInteger("SELECT kapasitas FROM tokobarang WHERE kode_brng=?", kodeBrg);

                    // harga sesuai jenis: distributor (kemasan besar), grosir (isi), retail (kecil), resep (kecil/khusus)
                    double hDistributor = Valid.SetAngka(Sequel.cariIsi("SELECT distributor FROM tokobarang WHERE kode_brng=?", kodeBrg));
                    double hGrosir      = Valid.SetAngka(Sequel.cariIsi("SELECT grosir      FROM tokobarang WHERE kode_brng=?", kodeBrg));
                    double hRetail      = Valid.SetAngka(Sequel.cariIsi("SELECT retail      FROM tokobarang WHERE kode_brng=?", kodeBrg));
                    double hResep       = Valid.SetAngka(Sequel.cariIsi("SELECT h_resep     FROM tokobarang WHERE kode_brng=?", kodeBrg));

                    String u0 = Sequel.cariIsi("SELECT kode_sat  FROM tokobarang WHERE kode_brng=?", kodeBrg);
                    String u1 = Sequel.cariIsi("SELECT kode_sat1 FROM tokobarang WHERE kode_brng=?", kodeBrg);
                    String u2 = Sequel.cariIsi("SELECT kode_sat2 FROM tokobarang WHERE kode_brng=?", kodeBrg);

                    // tentukan harga berdasarkan satuan yg dipilih
                    double harga;
                    if (unitDipilih.equalsIgnoreCase(u0))       harga = hDistributor;                  // kemasan besar
                    else if (unitDipilih.equalsIgnoreCase(u1))  harga = hGrosir;                       // satuan isi
                    else if (hResep > 0)                        harga = hResep;                        // kalau ada harga resep
                    else                                        harga = hRetail;                       // default unit kecil

                    // stok disimpan sbg unit terkecil → konversi agar tampil sesuai satuan yang dipilih
                    double stokPack = Valid.SetAngka(Sequel.cariIsi("SELECT stok FROM tokobarang WHERE kode_brng=?", kodeBrg));
                    //double stokTampil   = stokPack; // default untuk unit terkecil
                   double stokTampil;
                   final String unitFinal;      // untuk tampilan
                    final String satCodeToSave;  // untuk simpan ke DB
                    final double hargaFinal;
                    final double stokTampilFinal;
                        if ("RSEP".equalsIgnoreCase(unitDipilih)) {
                        unitFinal      = u2;  // tampil tetap “RESEP”
                        satCodeToSave  = u2;       // yang disimpan u2
                        hargaFinal = hResep;
                        stokTampilFinal= stokPack * isi * kapasitas;
                    } else if (unitDipilih.equalsIgnoreCase(u0)) {
                        unitFinal      = u0;
                        satCodeToSave  = u0;
                        hargaFinal     = hDistributor;
                        stokTampilFinal= stokPack;
                    } else if (unitDipilih.equalsIgnoreCase(u1)) {
                        unitFinal      = u1;
                        satCodeToSave  = u1;
                        hargaFinal     = hGrosir;
                        stokTampilFinal= stokPack * isi;
                    } else { // u2 / lainnya
                        unitFinal      = u2;
                        satCodeToSave  = u2;
                        hargaFinal     = hRetail;
                        stokTampilFinal= stokPack * isi * kapasitas;
                    }
                 final int mr = row;
//                    final String unitFinal       = unitDipilih;
//                    final double hargaFinal      = harga;
//                    final double stokTampilFinal = stokTampil;

                    SwingUtilities.invokeLater(() -> {
                        TableModel m = table.getModel();
                        m.setValueAt(unitFinal,       mr, C_SAT);
                        m.setValueAt(hargaFinal,      mr, C_HRG);
                        m.setValueAt(stokTampilFinal, mr, C_STK);   // kalau mau update stok tampil
                        recalcRow(m, mr);
                    });
                }
            });

            return super.getTableCellEditorComponent(table, value, isSelected, viewRow, viewCol);
        }
    };

    int viewCol = tbObat.convertColumnIndexToView(C_SAT);
    tbObat.getColumnModel().getColumn(viewCol).setCellEditor(editorSatuan);
}
private void recalcRow(TableModel model, int row) {
    double qty    = d(model.getValueAt(row, C_JML));
    double harga  = d(model.getValueAt(row, C_HRG));
    double dscPct = d(model.getValueAt(row, C_DSC));
    double tmbRp  = d(model.getValueAt(row, C_TMB));

    double subtotal = qty * harga;
    double dscRp    = Math.round(subtotal * (dscPct / 100.0));
    double total    = subtotal - dscRp + tmbRp;

    // update kolom-kolom hitungan
    if (model instanceof javax.swing.table.TableModel) {
    javax.swing.table.TableModel tm = (javax.swing.table.TableModel) model;
    tm.setValueAt(subtotal, row, C_SUB);
    tm.setValueAt(dscRp,   row, C_DSCN);
    tm.setValueAt(total,   row, C_TOT);
}
}

// === di deklarasi kelas DlgTokoPenjualan ===



// panggil ini di constructor/settup form setelah initComponents()
private void initPanelResepToko() {
    panelResepToko = new javax.swing.JPanel(new java.awt.BorderLayout());
    tmResepToko = new javax.swing.table.DefaultTableModel(
        null,
        new Object[]{"No. Resep","Nama Pasien","Nama Obat","Jml","Satuan","Aturan Pakai","Dokter","Keterangan","Tgl"}
    ){
        Class<?>[] types = new Class[]{
            Long.class, String.class, String.class, Double.class,
            String.class, String.class, String.class,String.class, java.sql.Timestamp.class
        };
        @Override public Class<?> getColumnClass(int c){ return types[c]; }
        @Override public boolean isCellEditable(int r,int c){ 
            // izinkan edit Jml & Aturan
            return c==3 || c==5;
        }
    };
    
    tbResepToko = new javax.swing.JTable(tmResepToko);
    tbResepToko.setAutoCreateRowSorter(true);
    tbResepToko.setRowHeight(26);
    tbResepToko.getColumnModel().getColumn(7).setMinWidth(0);
    tbResepToko.getColumnModel().getColumn(7).setMaxWidth(0); // hide ID
applyResepTokoStyles();
    // editor angka utk kolom Jml
    javax.swing.JFormattedTextField num = new javax.swing.JFormattedTextField(new java.text.DecimalFormat("#0.##"));
    num.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    tbResepToko.getColumnModel().getColumn(3).setCellEditor(new javax.swing.DefaultCellEditor(num));

    // simpan perubahan inline (Jml/Aturan) → update DB
    tmResepToko.addTableModelListener(ev -> {
        if (ev.getType()==javax.swing.event.TableModelEvent.UPDATE) {
            int r = ev.getFirstRow();
            if (r>=0) { updateResepTokoDariTabel(r); }
        }
    });

    spResepToko = new javax.swing.JScrollPane(tbResepToko);
    panelResepToko.add(spResepToko, java.awt.BorderLayout.CENTER);

    // taruh panelResepToko ke panel kananmu (misal panelKanan)
    jPanel2.setLayout(new java.awt.BorderLayout());
   jPanel2.add(panelResepToko, java.awt.BorderLayout.CENTER);
//    autoRefreshTimer.setRepeats(true);
//    autoRefreshTimer.start();
   
}

private void loadResepTokoByTanggal(java.util.Date tanggal) {
    // kosongkan tabel dulu
    Valid.tabelKosong(tmResepToko);
    if (tanggal == null) return;

    // hitung rentang [00:00:00, 24:00:00) untuk tanggal tsb
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.setTime(tanggal);
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
    cal.set(java.util.Calendar.MINUTE, 0);
    cal.set(java.util.Calendar.SECOND, 0);
    cal.set(java.util.Calendar.MILLISECOND, 0);
    java.sql.Timestamp start = new java.sql.Timestamp(cal.getTimeInMillis());
    cal.add(java.util.Calendar.DATE, 1);
    java.sql.Timestamp end = new java.sql.Timestamp(cal.getTimeInMillis());

    System.out.println("[TOKO] LOAD by date: " + start + " .. " + end);

    String sql =
      "SELECT resep_toko.id,resep_toko.kode_brng,resep_toko.nama_brng,resep_toko.jumlah,resep_toko.satuan,resep_toko.aturan_pakai,resep_toko.kd_dokter," +
      "       COALESCE(ket,'') AS keterangan, resep_toko.tgl_resep, no_resep, pasien.nm_pasien " +
      "FROM resep_toko " +
            "INNER JOIN reg_periksa  ON reg_periksa.no_rawat = resep_toko.no_rawat " +
        "INNER JOIN pasien  ON pasien.no_rkm_medis = reg_periksa.no_rkm_medis " +
      "WHERE tgl_resep >= ? AND tgl_resep < ? " +
      "ORDER BY resep_toko.tgl_resep, resep_toko.id";

    java.sql.PreparedStatement ps = null;
    java.sql.ResultSet rs = null;
    try {
        ps = koneksi.prepareStatement(sql);
        ps.setTimestamp(1, start);
        ps.setTimestamp(2, end);
        rs = ps.executeQuery();
        int add = 0;
        while (rs.next()) {
            tmResepToko.addRow(new Object[]{
                rs.getLong("no_resep"),
                rs.getString("nm_pasien"),
                rs.getString("nama_brng"),
                rs.getDouble("jumlah"),
                rs.getString("satuan"),
                rs.getString("aturan_pakai"),
                rs.getString("kd_dokter"),
                rs.getString("keterangan"),
                rs.getTimestamp("tgl_resep")
            });
            add++;
            
        }
        System.out.println("[TOKO] added rows = " + add);
    } catch (Exception e) {
        System.out.println("loadResepTokoByTanggal: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception ig) {}
        try { if (ps != null) ps.close(); } catch (Exception ig) {}
    }

    // pastikan tabel render
    javax.swing.SwingUtilities.invokeLater(() -> {
        if (tbResepToko.getModel() != tmResepToko) tbResepToko.setModel(tmResepToko);
        if (tbResepToko.getRowSorter() instanceof javax.swing.table.TableRowSorter) {
            ((javax.swing.table.TableRowSorter<?>) tbResepToko.getRowSorter()).setRowFilter(null);
        }
        tmResepToko.fireTableDataChanged();
        panelResepToko.revalidate();
        panelResepToko.repaint();
        jPanel2.revalidate();
        jPanel2.repaint();
    });
     
}

private void loadResepToko(String noResep) {
    Valid.tabelKosong(tmResepToko);
    if (noResep == null || noResep.trim().isEmpty()) return;

    System.out.println("[TOKO] LOAD no_resep = " + noResep);

    PreparedStatement pc = null;
    ResultSet rc = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        // cek jumlah baris
        String sqlCount = "SELECT COUNT(*) FROM resep_toko WHERE no_resep=?";
        pc = koneksi.prepareStatement(sqlCount);
        pc.setString(1, noResep.trim());
        rc = pc.executeQuery();
        if (rc.next()) {
            System.out.println("[TOKO] jumlah baris di DB utk no_resep itu = " + rc.getInt(1));
        }

        // load data
        String sql = "SELECT id,kode_brng,nama_brng,jumlah,satuan,aturan_pakai,kd_dokter,tgl_resep " +
                     "FROM resep_toko WHERE no_resep=? ORDER BY id";
        ps = koneksi.prepareStatement(sql);
        ps.setString(1, noResep.trim());
        rs = ps.executeQuery();
       while(rs.next()){
    Object[] row = new Object[]{
        rs.getLong("id"),
        rs.getString("kode_brng"),
        rs.getString("nama_brng"),
        rs.getDouble("jumlah"),
        rs.getString("satuan"),
        rs.getString("aturan_pakai"),
        rs.getString("kd_dokter"),
        rs.getTimestamp("tgl_resep")
    };
    
//    autoRefreshTimer.setRepeats(true);
//    autoRefreshTimer.start();
    System.out.println("[TOKO] ADD ROW: " + java.util.Arrays.toString(row));
    tmResepToko.addRow(row);
    if (tbResepToko.getRowSorter() instanceof javax.swing.table.TableRowSorter) {
    ((javax.swing.table.TableRowSorter<?>) tbResepToko.getRowSorter()).setRowFilter(null);
}
System.out.println("[TOKO] tmResepToko.rowCount = " + tmResepToko.getRowCount());
System.out.println("[TOKO] tb.model == tm? " + (tbResepToko.getModel() == tmResepToko));
System.out.println("[TOKO] tbResepToko=" + System.identityHashCode(tbResepToko)
                   + " tmResepToko=" + System.identityHashCode(tmResepToko)
                   + " tb.model=" + System.identityHashCode(tbResepToko.getModel()));
}
System.out.println("[TOKO] TOTAL ROWS DI MODEL = " + tmResepToko.getRowCount());
    } catch (SQLException e) {
        System.out.println("loadResepToko: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception ig) {}
        try { if (ps != null) ps.close(); } catch (Exception ig) {}
        try { if (rc != null) rc.close(); } catch (Exception ig) {}
        try { if (pc != null) pc.close(); } catch (Exception ig) {}
    }
}



private void updateResepTokoDariTabel(int row) {
    try {
        long id = (long) tmResepToko.getValueAt(row, 0);
        double jml = Valid.SetAngka(String.valueOf(tmResepToko.getValueAt(row, 3)));
        String aturan = String.valueOf(tmResepToko.getValueAt(row, 5));

        try (PreparedStatement ps = koneksi.prepareStatement(
                "UPDATE resep_toko SET jumlah=?, aturan_pakai=? WHERE id=?")) {
            ps.setDouble(1, jml);
            ps.setString(2, aturan==null || aturan.trim().isEmpty() ? null : aturan.trim());
            ps.setLong(3, id);
            ps.executeUpdate();
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null,"Gagal update resep: "+e.getMessage());
    }
}

private void cariResepToko(String keyword) {
    javax.swing.RowFilter<javax.swing.table.TableModel,Object> rf =
        javax.swing.RowFilter.regexFilter("(?i)"+keyword);
    ((javax.swing.table.TableRowSorter<?>) tbResepToko.getRowSorter()).setRowFilter(rf);
}
public void setNoResep(String noResep) {
    loadResepToko(noResep);
}
//public void onResepTokoSaved(String noResep) {
//    this.currentNoResep = (noResep == null ? "" : noResep.trim());
//    System.out.println("[TOKO] onResepTokoSaved -> " + this.currentNoResep);
//    loadResepToko(this.currentNoResep);
//}
    // kolom-kolom di JTable kamu (sesuaikan indeksnya)
 // "Stok" paling kanan

// panggil ini saat combobox "Jns. Jual" berubah ATAU saat baris dipilih
//private void applyJenisJualKeBaris(int row) throws SQLException {
//    if (row < 0) return;
//    String jenis = Jenisjual.getSelectedItem().toString(); // "Distributor" | "Grosir" | "Retail"
//    String kodeBrg = String.valueOf(tabMode.getValueAt(row, COL_KODE));
//
//    // ambil meta barang sekali aja (bisa lewat DAO)
//    BarangMeta m = daoBarang.getMeta(kodeBrg);
//    // m.kodeSatuan, m.kodeSatuan1, m.kodeSatuan2, m.isi, m.kapasitas, m.hargaDistributor, m.hargaGrosir, m.hargaRetail, m.stokPack
//    // Catatan:
//    // - stokPack = stok dalam satuan PACK (== kode_satuan / distributor)
//    // - isi       = jumlah "grosir unit" per PACK
//    // - kapasitas = jumlah "retail unit" per PACK
//    int isi = m.isi > 0 ? m.isi : 1;
//    int kapasitas = m.kapasitas > 0 ? m.kapasitas : 1;
//
//    String satuan;
//    double harga;
//    long stokTampil; // stok yang ditampilkan sesuai satuan yang dipilih
//
//    switch (jenis) {
//        case "Distributor":
//            satuan = m.kodeSatuan;              // kode_satuan
//            harga  = m.hargaDistributor;        // kolom "distributor"
//            stokTampil = m.stokPack;            // stok apa adanya (per PACK)
//            break;
//        case "Grosir":
//            satuan = m.kodeSatuan1;             // kode_satuan1
//            harga  = m.hargaGrosir;             // kolom "grosir"
//            stokTampil = m.stokPack * isi;      // stok per grosir-unit = pack * isi
//            break;
//        default: // "Retail"
//            satuan = m.kodeSatuan2;             // kode_satuan2
//            harga  = m.hargaRetail;             // kolom "retail"
//            stokTampil = m.stokPack * kapasitas;// stok per retail-unit = pack * kapasitas
//    }
//
//    // set ke tabel
//    tabMode.setValueAt(satuan, row, COL_SATUAN);
//    tabMode.setValueAt(Valid.SetAngka(harga), row, COL_HARGA);
//    tabMode.setValueAt(stokTampil, row, COL_STOK);
//}

// tambahkan listener pada combobox jenis jual

private void toggleDokter() {
    Object sel = Jenisjual.getSelectedItem();
    String val = sel == null ? "" : sel.toString().trim().toLowerCase();

    boolean isResep = val.contains("resep"); // cover "Resep" / "Resep Baru" / dll

    label11.setVisible(isResep);
    DokterCombo.setVisible(isResep);
    if (DokterCombo != null) DokterCombo.setVisible(isResep);

    if (!isResep) {
        DokterCombo.setSelectedIndex(-1); // kosongkan saat bukan resep
    } else {
        DokterCombo.requestFocusInWindow(); // fokuskan saat muncul
    }

    // refresh layout supaya langsung berubah
    panelisi3.revalidate();  // ganti dengan panel container-nya (mis. panelInput/panelHeader)
    panelisi3.repaint();
}

public void onResepTokoSaved(String noResep) {
    this.currentNoResep = (noResep == null ? "" : noResep.trim());
    initPanelResepTokoOnce();
    javax.swing.SwingUtilities.invokeLater(() -> {
        loadResepToko(this.currentNoResep);                 // isi tmResepToko
        // paksa JTable pakai model yang sama & render ulang
        if (tbResepToko.getModel() != tmResepToko) {
            tbResepToko.setModel(tmResepToko);
        }
        tmResepToko.fireTableDataChanged();
        tbResepToko.revalidate();
        tbResepToko.repaint();

        System.out.println("[TOKO] model sama? " + (tbResepToko.getModel() == tmResepToko));
        System.out.println("[TOKO] JTable rows now = " + tbResepToko.getRowCount());
    });
}


//private void initPanelResepTokoOnce() {
//    if (resepPanelInited) return;
//    initPanelResepToko();            // method yang kamu punya
//    resepPanelInited = true;
//}

private void initPanelResepTokoOnce() {
    if (resepPanelInited) return;
    //initPanelResepToko(); // method-mu yang bikin tabel, scrollpane, dsb
    resepPanelInited = true;

    // === Toolbar atas: Auto Refresh + Interval ===
    javax.swing.JPanel topBar = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 6));
    cbAutoRefresh = new javax.swing.JCheckBox("🔄 Auto Refresh");
    cbIntervalDetik = new javax.swing.JComboBox<>(new Integer[]{15, 30, 45, 60}); // detik
    cbIntervalDetik.setSelectedItem(30); // default 30 detik
    topBar.add(cbAutoRefresh);
    topBar.add(new javax.swing.JLabel("Interval (detik):"));
    topBar.add(cbIntervalDetik);

    // Sisipkan topBar ke atas panelResepToko
    panelResepToko.add(topBar, java.awt.BorderLayout.NORTH);

    // === Timer: awalnya OFF ===
    autoRefreshTimer = new javax.swing.Timer(30_000, e -> {
        if (resepMode == ResepMode.HEADER) {
        loadResepHeaderByTanggal(new java.util.Date());
    } else if (resepMode == ResepMode.DETAIL && currentNoResep != null) {
        loadResepToko(currentNoResep); // kalau memang mau refresh detail
    }
    System.out.println("[TOKO] Auto-refresh at " + new java.util.Date());
    });
    autoRefreshTimer.setRepeats(true);

    // === Listener toggle ===
    cbAutoRefresh.addActionListener(ev -> {
        boolean on = cbAutoRefresh.isSelected();
        // update interval tiap kali user ganti/toggle
        int detik = (Integer) cbIntervalDetik.getSelectedItem();
        autoRefreshTimer.setDelay(detik * 1000);
        autoRefreshTimer.setInitialDelay(detik * 1000);

        if (on) autoRefreshTimer.start(); else autoRefreshTimer.stop();
        System.out.println("[TOKO] AutoRefresh " + (on ? "ON" : "OFF") + " / " + detik + " dtk");
    });

    // === Listener interval (kalau auto-refresh sedang ON, ganti delay langsung) ===
    cbIntervalDetik.addActionListener(ev -> {
        if (autoRefreshTimer != null && autoRefreshTimer.isRunning()) {
            int detik = (Integer) cbIntervalDetik.getSelectedItem();
            autoRefreshTimer.setDelay(detik * 1000);
            autoRefreshTimer.setInitialDelay(detik * 1000);
            System.out.println("[TOKO] Interval diganti jadi " + detik + " dtk");
        }
    });

    // === Pastikan panel kanan benar-benar terpasang & tampil ===
    jPanel2.setLayout(new java.awt.BorderLayout());
    jPanel2.removeAll();
    jPanel2.add(panelResepToko, java.awt.BorderLayout.CENTER);
    jPanel2.revalidate();
    jPanel2.repaint();
}



private void applyResepTokoStyles() {
    // ===== 1) JTable dengan zebra striping =====
    tbResepToko = new javax.swing.JTable(tmResepToko) {
        @Override
        public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
            java.awt.Component c = super.prepareRenderer(r, row, col);
            if (!isRowSelected(row)) {
                c.setBackground((row % 2 == 0) ? new java.awt.Color(250,253,255)
                                               : new java.awt.Color(242,246,252));
            } else {
                c.setBackground(new java.awt.Color(205,230,255));
            }
            return c;
        }
    };

    tbResepToko.setAutoCreateRowSorter(true);
    tbResepToko.setRowHeight(28);
    tbResepToko.setShowHorizontalLines(true);
    tbResepToko.setShowVerticalLines(false);
    tbResepToko.setGridColor(new java.awt.Color(225,232,240));
    tbResepToko.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

    // ===== 2) Header =====
    javax.swing.table.JTableHeader hdr = tbResepToko.getTableHeader();
    hdr.setReorderingAllowed(false);
    hdr.setPreferredSize(new java.awt.Dimension(hdr.getPreferredSize().width, 30));
    hdr.setFont(hdr.getFont().deriveFont(java.awt.Font.BOLD, 12f));
    javax.swing.table.DefaultTableCellRenderer headerR =
        (javax.swing.table.DefaultTableCellRenderer) hdr.getDefaultRenderer();
    headerR.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    headerR.setBackground(new java.awt.Color(230,240,250));
    headerR.setForeground(new java.awt.Color(40,50,70));

    // ===== 3) Renderer umum =====
    javax.swing.table.DefaultTableCellRenderer left   = new javax.swing.table.DefaultTableCellRenderer();
    left.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

    javax.swing.table.DefaultTableCellRenderer center = new javax.swing.table.DefaultTableCellRenderer();
    center.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    javax.swing.table.DefaultTableCellRenderer right  = new javax.swing.table.DefaultTableCellRenderer();
    right.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

    // angka (kolom Jml)
    javax.swing.table.DefaultTableCellRenderer numRenderer = new javax.swing.table.DefaultTableCellRenderer() {
        private final java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.##");
        @Override
        public void setValue(Object value) {
            if (value instanceof Number) {
                setText(df.format(((Number) value).doubleValue()));
            } else {
                setText(value == null ? "" : value.toString());
            }
            setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        }
    };

    // tanggal
    javax.swing.table.DefaultTableCellRenderer dateRenderer = new javax.swing.table.DefaultTableCellRenderer() {
        private final java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("MMM d, yyyy");
        @Override
        public void setValue(Object value) {
            if (value instanceof java.util.Date) {
                setText(f.format((java.util.Date) value));
            } else if (value instanceof java.sql.Timestamp) {
                setText(f.format((java.sql.Timestamp) value));
            } else {
                setText(value == null ? "" : value.toString());
            }
            setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }
    };

    // aturan pakai (wrap text)
    javax.swing.table.TableCellRenderer wrapRenderer = new javax.swing.table.TableCellRenderer() {
        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            javax.swing.JTextArea ta = new javax.swing.JTextArea();
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setOpaque(true);
            ta.setFont(table.getFont());
            ta.setText(value == null ? "" : value.toString());
            if (!isSelected) {
                ta.setBackground((row % 2 == 0) ? new java.awt.Color(250,253,255)
                                                : new java.awt.Color(242,246,252));
            } else {
                ta.setBackground(new java.awt.Color(205,230,255));
            }
            ta.setBorder(javax.swing.BorderFactory.createEmptyBorder(4,4,4,4));
            return ta;
        }
    };

    // ===== 4) Set renderer per kolom =====
    javax.swing.table.TableColumnModel cols = tbResepToko.getColumnModel();
    // Header: {"ID","No. Resep","No. Rawat","Nama Pasien","Jml","Satuan","Aturan Pakai","Dokter","Tgl"}
    cols.getColumn(1).setCellRenderer(center);      // No. Resep
    cols.getColumn(2).setCellRenderer(center);      // No. Rawat
    cols.getColumn(3).setCellRenderer(left);        // Nama Pasien
    cols.getColumn(4).setCellRenderer(numRenderer); // Jml
    cols.getColumn(5).setCellRenderer(center);      // Satuan
    cols.getColumn(6).setCellRenderer(wrapRenderer);// Aturan Pakai
    cols.getColumn(7).setCellRenderer(center);      // Dokter
    cols.getColumn(8).setCellRenderer(dateRenderer);// Tgl

    // ===== 5) Editor angka utk kolom Jml =====
    javax.swing.JFormattedTextField num = new javax.swing.JFormattedTextField(new java.text.DecimalFormat("#0.##"));
    num.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    cols.getColumn(4).setCellEditor(new javax.swing.DefaultCellEditor(num));

    // ===== 6) Lebar kolom =====
    int[] pref = {140, 110, 140, 170, 60, 70, 160, 0, 110};
    for (int i = 1; i < pref.length; i++) { // kolom 0 (ID) tetap hidden
        cols.getColumn(i).setPreferredWidth(pref[i]);
    }
    cols.getColumn(7).setMinWidth(0);
    cols.getColumn(7).setMaxWidth(0);
    cols.getColumn(7).setPreferredWidth(0);
}

//private javax.swing.JTable tbResep;               // tabel kanan "Daftar Resep Dokter"
//private javax.swing.table.DefaultTableModel tmResepHeader;
private boolean isLoadingHeader = false;

private void initResepHeaderTable(){
    tmResepHeader = new DefaultTableModel(
        null, new Object[]{"No. Resep","Nama Pasien","Jml Item","Dokter","Tgl"}
    ){ @Override public boolean isCellEditable(int r,int c){ return false; } };

    tbResepToko.setModel(tmResepHeader);
    tbResepToko.setAutoCreateRowSorter(true);

    tbResepToko.getSelectionModel().addListSelectionListener(e -> {
        if (e.getValueIsAdjusting() || isLoadingHeader) return;

        int viewRow = tbResepToko.getSelectedRow();
        if (viewRow < 0) return;

        int modelRow = tbResepToko.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= tmResepHeader.getRowCount()) return;

        String noResep = String.valueOf(tmResepHeader.getValueAt(modelRow, 0));
       // muatDetailResepKePenjualan(noResep);
    });
}
/** Tampilkan satu baris per no_resep sesuai tanggal (atau filter lain) */
private void loadResepHeaderByTanggal(java.util.Date tgl){
    if (tmResepHeader == null) return;

    isLoadingHeader = true;                 // tahan listener klik selama reload
    try {
        tmResepHeader.setRowCount(0);

        // start = 00:00 hari tsb, end = 00:00 besok (half-open)
        java.sql.Timestamp start = java.sql.Timestamp.valueOf(
            new java.text.SimpleDateFormat("yyyy-MM-dd").format(tgl) + " 00:00:00");
        java.sql.Timestamp end = new java.sql.Timestamp(start.getTime() + 24L*60*60*1000);

        final String sql =
            "SELECT rt.no_resep,rt.status,d.nm_dokter, " +
            "       MAX(p.nm_pasien)        AS nm_pasien, " +
            "       COUNT(*)                AS jml_item, " +
            "       MAX(rt.kd_dokter)       AS kd_dokter, " +
            "       COALESCE(rt.keterangan, '') AS jenis, " +   
            "       DATE(MIN(rt.tgl_resep)) AS tgl " +
            "FROM resep_toko rt " +
            "JOIN reg_periksa rp ON rp.no_rawat = rt.no_rawat " +
                " JOIN dokter d ON d.kd_dokter = rt.kd_dokter " +
            "JOIN pasien      p  ON p.no_rkm_medis = rp.no_rkm_medis " +
            "WHERE rt.tgl_resep >= ? AND rt.tgl_resep < ? " +
            "GROUP BY rt.no_resep " +
            "ORDER BY MIN(rt.tgl_resep) DESC";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tmResepHeader.addRow(new Object[]{
                        rs.getString("no_resep"),
                        rs.getString("nm_pasien"),
                        rs.getInt("jml_item"),
                        rs.getString("kd_dokter"),
                        rs.getDate("tgl"),
                        rs.getString("jenis")
                    });
                }
            }
        }
    } catch (Exception ex) {
        System.out.println("loadResepHeaderByTanggal: " + ex.getMessage());
    } finally {
        isLoadingHeader = false;            // lepas guard listener
        if (tbResepToko != null) tbResepToko.clearSelection(); // hilangkan seleksi “hantu”
    }
}

// ==== HEADER: per nomor resep ====
private javax.swing.JTable tbResepHeader;
private javax.swing.table.DefaultTableModel tmResepHeader;

// ==== DETAIL: item obat ====
private javax.swing.JTable tbResepDetail;
private javax.swing.table.DefaultTableModel tmResepDetail;


   private void initPanelResepMasterDetail() {
    // --- model header (per no resep) ---
    tmResepHeader = new javax.swing.table.DefaultTableModel(
        null,
       new Object[]{"No. Resep","Nama Pasien","Dokter","Jml Item","Tgl","Status","Kd Dokter","NoRM","Jenis Resep"}
    ){
        Class<?>[] t = new Class[]{
            String.class, String.class, String.class, Integer.class, 
            java.sql.Timestamp.class, String.class, String.class, String.class,String.class
        };
        @Override public Class<?> getColumnClass(int c){ return t[c]; }
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };

    tbResepHeader = new javax.swing.JTable(tmResepHeader);
    tbResepHeader.setAutoCreateRowSorter(true);

// Sembunyikan kolom "Kd Dokter"
TableColumnModel cm = tbResepHeader.getColumnModel();
cm.getColumn(5).setPreferredWidth(80); // Status
cm.getColumn(6).setMinWidth(0); cm.getColumn(6).setMaxWidth(0); cm.getColumn(6).setPreferredWidth(0); // Kd Dokter hidden
cm.getColumn(7).setMinWidth(0); cm.getColumn(7).setMaxWidth(0); cm.getColumn(7).setPreferredWidth(0); 
cm.getColumn(8).setPreferredWidth(80);

    // --- model detail (item dalam resep) ---
    tmResepDetail = new javax.swing.table.DefaultTableModel(
        null,
        new Object[]{"Kode","Nama Obat","Jml","Satuan","Aturan Pakai","Keterangan", "Nama Racikan"}
    ){
        Class<?>[] t = new Class[]{String.class,String.class,Double.class,String.class,String.class,String.class, String.class};
        @Override public Class<?> getColumnClass(int c){ return t[c]; }
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    tbResepDetail = new javax.swing.JTable(tmResepDetail);
    tbResepDetail.setAutoCreateRowSorter(true);
    tbResepDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
TableColumnModel colModel = tbResepDetail.getColumnModel();

// Kolom Kode
colModel.getColumn(0).setPreferredWidth(100);

// Kolom Nama Obat → dibuat lebih lebar
colModel.getColumn(1).setPreferredWidth(250);

// Kolom Jml
colModel.getColumn(2).setPreferredWidth(60);

// Kolom Satuan
colModel.getColumn(3).setPreferredWidth(80);

// Kolom Aturan Pakai
colModel.getColumn(4).setPreferredWidth(120);

// Kolom Keterangan
colModel.getColumn(5).setPreferredWidth(80);

colModel.getColumn(5).setPreferredWidth(120);
    
    

    // tata letak 2 tabel (atas = header, bawah = detail)
    jPanel2.setLayout(new java.awt.BorderLayout());
    jPanel2.add(new javax.swing.JScrollPane(tbResepHeader), java.awt.BorderLayout.NORTH);
   jPanel3.setLayout(new java.awt.BorderLayout());
jPanel3.add(new javax.swing.JScrollPane(tbResepDetail), java.awt.BorderLayout.CENTER);

    // ketika pilih header → load detail
    tbResepHeader.getSelectionModel().addListSelectionListener(e -> {
    if (e.getValueIsAdjusting()) return;
    int viewRow = tbResepHeader.getSelectedRow();
    if (viewRow < 0) return;

    int modelRow = tbResepHeader.convertRowIndexToModel(viewRow);

    String noResep  = String.valueOf(tmResepHeader.getValueAt(modelRow, 0)); // No. Resep
    String nmPasien = String.valueOf(tmResepHeader.getValueAt(modelRow, 1)); // Nama Pasien
    String kdDokter = String.valueOf(tmResepHeader.getValueAt(modelRow, 6));
selectDokterByKode(kdDokter);
String noRM     = String.valueOf(tmResepHeader.getValueAt(modelRow, 7));// hidden col
//String jenis    = String.valueOf(tmResepHeader.getValueAt(modelRow, 7));

// simpan ke variabel global
    noResepTerpilih = noResep;
    // 1) Tampilkan detail resep
    loadResepDetail(noResep);

    // 2) Isi field pasien
    nmmem.setText(nmPasien);
    kdmem.setText(noRM);

    // 3) Set combo dokter by kode
    for (int i = 0; i < DokterCombo.getItemCount(); i++) {
        Object obj = DokterCombo.getItemAt(i);
        if (obj instanceof DokterItem) {
            DokterItem d = (DokterItem) obj;
            if (d.kd.equals(kdDokter)) {
                DokterCombo.setSelectedIndex(i);
                break;
            }
        }
    }
    // 4) Paksa jenis jual = Resep
    try {
        Jenisjual.setSelectedItem("Resep");   // pastikan ada item "Resep" di combobox
    } catch (Exception ex) {
        System.out.println("[WARN] Jenis Jual tidak ada item Resep");
    }
});
//    tbResepHeader.getSelectionModel().addListSelectionListener(e -> {
//    if (e.getValueIsAdjusting()) return;
//    int viewRow = tbResepHeader.getSelectedRow();
//    if (viewRow < 0) return;
//
//    int modelRow = tbResepHeader.convertRowIndexToModel(viewRow);
//    String noResep = String.valueOf(tmResepHeader.getValueAt(modelRow, 0)); // kolom 0 = No. Resep
//    loadResepDetail(noResep);
//});
}

   
private void selectDokterByKode(String kdDokter) {
    if (kdDokter == null || kdDokter.trim().isEmpty()) return;
    for (int i = 0; i < DokterCombo.getItemCount(); i++) {
        Object it = DokterCombo.getItemAt(i);
        if (it instanceof DokterItem) {
            DokterItem d = (DokterItem) it;
            if (kdDokter.equals(d.kd)) {
                DokterCombo.setSelectedIndex(i);
                return;
            }
        } else {
            // fallback kalau item combonya masih String "KD - Nama"
            String s = String.valueOf(it);
            if (s.startsWith(kdDokter + " ")) {
                DokterCombo.setSelectedIndex(i);
                return;
            }
        }
    }
}

private void attachHeaderPopup() {
    JPopupMenu pm = new JPopupMenu();
    JMenuItem miDetail = new JMenuItem("Lihat Detail Resep");
    pm.add(miDetail);

    miDetail.addActionListener(_e -> {
        int viewRow = tbResepHeader.getSelectedRow();
        if (viewRow < 0) return;
        int modelRow = tbResepHeader.convertRowIndexToModel(viewRow);
        String noResep = String.valueOf(tmResepHeader.getValueAt(modelRow, 0));
        showResepDetailDialog(noResep); // pakai dialog di atas
    });

    tbResepHeader.setComponentPopupMenu(pm);
}

private JDialog dlgDetail;
//private JTable tbResepDetail;
//private DefaultTableModel tmResepDetail;

private void initResepDetailDialog() {
    tmResepDetail = new DefaultTableModel(
        null, new Object[]{"Kode","Nama Obat","Jml","Satuan","Aturan","Ket"}
    ){
        @Override public boolean isCellEditable(int r,int c){ return false; }
        @Override public Class<?> getColumnClass(int c){
            return new Class[]{String.class,String.class,Double.class,String.class,String.class,String.class}[c];
        }
    };
    tbResepDetail = new JTable(tmResepDetail);
    tbResepDetail.setAutoCreateRowSorter(true);

    dlgDetail = new JDialog(SwingUtilities.getWindowAncestor(this), "Detail Resep", Dialog.ModalityType.APPLICATION_MODAL);
    dlgDetail.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dlgDetail.getContentPane().add(new JScrollPane(tbResepDetail));
    dlgDetail.setSize(800, 450);
    dlgDetail.setLocationRelativeTo(this);
}
private void showResepDetailDialog(String noResep) {
    if (dlgDetail == null) initResepDetailDialog();

    // kosongkan model
    Valid.tabelKosong(tmResepDetail);

    // load data detail
    String sql = "SELECT rtd.kode_brng, tb.nama_brng, rtd.jml, rtd.satuan, rtd.aturan_pakai, rtd.keterangan, rtd.nama_racikan " +
                 "FROM resep_toko_detail rtd " +
                 "JOIN tokobarang tb ON tb.kode_brng = rtd.kode_brng " +
                 "WHERE rtd.no_resep = ? ORDER BY tb.nama_brng";
    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setString(1, noResep);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tmResepDetail.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getDouble(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7)
                });
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Gagal load detail: " + ex.getMessage());
        return;
    }

    dlgDetail.setTitle("Detail Resep: " + noResep);
    dlgDetail.setVisible(true);
}

private void loadResepHeader(java.util.Date tgl) {
    Valid.tabelKosong(tmResepHeader);

    final String sql =
    "SELECT rt.no_resep, " +
    "       COALESCE(p.nm_pasien,'-') AS nm_pasien, " +
    "       COALESCE(d.nm_dokter,'-') AS nm_dokter, " +
    "       COUNT(rtd.kode_brng) AS jml_item, " +
    "       MIN(rt.tgl_resep)   AS tgl, " +
    "       COALESCE(rt.status,'baru') AS status, " +
    "       rt.kd_dokter, " +
    "       COALESCE(p.no_rkm_medis,'-') AS no_rkm_medis, " +
    "       CASE " +
    "         WHEN SUM(CASE WHEN rtd.keterangan='Racikan' THEN 1 ELSE 0 END) > 0 " +
    "          AND SUM(CASE WHEN rtd.keterangan='Non Racikan' THEN 1 ELSE 0 END) > 0 THEN 'Campuran' " +
    "         WHEN SUM(CASE WHEN rtd.keterangan='Racikan' THEN 1 ELSE 0 END) > 0 THEN 'Racikan' " +
    "         WHEN SUM(CASE WHEN rtd.keterangan='Non Racikan' THEN 1 ELSE 0 END) > 0 THEN 'Non Racikan' " +
    "         ELSE COALESCE(rt.keterangan,'') " +
    "       END AS jenis " +
    "FROM resep_toko rt " +
    "LEFT JOIN reg_periksa rp ON rp.no_rawat = rt.no_rawat " +   // ← LEFT JOIN
    "LEFT JOIN pasien p      ON p.no_rkm_medis = rp.no_rkm_medis " +
    "LEFT JOIN dokter d      ON d.kd_dokter   = rt.kd_dokter " +
    "LEFT JOIN resep_toko_detail rtd ON rtd.no_resep = rt.no_resep " +
    "WHERE DATE(rt.tgl_resep) = ? " +
    "GROUP BY rt.no_resep " +
    "ORDER BY MIN(rt.tgl_resep) DESC";

    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setDate(1, new java.sql.Date(tgl.getTime()));
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tmResepHeader.addRow(new Object[]{
                    rs.getString("no_resep"),
                    rs.getString("nm_pasien"),
                    rs.getString("nm_dokter"),
                    rs.getInt("jml_item"),
                    rs.getTimestamp("tgl"),
                    rs.getString("status"),
                    rs.getString("kd_dokter"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("jenis")
                });
            }
        }
    } catch (Exception ex) {
        System.err.println("loadResepHeader: " + ex.getMessage());
    }
}
private void loadResepDetail(String noResep) {
    // guard awal
    if (noResep == null || noResep.trim().isEmpty()) {
        Valid.tabelKosong(tmResepDetail);
        tbResepDetail.setEnabled(true);
        return;
    }

    // kosongkan tabel detail
    Valid.tabelKosong(tmResepDetail);

    // 1) Cek status resep (kalau ada kolom no_nota boleh ditarik juga)
    String status = null;
    String noNota = null; // opsional, kalau ada di tabel
    try (PreparedStatement ps = koneksi.prepareStatement(
            "SELECT status, no_nota FROM resep_toko WHERE no_resep=?")) {
        ps.setString(1, noResep);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                status = rs.getString("status");
                try { noNota = rs.getString("no_nota"); } catch (Exception ignore) {}
            }
        }
    } catch (Exception ex) {
        System.out.println("[RESEP] cek status gagal: " + ex.getMessage());
    }

    // 2) Jika selesai → tampilkan pesan & stop (baris harus 7 kolom supaya match model)
    if ("selesai".equalsIgnoreCase(status)) {
        tmResepDetail.addRow(new Object[]{
            "-", // Kode
            "✅ Resep sudah dikerjakan" + (noNota != null && !noNota.isEmpty() ? " (No. Nota: " + noNota + ")" : ""),
            null, // Jml (Double)
            null, // Satuan
            null, // Aturan Pakai
            "Selesai", // Keterangan
            "-"   // Nama Racikan
        });
        tbResepDetail.setEnabled(false);
        return;
    } else {
        tbResepDetail.setEnabled(true);
    }

    // 3) Belum selesai → load item obat
    final String sql =
        "SELECT rtd.kode_brng, tb.nama_brng, rtd.jml, rtd.satuan, rtd.aturan_pakai, " +
        "       rtd.keterangan, COALESCE(rtd.nama_racikan,'-') AS nama_racikan " +
        "FROM resep_toko_detail rtd " +
        "JOIN tokobarang tb ON tb.kode_brng = rtd.kode_brng " +
        "WHERE rtd.no_resep = ? " +
        "ORDER BY (rtd.keterangan='Racikan'), rtd.nama_racikan, tb.nama_brng";

    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setString(1, noResep);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tmResepDetail.addRow(new Object[]{
                    rs.getString("kode_brng"),
                    rs.getString("nama_brng"),
                    rs.getDouble("jml"),
                    rs.getString("satuan"),
                    rs.getString("aturan_pakai"),
                    rs.getString("keterangan"),
                    rs.getString("nama_racikan")
                });
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Gagal load detail: " + ex.getMessage());
    }
}

private void tandaiDiproses(String noResep, String nipPetugas){
    final String sql = 
        "UPDATE resep_toko SET status='diproses', processed_by=? " +
        "WHERE no_resep=? AND status='baru'";
    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setString(1, nipPetugas);
        ps.setString(2, noResep);
        ps.executeUpdate(); // abaikan jika 0 (sudah diproses)
    } catch (Exception ignored) {}
}

private boolean tandaiResepSelesai(String noResep, String noNota, String nipPetugas){
    final String sql =
        "UPDATE resep_toko " +
        "SET status='selesai', processed_at=NOW(), processed_by=?, no_nota=? " +
        "WHERE no_resep=? AND status IN ('baru','diproses')";
    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setString(1, nipPetugas);
        ps.setString(2, noNota);
        ps.setString(3, noResep);
        return ps.executeUpdate() > 0;
    } catch (Exception ex) {
        System.out.println("[RESEP] gagal update status: " + ex.getMessage());
        return false;
    }
}
// --- util: ambil harga berdasar satuan yang dipakai (sesuaikan logika versi kamu) ---
//private double getHargaUntukSatuan(String kodeBrg, String satuan){
//    // contoh logika: prioritas harga sesuai mapping yang sudah kamu punya
//    double hRetail   = Sequel.cariIsiAngka("SELECT h_retail FROM tokobarang WHERE kode_brng=?", kodeBrg);
//    double hResep    = Sequel.cariIsiAngka("SELECT h_resep  FROM tokobarang WHERE kode_brng=?", kodeBrg);
//    double hGrosir   = Sequel.cariIsiAngka("SELECT h_grosir FROM tokobarang WHERE kode_brng=?", kodeBrg);
//    double hDist     = Sequel.cariIsiAngka("SELECT h_beli   FROM tokobarang WHERE kode_brng=?", kodeBrg); // contoh
//
//    // pakai aturanmu sebelumnya:
//    if("Fls".equalsIgnoreCase(satuan)) return (hResep>0? hResep : hRetail);
//    // tambahkan mapping lain kalau perlu...
//    return (hResep>0? hResep : hRetail);
//}

// --- util: tambah 1 baris item ke tabel penjualan ---
//private void tambahKeTabelPenjualan(String kode, String nama, String kategori, String satuan, double jml){
//    double harga = getHargaUntukSatuan(kode, satuan);
//    double subtotal = harga * jml;
//
//    tabMode.addRow(new Object[]{
//        jml,                // Jml
//        kode,               // Kode Barang
//        nama,               // Nama Barang
//        kategori,           // Kategori
//        satuan,             // Satuan
//        Valid.SetAngka(harga),     // Harga(Rp)
//        Valid.SetAngka(subtotal),  // Subtotal(Rp)
//        0,0,                 // diskon / potongan kalau ada
//        Valid.SetAngka(subtotal),  // Total(Rp)
//        /* stok */ Sequel.cariIsiAngka("SELECT stok FROM tokobarang WHERE kode_brng=?", kode)
//    });
//}

/** Ambil seluruh item resep (detail) dan masukkan ke tabel penjualan */
//private void muatDetailResepKePenjualan(String noResep){
//    final String sql =
//        "SELECT rt.kode_brng, rt.nama_brng, rt.jumlah, COALESCE(rt.satuan,'') AS satuan " +
//        " " +
//        "WHERE rt.no_resep=? " +
//        "ORDER BY rt.id ASC";
//
//    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
//        ps.setString(1, noResep);
//        try (ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                String kode   = rs.getString("kode_brng");
//                String nama   = rs.getString("nama_brng");
//                double jml    = rs.getDouble("jumlah");
//                String satuan = rs.getString("satuan");
//
//                String kategori = Sequel.cariIsi(
//                    "SELECT j.nama FROM tokojenisbarang j " +
//                    "JOIN tokobarang b ON b.kd_jns=j.kd_jns WHERE b.kode_brng=?", kode);
//
//                tambahKeTabelPenjualan(kode, nama, kategori, satuan, jml);
//            }
//        }
//    } catch (Exception ex) {
//        System.out.println("muatDetailResepKePenjualan: " + ex.getMessage());
//    }
//}
//resep.setTokoPenjualanRef(this.dlgTokoPenjualan);
// Inner class sederhana
class DokterItem {
    String kd;
    String nama;
    DokterItem(String kd, String nama){
        this.kd = kd;
        this.nama = nama;
    }
    @Override
    public String toString() {
        return kd + " - " + nama;  // ini yang muncul di combobox
    }
}

private String ambilNoRawatDariResep(String noResep) {
    if (noResep == null || noResep.trim().isEmpty()) return null;
    return Sequel.cariIsi(
        "SELECT no_rawat FROM resep_toko WHERE no_resep=?",
        noResep.trim()
    );
}

private void loadDokterToCombo() {
    DokterCombo.removeAllItems();
    String sql = "SELECT kd_dokter, nm_dokter FROM dokter ORDER BY nm_dokter";
    try (PreparedStatement ps = koneksi.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            DokterCombo.addItem(new DokterItem(rs.getString("kd_dokter"),
                                             rs.getString("nm_dokter")));
        }
    } catch (Exception e) {
        System.out.println("Gagal load dokter: " + e.getMessage());
    }
}
}
