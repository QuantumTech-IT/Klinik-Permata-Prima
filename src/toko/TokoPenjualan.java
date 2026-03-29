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
//import java.text.DecimalFormat;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import keuangan.DlgBilingRalan;


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
private DlgBilingRalan billing = new DlgBilingRalan(null, false); 
private javax.swing.event.TableModelListener qtyListener;
private java.awt.CardLayout cardKanan;
private javax.swing.JPanel panelKeranjangKanan; 


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
private PreparedStatement pscaripiutang;
private ResultSet rskasir;
private javax.swing.JTable tbKeranjang = new javax.swing.JTable();
private javax.swing.table.DefaultTableModel tabModeKeranjang;
private boolean syncKeranjang = false;
private javax.swing.table.DefaultTableModel tabModeKeranjangResep;
private javax.swing.JTable tbKeranjangResep;
private javax.swing.JScrollPane spKeranjangResep;
private DefaultTableModel tabKeranjang;
//private JTable tbKeranjang;
private final java.util.Map<String,Integer> idxKeranjang = new java.util.HashMap<>();
private widget.Label labelNoRawatKunjungan;
private widget.ComboBox CbNoRawatKunjungan;
private final java.util.List<String> valueNoRawatKunjungan = new java.util.ArrayList<>();
private final java.util.List<String> valueNoRkmKunjungan = new java.util.ArrayList<>();
private final java.util.List<String> valueNmPasienKunjungan = new java.util.ArrayList<>();
//private boolean syncKeranjang = false;



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
        setModeResep("Resep".equalsIgnoreCase(String.valueOf(Jenisjual.getSelectedItem())));
        // default: sembunyikan field resep luar
        labelDokterLuar.setVisible(false);
        TNmDokterLuar.setVisible(false);

        labelNoResepLuar.setVisible(false);
        TNoResepLuar.setVisible(false);

        // default juga disable biar ga bisa diinput kalau lagi hidden
        TNmDokterLuar.setEnabled(false);
        TNoResepLuar.setEnabled(false);

        initNoRawatKunjunganPanel();

        // biar state sesuai pilihan combobox saat form baru dibuka
        applyJenisJual();
//        label23.setVisible(false);
//        label16.setVisible(false);
//        jPanel3.setVisible(false);
//        jPanel2.setVisible(false);
//        
        
//loadResepHeader(new java.util.Date()); 
        initPanelResepToko();
        initResepHeaderTable();
        
        initPanelResepMasterDetail();
        java.lang.String noResep = null;
        loadResepToko(noResep);
        loadResepDetail(noResep);
        pasangAksiTambahItemResepDetail();
        
       // loadResepTokoByTanggal(new java.util.Date());
        
loadResepHeaderByTanggal(new java.util.Date());
tbObat.setAutoCreateRowSorter(false);  // ini yang bikin baris gak lompat-lompat
tbObat.setRowSorter(null);
 initKeranjangPanel();
// initPanelKeranjangKanan();   // bikin panelKeranjangKanan
//setupPanelKananCards();      // pasang CardLayout ke jPanel2

//Jenisjual.addActionListener(e -> {
    
//    tampil(); // karena harga/satuan berubah sesuai jenis jual
//});
// pasangEnterPadaQty();
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
//switchPanelKananByJenis();
        tbObat.setPreferredScrollableViewportSize(new Dimension(800,800));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        initKeranjangModel();
        pasangEnterPadaQty();
        

        for (i = 0; i < 13; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(42);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(350);
            }else if(i==3){
                column.setPreferredWidth(160);
            }else if(i==4){
                column.setPreferredWidth(60);
            }else if(i==5){
                column.setPreferredWidth(90);
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
                column.setPreferredWidth(50);
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
                        refreshNoRawatKunjungan();
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
        labelNoResepLuar = new widget.Label();
        TNoResepLuar = new widget.TextBox();
        TNmDokterLuar = new widget.TextBox();
        labelDokterLuar = new widget.Label();
        scrollPane1 = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelKeranjang = new javax.swing.JPanel();

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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TCariKeyReleased(evt);
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
        Bayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BayarKeyPressed(evt);
            }
        });
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
        NoNota.setBounds(80, 10, 140, 23);

        label14.setForeground(new java.awt.Color(255, 255, 255));
        label14.setText("Petugas :");
        label14.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label14);
        label14.setBounds(360, 80, 80, 23);

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
        kdptg.setBounds(450, 80, 100, 23);

        label16.setForeground(new java.awt.Color(255, 255, 255));
        label16.setText("Detail Resep  :");
        label16.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label16);
        label16.setBounds(30, 150, 130, 23);

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
        nmptg.setBounds(550, 80, 222, 23);

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
        BtnMem.setBounds(780, 10, 28, 23);

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
        BtnPtg.setBounds(780, 80, 28, 23);

        label18.setForeground(new java.awt.Color(255, 255, 255));
        label18.setText("Catatan :");
        label18.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label18);
        label18.setBounds(380, 110, 70, 23);

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
        catatan.setBounds(460, 110, 275, 23);

        label12.setForeground(new java.awt.Color(255, 255, 255));
        label12.setText("Jns.Jual :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label12);
        label12.setBounds(230, 40, 55, 23);

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
        Tgl.setBounds(290, 10, 101, 23);

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Akun Bayar :");
        jLabel10.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        panelisi3.add(jLabel10);
        jLabel10.setBounds(390, 140, 80, 23);

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
        AkunBayar.setBounds(470, 140, 353, 23);

        Jenisjual.setBackground(new java.awt.Color(0, 153, 255));
        Jenisjual.setForeground(new java.awt.Color(255, 255, 255));
        Jenisjual.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Biasa", "Resep", "Resep Luar" }));
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
        Jenisjual.setBounds(290, 40, 100, 23);

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
        DokterCombo.setBounds(70, 100, 280, 23);

        label13.setForeground(new java.awt.Color(255, 255, 255));
        label13.setText("Tanggal :");
        label13.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label13);
        label13.setBounds(220, 10, 70, 23);

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
        jPanel3.setBounds(80, 170, 740, 120);

        label23.setForeground(new java.awt.Color(255, 255, 255));
        label23.setText("Daftar Resep Dokter :");
        label23.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label23.setName("label23"); // NOI18N
        label23.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label23);
        label23.setBounds(830, 10, 130, 23);

        labelNoResepLuar.setForeground(new java.awt.Color(255, 255, 255));
        labelNoResepLuar.setText("No.R.Luar :");
        labelNoResepLuar.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        labelNoResepLuar.setName("labelNoResepLuar"); // NOI18N
        labelNoResepLuar.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(labelNoResepLuar);
        labelNoResepLuar.setBounds(0, 40, 70, 23);

        TNoResepLuar.setBackground(new java.awt.Color(0, 204, 255));
        TNoResepLuar.setForeground(new java.awt.Color(102, 102, 102));
        TNoResepLuar.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        TNoResepLuar.setName("TNoResepLuar"); // NOI18N
        TNoResepLuar.setOpaque(true);
        TNoResepLuar.setPreferredSize(new java.awt.Dimension(207, 23));
        TNoResepLuar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoResepLuarKeyPressed(evt);
            }
        });
        panelisi3.add(TNoResepLuar);
        TNoResepLuar.setBounds(80, 40, 140, 23);

        TNmDokterLuar.setBackground(new java.awt.Color(0, 204, 255));
        TNmDokterLuar.setForeground(new java.awt.Color(102, 102, 102));
        TNmDokterLuar.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        TNmDokterLuar.setName("TNmDokterLuar"); // NOI18N
        TNmDokterLuar.setOpaque(true);
        TNmDokterLuar.setPreferredSize(new java.awt.Dimension(207, 23));
        TNmDokterLuar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNmDokterLuarKeyPressed(evt);
            }
        });
        panelisi3.add(TNmDokterLuar);
        TNmDokterLuar.setBounds(80, 70, 140, 23);

        labelDokterLuar.setForeground(new java.awt.Color(255, 255, 255));
        labelDokterLuar.setText("Dr.R.Luar :");
        labelDokterLuar.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        labelDokterLuar.setName("labelDokterLuar"); // NOI18N
        labelDokterLuar.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(labelDokterLuar);
        labelDokterLuar.setBounds(0, 70, 70, 23);

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
        tbObat.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tbObat.setGridColor(new java.awt.Color(0, 0, 0));
        tbObat.setName("tbObat"); // NOI18N
        tbObat.setRowHeight(25);
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

        panelKeranjang.setName("panelKeranjang"); // NOI18N
        panelKeranjang.setPreferredSize(new java.awt.Dimension(600, 100));
        internalFrame1.add(panelKeranjang, java.awt.BorderLayout.LINE_END);

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
    boolean autoBillingSesudahSimpan = false;
    String noRawatAutoBilling = "";
    String akunAutoBilling = "";
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
boolean isResep     = jenis.equalsIgnoreCase("Resep");
boolean isResepLuar = jenis.equalsIgnoreCase("Resep Luar");

// Resep internal: dokter dari combo
String kdDokter = isResep ? ambilKdDokterDariCombo() : "";
String noResepAktif = isResep ? ambilNoResepHeaderTerpilih() : "";

// Validasi dokter untuk Resep internal
if (isResep && (kdDokter == null || kdDokter.trim().isEmpty())) {
    Valid.textKosong(DokterCombo, "dokter");
    Sequel.AutoComitTrue();
    return;
}

// Resep Luar: dokter luar + no resep luar wajib
String nmDokterLuar = "";
String noResepLuar  = "";
if (isResepLuar) {
    nmDokterLuar = TNmDokterLuar.getText().trim();
    noResepLuar  = TNoResepLuar.getText().trim();

    if (noResepLuar.isEmpty()) {
        Valid.textKosong(TNoResepLuar, "No Resep Luar");
        Sequel.AutoComitTrue();
        return;
    }
    if (nmDokterLuar.isEmpty()) {
        Valid.textKosong(TNmDokterLuar, "Nama Dokter Luar");
        Sequel.AutoComitTrue();
        return;
    }
}

String noRawat = "";
if (isResep && noResepAktif != null && !noResepAktif.trim().isEmpty()) {
    noRawat = ambilNoRawatDariResep(noResepAktif);
}
if (isResep && (noRawat == null || noRawat.trim().isEmpty())) {
    noRawat = ambilNoRawatTerpilihDariList();
}
if (isResep && (noRawat == null || noRawat.trim().isEmpty())) {
    noRawat = pilihNoRawatPasienPadaTanggal(kdmem.getText().trim(), Valid.SetTgl(Tgl.getSelectedItem() + ""));
    if (noRawat == null || noRawat.trim().isEmpty()) {
        Sequel.AutoComitTrue();
        return;
    }
}

String ketSave = catatan.getText();
if (isResepLuar) {
    String tag = "[RESEP LUAR] No=" + noResepLuar + " | Dokter=" + nmDokterLuar;
    ketSave = tag + (ketSave == null || ketSave.trim().isEmpty() ? "" : " | " + ketSave);

    kdDokter = "";  // kosong
    noRawat  = "";  // kosong
}


        
        // Insert header penjualan
        // Insert header penjualan (pakai kdmem/nmmem seperti biasa)
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
          ketSave,
          jenis, // "Resep Luar"
          String.valueOf(ongkir),
          String.valueOf(besarppn),
          kode_akun_bayar,
          String.valueOf(tagihanppn),
          AkunBayar.getSelectedItem().toString(),
          kdDokter,   // kosong untuk Resep Luar
          noRawat     // kosong untuk Resep Luar
        }
      );
