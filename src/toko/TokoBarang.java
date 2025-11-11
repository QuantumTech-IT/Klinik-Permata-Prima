/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgJnsPerawatan.java
 *
 * Created on May 22, 2010, 11:58:21 PM
 */

package toko;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import inventory.DlgCariSatuan;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import restore.DlgRestoreTokoBarang;
 import javax.swing.*;
 import javax.swing.table.*;
 import java.awt.*;
 import java.text.*;
 import java.util.*;
 import java.awt.geom.*; 

/**
 *
 * @author dosen
 */
public final class TokoBarang extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private Connection koneksi=koneksiDB.condb();
    public DlgCariSatuan satuan=new DlgCariSatuan(null,false); 
    public TokoCariJenis jenis=new TokoCariJenis(null,false);
    
    private int i = 0;
    private String tanggal = "0000-00-00";
    private javax.swing.JComboBox<String> cmbExpire;
    private static final int C_EXPIRE = 16;
    private javax.swing.JTable tbExp;
private javax.swing.table.DefaultTableModel expModel;
   
    /** Creates new form DlgJnsPerawatan
     * @param parent
     * @param modal */
    public TokoBarang(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initExpireCombo();
        initExpiredTable(); 
        styleExpTable(tbExp); // sekali, setelah initComponents
reloadExpiredTableFromDB();
        

        this.setLocation(8,1);
        setSize(628,674);

       Object[] row = {
    "Kode Barang","Nama Barang","Satuan","Jenis",
    "Stok","H. Dasar","H. Beli","H. Distributor","H. Grosir","H. Retail",
    "Kode Sat 1","Kode Sat 2","Isi","Kapasitas","Resep", "Kandungan", "Expired Date"
};

tabMode = new DefaultTableModel(null,row){
    @Override
    public boolean isCellEditable(int rowIndex, int colIndex) {
        return false; // semua kolom tidak bisa diedit
    }
    Class[] types = new Class[]{
        java.lang.Object.class, // Kode Barang
        java.lang.Object.class, // Nama Barang
        java.lang.Object.class, // Satuan
        java.lang.Object.class, // Jenis
        java.lang.Double.class, // Stok
        java.lang.Double.class, // H. Dasar
        java.lang.Double.class, // H. Beli
        java.lang.Double.class, // H. Distributor
        java.lang.Double.class, // H. Grosir
        java.lang.Double.class, // H. Retail
        java.lang.Object.class, // Kode Sat 1
        java.lang.Object.class, // Kode Sat 2
        java.lang.Object.class, // Isi
        java.lang.Object.class,
        java.lang.Double.class,// Kapasitas
        java.lang.Object.class,
        java.lang.Object.class
    };

    @Override
    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }
};
tbJnsPerawatan.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbJnsPerawatan.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbJnsPerawatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

       for (int i = 0; i < 17; i++) {
    TableColumn column = tbJnsPerawatan.getColumnModel().getColumn(i);
    if(i==0){ column.setPreferredWidth(90); }      // Kode Barang
    else if(i==1){ column.setPreferredWidth(200); }// Nama Barang
    else if(i==2){ column.setPreferredWidth(90); } // Satuan
    else if(i==3){ column.setPreferredWidth(110);} // Jenis
    else if(i==4){ column.setPreferredWidth(40); } // Stok
    else if(i==5){ column.setPreferredWidth(80); } // H. Dasar
    else if(i==6){ column.setPreferredWidth(80); } // H. Beli
    else if(i==7){ column.setPreferredWidth(80); } // H. Distributor
    else if(i==8){ column.setPreferredWidth(80); } // H. Grosir
    else if(i==9){ column.setPreferredWidth(80); } // H. Retail
    else if(i==10){ column.setPreferredWidth(80);} // Kode Sat 1
    else if(i==11){ column.setPreferredWidth(80);} // Kode Sat 2
    else if(i==12){ column.setPreferredWidth(60);} // Isi
    else if(i==13){ column.setPreferredWidth(80);}
    else if(i==14){ column.setPreferredWidth(80);}// Kapasitas
    else if(i==15){ column.setPreferredWidth(80);}// Kapasitas
    else if(i==16){ column.setPreferredWidth(80);}// Kapasitas
}

        kode_brng.setDocument(new batasInput((byte)40).getKata(kode_brng));
        nama_brng.setDocument(new batasInput((byte)80).getKata(nama_brng));
        kode_sat.setDocument(new batasInput((byte)4).getKata(kode_sat));
        stok.setDocument(new batasInput((byte)10).getKata(stok));
        dasar.setDocument(new batasInput((byte)20).getKata(dasar));
        beli.setDocument(new batasInput((byte)20).getKata(beli));
        distributor.setDocument(new batasInput((byte)20).getKata(distributor));
        grosir.setDocument(new batasInput((byte)20).getKata(grosir));
        retail.setDocument(new batasInput((byte)20).getKata(retail));
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
        ChkInput.setSelected(false);
        isForm(); 
        
        retail.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    public void insertUpdate(javax.swing.event.DocumentEvent e) { hitungPersenSatuan3(); }
    public void removeUpdate(javax.swing.event.DocumentEvent e) {  hitungPersenSatuan3(); }
    public void changedUpdate(javax.swing.event.DocumentEvent e) {  hitungPersenSatuan3(); }
});
        distributor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    public void insertUpdate(javax.swing.event.DocumentEvent e) { hitungPersenDistributor(); }
    public void removeUpdate(javax.swing.event.DocumentEvent e) { hitungPersenDistributor(); }
    public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungPersenDistributor(); }
});
        grosir.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    public void insertUpdate(javax.swing.event.DocumentEvent e){ hitungPersenGrosir(); }
    public void removeUpdate(javax.swing.event.DocumentEvent e){ hitungPersenGrosir(); }
    public void changedUpdate(javax.swing.event.DocumentEvent e){ hitungPersenGrosir(); }
});
        HResep.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e){ hitungPersenResep(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e){ hitungPersenResep(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e){ hitungPersenResep(); }
    });
    Isi.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e){ hitungPersenResep(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e){ hitungPersenResep(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e){ hitungPersenResep(); }
    });
    Kapasitas.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e){ hitungPersenResep(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e){ hitungPersenResep(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e){ hitungPersenResep(); }
    });
    
    // taruh di panel kanan/emptypanel kamu
    javax.swing.JScrollPane sp = new javax.swing.JScrollPane(tbExp);
    ExpiredList.setLayout(new java.awt.BorderLayout()); // ganti nama panel
    ExpiredList.add(sp, java.awt.BorderLayout.CENTER);
    ExpiredList.revalidate(); ExpiredList.repaint();
    
         satuan.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("TokoBarang")) {
                    if (satuan.getTable().getSelectedRow() != -1) {
                        if(i==1){
                            kode_sat.setText(satuan.getTable().getValueAt(satuan.getTable().getSelectedRow(), 0).toString());
                            nama_sat.setText(satuan.getTable().getValueAt(satuan.getTable().getSelectedRow(), 1).toString());
                            btnSatuan.requestFocus();
                        }else if(i == 2){
                            kode_sat1.setText(satuan.getTable().getValueAt(satuan.getTable().getSelectedRow(), 0).toString());
                            nama_sat1.setText(satuan.getTable().getValueAt(satuan.getTable().getSelectedRow(), 1).toString());
                            btnSatuan1.requestFocus();
                        
                        }else{
                            kode_sat2.setText(satuan.getTable().getValueAt(satuan.getTable().getSelectedRow(), 0).toString());
                            nama_sat2.setText(satuan.getTable().getValueAt(satuan.getTable().getSelectedRow(), 1).toString());
                           btnSatuan2.requestFocus();
                        }
                           
                        if(kode_sat.getText().equals(kode_sat1.getText())){
                            Isi.setText("1");
                        }
                    }
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
                satuan.emptTeks();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
         

        
//        satuan.addWindowListener(new WindowListener() {
//            @Override
//            public void windowOpened(WindowEvent e) {}
//            @Override
//            public void windowClosing(WindowEvent e) {}
//            @Override
//            public void windowClosed(WindowEvent e) {
//                if(akses.getform().equals("TokoBarang")){
//                    if(satuan.getTable().getSelectedRow()!= -1){                   
//                        kode_sat.setText(satuan.getTable().getValueAt(satuan.getTable().getSelectedRow(),0).toString());                    
//                        nama_sat.setText(satuan.getTable().getValueAt(satuan.getTable().getSelectedRow(),1).toString());
//                    }   
//                    kode_sat.requestFocus();
//                }
//            }
//            @Override
//            public void windowIconified(WindowEvent e) {}
//            @Override
//            public void windowDeiconified(WindowEvent e) {}
//            @Override
//            public void windowActivated(WindowEvent e) {}
//            @Override
//            public void windowDeactivated(WindowEvent e) {}
//
//        });
        // atau pakai DocumentListener ringan
//javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener(){
//    public void insertUpdate(javax.swing.event.DocumentEvent e){ onChange(); }
//    public void removeUpdate(javax.swing.event.DocumentEvent e){ onChange(); }
//    public void changedUpdate(javax.swing.event.DocumentEvent e){ onChange(); }
//
//    private void onChange() {
//        hitungHargaBesarKecil();
//        hitungPersenResep();   // tambahkan di sini
//        // kalau mau sekalian grosir, retail, distributor, dll juga bisa
//    }
//};
//
//// tambahkan listener ke semua field yang relevan
//beli.getDocument().addDocumentListener(dl);
//Isi.getDocument().addDocumentListener(dl);
//Kapasitas.getDocument().addDocumentListener(dl);
//percensatuanbesar.getDocument().addDocumentListener(dl);
//percensatuankecil.getDocument().addDocumentListener(dl);
//HResep.getDocument().addDocumentListener(dl);     // tambahkan juga resep
//percenresep.getDocument().addDocumentListener(dl); // kalau persen resep juga ikut mengubah
        
        jenis.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("TokoBarang")){
                    if(jenis.getTable().getSelectedRow()!= -1){                   
                        kdjenis.setText(jenis.getTable().getValueAt(jenis.getTable().getSelectedRow(),0).toString());                    
                        nmjenis.setText(jenis.getTable().getValueAt(jenis.getTable().getSelectedRow(),1).toString());
                    }   
                    kdjenis.requestFocus();
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
        
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Popup = new javax.swing.JPopupMenu();
        MnRestore = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbJnsPerawatan = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        label1 = new widget.Label();
        kode_brng = new widget.TextBox();
        nama_brng = new widget.TextBox();
        label7 = new widget.Label();
        label10 = new widget.Label();
        label19 = new widget.Label();
        kode_sat = new widget.TextBox();
        nama_sat = new widget.TextBox();
        btnSatuan = new widget.Button();
        label20 = new widget.Label();
        stok = new widget.TextBox();
        dasar = new widget.TextBox();
        label2 = new widget.Label();
        kdjenis = new widget.TextBox();
        nmjenis = new widget.TextBox();
        btnJenis = new widget.Button();
        label3 = new widget.Label();
        beli = new widget.TextBox();
        label4 = new widget.Label();
        distributor = new widget.TextBox();
        label5 = new widget.Label();
        grosir = new widget.TextBox();
        label6 = new widget.Label();
        retail = new widget.TextBox();
        label24 = new widget.Label();
        label21 = new widget.Label();
        Isi = new widget.TextBox();
        label39 = new widget.Label();
        label32 = new widget.Label();
        Kapasitas = new widget.TextBox();
        percenpackaging = new widget.TextBox();
        percensatuanbesar = new widget.TextBox();
        percensatuankecil = new widget.TextBox();
        label27 = new widget.Label();
        label44 = new widget.Label();
        label42 = new widget.Label();
        label8 = new widget.Label();
        kode_sat1 = new widget.TextBox();
        nama_sat1 = new widget.TextBox();
        btnSatuan1 = new widget.Button();
        kode_sat2 = new widget.TextBox();
        nama_sat2 = new widget.TextBox();
        btnSatuan2 = new widget.Button();
        HResep = new widget.TextBox();
        label9 = new widget.Label();
        percenresep = new widget.TextBox();
        label45 = new widget.Label();
        jPanel2 = new javax.swing.JPanel();
        Kandungan = new widget.TextBox();
        label31 = new widget.Label();
        DTPExpired = new widget.Tanggal();
        ChkKadaluarsa = new widget.CekBox();
        ExpiredList = new javax.swing.JPanel();
        label11 = new widget.Label();
        ChkInput = new widget.CekBox();

        Popup.setName("Popup"); // NOI18N

        MnRestore.setBackground(new java.awt.Color(255, 255, 254));
        MnRestore.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnRestore.setForeground(new java.awt.Color(50, 50, 50));
        MnRestore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnRestore.setText("Data Sampah");
        MnRestore.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnRestore.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnRestore.setName("MnRestore"); // NOI18N
        MnRestore.setPreferredSize(new java.awt.Dimension(200, 28));
        MnRestore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnRestoreActionPerformed(evt);
            }
        });
        Popup.add(MnRestore);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Barang Toko / Minimarket / Koperasi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setBackground(new java.awt.Color(204, 204, 204));
        Scroll.setComponentPopupMenu(Popup);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbJnsPerawatan.setAutoCreateRowSorter(true);
        tbJnsPerawatan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbJnsPerawatan.setComponentPopupMenu(Popup);
        tbJnsPerawatan.setName("tbJnsPerawatan"); // NOI18N
        tbJnsPerawatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbJnsPerawatanMouseClicked(evt);
            }
        });
        tbJnsPerawatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbJnsPerawatanKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbJnsPerawatan);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

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
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

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
        panelGlass8.add(BtnPrint);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelGlass8.add(BtnAll);

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
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(450, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
        BtnCari.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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
        panelGlass9.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(75, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 185));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 97));
        FormInput.setWarnaAtas(new java.awt.Color(102, 102, 102));
        FormInput.setWarnaBawah(new java.awt.Color(153, 153, 153));
        FormInput.setLayout(null);

        label1.setForeground(new java.awt.Color(255, 255, 255));
        label1.setText("Expired List :");
        label1.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label1.setName("label1"); // NOI18N
        FormInput.add(label1);
        label1.setBounds(920, 50, 89, 23);

        kode_brng.setForeground(new java.awt.Color(255, 255, 255));
        kode_brng.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        kode_brng.setName("kode_brng"); // NOI18N
        kode_brng.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kode_brngKeyPressed(evt);
            }
        });
        FormInput.add(kode_brng);
        kode_brng.setBounds(93, 10, 160, 23);

        nama_brng.setForeground(new java.awt.Color(255, 255, 255));
        nama_brng.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        nama_brng.setName("nama_brng"); // NOI18N
        nama_brng.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nama_brngKeyPressed(evt);
            }
        });
        FormInput.add(nama_brng);
        nama_brng.setBounds(93, 40, 480, 23);

        label7.setForeground(new java.awt.Color(255, 255, 255));
        label7.setText("Jenis :");
        label7.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label7.setName("label7"); // NOI18N
        FormInput.add(label7);
        label7.setBounds(20, 160, 40, 23);

        label10.setForeground(new java.awt.Color(255, 255, 255));
        label10.setText("Nama Barang :");
        label10.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label10.setName("label10"); // NOI18N
        FormInput.add(label10);
        label10.setBounds(0, 40, 89, 23);

        label19.setForeground(new java.awt.Color(255, 255, 255));
        label19.setText("Satuan 1:");
        label19.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label19);
        label19.setBounds(0, 70, 89, 23);

        kode_sat.setEditable(false);
        kode_sat.setForeground(new java.awt.Color(255, 255, 255));
        kode_sat.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        kode_sat.setName("kode_sat"); // NOI18N
        kode_sat.setPreferredSize(new java.awt.Dimension(207, 23));
        kode_sat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kode_satKeyPressed(evt);
            }
        });
        FormInput.add(kode_sat);
        kode_sat.setBounds(100, 70, 60, 23);

        nama_sat.setEditable(false);
        nama_sat.setForeground(new java.awt.Color(255, 255, 255));
        nama_sat.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        nama_sat.setName("nama_sat"); // NOI18N
        nama_sat.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nama_sat);
        nama_sat.setBounds(160, 70, 150, 23);

        btnSatuan.setBackground(new java.awt.Color(255, 255, 255));
        btnSatuan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/EDIT2.png"))); // NOI18N
        btnSatuan.setMnemonic('1');
        btnSatuan.setToolTipText("Alt+1");
        btnSatuan.setGlassColor(new java.awt.Color(255, 255, 255));
        btnSatuan.setName("btnSatuan"); // NOI18N
        btnSatuan.setPreferredSize(new java.awt.Dimension(28, 23));
        btnSatuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSatuanActionPerformed(evt);
            }
        });
        FormInput.add(btnSatuan);
        btnSatuan.setBounds(310, 70, 25, 23);

        label20.setForeground(new java.awt.Color(255, 255, 255));
        label20.setText("Stok :");
        label20.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label20.setName("label20"); // NOI18N
        label20.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label20);
        label20.setBounds(590, 40, 70, 23);

        stok.setEditable(false);
        stok.setForeground(new java.awt.Color(255, 255, 255));
        stok.setText("0");
        stok.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        stok.setName("stok"); // NOI18N
        stok.setPreferredSize(new java.awt.Dimension(207, 23));
        stok.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                stokKeyPressed(evt);
            }
        });
        FormInput.add(stok);
        stok.setBounds(663, 40, 60, 23);

        dasar.setForeground(new java.awt.Color(255, 255, 255));
        dasar.setText("0");
        dasar.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        dasar.setName("dasar"); // NOI18N
        dasar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dasarMouseExited(evt);
            }
        });
        dasar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dasarKeyPressed(evt);
            }
        });
        FormInput.add(dasar);
        dasar.setBounds(573, 10, 150, 23);

        label2.setForeground(new java.awt.Color(255, 255, 255));
        label2.setText("Harga Dasar : Rp.");
        label2.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label2.setName("label2"); // NOI18N
        FormInput.add(label2);
        label2.setBounds(450, 10, 120, 23);

        kdjenis.setEditable(false);
        kdjenis.setForeground(new java.awt.Color(255, 255, 255));
        kdjenis.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        kdjenis.setName("kdjenis"); // NOI18N
        kdjenis.setPreferredSize(new java.awt.Dimension(207, 23));
        kdjenis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdjenisKeyPressed(evt);
            }
        });
        FormInput.add(kdjenis);
        kdjenis.setBounds(70, 160, 61, 23);

        nmjenis.setEditable(false);
        nmjenis.setForeground(new java.awt.Color(255, 255, 255));
        nmjenis.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        nmjenis.setName("nmjenis"); // NOI18N
        nmjenis.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmjenis);
        nmjenis.setBounds(140, 160, 203, 23);

        btnJenis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/EDIT2.png"))); // NOI18N
        btnJenis.setMnemonic('1');
        btnJenis.setToolTipText("Alt+1");
        btnJenis.setName("btnJenis"); // NOI18N
        btnJenis.setPreferredSize(new java.awt.Dimension(28, 23));
        btnJenis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJenisActionPerformed(evt);
            }
        });
        FormInput.add(btnJenis);
        btnJenis.setBounds(350, 160, 25, 23);

        label3.setForeground(new java.awt.Color(0, 0, 0));
        label3.setText("Laba Penjualan");
        label3.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label3.setName("label3"); // NOI18N
        FormInput.add(label3);
        label3.setBounds(760, 80, 110, 23);

        beli.setForeground(new java.awt.Color(0, 0, 0));
        beli.setText("0");
        beli.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        beli.setName("beli"); // NOI18N
        beli.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                beliMouseExited(evt);
            }
        });
        beli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beliActionPerformed(evt);
            }
        });
        beli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                beliKeyPressed(evt);
            }
        });
        FormInput.add(beli);
        beli.setBounds(630, 70, 150, 23);

        label4.setForeground(new java.awt.Color(0, 0, 0));
        label4.setText("Harga Satuan 1 : Rp.");
        label4.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label4.setName("label4"); // NOI18N
        FormInput.add(label4);
        label4.setBounds(490, 100, 140, 23);

        distributor.setBackground(new java.awt.Color(255, 153, 153));
        distributor.setForeground(new java.awt.Color(51, 51, 51));
        distributor.setText("0");
        distributor.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        distributor.setName("distributor"); // NOI18N
        distributor.setOpaque(true);
        distributor.setSelectionColor(new java.awt.Color(255, 204, 102));
        distributor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                distributorMouseExited(evt);
            }
        });
        distributor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distributorActionPerformed(evt);
            }
        });
        distributor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                distributorKeyPressed(evt);
            }
        });
        FormInput.add(distributor);
        distributor.setBounds(630, 100, 150, 23);

        label5.setForeground(new java.awt.Color(0, 0, 0));
        label5.setText("Harga Satuan 2 : Rp.");
        label5.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label5.setName("label5"); // NOI18N
        FormInput.add(label5);
        label5.setBounds(490, 130, 140, 23);

        grosir.setBackground(new java.awt.Color(255, 153, 153));
        grosir.setForeground(new java.awt.Color(51, 51, 51));
        grosir.setText("0");
        grosir.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        grosir.setName("grosir"); // NOI18N
        grosir.setOpaque(true);
        grosir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                grosirMouseExited(evt);
            }
        });
        grosir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                grosirKeyPressed(evt);
            }
        });
        FormInput.add(grosir);
        grosir.setBounds(630, 130, 150, 23);

        label6.setForeground(new java.awt.Color(0, 0, 0));
        label6.setText("Harga Satuan 3 : Rp.");
        label6.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label6.setName("label6"); // NOI18N
        FormInput.add(label6);
        label6.setBounds(490, 160, 140, 23);

        retail.setBackground(new java.awt.Color(255, 153, 153));
        retail.setForeground(new java.awt.Color(51, 51, 51));
        retail.setText("0");
        retail.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        retail.setName("retail"); // NOI18N
        retail.setOpaque(true);
        retail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                retailMouseExited(evt);
            }
        });
        retail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                retailKeyPressed(evt);
            }
        });
        FormInput.add(retail);
        retail.setBounds(630, 160, 150, 23);

        label24.setForeground(new java.awt.Color(255, 255, 255));
        label24.setText("Satuan 2:");
        label24.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label24.setName("label24"); // NOI18N
        label24.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label24);
        label24.setBounds(0, 100, 88, 23);

        label21.setForeground(new java.awt.Color(255, 255, 255));
        label21.setText("Satuan 3:");
        label21.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label21.setName("label21"); // NOI18N
        label21.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label21);
        label21.setBounds(0, 130, 88, 23);

        Isi.setForeground(new java.awt.Color(255, 255, 255));
        Isi.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        Isi.setName("Isi"); // NOI18N
        Isi.setPreferredSize(new java.awt.Dimension(207, 23));
        Isi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                IsiKeyPressed(evt);
            }
        });
        FormInput.add(Isi);
        Isi.setBounds(410, 100, 70, 23);

        label39.setForeground(new java.awt.Color(255, 255, 255));
        label39.setText("Isi :");
        label39.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label39.setName("label39"); // NOI18N
        label39.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label39);
        label39.setBounds(340, 100, 69, 23);

        label32.setForeground(new java.awt.Color(255, 255, 255));
        label32.setText("Kapasitas :");
        label32.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label32.setName("label32"); // NOI18N
        label32.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label32);
        label32.setBounds(340, 130, 69, 23);

        Kapasitas.setForeground(new java.awt.Color(255, 255, 255));
        Kapasitas.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        Kapasitas.setName("Kapasitas"); // NOI18N
        Kapasitas.setPreferredSize(new java.awt.Dimension(207, 23));
        Kapasitas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KapasitasKeyPressed(evt);
            }
        });
        FormInput.add(Kapasitas);
        Kapasitas.setBounds(410, 130, 70, 23);

        percenpackaging.setBackground(new java.awt.Color(255, 153, 0));
        percenpackaging.setForeground(new java.awt.Color(0, 0, 0));
        percenpackaging.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        percenpackaging.setName("percenpackaging"); // NOI18N
        percenpackaging.setOpaque(true);
        percenpackaging.setPreferredSize(new java.awt.Dimension(207, 23));
        percenpackaging.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                percenpackagingMouseMoved(evt);
            }
        });
        percenpackaging.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                percenpackagingMouseExited(evt);
            }
        });
        percenpackaging.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                percenpackagingKeyPressed(evt);
            }
        });
        FormInput.add(percenpackaging);
        percenpackaging.setBounds(790, 100, 70, 23);

        percensatuanbesar.setBackground(new java.awt.Color(255, 153, 0));
        percensatuanbesar.setForeground(new java.awt.Color(0, 0, 0));
        percensatuanbesar.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        percensatuanbesar.setName("percensatuanbesar"); // NOI18N
        percensatuanbesar.setOpaque(true);
        percensatuanbesar.setPreferredSize(new java.awt.Dimension(207, 23));
        percensatuanbesar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                percensatuanbesarMouseMoved(evt);
            }
        });
        percensatuanbesar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                percensatuanbesarMouseExited(evt);
            }
        });
        percensatuanbesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                percensatuanbesarActionPerformed(evt);
            }
        });
        percensatuanbesar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                percensatuanbesarKeyPressed(evt);
            }
        });
        FormInput.add(percensatuanbesar);
        percensatuanbesar.setBounds(790, 130, 70, 23);

        percensatuankecil.setBackground(new java.awt.Color(255, 153, 0));
        percensatuankecil.setForeground(new java.awt.Color(0, 0, 0));
        percensatuankecil.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        percensatuankecil.setName("percensatuankecil"); // NOI18N
        percensatuankecil.setOpaque(true);
        percensatuankecil.setPreferredSize(new java.awt.Dimension(207, 23));
        percensatuankecil.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                percensatuankecilMouseMoved(evt);
            }
        });
        percensatuankecil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                percensatuankecilMouseExited(evt);
            }
        });
        percensatuankecil.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                percensatuankecilKeyPressed(evt);
            }
        });
        FormInput.add(percensatuankecil);
        percensatuankecil.setBounds(790, 160, 70, 23);

        label27.setForeground(new java.awt.Color(0, 0, 0));
        label27.setText("%");
        label27.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label27.setName("label27"); // NOI18N
        label27.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label27);
        label27.setBounds(860, 130, 20, 23);

        label44.setForeground(new java.awt.Color(0, 0, 0));
        label44.setText("%");
        label44.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label44.setName("label44"); // NOI18N
        label44.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label44);
        label44.setBounds(860, 160, 20, 23);

        label42.setForeground(new java.awt.Color(0, 0, 0));
        label42.setText("%");
        label42.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label42.setName("label42"); // NOI18N
        label42.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label42);
        label42.setBounds(860, 100, 20, 23);

        label8.setForeground(new java.awt.Color(0, 0, 0));
        label8.setText("Harga Beli : Rp.");
        label8.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label8.setName("label8"); // NOI18N
        FormInput.add(label8);
        label8.setBounds(530, 70, 100, 23);

        kode_sat1.setEditable(false);
        kode_sat1.setForeground(new java.awt.Color(255, 255, 255));
        kode_sat1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        kode_sat1.setName("kode_sat1"); // NOI18N
        kode_sat1.setPreferredSize(new java.awt.Dimension(207, 23));
        kode_sat1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kode_sat1KeyPressed(evt);
            }
        });
        FormInput.add(kode_sat1);
        kode_sat1.setBounds(100, 100, 60, 23);

        nama_sat1.setEditable(false);
        nama_sat1.setForeground(new java.awt.Color(255, 255, 255));
        nama_sat1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        nama_sat1.setName("nama_sat1"); // NOI18N
        nama_sat1.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nama_sat1);
        nama_sat1.setBounds(160, 100, 150, 23);

        btnSatuan1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/EDIT2.png"))); // NOI18N
        btnSatuan1.setMnemonic('1');
        btnSatuan1.setToolTipText("Alt+1");
        btnSatuan1.setName("btnSatuan1"); // NOI18N
        btnSatuan1.setPreferredSize(new java.awt.Dimension(28, 23));
        btnSatuan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSatuan1ActionPerformed(evt);
            }
        });
        FormInput.add(btnSatuan1);
        btnSatuan1.setBounds(310, 100, 25, 23);

        kode_sat2.setEditable(false);
        kode_sat2.setForeground(new java.awt.Color(255, 255, 255));
        kode_sat2.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        kode_sat2.setName("kode_sat2"); // NOI18N
        kode_sat2.setPreferredSize(new java.awt.Dimension(207, 23));
        kode_sat2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kode_sat2KeyPressed(evt);
            }
        });
        FormInput.add(kode_sat2);
        kode_sat2.setBounds(100, 130, 60, 23);

        nama_sat2.setEditable(false);
        nama_sat2.setForeground(new java.awt.Color(255, 255, 255));
        nama_sat2.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        nama_sat2.setName("nama_sat2"); // NOI18N
        nama_sat2.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nama_sat2);
        nama_sat2.setBounds(160, 130, 150, 23);

        btnSatuan2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/EDIT2.png"))); // NOI18N
        btnSatuan2.setMnemonic('1');
        btnSatuan2.setToolTipText("Alt+1");
        btnSatuan2.setName("btnSatuan2"); // NOI18N
        btnSatuan2.setPreferredSize(new java.awt.Dimension(28, 23));
        btnSatuan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSatuan2ActionPerformed(evt);
            }
        });
        FormInput.add(btnSatuan2);
        btnSatuan2.setBounds(310, 130, 25, 23);

        HResep.setBackground(new java.awt.Color(255, 153, 153));
        HResep.setForeground(new java.awt.Color(51, 51, 51));
        HResep.setText("0");
        HResep.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        HResep.setName("HResep"); // NOI18N
        HResep.setOpaque(true);
        HResep.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                HResepMouseExited(evt);
            }
        });
        HResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                HResepKeyPressed(evt);
            }
        });
        FormInput.add(HResep);
        HResep.setBounds(630, 190, 150, 23);

        label9.setForeground(new java.awt.Color(0, 0, 0));
        label9.setText("Harga Resep : Rp.");
        label9.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label9.setName("label9"); // NOI18N
        FormInput.add(label9);
        label9.setBounds(490, 190, 140, 23);

        percenresep.setBackground(new java.awt.Color(255, 153, 0));
        percenresep.setForeground(new java.awt.Color(0, 0, 0));
        percenresep.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        percenresep.setName("percenresep"); // NOI18N
        percenresep.setOpaque(true);
        percenresep.setPreferredSize(new java.awt.Dimension(207, 23));
        percenresep.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                percenresepMouseMoved(evt);
            }
        });
        percenresep.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                percenresepMouseExited(evt);
            }
        });
        percenresep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                percenresepKeyPressed(evt);
            }
        });
        FormInput.add(percenresep);
        percenresep.setBounds(790, 190, 70, 23);

        label45.setForeground(new java.awt.Color(0, 0, 0));
        label45.setText("%");
        label45.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label45.setName("label45"); // NOI18N
        label45.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label45);
        label45.setBounds(860, 190, 20, 23);

        jPanel2.setBackground(new java.awt.Color(102, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setName("jPanel2"); // NOI18N
        FormInput.add(jPanel2);
        jPanel2.setBounds(497, 67, 400, 160);

        Kandungan.setForeground(new java.awt.Color(255, 255, 255));
        Kandungan.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        Kandungan.setName("Kandungan"); // NOI18N
        Kandungan.setPreferredSize(new java.awt.Dimension(207, 23));
        Kandungan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KandunganKeyPressed(evt);
            }
        });
        FormInput.add(Kandungan);
        Kandungan.setBounds(100, 190, 388, 23);

        label31.setForeground(new java.awt.Color(255, 255, 255));
        label31.setText("Kandungan :");
        label31.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        label31.setName("label31"); // NOI18N
        label31.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label31);
        label31.setBounds(10, 190, 88, 23);

        DTPExpired.setForeground(new java.awt.Color(50, 70, 50));
        DTPExpired.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "07-09-2025" }));
        DTPExpired.setDisplayFormat("dd-MM-yyyy");
        DTPExpired.setName("DTPExpired"); // NOI18N
        DTPExpired.setOpaque(false);
        DTPExpired.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPExpiredKeyPressed(evt);
            }
        });
        FormInput.add(DTPExpired);
        DTPExpired.setBounds(180, 220, 100, 23);

        ChkKadaluarsa.setForeground(new java.awt.Color(255, 255, 255));
        ChkKadaluarsa.setSelected(true);
        ChkKadaluarsa.setText("Tanggal Kadaluwarsa :");
        ChkKadaluarsa.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        ChkKadaluarsa.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ChkKadaluarsa.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkKadaluarsa.setName("ChkKadaluarsa"); // NOI18N
        ChkKadaluarsa.setOpaque(false);
        FormInput.add(ChkKadaluarsa);
        ChkKadaluarsa.setBounds(20, 220, 151, 23);

        ExpiredList.setName("ExpiredList"); // NOI18N
        FormInput.add(ExpiredList);
        ExpiredList.setBounds(930, 70, 580, 160);

        label11.setForeground(new java.awt.Color(255, 255, 255));
        label11.setText("Kode Barang :");
        label11.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        label11.setName("label11"); // NOI18N
        FormInput.add(label11);
        label11.setBounds(0, 10, 89, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(kode_brng.getText().trim().equals("")){
            Valid.textKosong(kode_brng,"Kode Barang");
        }else if(nama_brng.getText().trim().equals("")){
            Valid.textKosong(nama_brng,"Nama Barang");
        }else if(dasar.getText().trim().equals("")){
            Valid.textKosong(dasar,"Harga Dasar");
        }else if(beli.getText().trim().equals("")){
            Valid.textKosong(beli,"Harga Beli");
        }else if(distributor.getText().trim().equals("")){
            Valid.textKosong(distributor,"Harga Distributor");
        }else if(grosir.getText().trim().equals("")){
            Valid.textKosong(grosir,"Harga Grosir");
        }else if(retail.getText().trim().equals("")){
            Valid.textKosong(retail,"Harga Retail");
        }else if(kode_sat.getText().trim().equals("")||nama_sat.getText().trim().equals("")){
            Valid.textKosong(kode_sat,"Satuan");
        }else if(kdjenis.getText().trim().equals("")||nmjenis.getText().trim().equals("")){
            Valid.textKosong(kdjenis,"Jenis Barang");
        }else {
            if (ChkKadaluarsa.isSelected() == true) {
                tanggal = Valid.SetTgl(DTPExpired.getSelectedItem() + "");
            } else {
                tanggal = "0000-00-00";
            }
           if(Sequel.menyimpantf("tokobarang",
    "?,?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?",
    "Kode Barang", 18, new String[]{
        kode_brng.getText(),
        nama_brng.getText(),
        kode_sat.getText(),
        kdjenis.getText(),
        stok.getText(),
        dasar.getText(),
        beli.getText(),
        distributor.getText(),
        grosir.getText(),
        retail.getText(),
        "1",                   // status
        kode_sat1.getText(),
        kode_sat2.getText(),
        Isi.getText(),
        Kapasitas.getText(),
        HResep.getText(),      // sekarang urutannya pas
        Kandungan.getText(),
        tanggal
    })==true){
        tampil();
        emptTeks();
}  
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,retail,BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        ChkInput.setSelected(true);
        isForm(); 
        emptTeks();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbJnsPerawatan.getSelectedRow()> -1){
            Sequel.mengedit("tokobarang","kode_brng='"+kode_brng.getText()+"'","status='0'");
            BtnCariActionPerformed(evt);
            emptTeks();
        }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(kode_brng.getText().trim().equals("")){
            Valid.textKosong(kode_brng,"Kode Barang");
        }else if(nama_brng.getText().trim().equals("")){
            Valid.textKosong(nama_brng,"Nama Barang");
        }else if(dasar.getText().trim().equals("")){
            Valid.textKosong(dasar,"Harga Dasar");
        }else if(beli.getText().trim().equals("")){
            Valid.textKosong(beli,"Harga Beli");
        }else if(distributor.getText().trim().equals("")){
            Valid.textKosong(distributor,"Harga Distributor");
        }else if(grosir.getText().trim().equals("")){
            Valid.textKosong(grosir,"Harga Grosir");
        }else if(retail.getText().trim().equals("")){
            Valid.textKosong(retail,"Harga Retail");
        }else if(kode_sat.getText().trim().equals("")||nama_sat.getText().trim().equals("")){
            Valid.textKosong(kode_sat,"Satuan");
        }else if(kdjenis.getText().trim().equals("")||nmjenis.getText().trim().equals("")){
            Valid.textKosong(kdjenis,"Jenis Barang");
        }else  {
            if (ChkKadaluarsa.isSelected() == true) {
                tanggal = Valid.SetTgl(DTPExpired.getSelectedItem()+"");
            } else if (ChkKadaluarsa.isSelected() == false) {
                tanggal = "0000-00-00";
            }
            if(tbJnsPerawatan.getSelectedRow()> -1){
              // String kodeLama = kodeBrgLama; // simpan saat baris dipilih
            
boolean ok = Sequel.mengedittf("tokobarang","kode_brng=?",
            "nama_brng=?, kode_sat=?, jenis=?, stok=?, dasar=?, h_beli=?, distributor=?, grosir=?, retail=?, " +
            "status=?, kode_sat1=?, kode_sat2=?, isi=?, kapasitas=?, h_resep=?, kandungan=?, expire=?",
            18, new String[]{
                nama_brng.getText(),
                kode_sat.getText(),
                kdjenis.getText(),
                stok.getText(),
                dasar.getText(),
                beli.getText(),
                distributor.getText(),
                grosir.getText(),
                retail.getText(),
                "1", // status
                kode_sat1.getText(),
                kode_sat2.getText(),
                Isi.getText(),
                Kapasitas.getText(),
                HResep.getText(),
                Kandungan.getText(),
                tanggal,
                kode_brng.getText() // WHERE
            });
        if(ok){
            tampil();
            emptTeks();
        }
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnAll,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
                Map<String, Object> param = new HashMap<>();                
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());   
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                if(TCari.getText().trim().equals("")){
                    Valid.MyReportqry("rptBarangToko.jasper","report","::[ Data Barang Toko / Minimarket / Koperasi ]::",
                       "select tokobarang.kode_brng,tokobarang.nama_brng,kodesatuan.satuan,tokojenisbarang.nm_jenis, "+
                        "tokobarang.stok,tokobarang.dasar,tokobarang.h_beli,tokobarang.distributor,tokobarang.grosir,tokobarang.retail "+
                        "from tokobarang inner join kodesatuan on tokobarang.kode_sat=kodesatuan.kode_sat "+
                        "inner join tokojenisbarang on tokobarang.jenis=tokojenisbarang.kd_jenis "+
                        "where tokobarang.status='1' order by tokobarang.kode_brng",param);
                }else{
                    Valid.MyReportqry("rptBarangToko.jasper","report","::[ Data Barang Toko / Minimarket / Koperasi ]::",
                       "select tokobarang.kode_brng,tokobarang.nama_brng,kodesatuan.satuan,tokojenisbarang.nm_jenis, "+
                        "tokobarang.stok,tokobarang.dasar,tokobarang.h_beli,tokobarang.distributor,tokobarang.grosir,tokobarang.retail "+
                        "from tokobarang inner join kodesatuan on tokobarang.kode_sat=kodesatuan.kode_sat "+
                        "inner join tokojenisbarang on tokobarang.jenis=tokojenisbarang.kd_jenis "+
                        "where tokobarang.status='1' and tokobarang.kode_brng like '%"+TCari.getText().trim()+"%' "+
                        "or tokobarang.status='1' and tokobarang.nama_brng like '%"+TCari.getText().trim()+"%' "+
                        "or tokobarang.status='1' and kodesatuan.satuan like '%"+TCari.getText().trim()+"%' "+
                        "or tokobarang.status='1' and tokojenisbarang.nm_jenis like '%"+TCari.getText().trim()+"%' order by tokobarang.kode_brng",param);
                }
                    
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnAll);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbJnsPerawatan.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            tampil();
        }else{
            Valid.pindah(evt, BtnPrint,BtnKeluar);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbJnsPerawatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbJnsPerawatanMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }            
        }
}//GEN-LAST:event_tbJnsPerawatanMouseClicked

    private void tbJnsPerawatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbJnsPerawatanKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                TCari.setText("");
                TCari.requestFocus();
            }
        }
}//GEN-LAST:event_tbJnsPerawatanKeyPressed