//        boolean ok = Sequel.menyimpantf2(
//            "tokopenjualan",
//            "?,?,?,?,?,?,?,?,?,?,?,?,?,?",
//            "nota_jual", 14,
//            new String[]{
//                NoNota.getText(),
//                Valid.SetTgl(Tgl.getSelectedItem()+""),
//                kdptg.getText(),
//                kdmem.getText(),
//                nmmem.getText(),
//                catatan.getText(),
//                jenis,
//                String.valueOf(ongkir),
//                String.valueOf(besarppn),
//                kode_akun_bayar,
//                String.valueOf(tagihanppn),
//                AkunBayar.getSelectedItem().toString(),
//                (kdDokter == null ? "" : kdDokter),
//                (noRawat == null ? "" : noRawat)// 
//            }
//        );

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
                    ketSave,
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
        if (noResepAktif == null || noResepAktif.trim().isEmpty()) {
            noResepAktif = buatResepTokoOtomatisDariPenjualan(noRawat, kdDokter);
            if (noResepAktif == null || noResepAktif.trim().isEmpty()) {
                sukses = false;
                System.out.println("[RESEP] gagal membuat header/detail resep otomatis.");
            }
        }

        if (sukses && noResepAktif != null && !noResepAktif.trim().isEmpty()) {
            boolean done = tandaiResepSelesai(
                noResepAktif,                 // dari header/otomatis
                NoNota.getText().trim(),      // nota penjualan
                kdptg.getText().trim()        // nip petugas
            );
            if (!done) {
                System.out.println("[RESEP] status resep tidak berubah (mungkin sudah selesai).");
            }
            noResepTerpilih = noResepAktif;
        } else {
            System.out.println("[RESEP] noResepAktif kosong, tidak bisa tandai selesai.");
        }
    }

    if (isResep && noRawat != null && !noRawat.trim().isEmpty()) {
        autoBillingSesudahSimpan = true;
        noRawatAutoBilling = noRawat.trim();
        akunAutoBilling = String.valueOf(AkunBayar.getSelectedItem());
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

        if (sukses && autoBillingSesudahSimpan) {
            boolean okBilling = billing.simpanOtomatisDariToko(noRawatAutoBilling, akunAutoBilling);
            if (!okBilling) {
                String alasan = billing.getAlasanGagalAutoBilling();
                if (alasan == null || alasan.trim().isEmpty()) {
                    alasan = "-";
                }
                JOptionPane.showMessageDialog(
                    this,
                    "Nota toko tersimpan, tapi sinkron billing otomatis gagal.\n" +
                    "No.Rawat: " + noRawatAutoBilling + "\n" +
                    "Alasan: " + alasan
                );
            }
        }

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
           //pasangListenerQty();          // pasang lagi karena model bisa berubah
rebuildKeranjangDariTable();
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
            
//initKeranjangPanel();
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
                refreshNoRawatKunjungan();
                break;
            case KeyEvent.VK_PAGE_UP:
                Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", nmmem,kdmem.getText());
                refreshNoRawatKunjungan();
                Tgl.requestFocus();
                break;
            case KeyEvent.VK_ENTER:
                Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", nmmem,kdmem.getText());
                refreshNoRawatKunjungan();
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
        refreshNoRawatKunjungan();
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
            refreshNoRawatKunjungan();
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
         if (evt.getStateChange() != java.awt.event.ItemEvent.SELECTED) return;
            applyJenisJual();
            tampil();
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

    private void TCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_TCariKeyReleased

    private void BayarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BayarKeyPressed
       pasangEnterPadaQty();
    }//GEN-LAST:event_BayarKeyPressed

    private void TNoResepLuarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoResepLuarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoResepLuarKeyPressed

    private void TNmDokterLuarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNmDokterLuarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNmDokterLuarKeyPressed

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
    private widget.TextBox TNmDokterLuar;
    private widget.TextBox TNoResepLuar;
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
    private widget.Label labelDokterLuar;
    private widget.Label labelNoResepLuar;
    private widget.TextBox nmmem;
    private widget.TextBox nmptg;
    private javax.swing.JPanel panelKeranjang;
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
    "WHERE b.status='1' AND " +
      " (b.kode_brng LIKE ? OR b.nama_brng LIKE ? OR j.nm_jenis LIKE ? OR IFNULL(b.kandungan,'') LIKE ?) " +
        "ORDER BY b.nama_brng";

try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
    String q = "%" + TCari.getText().trim() + "%";
    ps.setString(1, q);
    ps.setString(2, q);
    ps.setString(3, q);
    ps.setString(4, q);

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
                    stok   = stokPack * kapasitas; // âœ… TAB per BOX
                } else {
                    harga  = rs.getDouble("h_resep");
                    satuan = rs.getString("kode_sat2");
                    stok   = stokPack * kapasitas; // âœ… TAB per BOX
                }

            Object qtyAwal = ""; // default kosong
Integer idx = idxKeranjang.get(rs.getString("kode_brng"));
if (idx != null) qtyAwal = tabKeranjang.getValueAt(idx, 3); // kolom Jml di keranjang

tabMode.addRow(new Object[]{
    qtyAwal,
    rs.getString("kode_brng"),
    rs.getString("nama_brng"),
    rs.getString("nm_jenis"),
    satuan,
    harga,
    0.0,0.0,0.0,0.0,0.0,
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

    javax.swing.SwingUtilities.invokeLater(() -> pasangEnterPadaQty());
//    formatKolomJmlTanpaKoma();
}

private static double d(Object v) {
    try { return v==null?0:Double.parseDouble(v.toString()); }
    catch (Exception e) { return 0; }
}

//  private static double parsePlainAngka(String s) {
//      if (s == null) return 0d;
//      s = s.trim();
//      if (s.isEmpty()) return 0d;
//      try {
//          // Keep existing parsing behavior (thousand separators, locale) if available.
//          return Valid.SetAngka(s);
//      } catch (Exception ignore) {
//      }
//      try {
//          return Double.parseDouble(s.replace(",", "."));
//      } catch (Exception ignore) {
//          return 0d;
//      }
//  }

//  private static double parseFractionOnly(String part) {
//      if (part == null) return 0d;
//      String p = part.trim();
//      if (p.isEmpty()) return 0d;
//      String[] ab = p.split("\\s*/\\s*");
//      if (ab.length != 2) return 0d;
//      double num = parsePlainAngka(ab[0]);
//      double den = parsePlainAngka(ab[1]);
//      if (den == 0d) return 0d;
//      return num / den;
//  }

//  // Parse qty like: "0.5", "0,5", "1/2", "3/4", "1 1/2".
//  private static double d(Object v) {
//      if (v == null) return 0d;
//      if (v instanceof Number) return ((Number) v).doubleValue();
//      String s = String.valueOf(v).trim();
//      if (s.isEmpty()) return 0d;

//      if (s.contains("/")) {
//          String[] parts = s.split("\\s+");
//          if (parts.length == 2 && parts[1].contains("/")) {
//              double whole = parsePlainAngka(parts[0]);
//              double frac = parseFractionOnly(parts[1]);
//              return whole < 0 ? (whole - frac) : (whole + frac);
//          }
//          if (parts.length == 1) {
//              return parseFractionOnly(parts[0]);
//          }
//          // If format is unusual, try fraction parse on the raw string.
//          double frac = parseFractionOnly(s);
//          if (frac != 0d) return frac;
//      }

//      return parsePlainAngka(s);
//  }
    
    
    
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
    private static double num(Object v) {
    try { return v == null ? 0 : Double.parseDouble(v.toString()); }
    catch (Exception e) { return 0; }
}
    public void autoNomor(){
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(tokopenjualan.nota_jual,5),signed)),0) from tokopenjualan where tokopenjualan.tgl_jual='"+Valid.SetTgl(Tgl.getSelectedItem()+"")+"' ",
                "TJ"+Tgl.getSelectedItem().toString().substring(6,10)+Tgl.getSelectedItem().toString().substring(3,5)+Tgl.getSelectedItem().toString().substring(0,2),5,NoNota); 
    }
// private static double num(Object v) {
//     return d(v);
// }

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
            System.out.println("DEBUG: transaksi sukses, buka preview...");

    
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

            // Tampilkan di combo (bebas: mau â€œkd - namaâ€ atau cuma kode/nama)
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

    // âœ… 1) HAPUS LISTENER LAMA DULU (SEBELUM setSelectedItem / addItem)
    java.awt.event.ItemListener[] all = comboSatuan.getItemListeners();
    for (java.awt.event.ItemListener il : all) comboSatuan.removeItemListener(il);

    // âœ… 2) LOCK supaya event tidak jalan waktu setup
    final boolean[] adjusting = { true };

    final int row = table.convertRowIndexToModel(viewRow);
    comboSatuan.removeAllItems();

    Object kodeObj = table.getModel().getValueAt(row, COL_KODE);
    final String kodeBrg = (kodeObj == null) ? "" : kodeObj.toString();

    // pasang listener BARU (tapi ditahan oleh adjusting)
    comboSatuan.addItemListener(e -> {
        if (adjusting[0]) return;
        if (e.getStateChange() != java.awt.event.ItemEvent.SELECTED) return;

        String unitDipilih = (String) e.getItem();

        int isi       = Sequel.cariInteger("SELECT isi FROM tokobarang WHERE kode_brng=?", kodeBrg);
        int kapasitas = Sequel.cariInteger("SELECT kapasitas FROM tokobarang WHERE kode_brng=?", kodeBrg);

        double hDistributor = Valid.SetAngka(Sequel.cariIsi("SELECT distributor FROM tokobarang WHERE kode_brng=?", kodeBrg));
        double hGrosir      = Valid.SetAngka(Sequel.cariIsi("SELECT grosir      FROM tokobarang WHERE kode_brng=?", kodeBrg));
        double hRetail      = Valid.SetAngka(Sequel.cariIsi("SELECT retail      FROM tokobarang WHERE kode_brng=?", kodeBrg));
        double hResep       = Valid.SetAngka(Sequel.cariIsi("SELECT h_resep     FROM tokobarang WHERE kode_brng=?", kodeBrg));

        String u0 = Sequel.cariIsi("SELECT kode_sat  FROM tokobarang WHERE kode_brng=?", kodeBrg);
        String u1 = Sequel.cariIsi("SELECT kode_sat1 FROM tokobarang WHERE kode_brng=?", kodeBrg);
        String u2 = Sequel.cariIsi("SELECT kode_sat2 FROM tokobarang WHERE kode_brng=?", kodeBrg);

        double stokPack = Valid.SetAngka(Sequel.cariIsi("SELECT stok FROM tokobarang WHERE kode_brng=?", kodeBrg));

        final String unitFinal;
        final double hargaFinal;
        final double stokTampilFinal;

        if ("RSEP".equalsIgnoreCase(unitDipilih)) {
            unitFinal       = u2;         // atau "RSEP" kalau memang mau tampil RSEP
            hargaFinal      = hResep;
            stokTampilFinal = stokPack * kapasitas;
        } else if (unitDipilih.equalsIgnoreCase(u0)) {
            unitFinal       = u0;
            hargaFinal      = hDistributor;
            stokTampilFinal = stokPack;
        } else if (unitDipilih.equalsIgnoreCase(u1)) {
            unitFinal       = u1;
            hargaFinal      = hGrosir;
            stokTampilFinal = stokPack * isi;
        } else {
            unitFinal       = u2;
            hargaFinal      = hRetail;
            stokTampilFinal = stokPack * kapasitas;
        }

        final int mr = row;

        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.table.TableModel m = table.getModel();
            m.setValueAt(unitFinal,       mr, C_SAT);
            m.setValueAt(hargaFinal,      mr, C_HRG);
            m.setValueAt(stokTampilFinal, mr, C_STK);
            recalcRow(m, mr);
        });
    });
try {
        String sat0 = Sequel.cariIsi("SELECT kode_sat  FROM tokobarang WHERE kode_brng=?", kodeBrg);
        String sat1 = Sequel.cariIsi("SELECT kode_sat1 FROM tokobarang WHERE kode_brng=?", kodeBrg);
        String sat2 = Sequel.cariIsi("SELECT kode_sat2 FROM tokobarang WHERE kode_brng=?", kodeBrg);

        if (sat0 != null && !sat0.trim().isEmpty() && !"-".equals(sat0)) comboSatuan.addItem(sat0);
        if (sat1 != null && !sat1.trim().isEmpty() && !"-".equals(sat1)) comboSatuan.addItem(sat1);
        if (sat2 != null && !sat2.trim().isEmpty() && !"-".equals(sat2)) comboSatuan.addItem(sat2);

        double hResepAda = Valid.SetAngka(Sequel.cariIsi("SELECT h_resep FROM tokobarang WHERE kode_brng=?", kodeBrg));
        if (hResepAda > 0) comboSatuan.addItem("RSEP");

        if (comboSatuan.getItemCount() == 0) comboSatuan.addItem("PCS");
    } catch (Exception ex) {
        if (comboSatuan.getItemCount() == 0) comboSatuan.addItem("PCS");
    }

    if (value != null) comboSatuan.setSelectedItem(String.valueOf(value));

    // âœ… buka lock setelah semua setup selesai
    adjusting[0] = false;

 
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

    // simpan perubahan inline (Jml/Aturan) â†’ update DB
    tmResepToko.addTableModelListener(ev -> {
        if (ev.getType()==javax.swing.event.TableModelEvent.UPDATE) {
            int r = ev.getFirstRow();
            if (r>=0) { updateResepTokoDariTabel(r); }
        }
    });

    spResepToko = new javax.swing.JScrollPane(tbResepToko);
    panelResepToko.add(spResepToko, java.awt.BorderLayout.CENTER);

    // taruh panelResepToko ke panel kananmu (misal panelKanan)
//    jPanel2.setLayout(new java.awt.BorderLayout());
//   jPanel2.add(panelResepToko, java.awt.BorderLayout.CENTER);
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

private void toggleDokterByJenis() {
    String pilih = String.valueOf(Jenisjual.getSelectedItem()).trim();

    boolean isResep     = "Resep".equalsIgnoreCase(pilih);
    boolean isResepLuar = "Resep Luar".equalsIgnoreCase(pilih);

    // ===== Dokter internal (combo) hanya untuk Resep internal =====
    label11.setVisible(isResep);
    DokterCombo.setVisible(isResep);

    if (DokterCombo != null) DokterCombo.setVisible(isResep);

    if (!isResep) {
        DokterCombo.setSelectedIndex(-1); // kosongkan saat bukan Resep internal
    } else {
        DokterCombo.requestFocusInWindow();
    }

    // ===== Dokter luar (textfield) hanya untuk Resep Luar =====
    labelDokterLuar.setVisible(isResepLuar);
    TNmDokterLuar.setVisible(isResepLuar);
    TNmDokterLuar.setEnabled(isResepLuar);

    if (!isResepLuar) {
        TNmDokterLuar.setText("");
    } else {
        TNmDokterLuar.requestFocusInWindow();
    }

    // refresh layout
    panelisi3.revalidate();
    panelisi3.repaint();
}
//private void toggleDokter() {
//    Object sel = Jenisjual.getSelectedItem();
//    String val = sel == null ? "" : sel.toString().trim().toLowerCase();
//
//    boolean isResep = val.contains("resep"); // cover "Resep" / "Resep Baru" / dll
//
//    label11.setVisible(isResep);
//    DokterCombo.setVisible(isResep);
//    if (DokterCombo != null) DokterCombo.setVisible(isResep);
//
//    if (!isResep) {
//        DokterCombo.setSelectedIndex(-1); // kosongkan saat bukan resep
//    } else {
//        DokterCombo.requestFocusInWindow(); // fokuskan saat muncul
//    }
//
//    // refresh layout supaya langsung berubah
//    panelisi3.revalidate();  // ganti dengan panel container-nya (mis. panelInput/panelHeader)
//    panelisi3.repaint();
//}

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
    cbAutoRefresh = new javax.swing.JCheckBox("ðŸ”„ Auto Refresh");
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
        if (tbResepToko != null) tbResepToko.clearSelection(); // hilangkan seleksi â€œhantuâ€
    }
}

// ==== HEADER: per nomor resep ====
private javax.swing.JTable tbResepHeader;
private javax.swing.table.DefaultTableModel tmResepHeader;

// ==== DETAIL: item obat ====
private javax.swing.JTable tbResepDetail;
private javax.swing.table.DefaultTableModel tmResepDetail;
private boolean modeEditResepDetail = false;
private boolean muteResepDetailEvent = false;


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
        new Object[]{"ID","Kode","Nama Obat","Jml","Satuan","Aturan Pakai","Keterangan", "Nama Racikan"}
    ){
        Class<?>[] t = new Class[]{Long.class,String.class,String.class,Double.class,String.class,String.class,String.class, String.class};
        @Override public Class<?> getColumnClass(int c){ return t[c]; }
        @Override public boolean isCellEditable(int r,int c){
            return modeEditResepDetail && (c==3 || c==5);
        }
    };
    tbResepDetail = new javax.swing.JTable(tmResepDetail);
    tbResepDetail.setAutoCreateRowSorter(true);
    tbResepDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
TableColumnModel colModel = tbResepDetail.getColumnModel();

// ID → hidden
colModel.getColumn(0).setMinWidth(0); colModel.getColumn(0).setMaxWidth(0); colModel.getColumn(0).setPreferredWidth(0);
// Kode
colModel.getColumn(1).setPreferredWidth(90);
colModel.getColumn(1).setHeaderValue("Kode");
// Nama Obat / Racikan
colModel.getColumn(2).setPreferredWidth(200);
colModel.getColumn(2).setHeaderValue("Nama Obat / Racikan");
// Jml
colModel.getColumn(3).setPreferredWidth(50);
// Satuan
colModel.getColumn(4).setPreferredWidth(65);
// Aturan Pakai
colModel.getColumn(5).setPreferredWidth(120);
// Komposisi / Keterangan → perlebar
colModel.getColumn(6).setPreferredWidth(380);
colModel.getColumn(6).setHeaderValue("Komposisi / Keterangan");
// Nama Racikan → sembunyikan (duplikat)
colModel.getColumn(7).setMinWidth(0); colModel.getColumn(7).setMaxWidth(0); colModel.getColumn(7).setPreferredWidth(0);

// --- custom renderer untuk baris racikan ---
tbResepDetail.setRowHeight(24);
javax.swing.table.TableCellRenderer racikanRenderer = new javax.swing.table.DefaultTableCellRenderer() {
    private final java.awt.Color BG_RACIKAN  = new java.awt.Color(240, 232, 255); // lavender
    private final java.awt.Color FG_RACIKAN  = new java.awt.Color(90, 0, 150);
    private final java.awt.Color BG_NORMAL   = java.awt.Color.WHITE;
    private final java.awt.Color BG_SELECTED = new java.awt.Color(198, 181, 230);

    @Override
    public java.awt.Component getTableCellRendererComponent(
            javax.swing.JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int col) {

        // ambil kode dari model (kolom 1 = Kode)
        Object kodeObj = table.getModel().getValueAt(
                table.convertRowIndexToModel(row), 1);
        boolean isRacikan = (kodeObj == null || kodeObj.toString().trim().isEmpty());

        // nilai tampilan
        String display;
        int modelCol = table.convertColumnIndexToModel(col);
        if (isRacikan) {
            if (modelCol == 1) {
                display = "[Racikan]";
            } else if (modelCol == 3) {
                display = "-";          // jml
            } else if (modelCol == 4) {
                display = "-";          // satuan
            } else {
                display = value == null ? "" : value.toString();
            }
        } else {
            display = value == null ? "" : value.toString();
        }

        super.getTableCellRendererComponent(table, display, isSelected, hasFocus, row, col);

        // warna baris
        if (isSelected) {
            setBackground(isRacikan ? BG_SELECTED : table.getSelectionBackground());
        } else {
            setBackground(isRacikan ? BG_RACIKAN : BG_NORMAL);
        }
        if (isRacikan) {
            setForeground(modelCol == 1 ? FG_RACIKAN : java.awt.Color.DARK_GRAY);
            if (modelCol == 1) setFont(getFont().deriveFont(java.awt.Font.BOLD | java.awt.Font.ITALIC));
            else setFont(getFont().deriveFont(java.awt.Font.PLAIN));
        } else {
            setForeground(table.getForeground());
            setFont(getFont().deriveFont(java.awt.Font.PLAIN));
        }

        // tooltip full teks untuk kolom Komposisi
        if (modelCol == 6 && value != null) {
            String full = value.toString().replace("\n", "<br>");
            setToolTipText("<html>" + full + "</html>");
        } else {
            setToolTipText(null);
        }
        return this;
    }
};
for (int ci = 1; ci <= 6; ci++) {
    colModel.getColumn(ci).setCellRenderer(racikanRenderer);
}
    
    
    JPopupMenu pmDetail = new JPopupMenu();
    JMenuItem miEdit = new JMenuItem("Aktifkan Mode Edit Detail Resep");
    pmDetail.add(miEdit);
    tbResepDetail.setComponentPopupMenu(pmDetail);
    miEdit.addActionListener(e -> {
        modeEditResepDetail = !modeEditResepDetail;
        miEdit.setText(modeEditResepDetail
                ? "Nonaktifkan Mode Edit Detail Resep"
                : "Aktifkan Mode Edit Detail Resep");
        tbResepDetail.repaint();
        JOptionPane.showMessageDialog(this,
                modeEditResepDetail
                        ? "Mode edit detail resep aktif. Anda bisa ubah Jml/Aturan Pakai."
                        : "Mode edit detail resep dinonaktifkan.");
    });

    tmResepDetail.addTableModelListener(e -> {
        if (muteResepDetailEvent || !modeEditResepDetail) return;
        if (e.getType() != javax.swing.event.TableModelEvent.UPDATE) return;
        int row = e.getFirstRow();
        if (row >= 0 && row < tmResepDetail.getRowCount()) {
            simpanEditResepDetail(row);
        }
    });


    // tata letak 2 tabel (atas = header, bawah = detail)
    jPanel2.setLayout(new java.awt.BorderLayout());
    jPanel2.add(new javax.swing.JScrollPane(tbResepHeader), java.awt.BorderLayout.NORTH);
   jPanel3.setLayout(new java.awt.BorderLayout());
jPanel3.add(new javax.swing.JScrollPane(tbResepDetail), java.awt.BorderLayout.CENTER);

    // ketika pilih header -> load detail
    tbResepHeader.getSelectionModel().addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) return;
        applyHeaderSelectionToDetail();
    });

    // klik row yang sama berulang tetap diproses
    tbResepHeader.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override public void mouseReleased(java.awt.event.MouseEvent e) {
            if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                applyHeaderSelectionToDetail();
            }
        }
    });
}

private void applyHeaderSelectionToDetail() {
    if (tbResepHeader == null || tmResepHeader == null) return;

    int viewRow = tbResepHeader.getSelectedRow();
    if (viewRow < 0) return;

    int modelRow = tbResepHeader.convertRowIndexToModel(viewRow);
    if (modelRow < 0 || modelRow >= tmResepHeader.getRowCount()) return;

    String noResep  = String.valueOf(tmResepHeader.getValueAt(modelRow, 0)); // No. Resep
    String nmPasien = String.valueOf(tmResepHeader.getValueAt(modelRow, 1)); // Nama Pasien
    String kdDokter = String.valueOf(tmResepHeader.getValueAt(modelRow, 6)); // hidden
    String noRM     = String.valueOf(tmResepHeader.getValueAt(modelRow, 7)); // hidden

    noResepTerpilih = noResep;
    loadResepDetail(noResep);

    nmmem.setText(nmPasien);
    kdmem.setText(noRM);
    refreshNoRawatKunjungan();
    pilihNoRawatDiList(ambilNoRawatDariResep(noResep));
    selectDokterByKode(kdDokter);

    try {
        Jenisjual.setSelectedItem("Resep");
    } catch (Exception ex) {
        System.out.println("[WARN] Jenis Jual tidak ada item Resep");
    }
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
private JTable tbResepDetailPopup;
private DefaultTableModel tmResepDetailPopup;

private void initResepDetailDialog() {
    tmResepDetailPopup = new DefaultTableModel(
        null, new Object[]{"Kode","Nama Obat","Jml","Satuan","Aturan","Ket","Nama Racikan"}
    ){
        @Override public boolean isCellEditable(int r,int c){ return false; }
        @Override public Class<?> getColumnClass(int c){
            return new Class[]{String.class,String.class,Double.class,String.class,String.class,String.class,String.class}[c];
        }
    };
    tbResepDetailPopup = new JTable(tmResepDetailPopup);
    tbResepDetailPopup.setAutoCreateRowSorter(true);

    dlgDetail = new JDialog(SwingUtilities.getWindowAncestor(this), "Detail Resep", Dialog.ModalityType.APPLICATION_MODAL);
    dlgDetail.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dlgDetail.getContentPane().add(new JScrollPane(tbResepDetailPopup));
    dlgDetail.setSize(800, 450);
    dlgDetail.setLocationRelativeTo(this);
}
private void showResepDetailDialog(String noResep) {
    if (dlgDetail == null) initResepDetailDialog();

    // kosongkan model
    Valid.tabelKosong(tmResepDetailPopup);

    // load data detail
    String sql = "SELECT rtd.kode_brng, " +
                 "       COALESCE(tb.nama_brng, rtd.nama_racikan, rtd.keterangan, '[Racikan]') AS nama_brng, " +
                 "       rtd.jml, rtd.satuan, rtd.aturan_pakai, rtd.keterangan, rtd.nama_racikan " +
                 "FROM resep_toko_detail rtd " +
                 "LEFT JOIN tokobarang tb ON tb.kode_brng = rtd.kode_brng AND rtd.kode_brng <> '' " +
                 "WHERE rtd.no_resep = ? ORDER BY nama_brng";
    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setString(1, noResep);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tmResepDetailPopup.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getDouble(3),
                    getSatuanTerkecilToko(rs.getString(1), rs.getString(4)), rs.getString(5), rs.getString(6),rs.getString(7)
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
    "LEFT JOIN reg_periksa rp ON rp.no_rawat = rt.no_rawat " +   // â† LEFT JOIN
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
    muteResepDetailEvent = true;
    try {
        if (noResep == null || noResep.trim().isEmpty()) {
            Valid.tabelKosong(tmResepDetail);
            tbResepDetail.setEnabled(true);
            return;
        }

        Valid.tabelKosong(tmResepDetail);

        String status = null;
        String noNota = null;
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

        if ("selesai".equalsIgnoreCase(status)) {
            tmResepDetail.addRow(new Object[]{
                null,
                "-",
                "Resep sudah dikerjakan" + (noNota != null && !noNota.isEmpty() ? " (No. Nota: " + noNota + ")" : ""),
                null,
                null,
                null,
                "Selesai",
                "-"
            });
            tbResepDetail.setEnabled(false);
            return;
        } else {
            tbResepDetail.setEnabled(true);
        }

        final String sql =
            "SELECT rtd.id, rtd.kode_brng, " +
            "       COALESCE(tb.nama_brng, rtd.nama_racikan, rtd.keterangan, '[Racikan]') AS nama_brng, " +
            "       rtd.jml, rtd.satuan, rtd.aturan_pakai, " +
            "       rtd.keterangan, COALESCE(rtd.nama_racikan,'-') AS nama_racikan " +
            "FROM resep_toko_detail rtd " +
            "LEFT JOIN tokobarang tb ON tb.kode_brng = rtd.kode_brng AND rtd.kode_brng <> '' " +
            "WHERE rtd.no_resep = ? " +
            "ORDER BY (rtd.kode_brng = '' OR rtd.kode_brng IS NULL), rtd.nama_racikan, nama_brng";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noResep);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tmResepDetail.addRow(new Object[]{
                        rs.getLong("id"),
                        rs.getString("kode_brng"),
                        rs.getString("nama_brng"),
                        rs.getDouble("jml"),
                        getSatuanTerkecilToko(rs.getString("kode_brng"), rs.getString("satuan")),
                        rs.getString("aturan_pakai"),
                        rs.getString("keterangan"),
                        rs.getString("nama_racikan")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal load detail: " + ex.getMessage());
        }
    } finally {
        muteResepDetailEvent = false;
    }
}

private void pasangAksiTambahItemResepDetail() {
    if (tbResepDetail == null) return;

    javax.swing.InputMap input = tbResepDetail.getInputMap(javax.swing.JComponent.WHEN_FOCUSED);
    javax.swing.ActionMap action = tbResepDetail.getActionMap();

    input.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, 0), "tambah_item_resep_detail");
    input.put(
        javax.swing.KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_N,
            java.awt.event.InputEvent.CTRL_DOWN_MASK
        ),
        "tambah_item_resep_detail"
    );

    action.put("tambah_item_resep_detail", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (!modeEditResepDetail) {
                JOptionPane.showMessageDialog(
                    TokoPenjualan.this,
                    "Aktifkan Mode Edit Detail Resep dulu.\nShortcut tambah item: Insert / Ctrl+N"
                );
                return;
            }
            tambahItemResepDetail();
        }
    });
}