private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
  isForm();                
}//GEN-LAST:event_ChkInputActionPerformed

private void kode_brngKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kode_brngKeyPressed
        Valid.pindah(evt,kode_sat,dasar,TCari);
}//GEN-LAST:event_kode_brngKeyPressed

private void nama_brngKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nama_brngKeyPressed
        Valid.pindah(evt,dasar,kode_sat);
}//GEN-LAST:event_nama_brngKeyPressed

private void kode_satKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kode_satKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            Sequel.cariIsi("select kodesatuan.satuan from kodesatuan where kodesatuan.kode_sat=?", nama_sat,kode_sat.getText());           
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            nama_brng.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kdjenis.requestFocus(); 
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnSatuanActionPerformed(null);
        }
}//GEN-LAST:event_kode_satKeyPressed

private void btnSatuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSatuanActionPerformed
i=1;
    akses.setform("TokoBarang");
    satuan.isCek();
    satuan.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
    satuan.setLocationRelativeTo(internalFrame1);
    satuan.setVisible(true);
}//GEN-LAST:event_btnSatuanActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void stokKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stokKeyPressed
        Valid.pindah(evt,kdjenis,BtnSimpan);
    }//GEN-LAST:event_stokKeyPressed

    private void dasarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dasarKeyPressed
        Valid.pindah(evt,kode_brng,nama_brng);
    }//GEN-LAST:event_dasarKeyPressed

    private void kdjenisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdjenisKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            Sequel.cariIsi("select nm_jenis from tokojenisbarang where kd_jenis=?", nmjenis,kdjenis.getText());           
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){            
            kode_sat.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            beli.requestFocus(); 
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnJenisActionPerformed(null);
        }
    }//GEN-LAST:event_kdjenisKeyPressed

    private void btnJenisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJenisActionPerformed
        akses.setform("TokoBarang");
        jenis.isCek();
        jenis.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        jenis.setLocationRelativeTo(internalFrame1);
        jenis.setVisible(true);
    }//GEN-LAST:event_btnJenisActionPerformed

    private void MnRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnRestoreActionPerformed
        DlgRestoreTokoBarang restore=new DlgRestoreTokoBarang(null,true);
        restore.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        restore.setLocationRelativeTo(internalFrame1);
        restore.setVisible(true);
    }//GEN-LAST:event_MnRestoreActionPerformed

    private void beliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_beliKeyPressed