private void simpanEditResepDetail(int row) {
    try {
        Object idObj = tmResepDetail.getValueAt(row, 0);
        if (idObj == null) return;

        long id = Long.parseLong(String.valueOf(idObj));
        double jml = Valid.SetAngka(String.valueOf(tmResepDetail.getValueAt(row, 3)));
        if (jml <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0.");
            if (noResepTerpilih != null) loadResepDetail(noResepTerpilih);
            return;
        }

        String aturan = String.valueOf(tmResepDetail.getValueAt(row, 5));
        if (aturan != null) aturan = aturan.trim();
        if (aturan != null && aturan.isEmpty()) aturan = null;

        try (PreparedStatement ps = koneksi.prepareStatement(
                "UPDATE resep_toko_detail SET jml=?, aturan_pakai=? WHERE id=?")) {
            ps.setDouble(1, jml);
            if (aturan == null) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, aturan);
            }
            ps.setLong(3, id);
            ps.executeUpdate();
        }

        String noResep = (noResepTerpilih == null ? "" : noResepTerpilih.trim());
        String kodeBrng = String.valueOf(tmResepDetail.getValueAt(row, 1));
        if (!noResep.isEmpty() && kodeBrng != null && !kodeBrng.trim().isEmpty()) {
            int updated;
            try (PreparedStatement up = koneksi.prepareStatement(
                    "UPDATE resep_dokter SET jml=?, aturan_pakai=? WHERE no_resep=? AND kode_brng=?")) {
                up.setDouble(1, jml);
                if (aturan == null) {
                    up.setNull(2, java.sql.Types.VARCHAR);
                } else {
                    up.setString(2, aturan);
                }
                up.setString(3, noResep);
                up.setString(4, kodeBrng.trim());
                updated = up.executeUpdate();
            }

            if (updated == 0) {
                if (pastikanDatabarangUntukResep(kodeBrng.trim())) {
                    try (PreparedStatement ins = koneksi.prepareStatement(
                            "INSERT INTO resep_dokter (no_resep, kode_brng, jml, aturan_pakai) VALUES (?,?,?,?)")) {
                        ins.setString(1, noResep);
                        ins.setString(2, kodeBrng.trim());
                        ins.setDouble(3, jml);
                        if (aturan == null) {
                            ins.setNull(4, java.sql.Types.VARCHAR);
                        } else {
                            ins.setString(4, aturan);
                        }
                        ins.executeUpdate();
                    }
                } else {
                    System.out.println("[RESEP_EDIT] Skip sinkron ke resep_dokter, kode tidak ditemukan di databarang/tokobarang: " + kodeBrng);
                }
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Gagal menyimpan edit detail resep: " + ex.getMessage());
    }
}

private boolean pastikanDatabarangUntukResep(String kodeBrng) throws SQLException {
    if (kodeBrng == null || kodeBrng.trim().isEmpty()) {
        return false;
    }
    String kode = kodeBrng.trim();

    try (PreparedStatement cek = koneksi.prepareStatement(
            "SELECT 1 FROM databarang WHERE kode_brng=? LIMIT 1")) {
        cek.setString(1, kode);
        try (ResultSet rsCek = cek.executeQuery()) {
            if (rsCek.next()) {
                return true;
            }
        }
    }

    final String sqlToko =
            "SELECT nama_brng, kode_sat, dasar, h_beli, distributor, grosir, retail, status " +
            "FROM tokobarang WHERE kode_brng=? LIMIT 1";
    try (PreparedStatement psToko = koneksi.prepareStatement(sqlToko)) {
        psToko.setString(1, kode);
        try (ResultSet rsToko = psToko.executeQuery()) {
            if (!rsToko.next()) {
                return false;
            }

            String nama = rsToko.getString("nama_brng");
            if (nama == null || nama.trim().isEmpty()) {
                nama = kode;
            } else {
                nama = nama.trim();
            }

            String kodeSat = rsToko.getString("kode_sat");
            if (kodeSat == null || kodeSat.trim().isEmpty()) {
                kodeSat = "-";
            } else {
                kodeSat = kodeSat.trim();
            }

            double dasar = rsToko.getDouble("dasar");
            if (rsToko.wasNull()) dasar = 0;
            double hBeli = rsToko.getDouble("h_beli");
            if (rsToko.wasNull()) hBeli = dasar;
            double distributor = rsToko.getDouble("distributor");
            if (rsToko.wasNull()) distributor = hBeli;
            double grosir = rsToko.getDouble("grosir");
            if (rsToko.wasNull()) grosir = distributor;
            double retail = rsToko.getDouble("retail");
            if (rsToko.wasNull()) retail = grosir;

            String status = rsToko.getString("status");
            status = "0".equals(status) ? "0" : "1";

            final String sqlInsert =
                    "INSERT INTO databarang " +
                    "(kode_brng,nama_brng,kode_satbesar,kode_sat,letak_barang,dasar,h_beli,ralan,kelas1,kelas2,kelas3,utama,vip,vvip,beliluar,jualbebas,karyawan,stokminimal,kdjns,isi,kapasitas,expire,status,kode_industri,kode_kategori,kode_golongan) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ins = koneksi.prepareStatement(sqlInsert)) {
                int idx = 1;
                ins.setString(idx++, kode);
                ins.setString(idx++, nama);
                ins.setString(idx++, kodeSat);
                ins.setString(idx++, kodeSat);
                ins.setString(idx++, "");
                ins.setDouble(idx++, dasar);
                ins.setDouble(idx++, hBeli);
                ins.setDouble(idx++, distributor);
                ins.setDouble(idx++, grosir);
                ins.setDouble(idx++, retail);
                ins.setDouble(idx++, retail);
                ins.setDouble(idx++, retail);
                ins.setDouble(idx++, retail);
                ins.setDouble(idx++, retail);
                ins.setDouble(idx++, retail);
                ins.setDouble(idx++, retail);
                ins.setDouble(idx++, retail);
                ins.setDouble(idx++, 0d);
                ins.setString(idx++, "-");
                ins.setDouble(idx++, 1d);
                ins.setDouble(idx++, 0d);
                ins.setNull(idx++, java.sql.Types.DATE);
                ins.setString(idx++, status);
                ins.setString(idx++, "-");
                ins.setString(idx++, "-");
                ins.setString(idx++, "-");
                ins.executeUpdate();
            }
        }
    } catch (SQLException ex) {
        String state = ex.getSQLState();
        if ("23000".equals(state)) {
            try (PreparedStatement cekUlang = koneksi.prepareStatement(
                    "SELECT 1 FROM databarang WHERE kode_brng=? LIMIT 1")) {
                cekUlang.setString(1, kode);
                try (ResultSet rs = cekUlang.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        }
        throw ex;
    }

    return true;
}
private static final class BarangPilihanResep {
    final String kode;
    final String nama;
    final String satuan;
    BarangPilihanResep(String kode, String nama, String satuan) {
        this.kode = kode;
        this.nama = nama;
        this.satuan = satuan;
    }
    @Override
    public String toString() {
        return nama + " [" + kode + "]";
    }
}

private static final class InputTambahResep {
    String kode;
    String namaBarang;
    String satuan;
    double jml;
    String aturan;
    String keterangan;
    String namaRacikan;
}

private InputTambahResep tampilDialogTambahItemResep() {
    final InputTambahResep input = new InputTambahResep();
    final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Tambah Item Detail Resep", true);
    dlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    final javax.swing.JTextField txtCari = new javax.swing.JTextField();
    final javax.swing.DefaultListModel<BarangPilihanResep> model = new javax.swing.DefaultListModel<>();
    final javax.swing.JList<BarangPilihanResep> list = new javax.swing.JList<>(model);
    list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    list.setVisibleRowCount(6);
    list.setCellRenderer(new javax.swing.DefaultListCellRenderer() {
        @Override
        public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> listx, Object value, int index,
                                                               boolean isSelected, boolean cellHasFocus) {
            java.awt.Component c = super.getListCellRendererComponent(listx, value, index, isSelected, cellHasFocus);
            if (value instanceof BarangPilihanResep) {
                BarangPilihanResep b = (BarangPilihanResep) value;
                setText("<html><b>" + b.nama + "</b> <span style='color:#657786'>[" + b.kode + "]</span></html>");
            }
            return c;
        }
    });
    final javax.swing.JScrollPane spList = new javax.swing.JScrollPane(list);

    final javax.swing.JTextField txtJml = new javax.swing.JTextField("1");
    final javax.swing.JTextField txtAturan = new javax.swing.JTextField();
    final javax.swing.JComboBox<String> cbJenis = new javax.swing.JComboBox<>(new String[]{"Non Racikan", "Racikan"});
    final javax.swing.JTextField txtNamaRacikan = new javax.swing.JTextField("-");
    txtNamaRacikan.setEnabled(false);
    cbJenis.addActionListener(e -> txtNamaRacikan.setEnabled("Racikan".equals(cbJenis.getSelectedItem())));

    final javax.swing.JLabel lblHint = new javax.swing.JLabel("Ketik minimal 2 huruf nama barang, lalu pilih hasilnya.");
    lblHint.setForeground(new java.awt.Color(90, 100, 110));

    Runnable refresh = () -> {
        model.clear();
        String key = txtCari.getText() == null ? "" : txtCari.getText().trim();
        if (key.length() < 2) return;
        java.util.List<String[]> rows = cariBarangTokoByNama(key, 80);
        for (String[] row : rows) {
            model.addElement(new BarangPilihanResep(row[0], row[1], row[2]));
        }
        if (!model.isEmpty()) list.setSelectedIndex(0);
    };
    txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { refresh.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { refresh.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh.run(); }
    });

    javax.swing.JPanel root = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
    root.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));

    javax.swing.JLabel title = new javax.swing.JLabel("Tambah Item ke Detail Resep");
    title.setFont(title.getFont().deriveFont(java.awt.Font.BOLD, 14f));
    root.add(title, java.awt.BorderLayout.NORTH);

    javax.swing.JPanel form = new javax.swing.JPanel(new java.awt.GridBagLayout());
    java.awt.GridBagConstraints g = new java.awt.GridBagConstraints();
    g.insets = new java.awt.Insets(4, 4, 4, 4);
    g.fill = java.awt.GridBagConstraints.HORIZONTAL;
    g.weightx = 0;
    g.gridx = 0;
    g.gridy = 0;
    form.add(new javax.swing.JLabel("Cari Nama Barang"), g);
    g.gridx = 1;
    g.weightx = 1;
    form.add(txtCari, g);

    g.gridx = 1;
    g.gridy++;
    g.weightx = 1;
    g.fill = java.awt.GridBagConstraints.HORIZONTAL;
    form.add(lblHint, g);

    g.gridx = 0;
    g.gridy++;
    g.weightx = 0;
    form.add(new javax.swing.JLabel("Hasil Pencarian"), g);
    g.gridx = 1;
    g.weightx = 1;
    g.fill = java.awt.GridBagConstraints.BOTH;
    g.weighty = 1;
    form.add(spList, g);
    g.weighty = 0;
    g.fill = java.awt.GridBagConstraints.HORIZONTAL;

    g.gridx = 0;
    g.gridy++;
    g.weightx = 0;
    form.add(new javax.swing.JLabel("Jumlah"), g);
    g.gridx = 1;
    g.weightx = 1;
    form.add(txtJml, g);

    g.gridx = 0;
    g.gridy++;
    g.weightx = 0;
    form.add(new javax.swing.JLabel("Aturan Pakai"), g);
    g.gridx = 1;
    g.weightx = 1;
    form.add(txtAturan, g);

    g.gridx = 0;
    g.gridy++;
    g.weightx = 0;
    form.add(new javax.swing.JLabel("Jenis"), g);
    g.gridx = 1;
    g.weightx = 1;
    form.add(cbJenis, g);

    g.gridx = 0;
    g.gridy++;
    g.weightx = 0;
    form.add(new javax.swing.JLabel("Nama Racikan"), g);
    g.gridx = 1;
    g.weightx = 1;
    form.add(txtNamaRacikan, g);
    root.add(form, java.awt.BorderLayout.CENTER);

    javax.swing.JButton btnCancel = new javax.swing.JButton("Batal");
    javax.swing.JButton btnOk = new javax.swing.JButton("Tambah");
    javax.swing.JPanel pnlBtn = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
    pnlBtn.add(btnCancel);
    pnlBtn.add(btnOk);
    root.add(pnlBtn, java.awt.BorderLayout.SOUTH);

    final boolean[] approved = new boolean[]{false};
    btnCancel.addActionListener(e -> dlg.dispose());
    btnOk.addActionListener(e -> {
        BarangPilihanResep b = list.getSelectedValue();
        if (b == null) {
            JOptionPane.showMessageDialog(dlg, "Pilih barang dari hasil pencarian.");
            return;
        }
        double j = Valid.SetAngka(txtJml.getText());
        if (j <= 0) {
            JOptionPane.showMessageDialog(dlg, "Jumlah harus lebih dari 0.");
            return;
        }
        input.kode = b.kode;
        input.namaBarang = b.nama;
        input.satuan = b.satuan;
        input.jml = j;
        input.aturan = txtAturan.getText() == null ? null : txtAturan.getText().trim();
        if (input.aturan != null && input.aturan.isEmpty()) input.aturan = null;
        input.keterangan = String.valueOf(cbJenis.getSelectedItem());
        if (input.keterangan == null || input.keterangan.trim().isEmpty()) input.keterangan = "Non Racikan";
        input.namaRacikan = txtNamaRacikan.getText() == null ? "" : txtNamaRacikan.getText().trim();
        if (!"Racikan".equals(input.keterangan)) {
            input.namaRacikan = null;
        } else if (input.namaRacikan.isEmpty()) {
            input.namaRacikan = "-";
        }
        approved[0] = true;
        dlg.dispose();
    });

    dlg.getRootPane().setDefaultButton(btnOk);
    dlg.getRootPane().registerKeyboardAction(
        e -> dlg.dispose(),
        javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
        javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
    );

    dlg.setContentPane(root);
    dlg.setSize(560, 520);
    dlg.setLocationRelativeTo(this);
    javax.swing.SwingUtilities.invokeLater(txtCari::requestFocusInWindow);
    dlg.setVisible(true);
    return approved[0] ? input : null;
}