hitungHargaBesarKecil();
//        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
//            //isHitung();        
//            distributor.requestFocus();
//        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
//            dasar.requestFocus();
//        }
    }//GEN-LAST:event_beliKeyPressed

    private void distributorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_distributorKeyPressed
        hitungPersenDistributor();
        Valid.pindah(evt,beli,grosir);
    }//GEN-LAST:event_distributorKeyPressed

    private void grosirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_grosirKeyPressed
        hitungPersenGrosir();
        Valid.pindah(evt,distributor,retail);
    }//GEN-LAST:event_grosirKeyPressed

    private void retailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_retailKeyPressed
        hitungPersenSatuan3();
        Valid.pindah(evt,grosir,BtnSimpan);
    }//GEN-LAST:event_retailKeyPressed

    private void dasarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dasarMouseExited
        if (dasar.getText().equals("")) {
            dasar.setText("0");
        }
    }//GEN-LAST:event_dasarMouseExited

    private void beliMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_beliMouseExited
        if (beli.getText().equals("")) {
            beli.setText("0");
        }
    }//GEN-LAST:event_beliMouseExited

    private void distributorMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_distributorMouseExited
        if (distributor.getText().equals("")) {
            distributor.setText("0");
        }
    }//GEN-LAST:event_distributorMouseExited

    private void grosirMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grosirMouseExited
        if (grosir.getText().equals("")) {
            grosir.setText("0");
        }
    }//GEN-LAST:event_grosirMouseExited

    private void retailMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_retailMouseExited
        if (retail.getText().equals("")) {
            retail.setText("0");
        }
    }//GEN-LAST:event_retailMouseExited

    private void IsiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_IsiKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            if(kode_sat2.getText().equals(kode_sat1.getText())){
                Isi.setText("1");
            }
           btnSatuan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            btnSatuan1.requestFocus();
            hitungHargaBesarKecil();
        }
    }//GEN-LAST:event_IsiKeyPressed

    private void KapasitasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KapasitasKeyPressed
        hitungHargaBesarKecil();
        //Valid.pindah(evt, Isi,BtnJenis);
    }//GEN-LAST:event_KapasitasKeyPressed

    private void percenpackagingMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_percenpackagingMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_percenpackagingMouseMoved

    private void percenpackagingMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_percenpackagingMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_percenpackagingMouseExited

    private void percenpackagingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_percenpackagingKeyPressed
        hitungHargaPackaging();
    }//GEN-LAST:event_percenpackagingKeyPressed

    private void percensatuanbesarMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_percensatuanbesarMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_percensatuanbesarMouseMoved

    private void percensatuanbesarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_percensatuanbesarMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_percensatuanbesarMouseExited

    private void percensatuanbesarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_percensatuanbesarActionPerformed
        hitungHargaBesarKecil();        // TODO add your handling code here:
    }//GEN-LAST:event_percensatuanbesarActionPerformed

    private void percensatuanbesarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_percensatuanbesarKeyPressed
        hitungHargaSatuanBesar();
    }//GEN-LAST:event_percensatuanbesarKeyPressed

    private void percensatuankecilMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_percensatuankecilMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_percensatuankecilMouseMoved

    private void percensatuankecilMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_percensatuankecilMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_percensatuankecilMouseExited

    private void percensatuankecilKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_percensatuankecilKeyPressed
        hitungHargaBesarKecil();
    }//GEN-LAST:event_percensatuankecilKeyPressed

    private void kode_sat1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kode_sat1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_kode_sat1KeyPressed

    private void btnSatuan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSatuan1ActionPerformed