private void tambahItemResepDetail() {
    try {
        String noResep = (noResepTerpilih == null ? "" : noResepTerpilih.trim());
        if (noResep.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih resep dulu.");
            return;
        }
        if (!tbResepDetail.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Resep sudah selesai, tidak bisa ditambah.");
            return;
        }

        InputTambahResep input = tampilDialogTambahItemResep();
        if (input == null) return;

        String kode = input.kode;
        String namaBarang = input.namaBarang;
        String satuan = input.satuan;
        double jml = input.jml;
        String aturan = input.aturan;
        String keterangan = input.keterangan;
        String namaRacikan = input.namaRacikan;

        try (PreparedStatement ins = koneksi.prepareStatement(
                "INSERT INTO resep_toko_detail (no_resep,kode_brng,jml,satuan,aturan_pakai,keterangan,nama_racikan) " +
                "VALUES (?,?,?,?,?,?,?)")) {
            ins.setString(1, noResep);
            ins.setString(2, kode);
            ins.setDouble(3, jml);
            if (satuan == null || satuan.trim().isEmpty()) {
                ins.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ins.setString(4, satuan.trim());
            }
            if (aturan == null) {
                ins.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ins.setString(5, aturan);
            }
            ins.setString(6, keterangan);
            if (namaRacikan == null) {
                ins.setNull(7, java.sql.Types.VARCHAR);
            } else {
                ins.setString(7, namaRacikan);
            }
            ins.executeUpdate();
        }

        if (pastikanDatabarangUntukResep(kode)) {
            int updated;
            try (PreparedStatement up = koneksi.prepareStatement(
                    "UPDATE resep_dokter SET jml=jml+?, aturan_pakai=? WHERE no_resep=? AND kode_brng=?")) {
                up.setDouble(1, jml);
                if (aturan == null) {
                    up.setNull(2, java.sql.Types.VARCHAR);
                } else {
                    up.setString(2, aturan);
                }
                up.setString(3, noResep);
                up.setString(4, kode);
                updated = up.executeUpdate();
            }
            if (updated == 0) {
                try (PreparedStatement insRd = koneksi.prepareStatement(
                        "INSERT INTO resep_dokter (no_resep, kode_brng, jml, aturan_pakai) VALUES (?,?,?,?)")) {
                    insRd.setString(1, noResep);
                    insRd.setString(2, kode);
                    insRd.setDouble(3, jml);
                    if (aturan == null) {
                        insRd.setNull(4, java.sql.Types.VARCHAR);
                    } else {
                        insRd.setString(4, aturan);
                    }
                    insRd.executeUpdate();
                }
            }
        } else {
            System.out.println("[RESEP_EDIT] Item baru tidak masuk resep_dokter karena kode tidak ada di databarang/tokobarang: " + kode);
        }

        loadResepDetail(noResep);
        JOptionPane.showMessageDialog(
            this,
            "Item resep ditambahkan: " + kode + (namaBarang == null || namaBarang.isEmpty() ? "" : " - " + namaBarang)
        );
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Gagal menambah item detail resep: " + ex.getMessage());
    }
}

private java.util.List<String[]> cariBarangTokoByNama(String keyword, int limit) {
    java.util.List<String[]> rows = new java.util.ArrayList<>();
    try (PreparedStatement ps = koneksi.prepareStatement(
            "SELECT kode_brng, nama_brng, " +
            "       COALESCE(NULLIF(kode_sat2,''), NULLIF(kode_sat1,''), NULLIF(kode_sat,''), '-') AS satuan_terkecil " +
            "FROM tokobarang " +
            "WHERE nama_brng LIKE ? " +
            "ORDER BY nama_brng " +
            "LIMIT ?")) {
        ps.setString(1, "%" + keyword + "%");
        ps.setInt(2, limit <= 0 ? 50 : limit);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{
                    rs.getString("kode_brng"),
                    rs.getString("nama_brng"),
                    rs.getString("satuan_terkecil")
                });
            }
        }
    } catch (Exception ex) {
        System.out.println("[RESEP_EDIT] cari barang toko gagal: " + ex.getMessage());
    }
    return rows;
}

private String getSatuanTerkecilToko(String kodeBrng, String fallbackSatuan) {
    String fallback = (fallbackSatuan == null || fallbackSatuan.trim().isEmpty()) ? "-" : fallbackSatuan.trim();
    if (kodeBrng == null || kodeBrng.trim().isEmpty()) {
        return fallback;
    }
    final String sql =
        "SELECT COALESCE(NULLIF(kode_sat2,''), NULLIF(kode_sat1,''), NULLIF(kode_sat,''), ?) AS satuan_terkecil " +
        "FROM tokobarang WHERE kode_brng=? LIMIT 1";
    try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
        ps.setString(1, fallback);
        ps.setString(2, kodeBrng);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String sat = rs.getString("satuan_terkecil");
                if (sat != null && !sat.trim().isEmpty()) return sat.trim();
            }
        }
    } catch (Exception ex) {
        System.out.println("[RESEP_EDIT] gagal ambil satuan terkecil: " + ex.getMessage());
    }
    return fallback;
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

private String ambilNoResepHeaderTerpilih() {
    try {
        if (tbResepHeader == null || tmResepHeader == null) return "";
        int viewRow = tbResepHeader.getSelectedRow();
        if (viewRow < 0) return "";
        int modelRow = tbResepHeader.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= tmResepHeader.getRowCount()) return "";
        Object v = tmResepHeader.getValueAt(modelRow, 0);
        return v == null ? "" : String.valueOf(v).trim();
    } catch (Exception ex) {
        System.out.println("ambilNoResepHeaderTerpilih: " + ex.getMessage());
        return "";
    }
}

private String buatResepTokoOtomatisDariPenjualan(String noRawat, String kdDokter) {
    if (noRawat == null || noRawat.trim().isEmpty()) {
        System.out.println("[RESEP_AUTO] no_rawat kosong, tidak bisa buat resep otomatis.");
        return "";
    }

    try {
        String noResepBaru = generateNoResepTokoOtomatis();
        String tglSql = Valid.SetTgl(Tgl.getSelectedItem() + "");
        String jamNow = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        String tglResep = tglSql + " " + jamNow;

        try (PreparedStatement psHdr = koneksi.prepareStatement(
                "INSERT INTO resep_toko (no_resep,no_rawat,kd_dokter,tgl_resep,keterangan,status) " +
                "VALUES (?,?,?,?,?,'baru')")) {
            psHdr.setString(1, noResepBaru);
            psHdr.setString(2, noRawat.trim());
            if (kdDokter == null || kdDokter.trim().isEmpty()) {
                psHdr.setNull(3, java.sql.Types.VARCHAR);
            } else {
                psHdr.setString(3, kdDokter.trim());
            }
            psHdr.setString(4, tglResep);
            psHdr.setString(5, "Non Racikan");
            psHdr.executeUpdate();
        }

        int totalDetail = 0;
        try (PreparedStatement psDtl = koneksi.prepareStatement(
                "INSERT INTO resep_toko_detail (no_resep,kode_brng,jml,satuan,aturan_pakai,keterangan,nama_racikan) " +
                "VALUES (?,?,?,?,?,?,?)")) {
            for (int r = 0; r < tabMode.getRowCount(); r++) {
                double qtyInput = Valid.SetAngka(String.valueOf(tabMode.getValueAt(r, 0)));
                if (qtyInput <= 0) continue;

                String kodeBrg = String.valueOf(tabMode.getValueAt(r, 1)).trim();
                if (kodeBrg.isEmpty()) continue;

                String satuan = String.valueOf(tabMode.getValueAt(r, 4)).trim();
                psDtl.setString(1, noResepBaru);
                psDtl.setString(2, kodeBrg);
                psDtl.setDouble(3, qtyInput);
                if (satuan.isEmpty() || "-".equals(satuan)) {
                    psDtl.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    psDtl.setString(4, satuan);
                }
                psDtl.setNull(5, java.sql.Types.VARCHAR);
                psDtl.setString(6, "Non Racikan");
                psDtl.setNull(7, java.sql.Types.VARCHAR);
                psDtl.addBatch();
                totalDetail++;
            }
            if (totalDetail > 0) {
                psDtl.executeBatch();
            }
        }

        if (totalDetail <= 0) {
            System.out.println("[RESEP_AUTO] detail resep kosong, header akan dihapus.");
            try (PreparedStatement del = koneksi.prepareStatement("DELETE FROM resep_toko WHERE no_resep=?")) {
                del.setString(1, noResepBaru);
                del.executeUpdate();
            }
            return "";
        }

        System.out.println("[RESEP_AUTO] resep otomatis terbentuk: " + noResepBaru + " | item=" + totalDetail);
        return noResepBaru;
    } catch (Exception ex) {
        System.out.println("[RESEP_AUTO] gagal membuat resep otomatis: " + ex.getMessage());
        return "";
    }
}

private String generateNoResepTokoOtomatis() throws SQLException {
    String tglSql = Valid.SetTgl(Tgl.getSelectedItem() + "");
    String prefix = tglSql.replace("-", "");

    int urut = 1;
    try (PreparedStatement ps = koneksi.prepareStatement(
            "SELECT IFNULL(MAX(CAST(RIGHT(no_resep,4) AS UNSIGNED)),0) AS max_urut " +
            "FROM resep_toko WHERE LEFT(no_resep,8)=?")) {
        ps.setString(1, prefix);
        try (ResultSet rsNo = ps.executeQuery()) {
            if (rsNo.next()) {
                urut = rsNo.getInt("max_urut") + 1;
            }
        }
    }

    for (int i = 0; i < 10000; i++) {
        String kandidat = prefix + String.format("%04d", (urut + i));
        try (PreparedStatement cek = koneksi.prepareStatement(
                "SELECT 1 FROM resep_toko WHERE no_resep=? LIMIT 1")) {
            cek.setString(1, kandidat);
            try (ResultSet rsCek = cek.executeQuery()) {
                if (!rsCek.next()) {
                    return kandidat;
                }
            }
        }
    }

    throw new SQLException("Nomor resep otomatis penuh untuk tanggal " + tglSql);
}

private void initNoRawatKunjunganPanel() {
    labelNoRawatKunjungan = new widget.Label();
    labelNoRawatKunjungan.setForeground(new java.awt.Color(255, 255, 255));
    labelNoRawatKunjungan.setText("No.Rawat :");
    labelNoRawatKunjungan.setName("labelNoRawatKunjungan");
    labelNoRawatKunjungan.setPreferredSize(new java.awt.Dimension(70, 23));
    panelisi3.add(labelNoRawatKunjungan);
    // Persist tepat di bawah baris Pasien (kdmem/nmmem)
    labelNoRawatKunjungan.setBounds(365, 40, 84, 23);

    CbNoRawatKunjungan = new widget.ComboBox();
    CbNoRawatKunjungan.setBackground(new java.awt.Color(0, 153, 255));
    CbNoRawatKunjungan.setForeground(new java.awt.Color(255, 255, 255));
    CbNoRawatKunjungan.setName("CbNoRawatKunjungan");
    CbNoRawatKunjungan.setFont(new java.awt.Font("Trebuchet MS", 0, 12));
    CbNoRawatKunjungan.setPreferredSize(new java.awt.Dimension(353, 23));
    CbNoRawatKunjungan.addItemListener(new java.awt.event.ItemListener() {
        @Override
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            if (evt.getStateChange() != java.awt.event.ItemEvent.SELECTED) return;
            int idx = CbNoRawatKunjungan.getSelectedIndex();
            if (idx <= 0 || idx >= valueNoRkmKunjungan.size()) return;

            String noRkm = valueNoRkmKunjungan.get(idx);
            String nmPasien = valueNmPasienKunjungan.get(idx);
            if (noRkm != null && !noRkm.trim().isEmpty()) {
                kdmem.setText(noRkm);
            }
            if (nmPasien != null && !nmPasien.trim().isEmpty()) {
                nmmem.setText(nmPasien);
            }
        }
    });
    panelisi3.add(CbNoRawatKunjungan);
    CbNoRawatKunjungan.setBounds(449, 40, 359, 23);
}

private String ambilNoRawatTerpilihDariList() {
    if (CbNoRawatKunjungan == null) return "";
    int idx = CbNoRawatKunjungan.getSelectedIndex();
    if (idx < 0 || idx >= valueNoRawatKunjungan.size()) return "";
    String noRawat = valueNoRawatKunjungan.get(idx);
    return noRawat == null ? "" : noRawat.trim();
}

private void pilihNoRawatDiList(String noRawat) {
    if (CbNoRawatKunjungan == null || noRawat == null || noRawat.trim().isEmpty()) return;
    for (int i = 0; i < valueNoRawatKunjungan.size(); i++) {
        if (noRawat.trim().equals(valueNoRawatKunjungan.get(i))) {
            CbNoRawatKunjungan.setSelectedIndex(i);
            return;
        }
    }
}

private void refreshNoRawatKunjungan() {
    if (CbNoRawatKunjungan == null) return;

    valueNoRawatKunjungan.clear();
    valueNoRkmKunjungan.clear();
    valueNmPasienKunjungan.clear();
    CbNoRawatKunjungan.removeAllItems();
    CbNoRawatKunjungan.addItem("-- Pilih Kunjungan Pasien --");
    valueNoRawatKunjungan.add("");
    valueNoRkmKunjungan.add("");
    valueNmPasienKunjungan.add("");

    String jenis = String.valueOf(Jenisjual.getSelectedItem()).trim();
    if (!"Resep".equalsIgnoreCase(jenis)) return;

    String noRkmMedis = kdmem.getText().trim();
    String tglReg = Valid.SetTgl(Tgl.getSelectedItem() + "");
    if (tglReg.isEmpty()) return;

    StringBuilder sql = new StringBuilder(
        "SELECT rp.no_rawat, COALESCE(p.nm_pasien,'-') AS nm_pasien, " +
        "       COALESCE(p.no_rkm_medis,'-') AS no_rkm_medis, " +
        "       COALESCE(pl.nm_poli,'-') AS nm_poli, COALESCE(d.nm_dokter,'-') AS nm_dokter, " +
        "       COALESCE(rp.jam_reg,'00:00:00') AS jam_reg " +
        "FROM reg_periksa rp " +
        "INNER JOIN pasien p ON p.no_rkm_medis = rp.no_rkm_medis " +
        "LEFT JOIN poliklinik pl ON pl.kd_poli = rp.kd_poli " +
        "LEFT JOIN dokter d ON d.kd_dokter = rp.kd_dokter " +
        "WHERE rp.tgl_registrasi = ? "
    );
    if (!noRkmMedis.isEmpty()) {
        sql.append("AND rp.no_rkm_medis = ? ");
    }
    sql.append("ORDER BY rp.jam_reg DESC, rp.no_rawat DESC");

    try (PreparedStatement psNoRawat = koneksi.prepareStatement(sql.toString())) {
        psNoRawat.setString(1, tglReg);
        if (!noRkmMedis.isEmpty()) {
            psNoRawat.setString(2, noRkmMedis);
        }
        try (ResultSet rsNoRawat = psNoRawat.executeQuery()) {
            while (rsNoRawat.next()) {
                String noRawat = rsNoRawat.getString("no_rawat");
                String nmPasien = rsNoRawat.getString("nm_pasien");
                String noRkm = rsNoRawat.getString("no_rkm_medis");
                String nmPoli = rsNoRawat.getString("nm_poli");
                String nmDokter = rsNoRawat.getString("nm_dokter");
                String jamReg = rsNoRawat.getString("jam_reg");

                valueNoRawatKunjungan.add(noRawat);
                valueNoRkmKunjungan.add(noRkm);
                valueNmPasienKunjungan.add(nmPasien);
                CbNoRawatKunjungan.addItem(
                    noRawat + " | " + nmPasien + " (" + noRkm + ") | " + jamReg + " | " + nmPoli + " | " + nmDokter
                );
            }
        }
    } catch (Exception ex) {
        System.out.println("refreshNoRawatKunjungan: " + ex.getMessage());
    }

    if (valueNoRawatKunjungan.size() == 2) {
        CbNoRawatKunjungan.setSelectedIndex(1);
    } else {
        CbNoRawatKunjungan.setSelectedIndex(0);
    }
}