i=2;    
        akses.setform("TokoBarang");
    satuan.isCek();
    satuan.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
    satuan.setLocationRelativeTo(internalFrame1);
    satuan.setVisible(true);
    }//GEN-LAST:event_btnSatuan1ActionPerformed

    private void kode_sat2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kode_sat2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_kode_sat2KeyPressed

    private void btnSatuan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSatuan2ActionPerformed
       i=3;    
        akses.setform("TokoBarang");
    satuan.isCek();
    satuan.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
    satuan.setLocationRelativeTo(internalFrame1);
    satuan.setVisible(true);
    }//GEN-LAST:event_btnSatuan2ActionPerformed

    private void beliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_beliActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_beliActionPerformed

    private void distributorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_distributorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_distributorActionPerformed

    private void HResepMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HResepMouseExited
//        hitungHargaResepPerBiji();
    }//GEN-LAST:event_HResepMouseExited

    private void HResepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HResepKeyPressed
       hitungPersenResep(); //hitungHargaResepPerBiji();
    }//GEN-LAST:event_HResepKeyPressed

    private void percenresepMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_percenresepMouseMoved
       // hitungHargaResepPerBiji();
    }//GEN-LAST:event_percenresepMouseMoved

    private void percenresepMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_percenresepMouseExited
       // hitungHargaResepPerBiji();
    }//GEN-LAST:event_percenresepMouseExited

    private void percenresepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_percenresepKeyPressed
       // hitungHargaResepPerBiji();
    }//GEN-LAST:event_percenresepKeyPressed

    private void KandunganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KandunganKeyPressed
        //Valid.pindah(evt, Nm,BtnSatuanBesar);
    }//GEN-LAST:event_KandunganKeyPressed

    private void DTPExpiredKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPExpiredKeyPressed
       // Valid.pindah(evt, stok_minimal, KdIF);
    }//GEN-LAST:event_DTPExpiredKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            TokoBarang dialog = new TokoBarang(new javax.swing.JFrame(), true);
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
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKadaluarsa;
    private widget.Tanggal DTPExpired;
    private javax.swing.JPanel ExpiredList;
    private widget.PanelBiasa FormInput;
    private widget.TextBox HResep;
    private widget.TextBox Isi;
    private widget.TextBox Kandungan;
    private widget.TextBox Kapasitas;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnRestore;
    private javax.swing.JPanel PanelInput;
    private javax.swing.JPopupMenu Popup;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox beli;
    private widget.Button btnJenis;
    private widget.Button btnSatuan;
    private widget.Button btnSatuan1;
    private widget.Button btnSatuan2;
    private widget.TextBox dasar;
    private widget.TextBox distributor;
    private widget.TextBox grosir;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox kdjenis;
    private widget.TextBox kode_brng;
    private widget.TextBox kode_sat;
    private widget.TextBox kode_sat1;
    private widget.TextBox kode_sat2;
    private widget.Label label1;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label19;
    private widget.Label label2;
    private widget.Label label20;
    private widget.Label label21;
    private widget.Label label24;
    private widget.Label label27;
    private widget.Label label3;
    private widget.Label label31;
    private widget.Label label32;
    private widget.Label label39;
    private widget.Label label4;
    private widget.Label label42;
    private widget.Label label44;
    private widget.Label label45;
    private widget.Label label5;
    private widget.Label label6;
    private widget.Label label7;
    private widget.Label label8;
    private widget.Label label9;
    private widget.TextBox nama_brng;
    private widget.TextBox nama_sat;
    private widget.TextBox nama_sat1;
    private widget.TextBox nama_sat2;
    private widget.TextBox nmjenis;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.TextBox percenpackaging;
    private widget.TextBox percenresep;
    private widget.TextBox percensatuanbesar;
    private widget.TextBox percensatuankecil;
    private widget.TextBox retail;
    private widget.TextBox stok;
    private widget.Table tbJnsPerawatan;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
           if (TCari.getText().trim().equals("")) {
            ps = koneksi.prepareStatement(
                "SELECT tokobarang.kode_brng, tokobarang.nama_brng, kodesatuan.satuan, tokojenisbarang.nm_jenis, " +
                "tokobarang.stok, tokobarang.dasar, tokobarang.h_beli, tokobarang.distributor,tokobarang.h_resep, tokobarang.kandungan, tokobarang.expire, " +
                "tokobarang.grosir, tokobarang.retail, " +
                "tokobarang.kode_sat1, tokobarang.kode_sat2, tokobarang.isi, tokobarang.kapasitas  " +
                "FROM tokobarang " +
                "INNER JOIN kodesatuan ON tokobarang.kode_sat = kodesatuan.kode_sat " +
                "INNER JOIN tokojenisbarang ON tokobarang.jenis = tokojenisbarang.kd_jenis " +
                "WHERE tokobarang.status='1' " +
                "ORDER BY tokobarang.kode_brng"
            );
        } else {
            ps = koneksi.prepareStatement(
                "SELECT tokobarang.kode_brng, tokobarang.nama_brng, kodesatuan.satuan, tokojenisbarang.nm_jenis, " +
                "tokobarang.stok, tokobarang.dasar, tokobarang.h_beli, tokobarang.distributor,tokobarang.expire, tokobarang.kandungan, " +
                "tokobarang.grosir, tokobarang.retail, " +
                "tokobarang.kode_sat1, tokobarang.kode_sat2, tokobarang.isi, tokobarang.kapasitas,  tokobarang.h_resep " +
                "FROM tokobarang " +
                "INNER JOIN kodesatuan ON tokobarang.kode_sat = kodesatuan.kode_sat " +
                "INNER JOIN tokojenisbarang ON tokobarang.jenis = tokojenisbarang.kd_jenis " +
                "WHERE tokobarang.status='1' AND ( " +
                "tokobarang.kode_brng LIKE ? OR " +
                "tokobarang.nama_brng LIKE ? OR " +
                "tokobarang.kandungan LIKE ? OR " +
                "kodesatuan.satuan LIKE ? OR " +
                "tokojenisbarang.nm_jenis LIKE ? ) " +
                "ORDER BY tokobarang.kode_brng"
            );
        }

        try {
            if (!TCari.getText().trim().equals("")) {
                ps.setString(1, "%" + TCari.getText().trim() + "%");
                ps.setString(2, "%" + TCari.getText().trim() + "%");
                ps.setString(3, "%" + TCari.getText().trim() + "%");
                ps.setString(4, "%" + TCari.getText().trim() + "%");
                ps.setString(5, "%" + TCari.getText().trim() + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString("kode_brng"),
                    rs.getString("nama_brng"),
                    rs.getString("satuan"),
                    rs.getString("nm_jenis"),
                    rs.getDouble("stok"),
                    rs.getDouble("dasar"),
                    rs.getDouble("h_beli"),
                    rs.getDouble("distributor"),
                    rs.getDouble("grosir"),
                    rs.getDouble("retail"),
                    rs.getString("kode_sat1"),
                    rs.getString("kode_sat2"),
                    rs.getString("isi"),
                    rs.getString("kapasitas"),
                    rs.getDouble("h_resep"),
                    rs.getString("kandungan"),
                    rs.getString("expire")
                });
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Data : " + e);
        }
    } catch (Exception e) {
        System.out.println("Notifikasi : " + e);
    }
    LCount.setText("" + tabMode.getRowCount());
}

    public void emptTeks() {
        kode_brng.setText("");
        nama_brng.setText("");
        kode_sat.setText("");
        dasar.setText("0");
        beli.setText("0");
        distributor.setText("0");
        grosir.setText("0");
        retail.setText("0");
        nama_sat.setText("");
        stok.setText("0");
        kdjenis.setText("");
        nmjenis.setText("");
        Kandungan.setText("");
        percenpackaging.setText("");
        percensatuanbesar.setText("");
        percensatuankecil.setText("");
        percenresep.setText("");
        HResep.setText("");
      
        TCari.setText("");
        kode_brng.requestFocus();
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(kode_brng,5),signed)),0) from tokobarang  ","BT",6,kode_brng);
        kode_brng.requestFocus();
    }
    
    public void onCari(){
        TCari.requestFocus();
    }

   private void getData() {
    if(tbJnsPerawatan.getSelectedRow() != -1){
        kode_brng.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),0).toString());
        nama_brng.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),1).toString());
        nama_sat.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),2).toString());
        nmjenis.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),3).toString());
        stok.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),4).toString());
        dasar.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),5).toString());
        beli.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),6).toString());
        distributor.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),7).toString());
        grosir.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),8).toString());
        retail.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),9).toString());
        kode_sat1.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),10).toString());
        kode_sat2.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),11).toString());
        Isi.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),12).toString());
        Kapasitas.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),13).toString());
        HResep.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),14).toString());
        Kandungan.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),15).toString());
        // tetap ambil kode_sat & kdjenis dari DB supaya sinkron
        kode_sat.setText(Sequel.cariIsi(
            "SELECT kode_sat FROM tokobarang WHERE kode_brng=?", kode_brng.getText()
        ));
        kdjenis.setText(Sequel.cariIsi(
            "SELECT jenis FROM tokobarang WHERE kode_brng=?", kode_brng.getText()
        ));
    }
}

    public JTable getTable(){
        return tbJnsPerawatan;
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,280));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.gettoko_barang());
        BtnHapus.setEnabled(akses.gettoko_barang());
        BtnEdit.setEnabled(akses.gettoko_barang());
        BtnPrint.setEnabled(akses.gettoko_barang());
        if(akses.getkode().equals("Admin Utama")){
            MnRestore.setEnabled(true);
        }else{
            MnRestore.setEnabled(false);
        }
        TCari.requestFocus();
    }
    