private String pilihNoRawatPasienPadaTanggal(String noRkmMedis, String tglReg) {
    if (noRkmMedis == null || noRkmMedis.trim().isEmpty()) {
        Valid.textKosong(kdmem, "pasien");
        return "";
    }
    if (tglReg == null || tglReg.trim().isEmpty()) {
        tglReg = Valid.SetTgl(Tgl.getSelectedItem() + "");
    }

    java.util.List<String> listNoRawat = new java.util.ArrayList<>();
    java.util.List<String> listLabel = new java.util.ArrayList<>();

    String sql =
        "SELECT rp.no_rawat, COALESCE(p.nm_pasien,'-') AS nm_pasien, " +
        "       COALESCE(pl.nm_poli,'-') AS nm_poli, COALESCE(d.nm_dokter,'-') AS nm_dokter, " +
        "       COALESCE(rp.jam_reg,'00:00:00') AS jam_reg " +
        "FROM reg_periksa rp " +
        "INNER JOIN pasien p ON p.no_rkm_medis = rp.no_rkm_medis " +
        "LEFT JOIN poliklinik pl ON pl.kd_poli = rp.kd_poli " +
        "LEFT JOIN dokter d ON d.kd_dokter = rp.kd_dokter " +
        "WHERE rp.no_rkm_medis = ? AND rp.tgl_registrasi = ? " +
        "ORDER BY rp.jam_reg DESC, rp.no_rawat DESC";

    try (PreparedStatement psNoRawat = koneksi.prepareStatement(sql)) {
        psNoRawat.setString(1, noRkmMedis.trim());
        psNoRawat.setString(2, tglReg);
        try (ResultSet rsNoRawat = psNoRawat.executeQuery()) {
            while (rsNoRawat.next()) {
                String noRawat = rsNoRawat.getString("no_rawat");
                String nmPasien = rsNoRawat.getString("nm_pasien");
                String nmPoli = rsNoRawat.getString("nm_poli");
                String nmDokter = rsNoRawat.getString("nm_dokter");
                String jamReg = rsNoRawat.getString("jam_reg");

                listNoRawat.add(noRawat);
                listLabel.add(noRawat + " | " + nmPasien + " | " + jamReg + " | " + nmPoli + " | " + nmDokter);
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Gagal mengambil No.Rawat: " + ex.getMessage());
        return "";
    }

    if (listNoRawat.isEmpty()) {
        JOptionPane.showMessageDialog(
            this,
            "No.Rawat pasien tidak ditemukan pada tanggal " + tglReg + ".\n" +
            "Pastikan pasien sudah terdaftar di reg_periksa pada hari tersebut."
        );
        return "";
    }

    if (listNoRawat.size() == 1) {
        return listNoRawat.get(0);
    }

    javax.swing.JComboBox<String> cbNoRawat = new javax.swing.JComboBox<>(listLabel.toArray(new String[0]));
    cbNoRawat.setSelectedIndex(0);

    int pilih = JOptionPane.showConfirmDialog(
        this,
        cbNoRawat,
        "Pilih No.Rawat Pasien",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (pilih == JOptionPane.OK_OPTION) {
        int idx = cbNoRawat.getSelectedIndex();
        if (idx >= 0 && idx < listNoRawat.size()) {
            return listNoRawat.get(idx);
        }
    }

    return "";
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

private void bukaBillingDariTokoPenjualan() {
    try {
        String noRawat = "";

        //String noRawat = null;
    
        noRawat = ambilNoRawatDariResep(noResepTerpilih);
    

        // 2) Kalau masih kosong, ambil dari tokopenjualan berdasarkan nota_jual
        if ((noRawat == null || noRawat.trim().isEmpty()) && NoNota.getText() != null && !NoNota.getText().trim().isEmpty()) {
            noRawat = Sequel.cariIsi("SELECT no_rawat FROM tokopenjualan WHERE nota_jual=? LIMIT 1", NoNota.getText().trim());
            System.out.println("DEBUG [TOKO] ambil no_rawat dari tokopenjualan: " + NoNota.getText() + " -> " + noRawat);
        }

        // 3) Kalau tetap kosong, stop (billing tidak dibuka)
        if (noRawat == null || noRawat.trim().isEmpty()) {
            System.out.println("INFO: Nota toko tanpa no_rawat, billing tidak dibuka.");
            return;
        }

        // 4) Buka billing (cara paling gampang: panggil DlgBilling existing)
        akses.setform("TokoPenjualan"); // opsional biar context jelas
        billing.TNoRw.setText(noRawat);
        billing.isCek();
        billing.isRawat();
        billing.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
        billing.setLocationRelativeTo(internalFrame1);
        billing.setVisible(true);

    } catch (Exception e) {
        System.out.println("Notif bukaBillingDariTokoPenjualan: " + e);
    }
}
private void initKeranjangPanel() {
    // model keranjang (tampilan ringkas)
    tabModeKeranjang = new javax.swing.table.DefaultTableModel(
        null,
        new Object[]{"Kode", "Nama", "Jml", "Sat", "Harga", "Subtotal"}
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    tbKeranjang.setModel(tabModeKeranjang);
    tbKeranjang.setRowHeight(22);
    tbKeranjang.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

    // lebar kolom biar rapi
    setW(tbKeranjang, 0, 70);   // Kode
    setW(tbKeranjang, 1, 220);  // Nama
    setW(tbKeranjang, 2, 40);   // Jml
    setW(tbKeranjang, 3, 45);   // Sat
    setW(tbKeranjang, 4, 70);   // Harga
    setW(tbKeranjang, 5, 80);   // Subtotal

    javax.swing.JScrollPane sp = new javax.swing.JScrollPane(tbKeranjang);

    panelKeranjang.removeAll();
    panelKeranjang.setLayout(new java.awt.BorderLayout());
    panelKeranjang.add(sp, java.awt.BorderLayout.CENTER);
    panelKeranjang.revalidate();
    panelKeranjang.repaint();

    // listen perubahan qty di tabel bawah (tbObat)
    pasangListenerQty();
}

private void setW(javax.swing.JTable t, int col, int w) {
    if (col < 0 || col >= t.getColumnCount()) return;
    t.getColumnModel().getColumn(col).setPreferredWidth(w);
}


// indeks kolom tbObat (sesuaikan jika beda)
//private static final int COL_JML      = 0;
//private static final int COL_KODE     = 1;
private static final int COL_NAMA     = 2;
//private static final int COL_SATUAN   = 4;
//private static final int COL_HARGA    = 5;
private static final int COL_SUBTOTAL = 6;

private void pasangListenerQty() {
    if (tbObat == null) return;

    javax.swing.table.TableModel m = tbObat.getModel();
    if (!(m instanceof javax.swing.table.DefaultTableModel)) return;

    final javax.swing.table.DefaultTableModel modelObat =
            (javax.swing.table.DefaultTableModel) m;

    // biar gak dobel listener
    if (qtyListener != null) {
        modelObat.removeTableModelListener(qtyListener);
    }

    qtyListener = e -> {
        if (syncKeranjang) return;
        if (e.getType() != javax.swing.event.TableModelEvent.UPDATE) return;

        int row = e.getFirstRow();
        int col = e.getColumn();

        if (col != COL_JML) return;

        syncKeranjang = true;
        try {
            syncKeranjangDariRow(row);  // method kamu
        } finally {
            syncKeranjang = false;
        }
    };

    modelObat.addTableModelListener(qtyListener);
}

//private void syncKeranjangDariRow(int row) {
//    javax.swing.table.TableModel m = tbObat.getModel();
//
//    double jml   = Valid.SetAngka(m.getValueAt(row, COL_JML).toString());
//    String kode  = m.getValueAt(row, COL_KODE).toString();
//    String nama  = m.getValueAt(row, COL_NAMA).toString();
//    String sat   = m.getValueAt(row, COL_SATUAN).toString();
//    double harga = Valid.SetAngka(m.getValueAt(row, COL_HARGA).toString());
//
//    double subtotal;
//    try {
//        subtotal = Valid.SetAngka(m.getValueAt(row, COL_SUBTOTAL).toString());
//    } catch (Exception e) {
//        subtotal = jml * harga;
//    }
//
//    int idx = cariIndexKeranjang(kode);
//
//    if (jml <= 0) {
//        if (idx >= 0) tabModeKeranjang.removeRow(idx);
//        return;
//    }
//
//    if (idx >= 0) {
//        tabModeKeranjang.setValueAt(nama, idx, 1);
//        tabModeKeranjang.setValueAt(jml, idx, 2);
//        tabModeKeranjang.setValueAt(sat, idx, 3);
//        tabModeKeranjang.setValueAt(harga, idx, 4);
//        tabModeKeranjang.setValueAt(subtotal, idx, 5);
//    } else {
//        tabModeKeranjang.addRow(new Object[]{kode, nama, jml, sat, harga, subtotal});
//    }
//}

private int cariIndexKeranjang(String kode) {
    for (int i = 0; i < tabModeKeranjang.getRowCount(); i++) {
        if (kode.equals(tabModeKeranjang.getValueAt(i, 0).toString())) return i;
    }
    return -1;
}
private void rebuildKeranjangDariTable() {
    tabModeKeranjang.setRowCount(0);
    javax.swing.table.TableModel m = tbObat.getModel();

    for (int r = 0; r < m.getRowCount(); r++) {
        double jml = 0;
        try { jml = Valid.SetAngka(m.getValueAt(r, COL_JML).toString()); } catch (Exception e) {}
        if (jml > 0) {
            syncKeranjangDariRow(r);
        }
    }
}
private boolean qtyEditorInstalled = false;
//private void pasangEnterPadaQty() {
//    final int COL_QTY = 0;
//
//JTextField tf = new JTextField();
//tf.setHorizontalAlignment(SwingConstants.CENTER);
//
//QtyEditor qtyEditor = new QtyEditor(tf);
//
//// ENTER: commit + update keranjang + fokus ke TCari
//tf.addActionListener(evt -> {
//    try {
//        if (tbObat.isEditing()) tbObat.getCellEditor().stopCellEditing();
//
//        int rowView = tbObat.getSelectedRow();
//        if (rowView < 0) return;
//
//        int rowModel = (tbObat.getRowSorter() != null)
//                ? tbObat.convertRowIndexToModel(rowView)
//                : rowView;
//
//        syncKeranjangDariRow(rowModel);
//
//        SwingUtilities.invokeLater(() -> {
//            TCari.requestFocusInWindow();
//            TCari.selectAll();
//        });
//    } catch (Exception ex) {
//        System.out.println("ENTER QTY error: " + ex);
//    }
//});
//
//tbObat.getColumnModel().getColumn(COL_QTY).setCellEditor(qtyEditor);
//
//    tf.addActionListener(evt -> {
//    try {
//        if (tbObat.isEditing()) {
//            tbObat.getCellEditor().stopCellEditing(); // commit qty
//        }
//
//        int rowView = tbObat.getSelectedRow();
//        if (rowView < 0) return;
//
//        int rowModel = rowView;
//        if (tbObat.getRowSorter() != null) {
//            rowModel = tbObat.convertRowIndexToModel(rowView);
//        }
//
//        // âœ… update panel kanan
//        syncKeranjangDariRow(rowModel);
//
//        // âœ… setelah enter, fokus balik ke TCari
//        javax.swing.SwingUtilities.invokeLater(() -> {
//            TCari.requestFocusInWindow();
//            TCari.selectAll(); // biar langsung ketik cari lagi
//        });
//
//    } catch (Exception ex) {
//        System.out.println("ENTER QTY error: " + ex);
//    }
//});
//
//    tbObat.getColumnModel().getColumn(COL_QTY).setCellEditor(qtyEditor);
//}
////private void formatKolomJmlTanpaKoma() {
////    final int COL_JML = 0;
////
////    DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
////        final DecimalFormat dfInt = new DecimalFormat("#0");    // 0,1,2...
////        final DecimalFormat dfDec = new DecimalFormat("#0.##"); // kalau ada pecahan tampil 1.5, 2.25
////
////        @Override
////        protected void setValue(Object value) {
////            if (value == null) {
////                setText("0");
////                return;
////            }
////            try {
////                double v = Double.parseDouble(value.toString());
////                // kalau bilangan bulat, tampil tanpa .0
////                if (v == Math.rint(v)) {
////                    setText(dfInt.format(v));
////                } else {
////                    setText(dfDec.format(v));
////                }
////            } catch (Exception e) {
////                setText(value.toString());
////            }
////        }
////    };
////    r.setHorizontalAlignment(SwingConstants.CENTER);
////
////    tbObat.getColumnModel().getColumn(COL_JML).setCellRenderer(r);
////}
//private static class QtyEditor extends DefaultCellEditor {
//    private final DecimalFormat dfInt = new DecimalFormat("#0");
//    private final DecimalFormat dfDec = new DecimalFormat("#0.##");
//
//    public QtyEditor(JTextField tf) {
//        super(tf);
//        setClickCountToStart(1);
//    }
//
//    @Override
//    public Component getTableCellEditorComponent(
//            JTable table, Object value, boolean isSelected, int row, int column) {
//
//        Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
//        JTextField tf = (JTextField) getComponent();
//
//        // âœ… jangan panggil helper trim() milik class luar
//        java.lang.String s = (value == null) ? "" : java.lang.String.valueOf(value).trim();
//
//        // kalau 0 / 0.0 / kosong -> tampil kosong
//        if (s.isEmpty() || "0".equals(s) || "0.0".equals(s) || "0,0".equals(s)) {
//            tf.setText("");
//        } else {
//            double v;
//            try {
//                v = Double.parseDouble(s.replace(",", "."));
//            } catch (Exception e) {
//                v = 0d;
//            }
//
//            if (v == Math.rint(v)) tf.setText(dfInt.format(v));
//            else tf.setText(dfDec.format(v));
//        }
//
//        tf.selectAll();
//        return c;
//    }
//
//    @Override
//    public Object getCellEditorValue() {
//        String t = ((JTextField) getComponent()).getText().trim();
//        if (t.isEmpty()) return "";           // âœ… simpan kosong
//        t = t.replace(",", ".");
//        try {
//            double v = Double.parseDouble(t);
//            if (v == 0d) return "";           // opsional: 0 jadi kosong
//            return v;                         // atau return t kalau kamu mau string
//        } catch (Exception e) {
//            return "";
//        }
//    }
//}
//
//private void initPanelKeranjangKanan() {
//    panelKeranjangKanan = new javax.swing.JPanel(new java.awt.BorderLayout());
//    panelKeranjangKanan.add(new javax.swing.JScrollPane(tbKeranjang), java.awt.BorderLayout.CENTER);
//}
//private void setupPanelKananCards() {
//    cardKanan = new java.awt.CardLayout();
//    panelKeranjang.removeAll();
//    panelKeranjang.setLayout(cardKanan);
//
//    // kartu RESEP (pakai panelResepToko yang kamu buat)
//    panelKeranjang.add(panelResepToko, "RESEP");
//
//    // kartu BASKET (keranjang)
//    panelKeranjang.add(panelKeranjangKanan, "BASKET");
//
//    panelKeranjang.revalidate();
//    panelKeranjang.repaint();
//
//    // show awal sesuai pilihan
//    switchPanelKananByJenis();
//}
//private void switchPanelKananByJenis() {
//    String pilihan = String.valueOf(Jenisjual.getSelectedItem());
//
//    boolean modeResep = !"Biasa".equalsIgnoreCase(pilihan);
//
//    if (modeResep) {
//        cardKanan.show(panelKeranjang, "RESEP");
//        // kalau mau: load/refresh resep dokter disini
//        // tampilResepToko(); / tampilDaftarResepDokter();
//    } else {
//        cardKanan.show(panelKeranjang, "BASKET");
//        rebuildKeranjangDariTable(); // biar sinkron kalau qty sudah ada
//    }
//}
//
//private void initPanelKeranjang() {
//    tabKeranjang = new DefaultTableModel(
//        null,
//        new Object[]{"Kode","Nama","Satuan","Jml","Harga","Subtotal","Disk%","Disk(Rp)","Tambahan","Total"}
//    ){
//        Class<?>[] types = new Class[]{
//            String.class,String.class,String.class,Double.class,Double.class,Double.class,
//            Double.class,Double.class,Double.class,Double.class
//        };
//        @Override public Class<?> getColumnClass(int c){ return types[c]; }
//        @Override public boolean isCellEditable(int r,int c){ return false; } // kalau mau edit qty di keranjang, bilang ya
//    };
//
//    tbKeranjang = new JTable(tabKeranjang);
//    tbKeranjang.setRowHeight(24);
//
//    JPanel panelKeranjang = new JPanel(new java.awt.BorderLayout());
//    panelKeranjang.setBorder(javax.swing.BorderFactory.createTitledBorder("Keranjang (Jml > 0)"));
//    panelKeranjang.add(new JScrollPane(tbKeranjang), java.awt.BorderLayout.CENTER);
//
//    // âœ… tempatkan panelKeranjang di UI kamu
//    // contoh: panelKanan.add(panelKeranjang, BorderLayout.CENTER);
//    // atau pakai JSplitPane: kiri tbObat, kanan panelKeranjang
//}
//
//private void hookMasterToKeranjang() {
//    tabMode.addTableModelListener(e -> {
//        if (syncKeranjang) return;
//        if (e.getType() != javax.swing.event.TableModelEvent.UPDATE) return;
//
//        int r = e.getFirstRow();
//        int c = e.getColumn();
//
//        // kita respon kalau kolom penting berubah
//        if (c != 0 && c != 4 && c != 5 && c != 6 && c != 7 && c != 8 && c != 9 && c != 10) return;
//
//        javax.swing.SwingUtilities.invokeLater(() -> syncRowMasterToKeranjang(r));
//    });
//}
//
//private void syncRowMasterToKeranjang(int r) {
//    final int C_JML=0,C_KODE=1,C_NAMA=2,C_SAT=4,C_HRG=5,C_SUB=6,C_DSC=7,C_DSCN=8,C_TMB=9,C_TOT=10;
//
//    syncKeranjang = true;
//    try {
//        String kode = (tabMode.getValueAt(r, C_KODE) == null) ? "" : tabMode.getValueAt(r, C_KODE).toString();
//        if (kode.trim().isEmpty()) return;
//
//        double qty = d(tabMode.getValueAt(r, C_JML));
//
//        // kalau qty <= 0 â†’ hapus dari keranjang
//        if (qty <= 0) {
//            Integer idx = idxKeranjang.remove(kode);
//            if (idx != null) {
//                tabKeranjang.removeRow(idx);
//                rebuildIdxKeranjang();
//            }
//            return;
//        }
//
//        Object[] data = new Object[]{
//            kode,
//            tabMode.getValueAt(r, C_NAMA),
//            tabMode.getValueAt(r, C_SAT),
//            qty,
//            d(tabMode.getValueAt(r, C_HRG)),
//            d(tabMode.getValueAt(r, C_SUB)),
//            d(tabMode.getValueAt(r, C_DSC)),
//            d(tabMode.getValueAt(r, C_DSCN)),
//            d(tabMode.getValueAt(r, C_TMB)),
//            d(tabMode.getValueAt(r, C_TOT))
//        };
//
//        Integer idx = idxKeranjang.get(kode);
//        if (idx == null) {
//            tabKeranjang.addRow(data);
//            idxKeranjang.put(kode, tabKeranjang.getRowCount() - 1);
//        } else {
//            for (int i=0; i<data.length; i++) tabKeranjang.setValueAt(data[i], idx, i);
//        }
//    } finally {
//        syncKeranjang = false;
//    }
//}
//
//private void rebuildIdxKeranjang() {
//    idxKeranjang.clear();
//    for (int i=0; i<tabKeranjang.getRowCount(); i++) {
//        String kode = String.valueOf(tabKeranjang.getValueAt(i, 0));
//        idxKeranjang.put(kode, i);
//    }
//}

private void setModeResep(boolean resep) {
    jPanel2.setVisible(resep); // Daftar Resep Dokter
    jPanel3.setVisible(resep); // Detail Resep

    label23.setVisible(resep); // "Daftar Resep Dokter :"
    label16.setVisible(resep);
    
    // refresh layout biar rapi
    jPanel2.getParent().revalidate();
    jPanel2.getParent().repaint();
}
// =====================
// INDEX KOLOM tbObat (kiri) - sesuai screenshot
// =====================
private static final int OB_COL_JML     = 0;
private static final int OB_COL_KODE    = 1;
private static final int OB_COL_NAMA    = 2;
private static final int OB_COL_SATUAN  = 4;
private static final int OB_COL_HARGA   = 5;
private static final int OB_COL_TUSLAH = 9;

// (opsional kalau mau reset tampilan kiri)
private static final int OB_COL_SUBTOTAL = 6;
private static final int OB_COL_TOTAL    = 10;

// =====================
// INDEX KOLOM Keranjang (kanan) - sesuai screenshot
// =====================
private static final int KR_COL_KODE     = 0;
private static final int KR_COL_NAMA     = 1;
private static final int KR_COL_JML      = 2;
private static final int KR_COL_SAT      = 3;
private static final int KR_COL_HARGA    = 4;
private static final int KR_COL_SUBTOTAL = 5;
private static final int KR_COL_TUSLAH = 6;


// asumsi sudah ada:
// JTable tbObat;
// JTable tbKeranjang;   // <- ganti kalau nama kamu beda
// JTextField TCari;

private DefaultTableModel tmKeranjang;

// =====================
// PANGGIL ini sekali setelah initComponents()
// =====================
private void initKeranjangModel() {
    tmKeranjang = (DefaultTableModel) tbKeranjang.getModel();
    tmKeranjang.addTableModelListener(e -> {
        SwingUtilities.invokeLater(this::hitungTotalBawahDariKeranjang);
    });
}

// =====================
// PASANG ENTER di kolom Jml tbObat
// =====================
private void pasangEnterPadaQty() {
    final int COL_QTY = OB_COL_JML;

    JTextField tf = new JTextField();
    tf.setHorizontalAlignment(SwingConstants.CENTER);

    QtyEditor qtyEditor = new QtyEditor(tf);
    qtyEditor.setClickCountToStart(1);

    tf.addActionListener(evt -> {
        try {
            // commit nilai edit ke TableModel
            if (tbObat.isEditing()) tbObat.getCellEditor().stopCellEditing();

            int rowView = tbObat.getSelectedRow();
            if (rowView < 0) return;

            int rowModel = (tbObat.getRowSorter() != null)
                    ? tbObat.convertRowIndexToModel(rowView)
                    : rowView;

            // âœ… ini yang bikin kode sama jadi nambah qty
            syncKeranjangDariRow(rowModel);

            SwingUtilities.invokeLater(() -> {
                TCari.requestFocusInWindow();
                TCari.selectAll();
            });

        } catch (Exception ex) {
            System.out.println("ENTER QTY error: " + ex);
        }
    });

    tbObat.getColumnModel().getColumn(COL_QTY).setCellEditor(qtyEditor);
}

// =====================
// INTI: kalau kode sudah ada di keranjang -> qty ditambah
// =====================
private void syncKeranjangDariRow(int rowModelObat) {
    TableModel m = tbObat.getModel();

    String kode   = s(m.getValueAt(rowModelObat, OB_COL_KODE));
    String nama   = s(m.getValueAt(rowModelObat, OB_COL_NAMA));
    String sat    = s(m.getValueAt(rowModelObat, OB_COL_SATUAN));
    double harga  = toDouble(m.getValueAt(rowModelObat, OB_COL_HARGA));
    double qtyIn  = toDouble(m.getValueAt(rowModelObat, OB_COL_JML));

    // âœ… ambil tuslah dari kolom Tuslah(Rp)
    double tuslahIn = toDouble(m.getValueAt(rowModelObat, OB_COL_TUSLAH));

    if (kode.isEmpty() || qtyIn <= 0) return;

    int idx = findRowKeranjangByKode(kode);

    if (idx >= 0) {
        // sudah ada -> tambah qty, subtotal dihitung ulang
        double qtyLama = toDouble(tmKeranjang.getValueAt(idx, KR_COL_JML));
        double qtyBaru = qtyLama + qtyIn;

        // tuslah lama kita "simpan" di subtotal dengan cara ekstrak? (tidak aman)
        // âœ… solusi paling aman: simpan tuslah di kolom tersembunyi keranjang (lihat opsi di bawah)
        // Tapi kalau kamu belum punya kolom tuslah di keranjang, kita akumulasikan tuslah langsung ke subtotal.
        // Jadi: subtotalBaru = subtotalLama + (qtyIn*harga) + tuslahIn

        double subtotalLama = toDouble(tmKeranjang.getValueAt(idx, KR_COL_SUBTOTAL));
        double subtotalTambah = (qtyIn * harga) + tuslahIn;
        double subtotalBaru = subtotalLama + subtotalTambah;

        tmKeranjang.setValueAt(qtyBaru, idx, KR_COL_JML);
        tmKeranjang.setValueAt(harga,  idx, KR_COL_HARGA);
        tmKeranjang.setValueAt(subtotalBaru, idx, KR_COL_SUBTOTAL);

        tmKeranjang.fireTableRowsUpdated(idx, idx);
    } else {
        // belum ada -> tambah row baru
        double subtotal = (qtyIn * harga) + tuslahIn;

        tmKeranjang.addRow(new Object[]{
            kode, nama, qtyIn, sat, harga, subtotal
        });
    }

    // âœ… OPSI A: jangan reset qty (biar tetap tampil)
    // (jangan set 0)

    hitungTotalBawahDariKeranjang(); // pastikan total bawah ikut update
}

// =====================
// Cari row di keranjang berdasarkan kode
// =====================
private int findRowKeranjangByKode(String kode) {
    for (int i = 0; i < tmKeranjang.getRowCount(); i++) {
        String k = s(tmKeranjang.getValueAt(i, KR_COL_KODE));
        if (k.equalsIgnoreCase(kode)) return i;
    }
    return -1;
}

// =====================
// Helpers
// =====================
private String s(Object v) {
    return (v == null) ? "" : v.toString().trim();
}

private double toDouble(Object v) {
    if (v == null) return 0d;
    if (v instanceof Number) return ((Number) v).doubleValue();
    String t = v.toString().trim();
    if (t.isEmpty()) return 0d;
    t = t.replace(",", ".");
    try { return Double.parseDouble(t); } catch (Exception e) { return 0d; }
}

// =====================
// Editor qty
// =====================
private static class QtyEditor extends DefaultCellEditor {
    private final JTextField tf;

    public QtyEditor(JTextField tf) {
        super(tf);
        this.tf = tf;
        this.tf.setBorder(null);
    }

    @Override
//    public Object getCellEditorValue() {
//        String txt = tf.getText().trim();
//        if (txt.isEmpty()) return 0d;
//        return d(txt);
//    }

  
      public Object getCellEditorValue() {
        String txt = tf.getText().trim();
        if (txt.isEmpty()) return 0d;
        txt = txt.replace(",", ".");
        try { return Double.parseDouble(txt); } catch (Exception e) { return 0d; }
    }
}
private void hitungTotalBawahDariKeranjang() {
    try {
        // kolom keranjang sesuai screenshot kanan:
        // 0 Kode, 1 Nama, 2 Jml, 3 Sat, 4 Harga, 5 Subtotal
        final int KR_COL_SUBTOTAL = 5;
        

        double jumlahTotal = 0;
        for (int i = 0; i < tmKeranjang.getRowCount(); i++) {
            jumlahTotal += toDouble(tmKeranjang.getValueAt(i, KR_COL_SUBTOTAL));
        }

        // ===== ambil input bawah (sesuaikan nama field kamu)
        double ppnPersen = toDouble(Persenppn.getText());   // contoh: 0.0
        double ongkir    = toDouble(Ongkir.getText());      // contoh: 0
        double bayar     = toDouble(Bayar.getText());
        

        double ppnRp      = (jumlahTotal * ppnPersen) / 100.0;
        double totalTagih = jumlahTotal + ppnRp + ongkir;
        double kembali    = bayar - totalTagih;

        // ===== set ke komponen bawah (sesuaikan)
        LTotal.setText(rp(jumlahTotal));
        TagihanPPn.setText(rp(totalTagih));
        LKembali.setText(rp(kembali));

        // kalau kamu punya tampilan PPN nominal:
        // PPNRp.setText(rp(ppnRp));

    } catch (Exception ex) {
        System.out.println("hitungTotalBawahDariKeranjang() error: " + ex);
    }
}
//private double toDouble(Object v) {
//    if (v == null) return 0d;
//    if (v instanceof Number) return ((Number) v).doubleValue();
//    String s = v.toString().trim();
//    if (s.isEmpty()) return 0d;
//
//    // buang pemisah ribuan, dukung koma jadi titik
//    s = s.replace(".", "").replace(",", "."); 
//    try { return Double.parseDouble(s); } catch (Exception e) { return 0d; }
//}

private String rp(double x) {
    return String.format("%,.0f", x).replace(",", ".");
}

private String genNoResepLuarDariNota(String nota) {
    String ymd = (nota != null && nota.matches("^TJ\\d{8}.*")) ? nota.substring(2,10)
              : new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    String tail = (nota != null && nota.length() >= 6) ? nota.substring(nota.length()-6) : "000001";
    return "RL" + ymd + "-" + tail;
}
private void applyJenisJual() {
      String pilih = String.valueOf(Jenisjual.getSelectedItem()).trim();
    boolean isResep = "Resep".equalsIgnoreCase(pilih);

    setModeResep(isResep);       // Resep Luar => false
    toggleResepLuarFields();
    refreshNoRawatKunjungan();
}

private void toggleResepLuarFields() {
    String pilih = String.valueOf(Jenisjual.getSelectedItem()).trim();

    boolean isResep     = "Resep".equalsIgnoreCase(pilih);
    boolean isResepLuar = "Resep Luar".equalsIgnoreCase(pilih);

    // ===== Dokter internal (combo) hanya untuk Resep internal =====
    label11.setVisible(isResep);
    DokterCombo.setVisible(isResep);
    if (DokterCombo != null) DokterCombo.setVisible(isResep);

    if (!isResep) {
        DokterCombo.setSelectedIndex(-1);
    }

    if (labelNoRawatKunjungan != null) labelNoRawatKunjungan.setVisible(isResep);
    if (CbNoRawatKunjungan != null) {
        CbNoRawatKunjungan.setVisible(isResep);
        CbNoRawatKunjungan.setEnabled(isResep);
    }

    // ===== Dokter Luar + No Resep Luar hanya untuk Resep Luar =====
    labelDokterLuar.setVisible(isResepLuar);
    TNmDokterLuar.setVisible(isResepLuar);
    TNmDokterLuar.setEnabled(isResepLuar);

    labelNoResepLuar.setVisible(isResepLuar);
    TNoResepLuar.setVisible(isResepLuar);
    TNoResepLuar.setEnabled(isResepLuar);

    if (!isResepLuar) {
        TNmDokterLuar.setText("");
        TNoResepLuar.setText("");
    } else {
        // optional: auto isi kalau kosong
        if (TNoResepLuar.getText().trim().isEmpty()) {
            TNoResepLuar.setText(genNoResepLuarDariNota(NoNota.getText().trim()));
        }
        TNoResepLuar.requestFocusInWindow();
    }

    panelisi3.revalidate();
    panelisi3.repaint();
}


}