//   private void isHitung() {   
//        if(this.isVisible()==true){
//            try {
//                if (!beli.getText().equals("")) {
//                    try{
//                        rs=koneksi.prepareStatement("select * from tokosetharga").executeQuery();
//                        if(rs.next()){
//                            grosir.setText(Double.toString(Valid.roundUp(Double.parseDouble(beli.getText()) + (Double.parseDouble(beli.getText()) * (rs.getDouble("grosir") / 100)),100)));
//                            distributor.setText(Double.toString(Valid.roundUp(Double.parseDouble(beli.getText()) + (Double.parseDouble(beli.getText()) * (rs.getDouble("distributor") / 100)),100)));
//                            retail.setText(Double.toString(Valid.roundUp(Double.parseDouble(beli.getText()) + (Double.parseDouble(beli.getText()) * (rs.getDouble("retail") / 100)),100)));
//                        }else{
//                            JOptionPane.showMessageDialog(null,"Pengaturan harga masih kosong...!!");
//                            TCari.requestFocus();
//                        }
//                    }catch(Exception e){
//                        System.out.println("Notifikasi : "+e);
//                    }finally{
//                        if(rs!=null){
//                            rs.close();
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                System.out.println("Notif : "+e);
//            }
//        }            
//    }
   
   private void hitungHargaSatuanBesar() {
    if(this.isVisible() == true) {
        try {
            if (!beli.getText().trim().equals("") && !percensatuanbesar.getText().trim().equals("")) {
                try {
                    double hargaBeli = Double.parseDouble(beli.getText().trim());
                    double persenLaba = Double.parseDouble(percensatuanbesar.getText().trim());

                    if (hargaBeli >= 0 && persenLaba >= 0) {
                        double hargaJual = hargaBeli + (hargaBeli * (persenLaba / 100));
                        grosir.setText(Double.toString(Valid.roundUp(hargaJual, 100)));
                    } else {
                        grosir.setText("");
                    }
                } catch (NumberFormatException e) {
                    grosir.setText("");
                }
            } else {
                grosir.setText("");
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }
}
   
   private void hitungHargaPackaging() {
    if(this.isVisible() == true) {
        try {
            // Hanya hitung jika KEDUA field sudah terisi (tidak kosong)
            if (!beli.getText().trim().equals("") && !percenpackaging.getText().trim().equals("")) {
                try {
                    // Ambil nilai harga beli
                    double hargaBeli = Double.parseDouble(beli.getText().trim());
                    
                    // Ambil nilai persentase laba packaging
                    double persenLaba = Double.parseDouble(percenpackaging.getText().trim());
                    
                    // Validasi nilai harus positif
                    if (hargaBeli >= 0 && persenLaba >= 0) {
                        // Hitung harga jual packaging
                        // Rumus: Harga Jual = Harga Beli + (Harga Beli × Persentase Laba / 100)
                        double hargaJualPackaging = hargaBeli + (hargaBeli * (persenLaba / 100));
                        
                        // Set hasil ke field HPackaging dengan pembulatan ke 100
                        distributor.setText(Double.toString(Valid.roundUp(hargaJualPackaging, 100)));
                    } else {
                        // Kosongkan field jika ada nilai negatif
                        distributor.setText("");
                    }
                    
                } catch (NumberFormatException e) {
                    // Kosongkan field packaging jika input tidak valid (tanpa popup mengganggu)
                    distributor.setText("");
                }
            } else {
                // Kosongkan field packaging jika salah satu input kosong
               distributor.setText("");
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }
}
   
   private void hitungHargaBesarKecil(){
    if (!isVisible()) return;

    // ==== ambil input ====
    double hargaBeliBox = num(beli);             // <-- field "Harga Beli" (per BOX)
    double isi          = num(Isi);         // <-- field "Isi" (strip per box)
    double kapasitas    = num(Kapasitas);   // <-- field "Kapasitas" (tablet per strip)

    double pctStrip     = num(percensatuanbesar);  // <-- field persen strip (%)
    double pctTablet    = num(percensatuankecil);  // <-- field persen tablet (%)

    if (hargaBeliBox <= 0){ 
        setNum(distributor, 0);
        setNum(grosir, 0); 
        setNum(retail, 0);
        return; 
    }
    if (isi <= 0) isi = 1;
    if (kapasitas <= 0) kapasitas = 1;

    // ==== harga beli per level ====
    double beliStrip  = hargaBeliBox / isi;
    double beliTablet = hargaBeliBox / (isi * kapasitas);

    final double STEP = 100; // ganti 500/1000 sesuai kebijakan

    // ==== jual per strip ====
    if (pctStrip >= 0){
        double jualStrip = roundUp(beliStrip * (1.0 + pctStrip/100.0), STEP);
        setNum(grosir, jualStrip);     // <-- field "Hrg Satuan Besar"
    } else setNum(grosir, 0);

    // ==== jual per tablet ====
    if (pctTablet >= 0){
        double jualTablet = roundUp(beliTablet * (1.0 + pctTablet/100.0), STEP);
        setNum(retail, jualTablet);    // <-- field "Hrg Satuan Kecil"
    } else setNum(retail, 0);
}
   /**
 * Hitung harga resep per biji (tablet) dari harga beli per BOX,
 * menggunakan persen khusus percenresep, dan pembulatan STEP_RESEP.
 * Konversi: BOX → (isi=strip/box) → (kapasitas=tablet/strip) → tablet.
 */
private void hitungHargaResepPerBiji() {
    if (!isVisible()) return;

    // --- input utama ---
    double hargaBeliBox = num(beli);        // Harga beli per BOX (h_beli)
    double isi          = Math.max(1, num(Isi));          // strip per BOX
    double kapasitas    = Math.max(1, num(Kapasitas));    // tablet per STRIP
    double pctResep     = num(percenresep);               // margin resep (%)

    // --- jika input belum valid ---
    if (hargaBeliBox <= 0) {
        setNum(HResep, 0);
        return;
    }

    // --- konversi ke harga beli per biji (tablet) ---
    double beliPerBiji = hargaBeliBox / (isi * kapasitas);

    // --- kebijakan pembulatan khusus harga resep ---
    final double STEP_RESEP = 100; // ganti sesuai kebijakanmu (mis. 50 / 100 / 500)

    // --- hitung harga jual resep per biji ---
    double hargaResep;
    if (pctResep >= 0) {
        hargaResep = roundUp(beliPerBiji * (1.0 + pctResep / 100.0), STEP_RESEP);
    } else {
        // jika persen negatif, anggap tidak dijual (0) atau boleh set = beliPerBiji
        hargaResep = 0;
    }

    // --- opsional: batasi minimum agar tidak di bawah harga beli ---
    // hargaResep = Math.max(hargaResep, roundUp(beliPerBiji, STEP_RESEP));

    setNum(HResep, hargaResep);
}
   
   private static double num(javax.swing.JTextField t){
    if (t == null) return 0;
    String s = t.getText();
    if (s == null) return 0;
    s = s.trim();
    if (s.isEmpty()) return 0;
    try { return Double.parseDouble(s.replace(".","").replace(",", ".")); }
    catch(Exception e){ return 0; }
}
   // bulatkan NAIK ke kelipatan step (100/500/1000)
private static double roundUp(double v, double step){
    if (step <= 0) return Math.round(v);
    return Math.ceil(v/step) * step;
}

// set angka ke textfield
private static void setNum(javax.swing.JTextField t, double v){
    t.setText(String.valueOf(v));
}
    private static double num(String s){
    if (s == null) return 0;
    s = s.trim().replace(",", "");
    if (s.isEmpty()) return 0;
    try { return Double.parseDouble(s); } catch(Exception e){ return 0; }
}

// hitung % laba ketika HARGA RESEP diisi/diubah
private void hitungPersenResep() {
    if (!this.isVisible()) return;

    double hbeli   = num(beli.getText());
    double hResep  = num(HResep.getText());
    double isiVal  = num(Isi.getText());
    double kapVal  = num(Kapasitas.getText());

    // jika ada kasus kapasitas kosong, kamu bisa default-kan ke 1
    // if (kapVal <= 0) kapVal = 1;

    if (hbeli > 0 && hResep >= 0 && isiVal > 0 && kapVal > 0) {
        double hbeliPerUnit = hbeli / (isiVal * kapVal);
        double persen = ((hResep - hbeliPerUnit) / hbeliPerUnit) * 100.0;

        SwingUtilities.invokeLater(() ->
            percenresep.setText(String.format(java.util.Locale.US, "%.2f", persen))
        );
    } else {
        SwingUtilities.invokeLater(() -> percenresep.setText(""));
    }
}

private void hitungPersenDistributor() {
    try {
        double hargaBeli = num(beli.getText());
        double hargaDistributor = num(distributor.getText());

        if (hargaBeli > 0 && hargaDistributor >= 0) {
            double persen = ((hargaDistributor - hargaBeli) / hargaBeli) * 100.0;

            // gunakan invokeLater supaya aman di DocumentListener
            SwingUtilities.invokeLater(() -> {
                percenpackaging.setText(String.format(java.util.Locale.US, "%.2f", persen));
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                percenpackaging.setText("");
            });
        }
    } catch (Exception e) {
        System.out.println("Notifikasi hitungPersenDistributor: " + e);
    }
}

private void hitungPersenGrosir() {
    if (!this.isVisible()) return;

    double hargaBeli   = num(beli.getText());      // harga beli per-kemasan besar
    double hargaGrosir = num(grosir.getText());    // harga jual per 1 isi
    double isiVal      = num(Isi.getText());       // jumlah unit dalam kemasan

    if (hargaBeli > 0 && hargaGrosir >= 0 && isiVal > 0) {
        double hargaBeliPerUnit = hargaBeli / isiVal;

        double persen = ((hargaGrosir - hargaBeliPerUnit) / hargaBeliPerUnit) * 100.0;

        SwingUtilities.invokeLater(() ->
            percensatuanbesar.setText(String.format(java.util.Locale.US, "%.2f", persen))
        );
    } else {
        SwingUtilities.invokeLater(() -> percensatuanbesar.setText(""));
    }
}

private void hitungPersenSatuan3() {
    if (!this.isVisible()) return;

    double hargaBeli    = num(beli.getText());       // harga beli kemasan besar
    double hargaSat3    = num(retail.getText());    // harga jual satuan 3
    double isiVal       = num(Isi.getText());        // jumlah isi per kemasan
    double kapasitasVal = num(Kapasitas.getText());  // kapasitas per isi

    if (hargaBeli > 0 && hargaSat3 >= 0 && isiVal > 0 && kapasitasVal > 0) {
        // harga beli per unit terkecil
        double hargaBeliPerUnit = hargaBeli / (isiVal * kapasitasVal);

        double persen = ((hargaSat3 - hargaBeliPerUnit) / hargaBeliPerUnit) * 100.0;

        SwingUtilities.invokeLater(() ->
            percensatuankecil.setText(String.format(java.util.Locale.US, "%.2f", persen))
        );
    } else {
        SwingUtilities.invokeLater(() -> percensatuankecil.setText(""));
    }
}

private void initExpireCombo() {
    cmbExpire = new javax.swing.JComboBox<>(new String[]{
        "Semua", "≤ 7 hari", "≤ 30 hari", "Sudah lewat"
    });
    cmbExpire.setFocusable(false);
    cmbExpire.setToolTipText("Filter kadaluarsa");
    //cmbExpire.addActionListener(e -> tampil());   // reload tabel saat pilihan berubah
   // cmbExpire.addActionListener(e -> updateExpireCountOnly());
cmbExpire.addActionListener(e -> reloadExpiredTableFromDB());

    // --- TARUH DI SAMPING KOMPONEN TANGGAL KADALUARSA ---
    // Misal komponen tanggalmu bernama: DTPTglKadaluarsa (ganti sesuai namanya)
    java.awt.Component anchor = DTPExpired; // <- ganti sesuai variabel tanggalmu

    java.awt.LayoutManager lm = FormInput.getLayout();

    if (lm == null) {
        // ABSOLUTE / NULL LAYOUT
        int x = anchor.getX() + anchor.getWidth() + 8;
        int y = anchor.getY();
        cmbExpire.setBounds(x, y, 130, anchor.getHeight());
        FormInput.add(cmbExpire);
        FormInput.revalidate();
        FormInput.repaint();

    } else if (lm instanceof java.awt.FlowLayout) {
        // FLOWLAYOUT → cukup add setelah komponen tanggal
        // supaya berurutan, tambahkan pada saat yang sama form dibuat
        FormInput.add(cmbExpire);
        FormInput.revalidate();

    } else {
        // LAYOUT LAIN (GroupLayout/GridBagLayout/dsb) → cara paling aman:
        // bungkus tanggal + combobox dalam panel kecil berlayout FlowLayout
        javax.swing.JPanel wrap = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        wrap.setOpaque(false);
        // Pindahkan komponen tanggal ke wrap
        java.awt.Container parent = anchor.getParent();
        parent.remove(anchor);
        wrap.add(anchor);
        wrap.add(cmbExpire);
        // Taruh wrap di posisi bekas anchor
        parent.add(wrap); // NetBeans GroupLayout akan menempatkan di slot yang sama
        parent.revalidate();
        parent.repaint();
    }
}

//private String buildExpireWhere(String dateExpr){
//    switch (cmbExpire.getSelectedIndex()){ // 0: Semua, 1: ≤7, 2: ≤30, 3: Lewat
//        case 1: return " AND " + dateExpr + " IS NOT NULL AND " + dateExpr +
//                         " BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) ";
//        case 2: return " AND " + dateExpr + " IS NOT NULL AND " + dateExpr +
//                         " BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) ";
//        case 3: return " AND " + dateExpr + " IS NOT NULL AND " + dateExpr + " < CURDATE() ";
//        default: return "";
//    }
//}

private java.time.LocalDate toLocalDate(Object v){
    if (v == null) return null;
    if (v instanceof java.sql.Date) return ((java.sql.Date)v).toLocalDate();
    if (v instanceof java.util.Date) return ((java.util.Date)v).toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    String s = v.toString().trim();
    if (s.isEmpty() || "0000-00-00".equals(s)) return null;
    try { // ISO: 2025-09-16
        return java.time.LocalDate.parse(s);
    } catch (Exception ignore) {
        try { // UI-mu terlihat dd-MM-yyyy
            return java.time.LocalDate.parse(s, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (Exception ex) {
            return null;
        }
    }
}

// Hitung sesuai pilihan combo, tanpa ubah isi tabel
private void updateExpireCountOnly() {
    int idx = cmbExpire.getSelectedIndex(); // 0: Semua, 1: ≤7, 2: ≤30, 3: Lewat
    java.time.LocalDate today = java.time.LocalDate.now();

    int count = 0;
    for (int r = 0; r < tabMode.getRowCount(); r++) {
        java.time.LocalDate exp = toLocalDate(tabMode.getValueAt(r, C_EXPIRE));
        if (exp == null) continue;
        long days = java.time.temporal.ChronoUnit.DAYS.between(today, exp);

        boolean match;
        switch (idx) {
            case 1: match = (days >= 0 && days <= 7);  break;   // ≤ 7 hari
            case 2: match = (days >= 0 && days <= 30); break;   // ≤ 30 hari
            case 3: match = (days < 0);                break;   // sudah lewat
            default: match = true;                                // semua
        }
        if (match) count++;
    }
    LCount.setText(String.valueOf(count));
}

// === setiap selesai load data ===
private void afterTampil() {
    updateExpireCountOnly(); // panggil ini di akhir metode tampil()
}
private void reloadTabel() {
    tampil();
    afterTampil();   // <- hitung/filter expire sesudah data terisi
}

private void initExpiredTable() {
    expModel = new javax.swing.table.DefaultTableModel(
        null, new Object[]{"Kode","Nama","Expired Date","Stok","Sisa (hari)"}
    ){
        Class<?>[] types = new Class[]{
            String.class, String.class, java.sql.Date.class, Double.class, Integer.class
        };
        @Override public Class<?> getColumnClass(int c){ return types[c]; }
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    tbExp = new javax.swing.JTable(expModel);
    tbExp.setAutoCreateRowSorter(true);
}
private String buildExpireWhere(String dateCol){
    switch (cmbExpire.getSelectedIndex()){ // 0: Semua, 1: ≤7, 2: ≤30, 3: Lewat
        case 1: return " AND "+dateCol+" BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) ";
        case 2: return " AND "+dateCol+" BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) ";
        case 3: return " AND "+dateCol+" < CURDATE() ";
        default: return "";
    }
}
private void reloadExpiredTableFromDB() {
    // kosongkan
    while(expModel.getRowCount()>0) expModel.removeRow(0);

    final String expCol   = "b.expire"; // pastikan b.expire tipe DATE
    final String expWhere = buildExpireWhere(expCol);

    String sql =
        "SELECT b.kode_brng, b.nama_brng, b."+expCol.substring(2)+" AS expire, b.stok, " +
        "       TIMESTAMPDIFF(DAY, CURDATE(), "+expCol+") AS sisa_hari " +
        "FROM tokobarang b " +
        "WHERE b.status='1' AND b.stok>0 AND "+expCol+" IS NOT NULL AND "+expCol+" <> '0000-00-00' " +
         expWhere +
        "ORDER BY "+expCol+" ASC, b.nama_brng ASC";

    try (PreparedStatement ps = koneksi.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            java.sql.Date exp = rs.getDate("expire");
            Integer sisa = rs.wasNull() ? null : rs.getInt("sisa_hari");
            expModel.addRow(new Object[]{
                rs.getString("kode_brng"),
                rs.getString("nama_brng"),
                exp,
                rs.getDouble("stok"),
                sisa
            });
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal load data expired: "+ex.getMessage(),
                "Kesalahan DB", JOptionPane.ERROR_MESSAGE);
    }
}

public void styleExpTable(JTable tbl) {
    // Font & ukuran baris
    tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    tbl.setRowHeight(30);
    tbl.setShowGrid(false);
    tbl.setIntercellSpacing(new Dimension(0, 1));
    tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tbl.setSelectionBackground(new Color(0xE0F2FE)); // biru muda
    tbl.setSelectionForeground(Color.BLACK);
    tbl.setFillsViewportHeight(true);

    // Header modern
    JTableHeader h = tbl.getTableHeader();
    h.setPreferredSize(new Dimension(h.getPreferredSize().width, 34));
    h.setReorderingAllowed(true);
    h.setFont(new Font("Segoe UI", Font.BOLD, 13));
    DefaultTableCellRenderer hdr = new DefaultTableCellRenderer() {
        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);
            setBackground(new Color(0x0EA5E9)); // sky-500
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return c;
        }
    };
    for (int i=0;i<tbl.getColumnModel().getColumnCount();i++) {
        tbl.getColumnModel().getColumn(i).setHeaderRenderer(hdr);
    }

    // Zebra renderer dasar
    DefaultTableCellRenderer zebra = new DefaultTableCellRenderer(){
        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            if (!isSelected) {
                Color even = new Color(0xFFFFFF);
                Color odd  = new Color(0xF8FAFC); // slate-50
                c.setBackground((row % 2 == 0) ? even : odd);
                c.setForeground(Color.DARK_GRAY);
            }
            return c;
        }
    };

    // Renderer angka kanan + format
    final DecimalFormat nf = new DecimalFormat("#,##0.##");
    DefaultTableCellRenderer num = new DefaultTableCellRenderer(){
        @Override public void setValue(Object v){
            setHorizontalAlignment(RIGHT);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 12));
            if (v instanceof Number) setText(nf.format(((Number)v).doubleValue()));
            else setText(v == null ? "" : v.toString());
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c){
            Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
            if (!s) comp.setBackground((r % 2 == 0) ? new Color(0xFFFFFF) : new Color(0xF8FAFC));
            return comp;
        }
    };

    // Renderer tanggal (dd-MM-yyyy)
    final java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd-MM-yyyy");
    DefaultTableCellRenderer dateR = new DefaultTableCellRenderer(){
        @Override public void setValue(Object v){
            setHorizontalAlignment(CENTER);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            if (v instanceof java.sql.Date) setText(df.format((java.util.Date)v));
            else if (v instanceof java.util.Date) setText(df.format((java.util.Date)v));
            else setText(v == null ? "" : v.toString());
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c){
            Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
            if (!s) comp.setBackground((r % 2 == 0) ? new Color(0xFFFFFF) : new Color(0xF8FAFC));
            return comp;
        }
    };

    // Renderer badge untuk "Sisa (hari)" (rounded pill)
    DefaultTableCellRenderer badge = new DefaultTableCellRenderer(){
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c){
            String text = "";
            Integer days = null;
            if (v instanceof Number) {
                days = ((Number)v).intValue();
            } else {
                try { days = Integer.valueOf(String.valueOf(v)); } catch(Exception ignore){}
            }
            if (days == null) text = "";
            else if (days < 0) text = "Lewat";
            else text = days + " hari";

            JLabel lbl = (JLabel)super.getTableCellRendererComponent(t, text, s, f, r, c);
            lbl.setHorizontalAlignment(CENTER);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            lbl.setForeground(Color.WHITE);

            // Warna badge
            Color col;
            if (days == null) col = new Color(0x94A3B8);         // slate-400
            else if (days < 0) col = new Color(0xEF4444);        // red-500
            else if (days <= 7) col = new Color(0xF59E0B);       // amber-500
            else if (days <= 30) col = new Color(0xEAB308);      // yellow-500
            else col = new Color(0x22C55E);                      // green-500

            // Lukis rounded background
            lbl.setOpaque(false);
            return new JPanel(new BorderLayout()){
                { setOpaque(false); add(lbl, BorderLayout.CENTER); }
                @Override protected void paintComponent(Graphics g){
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    g2.setColor(col);
                    g2.fillRoundRect(8, h/2-12, w-16, 24, 16, 16);
                    g2.dispose();
                }
            };
        }
    };

    // Pasang renderer default (zebra) dulu
    tbl.setDefaultRenderer(Object.class, zebra);

    // Tentukan indeks kolom sesuai model expModel: 0=Kode,1=Nama,2=Expired,3=Stok,4=Sisa
    TableColumnModel cm = tbl.getColumnModel();
    cm.getColumn(2).setCellRenderer(dateR);
    cm.getColumn(3).setCellRenderer(num);
    cm.getColumn(4).setCellRenderer(badge);

    // Lebar kolom
    cm.getColumn(0).setPreferredWidth(110); // Kode
    cm.getColumn(1).setPreferredWidth(260); // Nama
    cm.getColumn(2).setPreferredWidth(120); // Expired
    cm.getColumn(3).setPreferredWidth(90);  // Stok
    cm.getColumn(4).setPreferredWidth(110); // Sisa (hari)

    // Background viewport biar senada
    if (tbl.getParent() instanceof JViewport) {
        tbl.getParent().setBackground(new Color(0xFFFFFF));
    }
}
}
