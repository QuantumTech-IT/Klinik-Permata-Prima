/*
 * Kontribusi dari Abdul Wahid, RSUD Cipayung Jakarta Timur
 */


package rekammedis;

import freehand.DlgMarkingImageAssMedisIGD;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariDokter;


/**
 *
 * @author perpustakaan
 */
public final class RMPenilaianAwalMedisIGD extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;
    private DlgCariDokter dokter=new DlgCariDokter(null,false);
    private StringBuilder htmlContent;
    private String finger="",urlImage;
    
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public RMPenilaianAwalMedisIGD(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat","No.RM","Nama Pasien","Tgl.Lahir","J.K.","Kode Dokter","Nama Dokter","Tanggal","Anamnesis","Hubungan","Keluhan Utama","Riwayat Penyakit Sekarang","Riwayat Penyakit Dahulu",
            "Riwayat Penyakit Keluarga","Riwayat Penggunakan Obat","Riwayat Alergi","Keadaan Umum","GCS","Kesadaran","TD(mmHg)","Nadi(x/menit)","RR(x/menit)","Suhu","SpO2","BB(Kg)","TB(cm)","Kepala",
            "Mata","Gigi & Mulut","Leher","Thoraks","Abdomen","Genital & Anus","Ekstremitas","Ket.Pemeriksaan Fisik","Ket.Status Lokalis","EKG","Radiologi","Laborat","Diagnosis/Asesmen","Tatalaksana",
            "IntPrehospital","Jalan Napas","Pernapasan","Sirkulasi","Disabilitas","Penilaian BBL","Nilai Periksa","Eksposur","Indikasi Rawat Inap"
        }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 50; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(70);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(65);
            }else if(i==4){
                column.setPreferredWidth(55);
            }else if(i==5){
                column.setPreferredWidth(80);
            }else if(i==6){
                column.setPreferredWidth(150);
            }else if(i==7){
                column.setPreferredWidth(115);
            }else if(i==8){
                column.setPreferredWidth(80);
            }else if(i==9){
                column.setPreferredWidth(100);
            }else if(i==10){
                column.setPreferredWidth(300);
            }else if(i==11){
                column.setPreferredWidth(150);
            }else if(i==12){
                column.setPreferredWidth(150);
            }else if(i==13){
                column.setPreferredWidth(150);
            }else if(i==14){
                column.setPreferredWidth(150);
            }else if(i==15){
                column.setPreferredWidth(120);
            }else if(i==16){
                column.setPreferredWidth(90);
            }else if(i==17){
                column.setPreferredWidth(50);
            }else if(i==18){
                column.setPreferredWidth(80);
            }else if(i==19){
                column.setPreferredWidth(60);
            }else if(i==20){
                column.setPreferredWidth(75);
            }else if(i==21){
                column.setPreferredWidth(67);
            }else if(i==22){
                column.setPreferredWidth(40);
            }else if(i==23){
                column.setPreferredWidth(40);
            }else if(i==24){
                column.setPreferredWidth(40);
            }else if(i==25){
                column.setPreferredWidth(40);
            }else if(i==26){
                column.setPreferredWidth(80);
            }else if(i==27){
                column.setPreferredWidth(80);
            }else if(i==28){
                column.setPreferredWidth(80);
            }else if(i==29){
                column.setPreferredWidth(80);
            }else if(i==30){
                column.setPreferredWidth(80);
            }else if(i==31){
                column.setPreferredWidth(80);
            }else if(i==32){
                column.setPreferredWidth(80);
            }else if(i==33){
                column.setPreferredWidth(80);
            }else if(i==34){
                column.setPreferredWidth(300);
            }else if(i==35){
                column.setPreferredWidth(200);
            }else if(i==36){
                column.setPreferredWidth(170);
            }else if(i==37){
                column.setPreferredWidth(170);
            }else if(i==38){
                column.setPreferredWidth(170);
            }else if(i==39){
                column.setPreferredWidth(150);
            }else if(i==40){
                column.setPreferredWidth(500);
            }else if(i==41){
                column.setPreferredWidth(500);
            }else if(i==42){
                column.setPreferredWidth(500);
            }else if(i==43){
                column.setPreferredWidth(500);
            }else if(i==44){
                column.setPreferredWidth(500);
            }else if(i==45){
                column.setPreferredWidth(500);
            }else if(i==46){
                column.setPreferredWidth(500);
            }else if(i==47){
                column.setPreferredWidth(500);
            }else if(i==48){
                column.setPreferredWidth(900);
            }else if(i==49){
                column.setPreferredWidth(900);
            }
            
            
            
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        
        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        Hubungan.setDocument(new batasInput((int)30).getKata(Hubungan));
        KeluhanUtama.setDocument(new batasInput((int)2000).getKata(KeluhanUtama));
        RPS.setDocument(new batasInput((int)2000).getKata(RPS));
        RPK.setDocument(new batasInput((int)2000).getKata(RPK));
        RPD.setDocument(new batasInput((int)1000).getKata(RPD));
        RPO.setDocument(new batasInput((int)1000).getKata(RPO));
        Alergi.setDocument(new batasInput((int)50).getKata(Alergi));
        GCS.setDocument(new batasInput((byte)10).getKata(GCS));
        TD.setDocument(new batasInput((byte)8).getKata(TD));
        Nadi.setDocument(new batasInput((byte)5).getKata(Nadi));
        RR.setDocument(new batasInput((byte)5).getKata(RR));
        Suhu.setDocument(new batasInput((byte)5).getKata(Suhu));
        SPO.setDocument(new batasInput((byte)5).getKata(SPO));
        BB.setDocument(new batasInput((byte)5).getKata(BB));
        TB.setDocument(new batasInput((byte)5).getKata(TB));
        KetFisik.setDocument(new batasInput((int)5000).getKata(KetFisik));
        KetLokalis.setDocument(new batasInput((int)3000).getKata(KetLokalis));
        EKG.setDocument(new batasInput((int)3000).getKata(EKG));
        Diagnosis.setDocument(new batasInput((int)500).getKata(Diagnosis));
        Tatalaksana.setDocument(new batasInput((int)5000).getKata(Tatalaksana));
//        lblIP.setDocument(new batasInput ((byte)900).getKata(lblIP));
//        lblJalanNapas.setDocument(new batasInput ((byte)900).getKata(lblJalanNapas));
//        lblPern.setDocument(new batasInput ((byte)900).getKata(lblPern));
//        lblsir.setDocument(new batasInput ((byte)900).getKata(lblsir));
//        lbldis.setDocument(new batasInput ((byte)900).getKata(lbldis));
//        lblBBL.setDocument(new batasInput ((byte)900).getKata(lblBBL));
//        lblpmnilai.setDocument(new batasInput ((byte)900).getKata(lblpmnilai));
//        lbleks.setDocument(new batasInput ((byte)900).getKata(lbleks));
        TCari.setDocument(new batasInput((int)100).getKata(TCari));
        
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
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                    KdDokter.requestFocus();
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
        
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
                ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LoadHTML = new widget.editorpane();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnPenilaianMedis = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        internalFrame2 = new widget.InternalFrame();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        label14 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel9 = new widget.Label();
        Jk = new widget.TextBox();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        jLabel12 = new widget.Label();
        BB = new widget.TextBox();
        jLabel13 = new widget.Label();
        TB = new widget.TextBox();
        jLabel15 = new widget.Label();
        jLabel16 = new widget.Label();
        Nadi = new widget.TextBox();
        jLabel17 = new widget.Label();
        jLabel18 = new widget.Label();
        Suhu = new widget.TextBox();
        jLabel22 = new widget.Label();
        TD = new widget.TextBox();
        jLabel20 = new widget.Label();
        jLabel23 = new widget.Label();
        jLabel24 = new widget.Label();
        jLabel25 = new widget.Label();
        RR = new widget.TextBox();
        jLabel26 = new widget.Label();
        jLabel37 = new widget.Label();
        Alergi = new widget.TextBox();
        Anamnesis = new widget.ComboBox();
        scrollPane1 = new widget.ScrollPane();
        KeluhanUtama = new widget.TextArea();
        jLabel30 = new widget.Label();
        scrollPane2 = new widget.ScrollPane();
        RPD = new widget.TextArea();
        jLabel31 = new widget.Label();
        scrollPane3 = new widget.ScrollPane();
        RPK = new widget.TextArea();
        jLabel32 = new widget.Label();
        scrollPane4 = new widget.ScrollPane();
        RPO = new widget.TextArea();
        scrollPane5 = new widget.ScrollPane();
        KetFisik = new widget.TextArea();
        jLabel28 = new widget.Label();
        GCS = new widget.TextBox();
        jLabel94 = new widget.Label();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel38 = new widget.Label();
        Hubungan = new widget.TextBox();
        jLabel33 = new widget.Label();
        scrollPane7 = new widget.ScrollPane();
        RPS = new widget.TextArea();
        jSeparator12 = new javax.swing.JSeparator();
        jLabel39 = new widget.Label();
        Keadaan = new widget.ComboBox();
        jLabel40 = new widget.Label();
        Kesadaran = new widget.ComboBox();
        jLabel41 = new widget.Label();
        jLabel29 = new widget.Label();
        SPO = new widget.TextBox();
        jLabel35 = new widget.Label();
        Kepala = new widget.ComboBox();
        jLabel44 = new widget.Label();
        Gigi = new widget.ComboBox();
        jLabel45 = new widget.Label();
        Leher = new widget.ComboBox();
        jLabel46 = new widget.Label();
        Thoraks = new widget.ComboBox();
        jLabel49 = new widget.Label();
        Abdomen = new widget.ComboBox();
        jLabel50 = new widget.Label();
        Genital = new widget.ComboBox();
        jLabel51 = new widget.Label();
        Ekstremitas = new widget.ComboBox();
        jSeparator13 = new javax.swing.JSeparator();
        jLabel99 = new widget.Label();
        scrollPane8 = new widget.ScrollPane();
        KetLokalis = new widget.TextArea();
        jLabel79 = new widget.Label();
        jSeparator14 = new javax.swing.JSeparator();
        jLabel100 = new widget.Label();
        scrollPane9 = new widget.ScrollPane();
        EKG = new widget.TextArea();
        jSeparator15 = new javax.swing.JSeparator();
        jLabel101 = new widget.Label();
        jSeparator16 = new javax.swing.JSeparator();
        jLabel102 = new widget.Label();
        scrollPane13 = new widget.ScrollPane();
        Tatalaksana = new widget.TextArea();
        jLabel103 = new widget.Label();
        label11 = new widget.Label();
        TglAsuhan = new widget.Tanggal();
        jLabel42 = new widget.Label();
        Mata = new widget.ComboBox();
        jLabel80 = new widget.Label();
        jLabel81 = new widget.Label();
        scrollPane10 = new widget.ScrollPane();
        Radiologi = new widget.TextArea();
        jLabel82 = new widget.Label();
        scrollPane11 = new widget.ScrollPane();
        Laborat = new widget.TextArea();
        Diagnosis = new widget.TextArea();
        jn1 = new javax.swing.JCheckBox();
        btnsimpan = new javax.swing.JButton();
        jn6 = new javax.swing.JCheckBox();
        jn2 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jn3 = new javax.swing.JCheckBox();
        jn4 = new javax.swing.JCheckBox();
        jn5 = new javax.swing.JCheckBox();
        jn7 = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jn8 = new javax.swing.JCheckBox();
        jn9 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jn10 = new javax.swing.JCheckBox();
        jn11 = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jn12 = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        ipcc = new javax.swing.JCheckBox();
        iprjp = new javax.swing.JCheckBox();
        ipdef = new javax.swing.JCheckBox();
        ipint = new javax.swing.JCheckBox();
        ipvtp = new javax.swing.JCheckBox();
        ipdcp = new javax.swing.JCheckBox();
        ipbal = new javax.swing.JCheckBox();
        ipngt = new javax.swing.JCheckBox();
        ipktr = new javax.swing.JCheckBox();
        ipinf = new javax.swing.JCheckBox();
        label = new javax.swing.JLabel();
        ipob = new javax.swing.JTextField();
        ipnn = new javax.swing.JCheckBox();
        btnsimpan1 = new javax.swing.JButton();
        psp = new javax.swing.JCheckBox();
        jLabel27 = new javax.swing.JLabel();
        preg = new javax.swing.JCheckBox();
        pts = new javax.swing.JCheckBox();
        pireg = new javax.swing.JCheckBox();
        jLabel34 = new javax.swing.JLabel();
        gsim = new javax.swing.JCheckBox();
        gasim = new javax.swing.JCheckBox();
        gkan = new javax.swing.JCheckBox();
        gkir = new javax.swing.JCheckBox();
        jLabel36 = new javax.swing.JLabel();
        tpn = new javax.swing.JCheckBox();
        tpk = new javax.swing.JCheckBox();
        tpb = new javax.swing.JCheckBox();
        tpa = new javax.swing.JCheckBox();
        tpr = new javax.swing.JCheckBox();
        tpt = new javax.swing.JCheckBox();
        tph = new javax.swing.JCheckBox();
        tpc = new javax.swing.JCheckBox();
        tpf = new javax.swing.JCheckBox();
        btnsimpan2 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel43 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        sNr = new javax.swing.JCheckBox();
        sNk = new javax.swing.JCheckBox();
        sNir = new javax.swing.JCheckBox();
        sNl = new javax.swing.JCheckBox();
        jLabel48 = new javax.swing.JLabel();
        sKn = new javax.swing.JCheckBox();
        sKp = new javax.swing.JCheckBox();
        sKj = new javax.swing.JCheckBox();
        sKc = new javax.swing.JCheckBox();
        sKb = new javax.swing.JCheckBox();
        jLabel52 = new javax.swing.JLabel();
        sAh = new javax.swing.JCheckBox();
        sAk = new javax.swing.JCheckBox();
        sAd = new javax.swing.JCheckBox();
        sAb = new javax.swing.JCheckBox();
        jLabel53 = new javax.swing.JLabel();
        sirCRT = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        lblPern = new javax.swing.JLabel();
        btnsimpan3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lblJalanNapas = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lblBBL = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lblIP = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        dpis = new javax.swing.JCheckBox();
        dpais = new javax.swing.JCheckBox();
        jLabel56 = new javax.swing.JLabel();
        dpdi = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        dprc = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        dpms = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        dplatkan = new javax.swing.JCheckBox();
        dplatkir = new javax.swing.JCheckBox();
        dplattdk = new javax.swing.JCheckBox();
        jLabel60 = new javax.swing.JLabel();
        eDe = new javax.swing.JCheckBox();
        eCon = new javax.swing.JCheckBox();
        ePe = new javax.swing.JCheckBox();
        eTe = new javax.swing.JCheckBox();
        eSw = new javax.swing.JCheckBox();
        eKo = new javax.swing.JCheckBox();
        eTtj = new javax.swing.JCheckBox();
        eAb = new javax.swing.JCheckBox();
        eBu = new javax.swing.JCheckBox();
        eLas = new javax.swing.JCheckBox();
        jScrollPane5 = new javax.swing.JScrollPane();
        lbldis = new javax.swing.JLabel();
        btnsimpan4 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        lbleks = new javax.swing.JLabel();
        btnsimpan5 = new javax.swing.JButton();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        pBto = new javax.swing.JTextField();
        jlabeldawne = new javax.swing.JLabel();
        pSd = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        pBca = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        pBpm = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        pNm = new javax.swing.JTextField();
        jlabel = new javax.swing.JLabel();
        pSa = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        pBcb = new javax.swing.JTextField();
        pNf = new javax.swing.JTextField();
        pNr = new javax.swing.JTextField();
        pNs = new javax.swing.JTextField();
        pNa = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        lblpmnilai = new javax.swing.JLabel();
        btnsimpan6 = new javax.swing.JButton();
        btnsimpan7 = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        lblsir = new javax.swing.JLabel();
        BtnEdit2 = new widget.Button();
        PanelWall = new usu.widget.glass.PanelGlass();
        eTa = new javax.swing.JCheckBox();
        jn13 = new javax.swing.JCheckBox();
        jLabel104 = new widget.Label();
        indrnpya = new javax.swing.JCheckBox();
        indrnpt = new javax.swing.JCheckBox();
        jLabel63 = new widget.Label();
        jLabel67 = new widget.Label();
        sAh2 = new javax.swing.JCheckBox();
        prev = new javax.swing.JCheckBox();
        pal = new javax.swing.JCheckBox();
        kur = new javax.swing.JCheckBox();
        rehab = new javax.swing.JCheckBox();
        jLabel75 = new widget.Label();
        rip = new javax.swing.JCheckBox();
        rb = new javax.swing.JCheckBox();
        rint = new javax.swing.JCheckBox();
        ripn = new javax.swing.JCheckBox();
        btnsimpan8 = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        lblindikasiranap = new javax.swing.JLabel();
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnPenilaianMedis.setBackground(new java.awt.Color(255, 255, 254));
        MnPenilaianMedis.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPenilaianMedis.setForeground(new java.awt.Color(50, 50, 50));
        MnPenilaianMedis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPenilaianMedis.setText("Laporan Penilaian Medis");
        MnPenilaianMedis.setName("MnPenilaianMedis"); // NOI18N
        MnPenilaianMedis.setPreferredSize(new java.awt.Dimension(220, 26));
        MnPenilaianMedis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPenilaianMedisActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPenilaianMedis);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Penilaian Awal Medis IGD ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.setPreferredSize(new java.awt.Dimension(473, 1700));
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(870, 2500));
        FormInput.setLayout(null);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 131, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(309, 10, 260, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(207, 10, 100, 23);

        label14.setText("Dokter :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(0, 40, 70, 23);

        KdDokter.setEditable(false);
        KdDokter.setName("KdDokter"); // NOI18N
        KdDokter.setPreferredSize(new java.awt.Dimension(80, 23));
        KdDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokterKeyPressed(evt);
            }
        });
        FormInput.add(KdDokter);
        KdDokter.setBounds(74, 40, 90, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter"); // NOI18N
        NmDokter.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmDokter);
        NmDokter.setBounds(166, 40, 180, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('2');
        BtnDokter.setToolTipText("Alt+2");
        BtnDokter.setName("BtnDokter"); // NOI18N
        BtnDokter.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        BtnDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokterKeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter);
        BtnDokter.setBounds(348, 40, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(580, 10, 60, 23);

        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(644, 10, 80, 23);

        jLabel9.setText("Riwayat Penggunaan Obat :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 970, 180, 23);

        Jk.setEditable(false);
        Jk.setHighlighter(null);
        Jk.setName("Jk"); // NOI18N
        FormInput.add(Jk);
        Jk.setBounds(774, 10, 80, 23);

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 70, 23);

        jLabel11.setText("J.K. :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(740, 10, 30, 23);

        jLabel12.setText("BB :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(680, 1040, 30, 23);

        BB.setFocusTraversalPolicyProvider(true);
        BB.setName("BB"); // NOI18N
        BB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BBKeyPressed(evt);
            }
        });
        FormInput.add(BB);
        BB.setBounds(710, 1040, 45, 23);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Kg");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(760, 1040, 30, 23);

        TB.setFocusTraversalPolicyProvider(true);
        TB.setName("TB"); // NOI18N
        TB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TBKeyPressed(evt);
            }
        });
        FormInput.add(TB);
        TB.setBounds(620, 1040, 40, 23);

        jLabel15.setText("TB :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(590, 1040, 30, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("x/menit");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(370, 1070, 50, 23);

        Nadi.setFocusTraversalPolicyProvider(true);
        Nadi.setName("Nadi"); // NOI18N
        Nadi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NadiKeyPressed(evt);
            }
        });
        FormInput.add(Nadi);
        Nadi.setBounds(320, 1070, 50, 23);

        jLabel17.setText("Nadi :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(270, 1070, 40, 23);

        jLabel18.setText("Suhu :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(550, 1070, 40, 23);

        Suhu.setFocusTraversalPolicyProvider(true);
        Suhu.setName("Suhu"); // NOI18N
        Suhu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuhuKeyPressed(evt);
            }
        });
        FormInput.add(Suhu);
        Suhu.setBounds(600, 1070, 50, 23);

        jLabel22.setText("TD :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(20, 1070, 127, 23);

        TD.setFocusTraversalPolicyProvider(true);
        TD.setName("TD"); // NOI18N
        TD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TDKeyPressed(evt);
            }
        });
        FormInput.add(TD);
        TD.setBounds(150, 1070, 76, 23);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("°C");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(650, 1070, 30, 23);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("mmHg");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(230, 1070, 50, 23);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText(" cm");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(660, 1040, 30, 23);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("x/menit");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(510, 1070, 50, 23);

        RR.setFocusTraversalPolicyProvider(true);
        RR.setName("RR"); // NOI18N
        RR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RRKeyPressed(evt);
            }
        });
        FormInput.add(RR);
        RR.setBounds(460, 1070, 50, 23);

        jLabel26.setText("RR :");
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput.add(jLabel26);
        jLabel26.setBounds(410, 1070, 40, 23);

        jLabel37.setText("Riwayat Alergi :");
        jLabel37.setName("jLabel37"); // NOI18N
        FormInput.add(jLabel37);
        jLabel37.setBounds(440, 980, 150, 23);

        Alergi.setFocusTraversalPolicyProvider(true);
        Alergi.setName("Alergi"); // NOI18N
        Alergi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AlergiKeyPressed(evt);
            }
        });
        FormInput.add(Alergi);
        Alergi.setBounds(600, 980, 260, 23);

        Anamnesis.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Autoanamnesis", "Alloanamnesis" }));
        Anamnesis.setName("Anamnesis"); // NOI18N
        Anamnesis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AnamnesisKeyPressed(evt);
            }
        });
        FormInput.add(Anamnesis);
        Anamnesis.setBounds(644, 40, 128, 23);

        scrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane1.setName("scrollPane1"); // NOI18N

        KeluhanUtama.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        KeluhanUtama.setColumns(20);
        KeluhanUtama.setRows(5);
        KeluhanUtama.setName("KeluhanUtama"); // NOI18N
        KeluhanUtama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeluhanUtamaKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(KeluhanUtama);

        FormInput.add(scrollPane1);
        scrollPane1.setBounds(130, 860, 310, 43);

        jLabel30.setText("Riwayat Penyakit Sekarang :");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(440, 890, 150, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        RPD.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        RPD.setColumns(20);
        RPD.setRows(5);
        RPD.setName("RPD"); // NOI18N
        RPD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RPDKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(RPD);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(600, 930, 260, 43);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Kebutuhan Ruangan :");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(250, 1880, 180, 23);

        scrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane3.setName("scrollPane3"); // NOI18N

        RPK.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        RPK.setColumns(20);
        RPK.setRows(5);
        RPK.setName("RPK"); // NOI18N
        RPK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RPKKeyPressed(evt);
            }
        });
        scrollPane3.setViewportView(RPK);

        FormInput.add(scrollPane3);
        scrollPane3.setBounds(180, 910, 255, 42);

        jLabel32.setText("Riwayat Penyakit Keluarga :");
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput.add(jLabel32);
        jLabel32.setBounds(0, 920, 180, 23);

        scrollPane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane4.setName("scrollPane4"); // NOI18N

        RPO.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        RPO.setColumns(20);
        RPO.setRows(5);
        RPO.setName("RPO"); // NOI18N
        RPO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RPOKeyPressed(evt);
            }
        });
        scrollPane4.setViewportView(RPO);

        FormInput.add(scrollPane4);
        scrollPane4.setBounds(180, 960, 255, 42);

        scrollPane5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane5.setName("scrollPane5"); // NOI18N

        KetFisik.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        KetFisik.setColumns(20);
        KetFisik.setRows(8);
        KetFisik.setName("KetFisik"); // NOI18N
        KetFisik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KetFisikKeyPressed(evt);
            }
        });
        scrollPane5.setViewportView(KetFisik);

        FormInput.add(scrollPane5);
        scrollPane5.setBounds(520, 1100, 340, 113);

        jLabel28.setText("GCS(E,V,M) :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(470, 1040, 70, 23);

        GCS.setFocusTraversalPolicyProvider(true);
        GCS.setName("GCS"); // NOI18N
        GCS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GCSActionPerformed(evt);
            }
        });
        GCS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                GCSKeyPressed(evt);
            }
        });
        FormInput.add(GCS);
        GCS.setBounds(540, 1040, 50, 23);

        jLabel94.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel94.setText("II. PEMERIKSAAN FISIK");
        jLabel94.setName("jLabel94"); // NOI18N
        FormInput.add(jLabel94);
        jLabel94.setBounds(10, 1020, 180, 23);

        jSeparator1.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator1.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator1.setName("jSeparator1"); // NOI18N
        FormInput.add(jSeparator1);
        jSeparator1.setBounds(0, 70, 880, 1);

        jLabel38.setText("Anamnesis :");
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput.add(jLabel38);
        jLabel38.setBounds(570, 40, 70, 23);

        Hubungan.setName("Hubungan"); // NOI18N
        Hubungan.setPreferredSize(new java.awt.Dimension(207, 23));
        Hubungan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                HubunganKeyPressed(evt);
            }
        });
        FormInput.add(Hubungan);
        Hubungan.setBounds(774, 40, 80, 23);

        jLabel33.setText("Keluhan Utama :");
        jLabel33.setName("jLabel33"); // NOI18N
        FormInput.add(jLabel33);
        jLabel33.setBounds(0, 870, 125, 23);

        scrollPane7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane7.setName("scrollPane7"); // NOI18N

        RPS.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        RPS.setColumns(20);
        RPS.setRows(5);
        RPS.setName("RPS"); // NOI18N
        RPS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RPSKeyPressed(evt);
            }
        });
        scrollPane7.setViewportView(RPS);

        FormInput.add(scrollPane7);
        scrollPane7.setBounds(600, 880, 260, 43);

        jSeparator12.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator12.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator12.setName("jSeparator12"); // NOI18N
        FormInput.add(jSeparator12);
        jSeparator12.setBounds(0, 1010, 880, 10);

        jLabel39.setText("Kesadaran :");
        jLabel39.setName("jLabel39"); // NOI18N
        FormInput.add(jLabel39);
        jLabel39.setBounds(270, 1040, 70, 23);

        Keadaan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sehat", "Sakit Ringan", "Sakit Sedang", "Sakit Berat" }));
        Keadaan.setName("Keadaan"); // NOI18N
        Keadaan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeadaanKeyPressed(evt);
            }
        });
        FormInput.add(Keadaan);
        Keadaan.setBounds(150, 1040, 118, 23);

        jLabel40.setText("Kepala :");
        jLabel40.setName("jLabel40"); // NOI18N
        FormInput.add(jLabel40);
        jLabel40.setBounds(0, 1110, 127, 23);

        Kesadaran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Compos Mentis", "Apatis", "Somnolen", "Sopor", "Koma" }));
        Kesadaran.setName("Kesadaran"); // NOI18N
        Kesadaran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KesadaranKeyPressed(evt);
            }
        });
        FormInput.add(Kesadaran);
        Kesadaran.setBounds(340, 1040, 130, 23);

        jLabel41.setText("Keadaan Umum :");
        jLabel41.setName("jLabel41"); // NOI18N
        FormInput.add(jLabel41);
        jLabel41.setBounds(20, 1040, 127, 23);

        jLabel29.setText("SpO2 :");
        jLabel29.setName("jLabel29"); // NOI18N
        FormInput.add(jLabel29);
        jLabel29.setBounds(670, 1070, 40, 23);

        SPO.setFocusTraversalPolicyProvider(true);
        SPO.setName("SPO"); // NOI18N
        SPO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SPOKeyPressed(evt);
            }
        });
        FormInput.add(SPO);
        SPO.setBounds(720, 1070, 50, 23);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("%");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput.add(jLabel35);
        jLabel35.setBounds(770, 1070, 30, 23);

        Kepala.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Abnormal", "Tidak Diperiksa" }));
        Kepala.setName("Kepala"); // NOI18N
        Kepala.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KepalaKeyPressed(evt);
            }
        });
        FormInput.add(Kepala);
        Kepala.setBounds(130, 1110, 128, 23);

        jLabel44.setText("Gigi & Mulut :");
        jLabel44.setName("jLabel44"); // NOI18N
        FormInput.add(jLabel44);
        jLabel44.setBounds(0, 1170, 127, 23);

        Gigi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Abnormal", "Tidak Diperiksa" }));
        Gigi.setName("Gigi"); // NOI18N
        Gigi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                GigiKeyPressed(evt);
            }
        });
        FormInput.add(Gigi);
        Gigi.setBounds(130, 1170, 128, 23);

        jLabel45.setText("Leher :");
        jLabel45.setName("jLabel45"); // NOI18N
        FormInput.add(jLabel45);
        jLabel45.setBounds(0, 1200, 127, 23);

        Leher.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Abnormal", "Tidak Diperiksa" }));
        Leher.setName("Leher"); // NOI18N
        Leher.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LeherKeyPressed(evt);
            }
        });
        FormInput.add(Leher);
        Leher.setBounds(130, 1200, 128, 23);

        jLabel46.setText("Thoraks :");
        jLabel46.setName("jLabel46"); // NOI18N
        FormInput.add(jLabel46);
        jLabel46.setBounds(280, 1110, 95, 23);

        Thoraks.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Abnormal", "Tidak Diperiksa" }));
        Thoraks.setName("Thoraks"); // NOI18N
        Thoraks.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ThoraksKeyPressed(evt);
            }
        });
        FormInput.add(Thoraks);
        Thoraks.setBounds(380, 1110, 128, 23);

        jLabel49.setText("Abdomen :");
        jLabel49.setName("jLabel49"); // NOI18N
        FormInput.add(jLabel49);
        jLabel49.setBounds(280, 1140, 95, 23);

        Abdomen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Abnormal", "Tidak Diperiksa" }));
        Abdomen.setName("Abdomen"); // NOI18N
        Abdomen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AbdomenKeyPressed(evt);
            }
        });
        FormInput.add(Abdomen);
        Abdomen.setBounds(380, 1140, 128, 23);

        jLabel50.setText("Genital & Anus :");
        jLabel50.setName("jLabel50"); // NOI18N
        FormInput.add(jLabel50);
        jLabel50.setBounds(280, 1170, 95, 23);

        Genital.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Abnormal", "Tidak Diperiksa" }));
        Genital.setName("Genital"); // NOI18N
        Genital.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                GenitalKeyPressed(evt);
            }
        });
        FormInput.add(Genital);
        Genital.setBounds(380, 1170, 128, 23);

        jLabel51.setText("Ekstremitas :");
        jLabel51.setName("jLabel51"); // NOI18N
        FormInput.add(jLabel51);
        jLabel51.setBounds(280, 1200, 95, 23);

        Ekstremitas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Abnormal", "Tidak Diperiksa" }));
        Ekstremitas.setName("Ekstremitas"); // NOI18N
        Ekstremitas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EkstremitasKeyPressed(evt);
            }
        });
        FormInput.add(Ekstremitas);
        Ekstremitas.setBounds(380, 1200, 128, 23);

        jSeparator13.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator13.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator13.setName("jSeparator13"); // NOI18N
        FormInput.add(jSeparator13);
        jSeparator13.setBounds(0, 1220, 880, 3);

        jLabel99.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel99.setText("I. RIWAYAT KESEHATAN");
        jLabel99.setName("jLabel99"); // NOI18N
        FormInput.add(jLabel99);
        jLabel99.setBounds(10, 840, 180, 23);

        scrollPane8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane8.setName("scrollPane8"); // NOI18N

        KetLokalis.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        KetLokalis.setColumns(20);
        KetLokalis.setRows(5);
        KetLokalis.setName("KetLokalis"); // NOI18N
        KetLokalis.setPreferredSize(new java.awt.Dimension(182, 92));
        KetLokalis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KetLokalisKeyPressed(evt);
            }
        });
        scrollPane8.setViewportView(KetLokalis);

        FormInput.add(scrollPane8);
        scrollPane8.setBounds(40, 1570, 810, 83);

        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel79.setText("Keterangan :");
        jLabel79.setName("jLabel79"); // NOI18N
        FormInput.add(jLabel79);
        jLabel79.setBounds(40, 1550, 100, 23);

        jSeparator14.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator14.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator14.setName("jSeparator14"); // NOI18N
        FormInput.add(jSeparator14);
        jSeparator14.setBounds(0, 1660, 880, 3);

        jLabel100.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel100.setText("III. STATUS LOKALIS");
        jLabel100.setName("jLabel100"); // NOI18N
        FormInput.add(jLabel100);
        jLabel100.setBounds(10, 1230, 180, 23);

        scrollPane9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane9.setName("scrollPane9"); // NOI18N

        EKG.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        EKG.setColumns(20);
        EKG.setRows(5);
        EKG.setName("EKG"); // NOI18N
        EKG.setPreferredSize(new java.awt.Dimension(102, 52));
        EKG.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EKGKeyPressed(evt);
            }
        });
        scrollPane9.setViewportView(EKG);

        FormInput.add(scrollPane9);
        scrollPane9.setBounds(40, 1710, 260, 63);

        jSeparator15.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator15.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator15.setName("jSeparator15"); // NOI18N
        FormInput.add(jSeparator15);
        jSeparator15.setBounds(0, 1780, 880, 3);

        jLabel101.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel101.setText("IV. PEMERIKSAAN PENUNJANG");
        jLabel101.setName("jLabel101"); // NOI18N
        FormInput.add(jLabel101);
        jLabel101.setBounds(10, 1670, 190, 23);

        jSeparator16.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator16.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator16.setName("jSeparator16"); // NOI18N
        FormInput.add(jSeparator16);
        jSeparator16.setBounds(0, 1930, 880, 3);

        jLabel102.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel102.setText("VI. INDIKASI RAWAT INAP");
        jLabel102.setName("jLabel102"); // NOI18N
        FormInput.add(jLabel102);
        jLabel102.setBounds(240, 1780, 190, 23);

        scrollPane13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane13.setName("scrollPane13"); // NOI18N

        Tatalaksana.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Tatalaksana.setColumns(20);
        Tatalaksana.setRows(24);
        Tatalaksana.setName("Tatalaksana"); // NOI18N
        Tatalaksana.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TatalaksanaKeyPressed(evt);
            }
        });
        scrollPane13.setViewportView(Tatalaksana);

        FormInput.add(scrollPane13);
        scrollPane13.setBounds(40, 1970, 810, 300);

        jLabel103.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel103.setText("VI. TATALAKSANA");
        jLabel103.setName("jLabel103"); // NOI18N
        FormInput.add(jLabel103);
        jLabel103.setBounds(10, 1940, 190, 23);

        label11.setText("Tanggal :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label11);
        label11.setBounds(380, 40, 52, 23);

        TglAsuhan.setForeground(new java.awt.Color(50, 70, 50));
        TglAsuhan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24-10-2024 11:01:43" }));
        TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setName("TglAsuhan"); // NOI18N
        TglAsuhan.setOpaque(false);
        TglAsuhan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglAsuhanKeyPressed(evt);
            }
        });
        FormInput.add(TglAsuhan);
        TglAsuhan.setBounds(436, 40, 130, 23);

        jLabel42.setText("Mata :");
        jLabel42.setName("jLabel42"); // NOI18N
        FormInput.add(jLabel42);
        jLabel42.setBounds(0, 1140, 127, 23);

        Mata.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Abnormal", "Tidak Diperiksa" }));
        Mata.setName("Mata"); // NOI18N
        Mata.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MataKeyPressed(evt);
            }
        });
        FormInput.add(Mata);
        Mata.setBounds(130, 1140, 128, 23);

        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel80.setText("EKG :");
        jLabel80.setName("jLabel80"); // NOI18N
        FormInput.add(jLabel80);
        jLabel80.setBounds(40, 1690, 150, 23);

        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel81.setText("Radiologi :");
        jLabel81.setName("jLabel81"); // NOI18N
        FormInput.add(jLabel81);
        jLabel81.setBounds(310, 1690, 150, 23);

        scrollPane10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane10.setName("scrollPane10"); // NOI18N

        Radiologi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Radiologi.setColumns(20);
        Radiologi.setRows(5);
        Radiologi.setName("Radiologi"); // NOI18N
        Radiologi.setPreferredSize(new java.awt.Dimension(102, 52));
        Radiologi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RadiologiKeyPressed(evt);
            }
        });
        scrollPane10.setViewportView(Radiologi);

        FormInput.add(scrollPane10);
        scrollPane10.setBounds(310, 1710, 260, 63);

        jLabel82.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel82.setText("Laborat :");
        jLabel82.setName("jLabel82"); // NOI18N
        FormInput.add(jLabel82);
        jLabel82.setBounds(580, 1690, 150, 23);

        scrollPane11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane11.setName("scrollPane11"); // NOI18N

        Laborat.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Laborat.setColumns(20);
        Laborat.setRows(5);
        Laborat.setName("Laborat"); // NOI18N
        Laborat.setPreferredSize(new java.awt.Dimension(102, 52));
        Laborat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LaboratKeyPressed(evt);
            }
        });
        scrollPane11.setViewportView(Laborat);

        FormInput.add(scrollPane11);
        scrollPane11.setBounds(580, 1710, 260, 63);

        Diagnosis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Diagnosis.setColumns(20);
        Diagnosis.setRows(3);
        Diagnosis.setName("Diagnosis"); // NOI18N
        Diagnosis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosisKeyPressed(evt);
            }
        });
        FormInput.add(Diagnosis);
        Diagnosis.setBounds(38, 1800, 162, 70);

        jn1.setBackground(new java.awt.Color(255, 255, 255));
        jn1.setText("Paten");
        jn1.setName("jn1"); // NOI18N
        jn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jn1ActionPerformed(evt);
            }
        });
        FormInput.add(jn1);
        jn1.setBounds(10, 170, 100, 20);

        btnsimpan.setText("submit");
        btnsimpan.setName("btnsimpan"); // NOI18N
        btnsimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpanActionPerformed(evt);
            }
        });
        FormInput.add(btnsimpan);
        btnsimpan.setBounds(40, 560, 90, 22);

        jn6.setBackground(new java.awt.Color(255, 255, 255));
        jn6.setText("Obstruksi total");
        jn6.setName("jn6"); // NOI18N
        FormInput.add(jn6);
        jn6.setBounds(10, 320, 150, 20);

        jn2.setBackground(new java.awt.Color(255, 255, 255));
        jn2.setText("stridor");
        jn2.setName("jn2"); // NOI18N
        jn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jn2ActionPerformed(evt);
            }
        });
        FormInput.add(jn2);
        jn2.setBounds(40, 220, 106, 20);

        jLabel1.setText("CRT :");
        jLabel1.setName("jLabel1"); // NOI18N
        FormInput.add(jLabel1);
        jLabel1.setBounds(440, 410, 110, 16);

        jn3.setBackground(new java.awt.Color(255, 255, 255));
        jn3.setText("snoring");
        jn3.setName("jn3"); // NOI18N
        FormInput.add(jn3);
        jn3.setBounds(40, 240, 110, 20);

        jn4.setBackground(new java.awt.Color(255, 255, 255));
        jn4.setText("gurgling");
        jn4.setName("jn4"); // NOI18N
        FormInput.add(jn4);
        jn4.setBounds(40, 260, 100, 20);

        jn5.setBackground(new java.awt.Color(255, 255, 255));
        jn5.setText("wheezing");
        jn5.setName("jn5"); // NOI18N
        FormInput.add(jn5);
        jn5.setBounds(40, 280, 90, 20);

        jn7.setBackground(new java.awt.Color(255, 255, 255));
        jn7.setText("fasial");
        jn7.setName("jn7"); // NOI18N
        FormInput.add(jn7);
        jn7.setBounds(40, 370, 140, 20);

        jLabel2.setText("Trauma jalan napas");
        jLabel2.setName("jLabel2"); // NOI18N
        FormInput.add(jLabel2);
        jLabel2.setBounds(10, 350, 130, 16);

        jn8.setBackground(new java.awt.Color(255, 255, 255));
        jn8.setText("leher");
        jn8.setName("jn8"); // NOI18N
        FormInput.add(jn8);
        jn8.setBounds(40, 390, 100, 20);

        jn9.setBackground(new java.awt.Color(255, 255, 255));
        jn9.setText("inhalasi*");
        jn9.setName("jn9"); // NOI18N
        FormInput.add(jn9);
        jn9.setBounds(40, 410, 100, 20);

        jLabel3.setText("Risiko aspirasi ");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(20, 440, 110, 16);

        jn10.setBackground(new java.awt.Color(255, 255, 255));
        jn10.setText("perdarahan");
        jn10.setName("jn10"); // NOI18N
        FormInput.add(jn10);
        jn10.setBounds(40, 460, 110, 20);

        jn11.setBackground(new java.awt.Color(255, 255, 255));
        jn11.setText("muntahan*");
        jn11.setName("jn11"); // NOI18N
        FormInput.add(jn11);
        jn11.setBounds(40, 480, 120, 20);

        jLabel4.setText("Benda asing");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(20, 510, 80, 16);

        jn12.setName("jn12"); // NOI18N
        FormInput.add(jn12);
        jn12.setBounds(40, 530, 110, 22);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N
        FormInput.add(jSeparator2);
        jSeparator2.setBounds(640, 170, 10, 670);

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel5.setText("Tipe Pernapasan");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(200, 310, 130, 19);

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel14.setText("INTERVENSI PREHOSPITAL");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 80, 210, 19);

        ipcc.setBackground(new java.awt.Color(255, 255, 255));
        ipcc.setText("Cervical collar");
        ipcc.setName("ipcc"); // NOI18N
        FormInput.add(ipcc);
        ipcc.setBounds(10, 100, 120, 20);

        iprjp.setBackground(new java.awt.Color(255, 255, 255));
        iprjp.setText("RJP ");
        iprjp.setName("iprjp"); // NOI18N
        FormInput.add(iprjp);
        iprjp.setBounds(10, 120, 60, 20);

        ipdef.setBackground(new java.awt.Color(255, 255, 255));
        ipdef.setText("Defibrilasi");
        ipdef.setName("ipdef"); // NOI18N
        FormInput.add(ipdef);
        ipdef.setBounds(210, 100, 100, 20);

        ipint.setBackground(new java.awt.Color(255, 255, 255));
        ipint.setText("Intubasi");
        ipint.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ipint.setName("ipint"); // NOI18N
        FormInput.add(ipint);
        ipint.setBounds(130, 120, 80, 18);

        ipvtp.setBackground(new java.awt.Color(255, 255, 255));
        ipvtp.setText("VTP");
        ipvtp.setName("ipvtp"); // NOI18N
        FormInput.add(ipvtp);
        ipvtp.setBounds(130, 100, 60, 20);

        ipdcp.setBackground(new java.awt.Color(255, 255, 255));
        ipdcp.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        ipdcp.setText("Decompresi jarum/WSD*");
        ipdcp.setName("ipdcp"); // NOI18N
        FormInput.add(ipdcp);
        ipdcp.setBounds(210, 120, 150, 19);

        ipbal.setBackground(new java.awt.Color(255, 255, 255));
        ipbal.setText("Balut/Bidai*");
        ipbal.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ipbal.setName("ipbal"); // NOI18N
        FormInput.add(ipbal);
        ipbal.setBounds(360, 100, 100, 23);

        ipngt.setBackground(new java.awt.Color(255, 255, 255));
        ipngt.setText("NGT");
        ipngt.setName("ipngt"); // NOI18N
        FormInput.add(ipngt);
        ipngt.setBounds(460, 120, 60, 20);

        ipktr.setBackground(new java.awt.Color(255, 255, 255));
        ipktr.setText("Kateter urin");
        ipktr.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ipktr.setName("ipktr"); // NOI18N
        FormInput.add(ipktr);
        ipktr.setBounds(360, 120, 100, 18);

        ipinf.setBackground(new java.awt.Color(255, 255, 255));
        ipinf.setText("Infus");
        ipinf.setName("ipinf"); // NOI18N
        FormInput.add(ipinf);
        ipinf.setBounds(460, 100, 60, 20);

        label.setText("Obat :");
        label.setName("label"); // NOI18N
        FormInput.add(label);
        label.setBounds(520, 120, 80, 20);

        ipob.setName("ipob"); // NOI18N
        ipob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipobActionPerformed(evt);
            }
        });
        FormInput.add(ipob);
        ipob.setBounds(560, 120, 140, 20);

        ipnn.setBackground(new java.awt.Color(255, 255, 255));
        ipnn.setText("Tidak ada");
        ipnn.setName("ipnn"); // NOI18N
        FormInput.add(ipnn);
        ipnn.setBounds(520, 100, 90, 20);

        btnsimpan1.setText("submit");
        btnsimpan1.setName("btnsimpan1"); // NOI18N
        btnsimpan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpan1ActionPerformed(evt);
            }
        });
        FormInput.add(btnsimpan1);
        btnsimpan1.setBounds(710, 110, 80, 22);

        psp.setBackground(new java.awt.Color(255, 255, 255));
        psp.setText("Spontan");
        psp.setName("psp"); // NOI18N
        FormInput.add(psp);
        psp.setBounds(210, 170, 90, 20);

        jLabel27.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel27.setText("JALAN NAFAS");
        jLabel27.setName("jLabel27"); // NOI18N
        FormInput.add(jLabel27);
        jLabel27.setBounds(0, 150, 130, 19);

        preg.setBackground(new java.awt.Color(255, 255, 255));
        preg.setText("Reguler");
        preg.setName("preg"); // NOI18N
        FormInput.add(preg);
        preg.setBounds(210, 190, 90, 20);

        pts.setBackground(new java.awt.Color(255, 255, 255));
        pts.setText("Tidak Spontan");
        pts.setName("pts"); // NOI18N
        FormInput.add(pts);
        pts.setBounds(300, 170, 130, 20);

        pireg.setBackground(new java.awt.Color(255, 255, 255));
        pireg.setText("Irreguler");
        pireg.setName("pireg"); // NOI18N
        FormInput.add(pireg);
        pireg.setBounds(300, 190, 100, 20);

        jLabel34.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel34.setText("SIRKULASI");
        jLabel34.setName("jLabel34"); // NOI18N
        FormInput.add(jLabel34);
        jLabel34.setBounds(440, 150, 130, 19);

        gsim.setBackground(new java.awt.Color(255, 255, 255));
        gsim.setText("Simetris");
        gsim.setName("gsim"); // NOI18N
        FormInput.add(gsim);
        gsim.setBounds(210, 240, 90, 20);

        gasim.setBackground(new java.awt.Color(255, 255, 255));
        gasim.setText("Asimetris");
        gasim.setName("gasim"); // NOI18N
        FormInput.add(gasim);
        gasim.setBounds(310, 240, 106, 20);

        gkan.setBackground(new java.awt.Color(255, 255, 255));
        gkan.setText("Jejas dinding dada kanan");
        gkan.setName("gkan"); // NOI18N
        FormInput.add(gkan);
        gkan.setBounds(210, 260, 220, 20);

        gkir.setBackground(new java.awt.Color(255, 255, 255));
        gkir.setText("Jejas dinding dada kiri*");
        gkir.setName("gkir"); // NOI18N
        FormInput.add(gkir);
        gkir.setBounds(210, 280, 210, 20);

        jLabel36.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel36.setText("Gerakan Dada");
        jLabel36.setName("jLabel36"); // NOI18N
        FormInput.add(jLabel36);
        jLabel36.setBounds(200, 220, 130, 19);

        tpn.setBackground(new java.awt.Color(255, 255, 255));
        tpn.setText("Normal");
        tpn.setName("tpn"); // NOI18N
        tpn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpnActionPerformed(evt);
            }
        });
        FormInput.add(tpn);
        tpn.setBounds(210, 330, 90, 20);

        tpk.setBackground(new java.awt.Color(255, 255, 255));
        tpk.setText("Kussmaul");
        tpk.setName("tpk"); // NOI18N
        tpk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpkActionPerformed(evt);
            }
        });
        FormInput.add(tpk);
        tpk.setBounds(210, 350, 90, 20);

        tpb.setBackground(new java.awt.Color(255, 255, 255));
        tpb.setText("Biot");
        tpb.setName("tpb"); // NOI18N
        FormInput.add(tpb);
        tpb.setBounds(210, 370, 70, 20);

        tpa.setBackground(new java.awt.Color(255, 255, 255));
        tpa.setText("Apneustic");
        tpa.setName("tpa"); // NOI18N
        FormInput.add(tpa);
        tpa.setBounds(210, 390, 90, 20);

        tpr.setBackground(new java.awt.Color(255, 255, 255));
        tpr.setText("Reaktif");
        tpr.setName("tpr"); // NOI18N
        FormInput.add(tpr);
        tpr.setBounds(210, 410, 90, 20);

        tpt.setBackground(new java.awt.Color(255, 255, 255));
        tpt.setText("Takipneu");
        tpt.setName("tpt"); // NOI18N
        FormInput.add(tpt);
        tpt.setBounds(300, 330, 130, 20);

        tph.setBackground(new java.awt.Color(255, 255, 255));
        tph.setText("Hiperventilasi");
        tph.setName("tph"); // NOI18N
        FormInput.add(tph);
        tph.setBounds(300, 350, 120, 20);

        tpc.setBackground(new java.awt.Color(255, 255, 255));
        tpc.setText("Cheyne stoke");
        tpc.setName("tpc"); // NOI18N
        FormInput.add(tpc);
        tpc.setBounds(300, 370, 120, 20);

        tpf.setBackground(new java.awt.Color(255, 255, 255));
        tpf.setText("Flare");
        tpf.setName("tpf"); // NOI18N
        FormInput.add(tpf);
        tpf.setBounds(300, 390, 120, 20);

        btnsimpan2.setText("submit");
        btnsimpan2.setName("btnsimpan2"); // NOI18N
        btnsimpan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpan2ActionPerformed(evt);
            }
        });
        FormInput.add(btnsimpan2);
        btnsimpan2.setBounds(270, 440, 90, 22);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setName("jSeparator3"); // NOI18N
        FormInput.add(jSeparator3);
        jSeparator3.setBounds(190, 170, 10, 670);

        jLabel43.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel43.setText("Nilai");
        jLabel43.setName("jLabel43"); // NOI18N
        FormInput.add(jLabel43);
        jLabel43.setBounds(580, 580, 40, 19);

        jLabel47.setText("Obstruksi Partial");
        jLabel47.setName("jLabel47"); // NOI18N
        FormInput.add(jLabel47);
        jLabel47.setBounds(20, 200, 110, 16);

        sNr.setBackground(new java.awt.Color(255, 255, 255));
        sNr.setText("Reguler");
        sNr.setName("sNr"); // NOI18N
        FormInput.add(sNr);
        sNr.setBounds(450, 190, 80, 20);

        sNk.setBackground(new java.awt.Color(255, 255, 255));
        sNk.setText("Kuat");
        sNk.setName("sNk"); // NOI18N
        FormInput.add(sNk);
        sNk.setBounds(450, 210, 90, 20);

        sNir.setBackground(new java.awt.Color(255, 255, 255));
        sNir.setText("Irreguler");
        sNir.setName("sNir"); // NOI18N
        FormInput.add(sNir);
        sNir.setBounds(540, 190, 90, 20);

        sNl.setBackground(new java.awt.Color(255, 255, 255));
        sNl.setText("Lemah");
        sNl.setName("sNl"); // NOI18N
        FormInput.add(sNl);
        sNl.setBounds(540, 210, 90, 20);

        jLabel48.setText("Nadi :");
        jLabel48.setName("jLabel48"); // NOI18N
        FormInput.add(jLabel48);
        jLabel48.setBounds(440, 170, 110, 16);

        sKn.setBackground(new java.awt.Color(255, 255, 255));
        sKn.setText("Normal");
        sKn.setName("sKn"); // NOI18N
        FormInput.add(sKn);
        sKn.setBounds(450, 260, 100, 20);

        sKp.setBackground(new java.awt.Color(255, 255, 255));
        sKp.setText("Pucat");
        sKp.setName("sKp"); // NOI18N
        FormInput.add(sKp);
        sKp.setBounds(550, 260, 90, 20);

        sKj.setBackground(new java.awt.Color(255, 255, 255));
        sKj.setText("Jaundice");
        sKj.setName("sKj"); // NOI18N
        FormInput.add(sKj);
        sKj.setBounds(450, 280, 100, 20);

        sKc.setBackground(new java.awt.Color(255, 255, 255));
        sKc.setText("Cyanosis");
        sKc.setName("sKc"); // NOI18N
        FormInput.add(sKc);
        sKc.setBounds(550, 280, 90, 20);

        sKb.setBackground(new java.awt.Color(255, 255, 255));
        sKb.setText("Berkeringat");
        sKb.setName("sKb"); // NOI18N
        FormInput.add(sKb);
        sKb.setBounds(450, 300, 110, 20);

        jLabel52.setText("Kulit/ Mukosa :");
        jLabel52.setName("jLabel52"); // NOI18N
        FormInput.add(jLabel52);
        jLabel52.setBounds(440, 240, 110, 16);

        sAh.setBackground(new java.awt.Color(255, 255, 255));
        sAh.setText("Hangat");
        sAh.setName("sAh"); // NOI18N
        FormInput.add(sAh);
        sAh.setBounds(460, 350, 90, 20);

        sAk.setBackground(new java.awt.Color(255, 255, 255));
        sAk.setText("Kering");
        sAk.setName("sAk"); // NOI18N
        FormInput.add(sAk);
        sAk.setBounds(460, 370, 90, 20);

        sAd.setBackground(new java.awt.Color(255, 255, 255));
        sAd.setText("Dingin");
        sAd.setName("sAd"); // NOI18N
        FormInput.add(sAd);
        sAd.setBounds(550, 350, 90, 20);

        sAb.setBackground(new java.awt.Color(255, 255, 255));
        sAb.setText("Basah");
        sAb.setName("sAb"); // NOI18N
        FormInput.add(sAb);
        sAb.setBounds(550, 370, 90, 20);

        jLabel53.setText("Akral :");
        jLabel53.setName("jLabel53"); // NOI18N
        FormInput.add(jLabel53);
        jLabel53.setBounds(440, 330, 110, 16);

        sirCRT.setName("sirCRT"); // NOI18N
        sirCRT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sirCRTActionPerformed(evt);
            }
        });
        FormInput.add(sirCRT);
        sirCRT.setBounds(480, 410, 140, 20);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setName("jSeparator4"); // NOI18N
        FormInput.add(jSeparator4);
        jSeparator4.setBounds(430, 170, 10, 380);

        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lblPern.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPern.setName("lblPern"); // NOI18N
        jScrollPane1.setViewportView(lblPern);

        FormInput.add(jScrollPane1);
        jScrollPane1.setBounds(220, 480, 180, 70);

        btnsimpan3.setText("submit");
        btnsimpan3.setName("btnsimpan3"); // NOI18N
        btnsimpan3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpan3ActionPerformed(evt);
            }
        });
        FormInput.add(btnsimpan3);
        btnsimpan3.setBounds(500, 440, 90, 22);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        lblJalanNapas.setBackground(new java.awt.Color(0, 204, 204));
        lblJalanNapas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblJalanNapas.setName("lblJalanNapas"); // NOI18N
        jScrollPane2.setViewportView(lblJalanNapas);

        FormInput.add(jScrollPane2);
        jScrollPane2.setBounds(10, 590, 150, 60);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        lblBBL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblBBL.setName("lblBBL"); // NOI18N
        jScrollPane3.setViewportView(lblBBL);

        FormInput.add(jScrollPane3);
        jScrollPane3.setBounds(210, 790, 180, 60);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        lblIP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblIP.setName("lblIP"); // NOI18N
        jScrollPane4.setViewportView(lblIP);

        FormInput.add(jScrollPane4);
        jScrollPane4.setBounds(810, 100, 200, 40);

        jLabel54.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel54.setText("DISABILITAS");
        jLabel54.setName("jLabel54"); // NOI18N
        FormInput.add(jLabel54);
        jLabel54.setBounds(650, 150, 90, 19);

        jLabel55.setText("Pupil :");
        jLabel55.setName("jLabel55"); // NOI18N
        FormInput.add(jLabel55);
        jLabel55.setBounds(650, 170, 70, 16);

        dpis.setBackground(new java.awt.Color(255, 255, 255));
        dpis.setText("Isokor");
        dpis.setName("dpis"); // NOI18N
        FormInput.add(dpis);
        dpis.setBounds(660, 190, 90, 20);

        dpais.setBackground(new java.awt.Color(255, 255, 255));
        dpais.setText("Anisokor");
        dpais.setName("dpais"); // NOI18N
        FormInput.add(dpais);
        dpais.setBounds(750, 190, 100, 20);

        jLabel56.setText("Diameter :");
        jLabel56.setName("jLabel56"); // NOI18N
        FormInput.add(jLabel56);
        jLabel56.setBounds(650, 220, 80, 20);

        dpdi.setName("dpdi"); // NOI18N
        FormInput.add(dpdi);
        dpdi.setBounds(750, 220, 100, 20);

        jLabel57.setText("Reflek cahaya : ");
        jLabel57.setName("jLabel57"); // NOI18N
        FormInput.add(jLabel57);
        jLabel57.setBounds(650, 250, 100, 20);

        dprc.setName("dprc"); // NOI18N
        FormInput.add(dprc);
        dprc.setBounds(750, 250, 100, 20);

        jLabel58.setText("Meningeal signs : ");
        jLabel58.setName("jLabel58"); // NOI18N
        FormInput.add(jLabel58);
        jLabel58.setBounds(650, 280, 120, 20);

        dpms.setName("dpms"); // NOI18N
        FormInput.add(dpms);
        dpms.setBounds(750, 280, 100, 20);

        jLabel59.setText("Lateralisasi : ");
        jLabel59.setName("jLabel59"); // NOI18N
        FormInput.add(jLabel59);
        jLabel59.setBounds(650, 310, 90, 16);

        dplatkan.setBackground(new java.awt.Color(255, 255, 255));
        dplatkan.setText("kanan");
        dplatkan.setName("dplatkan"); // NOI18N
        FormInput.add(dplatkan);
        dplatkan.setBounds(650, 330, 70, 20);

        dplatkir.setBackground(new java.awt.Color(255, 255, 255));
        dplatkir.setText("kiri");
        dplatkir.setName("dplatkir"); // NOI18N
        FormInput.add(dplatkir);
        dplatkir.setBounds(730, 330, 60, 20);

        dplattdk.setBackground(new java.awt.Color(255, 255, 255));
        dplattdk.setText("tidak ada*");
        dplattdk.setName("dplattdk"); // NOI18N
        FormInput.add(dplattdk);
        dplattdk.setBounds(800, 330, 130, 20);

        jLabel60.setFont(new java.awt.Font("Lucida Grande", 1, 15)); // NOI18N
        jLabel60.setText("Eksposur");
        jLabel60.setName("jLabel60"); // NOI18N
        FormInput.add(jLabel60);
        jLabel60.setBounds(660, 460, 90, 16);

        eDe.setBackground(new java.awt.Color(255, 255, 255));
        eDe.setText("Deformitas");
        eDe.setName("eDe"); // NOI18N
        FormInput.add(eDe);
        eDe.setBounds(660, 480, 110, 20);

        eCon.setBackground(new java.awt.Color(255, 255, 255));
        eCon.setText("Contusio");
        eCon.setName("eCon"); // NOI18N
        FormInput.add(eCon);
        eCon.setBounds(660, 500, 110, 20);

        ePe.setBackground(new java.awt.Color(255, 255, 255));
        ePe.setText("Penetrasi");
        ePe.setName("ePe"); // NOI18N
        FormInput.add(ePe);
        ePe.setBounds(660, 520, 110, 20);

        eTe.setBackground(new java.awt.Color(255, 255, 255));
        eTe.setText("Tenderness");
        eTe.setName("eTe"); // NOI18N
        FormInput.add(eTe);
        eTe.setBounds(660, 540, 110, 20);

        eSw.setBackground(new java.awt.Color(255, 255, 255));
        eSw.setText("Swelling");
        eSw.setName("eSw"); // NOI18N
        FormInput.add(eSw);
        eSw.setBounds(660, 560, 110, 20);

        eKo.setBackground(new java.awt.Color(255, 255, 255));
        eKo.setText("Ekskoriasi");
        eKo.setName("eKo"); // NOI18N
        FormInput.add(eKo);
        eKo.setBounds(770, 480, 114, 20);

        eTtj.setBackground(new java.awt.Color(255, 255, 255));
        eTtj.setText("Tidak tampak jelas");
        eTtj.setName("eTtj"); // NOI18N
        FormInput.add(eTtj);
        eTtj.setBounds(660, 580, 170, 20);

        eAb.setBackground(new java.awt.Color(255, 255, 255));
        eAb.setText("Abrasi");
        eAb.setName("eAb"); // NOI18N
        FormInput.add(eAb);
        eAb.setBounds(770, 500, 120, 20);

        eBu.setBackground(new java.awt.Color(255, 255, 255));
        eBu.setText("Burn");
        eBu.setName("eBu"); // NOI18N
        FormInput.add(eBu);
        eBu.setBounds(770, 520, 110, 20);

        eLas.setBackground(new java.awt.Color(255, 255, 255));
        eLas.setText("Laserasi");
        eLas.setName("eLas"); // NOI18N
        FormInput.add(eLas);
        eLas.setBounds(770, 540, 114, 20);

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        lbldis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbldis.setName("lbldis"); // NOI18N
        jScrollPane5.setViewportView(lbldis);

        FormInput.add(jScrollPane5);
        jScrollPane5.setBounds(660, 390, 210, 70);

        btnsimpan4.setText("submit");
        btnsimpan4.setName("btnsimpan4"); // NOI18N
        btnsimpan4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpan4ActionPerformed(evt);
            }
        });
        FormInput.add(btnsimpan4);
        btnsimpan4.setBounds(720, 360, 100, 22);

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        lbleks.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbleks.setName("lbleks"); // NOI18N
        jScrollPane6.setViewportView(lbleks);

        FormInput.add(jScrollPane6);
        jScrollPane6.setBounds(680, 660, 170, 70);

        btnsimpan5.setText("submit");
        btnsimpan5.setName("btnsimpan5"); // NOI18N
        btnsimpan5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpan5ActionPerformed(evt);
            }
        });
        FormInput.add(btnsimpan5);
        btnsimpan5.setBounds(720, 620, 90, 22);

        jLabel61.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel61.setText("PERNAPASAN ");
        jLabel61.setName("jLabel61"); // NOI18N
        FormInput.add(jLabel61);
        jLabel61.setBounds(200, 150, 130, 19);

        jLabel62.setText("Tonus otot baik : ");
        jLabel62.setName("jLabel62"); // NOI18N
        FormInput.add(jLabel62);
        jLabel62.setBounds(210, 670, 120, 30);

        pBto.setName("pBto"); // NOI18N
        FormInput.add(pBto);
        pBto.setBounds(350, 680, 50, 20);

        jlabeldawne.setText("Skor DOWNE :");
        jlabeldawne.setName("jlabeldawne"); // NOI18N
        FormInput.add(jlabeldawne);
        jlabeldawne.setBounds(210, 740, 110, 16);

        pSd.setName("pSd"); // NOI18N
        FormInput.add(pSd);
        pSd.setBounds(350, 740, 50, 20);

        jLabel64.setText("Cairan amnion jernih :");
        jLabel64.setName("jLabel64"); // NOI18N
        FormInput.add(jLabel64);
        jLabel64.setBounds(210, 620, 140, 20);

        pBca.setName("pBca"); // NOI18N
        FormInput.add(pBca);
        pBca.setBounds(350, 620, 50, 20);

        jLabel65.setText("Pernapasan / menangis:");
        jLabel65.setName("jLabel65"); // NOI18N
        FormInput.add(jLabel65);
        jLabel65.setBounds(210, 650, 140, 20);

        pBpm.setName("pBpm"); // NOI18N
        pBpm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pBpmActionPerformed(evt);
            }
        });
        FormInput.add(pBpm);
        pBpm.setBounds(350, 650, 50, 20);

        jLabel66.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel66.setText("Sianosis :");
        jLabel66.setName("jLabel66"); // NOI18N
        FormInput.add(jLabel66);
        jLabel66.setBounds(460, 640, 50, 16);

        pNm.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        pNm.setName("pNm"); // NOI18N
        FormInput.add(pNm);
        pNm.setBounds(570, 680, 50, 20);

        jlabel.setText("Skor APGAR :");
        jlabel.setName("jlabel"); // NOI18N
        FormInput.add(jlabel);
        jlabel.setBounds(210, 710, 90, 16);

        pSa.setName("pSa"); // NOI18N
        FormInput.add(pSa);
        pSa.setBounds(350, 710, 50, 20);

        jLabel68.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel68.setText("PENILAIAN BAYI BARU LAHIR");
        jLabel68.setName("jLabel68"); // NOI18N
        FormInput.add(jLabel68);
        jLabel68.setBounds(210, 560, 220, 19);

        jLabel69.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel69.setText("Pemeriksaan");
        jLabel69.setName("jLabel69"); // NOI18N
        FormInput.add(jLabel69);
        jLabel69.setBounds(460, 580, 110, 19);

        jLabel70.setText("Cukup bulan :");
        jLabel70.setName("jLabel70"); // NOI18N
        FormInput.add(jLabel70);
        jLabel70.setBounds(210, 590, 110, 16);

        jLabel71.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel71.setText("Merintih :");
        jLabel71.setName("jLabel71"); // NOI18N
        FormInput.add(jLabel71);
        jLabel71.setBounds(460, 680, 50, 20);

        jLabel72.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel72.setText("Retraksi :");
        jLabel72.setName("jLabel72"); // NOI18N
        FormInput.add(jLabel72);
        jLabel72.setBounds(460, 620, 50, 16);

        jLabel73.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel73.setText("Frekuensi Napas : ");
        jLabel73.setName("jLabel73"); // NOI18N
        FormInput.add(jLabel73);
        jLabel73.setBounds(460, 600, 90, 16);

        jLabel74.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel74.setText("Air Entry :");
        jLabel74.setName("jLabel74"); // NOI18N
        FormInput.add(jLabel74);
        jLabel74.setBounds(460, 660, 50, 20);

        pBcb.setName("pBcb"); // NOI18N
        pBcb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pBcbActionPerformed(evt);
            }
        });
        FormInput.add(pBcb);
        pBcb.setBounds(350, 590, 50, 20);

        pNf.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        pNf.setName("pNf"); // NOI18N
        FormInput.add(pNf);
        pNf.setBounds(570, 600, 50, 20);

        pNr.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        pNr.setName("pNr"); // NOI18N
        FormInput.add(pNr);
        pNr.setBounds(570, 620, 50, 20);

        pNs.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        pNs.setName("pNs"); // NOI18N
        FormInput.add(pNs);
        pNs.setBounds(570, 640, 50, 20);

        pNa.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        pNa.setName("pNa"); // NOI18N
        FormInput.add(pNa);
        pNa.setBounds(570, 660, 50, 20);

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        lblpmnilai.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblpmnilai.setName("lblpmnilai"); // NOI18N
        jScrollPane7.setViewportView(lblpmnilai);

        FormInput.add(jScrollPane7);
        jScrollPane7.setBounds(460, 740, 170, 80);

        btnsimpan6.setText("submit");
        btnsimpan6.setName("btnsimpan6"); // NOI18N
        btnsimpan6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpan6ActionPerformed(evt);
            }
        });
        FormInput.add(btnsimpan6);
        btnsimpan6.setBounds(250, 763, 90, 20);

        btnsimpan7.setText("submit");
        btnsimpan7.setName("btnsimpan7"); // NOI18N
        btnsimpan7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpan7ActionPerformed(evt);
            }
        });
        FormInput.add(btnsimpan7);
        btnsimpan7.setBounds(500, 710, 100, 22);

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        lblsir.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblsir.setName("lblsir"); // NOI18N
        jScrollPane8.setViewportView(lblsir);

        FormInput.add(jScrollPane8);
        jScrollPane8.setBounds(450, 480, 180, 70);

        BtnEdit2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit2.setMnemonic('G');
        BtnEdit2.setText("MARKING LOKALIS");
        BtnEdit2.setToolTipText("Alt+G");
        BtnEdit2.setName("BtnEdit2"); // NOI18N
        BtnEdit2.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEdit2ActionPerformed(evt);
            }
        });
        BtnEdit2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEdit2KeyPressed(evt);
            }
        });
        FormInput.add(BtnEdit2);
        BtnEdit2.setBounds(720, 1230, 180, 30);

        PanelWall.setBackground(new java.awt.Color(29, 29, 29));
        PanelWall.setBackgroundImage(new javax.swing.ImageIcon(getClass().getResource("/picture/semua2.png"))); // NOI18N
        PanelWall.setBackgroundImageType(usu.widget.constan.BackgroundConstan.BACKGROUND_IMAGE_STRECT);
        PanelWall.setPreferredSize(new java.awt.Dimension(200, 200));
        PanelWall.setRound(false);
        PanelWall.setWarna(new java.awt.Color(110, 110, 110));
        PanelWall.setLayout(null);
        FormInput.add(PanelWall);
        PanelWall.setBounds(60, 1290, 791, 260);

        eTa.setBackground(new java.awt.Color(255, 255, 255));
        eTa.setText("Tidak Ada");
        eTa.setName("eTa"); // NOI18N
        FormInput.add(eTa);
        eTa.setBounds(770, 560, 130, 20);

        jn13.setBackground(new java.awt.Color(255, 255, 255));
        jn13.setText("ronkhi");
        jn13.setName("jn13"); // NOI18N
        FormInput.add(jn13);
        jn13.setBounds(40, 300, 110, 20);

        jLabel104.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel104.setText("V. DIAGNOSIS/ASESMEN");
        jLabel104.setName("jLabel104"); // NOI18N
        FormInput.add(jLabel104);
        jLabel104.setBounds(10, 1780, 190, 23);

        indrnpya.setBackground(new java.awt.Color(255, 255, 255));
        indrnpya.setText("YA");
        indrnpya.setName("indrnpya"); // NOI18N
        FormInput.add(indrnpya);
        indrnpya.setBounds(250, 1800, 90, 20);

        indrnpt.setBackground(new java.awt.Color(255, 255, 255));
        indrnpt.setText("TIDAK");
        indrnpt.setName("indrnpt"); // NOI18N
        FormInput.add(indrnpt);
        indrnpt.setBounds(360, 1800, 90, 20);

        jLabel63.setText("Riwayat Penyakit Dahulu :");
        jLabel63.setName("jLabel63"); // NOI18N
        FormInput.add(jLabel63);
        jLabel63.setBounds(440, 940, 150, 23);

        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel67.setText("Jika YA, ");
        jLabel67.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel67.setName("jLabel67"); // NOI18N
        FormInput.add(jLabel67);
        jLabel67.setBounds(250, 1820, 80, 23);

        sAh2.setBackground(new java.awt.Color(255, 255, 255));
        sAh2.setText("YA");
        sAh2.setName("sAh2"); // NOI18N
        FormInput.add(sAh2);
        sAh2.setBounds(270, 1800, 90, 20);

        prev.setBackground(new java.awt.Color(255, 255, 255));
        prev.setText("Preventif");
        prev.setName("prev"); // NOI18N
        FormInput.add(prev);
        prev.setBounds(250, 1860, 90, 20);

        pal.setBackground(new java.awt.Color(255, 255, 255));
        pal.setText("Paliatif");
        pal.setName("pal"); // NOI18N
        FormInput.add(pal);
        pal.setBounds(580, 1860, 80, 20);

        kur.setBackground(new java.awt.Color(255, 255, 255));
        kur.setText("Kuratif");
        kur.setName("kur"); // NOI18N
        FormInput.add(kur);
        kur.setBounds(350, 1860, 90, 20);

        rehab.setBackground(new java.awt.Color(255, 255, 255));
        rehab.setText("Rehabilitatif");
        rehab.setName("rehab"); // NOI18N
        FormInput.add(rehab);
        rehab.setBounds(460, 1860, 120, 20);

        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel75.setText("Kebutuhan Pelayanan :");
        jLabel75.setName("jLabel75"); // NOI18N
        FormInput.add(jLabel75);
        jLabel75.setBounds(250, 1840, 180, 23);

        rip.setBackground(new java.awt.Color(255, 255, 255));
        rip.setText("R.Isolasi (+)");
        rip.setName("rip"); // NOI18N
        FormInput.add(rip);
        rip.setBounds(350, 1900, 110, 20);

        rb.setBackground(new java.awt.Color(255, 255, 255));
        rb.setText("R.Biasa");
        rb.setName("rb"); // NOI18N
        FormInput.add(rb);
        rb.setBounds(250, 1900, 90, 20);

        rint.setBackground(new java.awt.Color(255, 255, 255));
        rint.setText("R. Intensif ");
        rint.setName("rint"); // NOI18N
        FormInput.add(rint);
        rint.setBounds(570, 1900, 90, 20);

        ripn.setBackground(new java.awt.Color(255, 255, 255));
        ripn.setText("R.Isolasi (-)");
        ripn.setName("ripn"); // NOI18N
        FormInput.add(ripn);
        ripn.setBounds(460, 1900, 110, 20);

        btnsimpan8.setText("submit");
        btnsimpan8.setName("btnsimpan8"); // NOI18N
        btnsimpan8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpan8ActionPerformed(evt);
            }
        });
        FormInput.add(btnsimpan8);
        btnsimpan8.setBounds(480, 1820, 100, 22);

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        lblindikasiranap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblindikasiranap.setName("lblindikasiranap"); // NOI18N
        jScrollPane9.setViewportView(lblindikasiranap);

        FormInput.add(jScrollPane9);
        jScrollPane9.setBounds(660, 1800, 240, 120);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Input Penilaian", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tgl.Asuhan :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24-10-2024" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24-10-2024" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(195, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
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
        jLabel7.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(LCount);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        TabRawat.addTab("Data Penilaian", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRM.getText().trim().equals("")) {
    Valid.textKosong(TNoRw,"Nama Pasien");
} else if(NmDokter.getText().trim().equals("")) {
    Valid.textKosong(BtnDokter,"Dokter");
} else if(KeluhanUtama.getText().trim().equals("")) {
    Valid.textKosong(KeluhanUtama,"Keluhan Utama");
} else if(RPS.getText().trim().equals("")) {
    Valid.textKosong(RPS,"Riwayat Penyakit Sekarang");
} else if(RPK.getText().trim().equals("")) {
    Valid.textKosong(RPK,"Riwayat Penyakit Keluarga");
} else if(RPD.getText().trim().equals("")) {
    Valid.textKosong(RPD,"Riwayat Penyakit Dahulu");
} else if(RPO.getText().trim().equals("")) {
    Valid.textKosong(RPO,"Riwayat Penggunaan obat");
} else {
    // Deklarasi dan inisialisasi variabel untuk data penilaian medis IGD primer
    JTextField int_ = new JTextField();
    JTextField jalan_nafas = new JTextField();
    JTextField pernapasan = new JTextField();
    JTextField sirkulasi = new JTextField();
    JTextField disabilitas = new JTextField();
    JTextField penilaian_bbl = new JTextField();
    JTextField nilai_periksa = new JTextField();
    JTextField eksposur = new JTextField();
    JTextField indikasiRanap = new JTextField();
    String nilaiLblIP = lblIP.getText().replaceAll("\\<.*?\\>", "");
    String nilailblJalanNapas = lblJalanNapas.getText().replaceAll("\\<.*?\\>", "");
    String nilailblPern = lblPern.getText().replaceAll("\\<.*?\\>", "");
    String nilailblsir = lblsir.getText().replaceAll("\\<.*?\\>", "");
    String nilailbldis = lbldis.getText().replaceAll("\\<.*?\\>", "");
    String nilailblBBL = lblBBL.getText().replaceAll("\\<.*?\\>", "");
    String nilailblpmnilai = lblpmnilai.getText().replaceAll("\\<.*?\\>", "");
    String nilailbleks = lbleks.getText().replaceAll("\\<.*?\\>", "");
    String nilailblindikasiranap = lblindikasiranap.getText().replaceAll("\\<.*?\\>", "");
    
    
    // Simpan data ke tabel penilaian_medis_igd
    if (Sequel.menyimpantf("penilaian_medis_igd", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
        "No.Rawat", 36, new String[]{
            TNoRw.getText(), Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19), KdDokter.getText(), Anamnesis.getSelectedItem().toString(), Hubungan.getText(),
            KeluhanUtama.getText(), RPS.getText(), RPD.getText(), RPK.getText(), RPO.getText(), Alergi.getText(), Keadaan.getSelectedItem().toString(), GCS.getText(), Kesadaran.getSelectedItem().toString(), TD.getText(),
            Nadi.getText(), RR.getText(), Suhu.getText(), SPO.getText(), BB.getText(), TB.getText(), Kepala.getSelectedItem().toString(), Mata.getSelectedItem().toString(), Gigi.getSelectedItem().toString(), Leher.getSelectedItem().toString(),
            Thoraks.getSelectedItem().toString(), Abdomen.getSelectedItem().toString(), Genital.getSelectedItem().toString(), Ekstremitas.getSelectedItem().toString(), KetFisik.getText(), KetLokalis.getText(), EKG.getText(),
            Radiologi.getText(), Laborat.getText(), Diagnosis.getText(), Tatalaksana.getText()
        })) {
        // Simpan data ke tabel penilaian_medis_igd_primer
        if (Sequel.menyimpantf("penilaian_medis_igd_primer", "?,?,?,?,?,?,?,?,?,?", "No.Rawat", 10, new String[]{
            TNoRw.getText(), lblIP.getText().replaceAll("\\<.*?\\>", ""), lblJalanNapas.getText().replaceAll("\\<.*?\\>",""), lblPern.getText().replaceAll("\\<.*?\\>",""), lblsir.getText().replaceAll("\\<.*?\\>",""), lbldis.getText().replaceAll("\\<.*?\\>",""), lblBBL.getText().replaceAll("\\<.*?\\>",""), lblpmnilai.getText().replaceAll("\\<.*?\\>",""), lbleks.getText().replaceAll("\\<.*?\\>",""),lblindikasiranap.getText().replaceAll("\\<.*?\\>","")
        })) {
            
            {
             // Jika penyimpanan berhasil, tambahkan baris baru ke tabel
//            tabMode.addRow(new String[]{
//                TNoRw.getText(), lblIP.getText().replaceAll("\\<.*?\\>", ""), lblJalanNapas.getText().replaceAll("\\<.*?\\>",""), lblPern.getText().replaceAll("\\<.*?\\>",""), lblsir.getText().replaceAll("\\<.*?\\>",""),
//                lbldis.getText().replaceAll("\\<.*?\\>",""), lblBBL.getText().replaceAll("\\<.*?\\>",""), lblpmnilai.getText().replaceAll("\\<.*?\\>",""), lbleks.getText().replaceAll("\\<.*?\\>","")
//            });
            //LCount.setText("" + tabMode.getRowCount());
            emptTeks1();
            }
            
            // Jika kedua penyimpanan berhasil, tambahkan baris ke tabel dan lakukan operasi lain yang diperlukan
            tabMode.addRow(new String[]{
                TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TglLahir.getText(), Jk.getText(), KdDokter.getText(), NmDokter.getText(), Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19),
                Anamnesis.getSelectedItem().toString(), Hubungan.getText(), KeluhanUtama.getText(), RPS.getText(), RPD.getText(), RPK.getText(), RPO.getText(), Alergi.getText(), Keadaan.getSelectedItem().toString(), GCS.getText(), Kesadaran.getSelectedItem().toString(),
                TD.getText(), Nadi.getText(), RR.getText(), Suhu.getText(), SPO.getText(), BB.getText(), TB.getText(), Kepala.getSelectedItem().toString(), Mata.getSelectedItem().toString(), Gigi.getSelectedItem().toString(), Leher.getSelectedItem().toString(),
                Thoraks.getSelectedItem().toString(), Abdomen.getSelectedItem().toString(), Genital.getSelectedItem().toString(), Ekstremitas.getSelectedItem().toString(), KetFisik.getText(), KetLokalis.getText(), EKG.getText(), Radiologi.getText(), Laborat.getText(),
                Diagnosis.getText(), Tatalaksana.getText(), lblIP.getText().replaceAll("\\<.*?\\>", ""), lblJalanNapas.getText().replaceAll("\\<.*?\\>",""), lblPern.getText().replaceAll("\\<.*?\\>",""), lblsir.getText().replaceAll("\\<.*?\\>",""), lbldis.getText().replaceAll("\\<.*?\\>",""), lblBBL.getText().replaceAll("\\<.*?\\>",""), lblpmnilai.getText().replaceAll("\\<.*?\\>",""), lbleks.getText().replaceAll("\\<.*?\\>",""),lblindikasiranap.getText().replaceAll("\\<.*?\\>",""),
                "Sudah isi AIGD" // Status baru
            });
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
            
            // Simpan status "Sudah AIGD" ke tabel status_medis_igd
            String noRawat = TNoRw.getText(); // Contoh mendapatkan nomor rawat dari inputan TNoRw (disesuaikan dengan implementasi Anda)
            String status = "Sudah AIGD"; // Status yang akan disimpan
            String query = "INSERT INTO status_medis_igd (no_rawat, tanggal_input, status) VALUES (?, NOW(), ?)";

            try (PreparedStatement statement = koneksi.prepareStatement(query)) {
                statement.setString(1, noRawat);
                statement.setString(2, status);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Status 'Sudah AIGD' berhasil disimpan untuk nomor rawat: " + noRawat);
                }
            } catch (Exception e) {
                System.out.println("Gagal menyimpan status ke status_medis_igd: " + e.getMessage());
            }
        } else {
            // Penyimpanan ke tabel penilaian_medis_igd_primer gagal, lakukan penanganan kesalahan
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data ke tabel penilaian_medis_igd_primer");
        }
    } else {
        // Penyimpanan ke tabel penilaian_medis_igd gagal, lakukan penanganan kesalahan
        JOptionPane.showMessageDialog(null, "Gagal menyimpan data ke tabel penilaian_medis_igd");
    }
}


}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,KetFisik,BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbObat.getSelectedRow()>-1){
            if(akses.getkode().equals("Admin Utama")){
                hapus();
            }else{
                if(KdDokter.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString())){
                    hapus();
                }else{
                    JOptionPane.showMessageDialog(null,"Hanya bisa dihapus oleh dokter yang bersangkutan..!!");
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
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
        if(TNoRM.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"Nama Pasien");
        }else if(NmDokter.getText().trim().equals("")){
            Valid.textKosong(BtnDokter,"Dokter");
        }else if(KeluhanUtama.getText().trim().equals("")){
            Valid.textKosong(KeluhanUtama,"Keluhan Utama");
        }else if(RPS.getText().trim().equals("")){
            Valid.textKosong(RPS,"Riwayat Penyakit Sekarang");
        }else if(RPK.getText().trim().equals("")){
            Valid.textKosong(RPK,"Riwayat Penyakit Keluarga");
        }else if(RPD.getText().trim().equals("")){
            Valid.textKosong(RPD,"Riwayat Penyakit Dahulu");
        }else if(RPO.getText().trim().equals("")){
            Valid.textKosong(RPO,"Riwayat Pengunaan obat");
        }else{
            if(tbObat.getSelectedRow()>-1){
                if(akses.getkode().equals("Admin Utama")){
                    ganti();
                }else{
                    if(KdDokter.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString())){
                        ganti();
                    }else{
                        JOptionPane.showMessageDialog(null,"Hanya bisa diganti oleh dokter yang bersangkutan..!!");
                    }
                }
            }else{
                JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
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
            BtnKeluarActionPerformed(null);
        }else{Valid.pindah(evt,BtnEdit,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            try{
                htmlContent = new StringBuilder();
                htmlContent.append(                             
                    "<tr class='isi'>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='105px'><b>No.Rawat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='70px'><b>No.RM</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='150px'><b>Nama Pasien</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='65px'><b>Tgl.Lahir</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='55px'><b>J.K.</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>NIP</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='150px'><b>Nama Dokter</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='115px'><b>Tanggal</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>Anamnesis</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='100px'><b>Hubungan</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='300px'><b>Keluhan Utama</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='150px'><b>Riwayat Penyakit Sekarang</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='150px'><b>Riwayat Penyakit Dahulu</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='150px'><b>Riwayat Penyakit Keluarga</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='150px'><b>Riwayat Penggunakan Obat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='120px'><b>Riwayat Alergi</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='90px'><b>Keadaan Umum</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='50px'><b>GCS</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>Kesadaran</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='60px'><b>TD(mmHg)</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='75px'><b>Nadi(x/menit)</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='67px'><b>RR(x/menit)</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='40px'><b>Suhu</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='40px'><b>SpO2</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='40px'><b>BB(Kg)</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='40px'><b>TB(cm)</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>Kepala</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>Mata</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>Gigi & Mulut</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>Leher</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>Thoraks</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>Abdomen</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>Genital & Anus</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='80px'><b>Ekstremitas</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='300px'><b>Ket.Pemeriksaan Fisik</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='200px'><b>Ket.Status Lokalis</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='170px'><b>EKG</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='170px'><b>Radiologi</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='170px'><b>Laborat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='150px'><b>Diagnosis/Asesmen</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center' width='300px'><b>Tatalaksana</b></td>"+
                    "</tr>"
                );
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                        "<tr class='isi'>"+
                           "<td valign='top'>"+tbObat.getValueAt(i,0).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,2).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,3).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,4).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,5).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,6).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,7).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,8).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,9).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,10).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,11).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,12).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,13).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,14).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,15).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,16).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,17).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,18).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,19).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,20).toString()+"</td>"+ 
                            "<td valign='top'>"+tbObat.getValueAt(i,21).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,22).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,23).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,24).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,25).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,26).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,27).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,28).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,29).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,30).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,31).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,32).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,33).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,34).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,35).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,36).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,37).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,38).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,39).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,40).toString()+"</td>"+ 
                        "</tr>");
                }
                LoadHTML.setText(
                    "<html>"+
                      "<table width='4600px' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
                       htmlContent.toString()+
                      "</table>"+
                    "</html>"
                );

                File g = new File("file2.css");            
                BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(
                    ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                    ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                    ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                    ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                    ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                    ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
                );
                bg.close();

                File f = new File("DataPenilaianAwalMedisRanap.html");            
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));            
                bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                            "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                            "<table width='4600px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                "<tr class='isi2'>"+
                                    "<td valign='top' align='center'>"+
                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                        "<font size='2' face='Tahoma'>DATA PENILAIAN AWAL MEDIS IGD<br><br></font>"+        
                                    "</td>"+
                               "</tr>"+
                            "</table>")
                );
                bw.close();                         
                Desktop.getDesktop().browse(f.toURI());

            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
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
            Valid.pindah(evt, BtnCari, TPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
            if((evt.getClickCount()==2)&&(tbObat.getSelectedColumn()==0)){
                TabRawat.setSelectedIndex(0);
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_SPACE){
                try {
                    getData();
                    TabRawat.setSelectedIndex(0);
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void MnPenilaianMedisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPenilaianMedisActionPerformed
        if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());          
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            param.put("url",Sequel.cariIsi("select url_image from asesmen_medis_igd_image_marking where no_rawat=?",TNoRw.getText()));                   
            param.put("lokalis","http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/imagefreehand/");
            finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),5).toString():finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString())); 
            
            Valid.MyReportqry("rptCetakPenilaianAwalMedisIGD.jasper","report","::[ Laporan Penilaian Awal Medis IGD ]::",
                "select reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, if(pasien.jk='L','Laki-Laki','Perempuan') as jk, pasien.tgl_lahir, penilaian_medis_igd.tanggal," +
                "penilaian_medis_igd.kd_dokter, penilaian_medis_igd.anamnesis, penilaian_medis_igd.hubungan, penilaian_medis_igd.keluhan_utama, penilaian_medis_igd.rps, penilaian_medis_igd.rpk, penilaian_medis_igd.rpd, penilaian_medis_igd.rpo, penilaian_medis_igd.alergi," +
                "penilaian_medis_igd.keadaan, penilaian_medis_igd.gcs, penilaian_medis_igd.kesadaran, penilaian_medis_igd.td, penilaian_medis_igd.nadi, penilaian_medis_igd.rr, penilaian_medis_igd.suhu, penilaian_medis_igd.spo, penilaian_medis_igd.bb, penilaian_medis_igd.tb," +
                "penilaian_medis_igd.kepala, penilaian_medis_igd.mata, penilaian_medis_igd.gigi, penilaian_medis_igd.leher, penilaian_medis_igd.thoraks, penilaian_medis_igd.abdomen, penilaian_medis_igd.ekstremitas, penilaian_medis_igd.genital, penilaian_medis_igd.ket_fisik," +
                "penilaian_medis_igd.ket_lokalis, penilaian_medis_igd.ekg, penilaian_medis_igd.rad, penilaian_medis_igd.lab, penilaian_medis_igd.diagnosis, penilaian_medis_igd.tata, dokter.nm_dokter," +
                "penilaian_medis_igd_primer.int_prehospital, penilaian_medis_igd_primer.jalan_nafas, penilaian_medis_igd_primer.pernapasan, penilaian_medis_igd_primer.sirkulasi, penilaian_medis_igd_primer.disabilitas," +
                "penilaian_medis_igd_primer.penilaian_bbl, penilaian_medis_igd_primer.nilai_periksa, penilaian_medis_igd_primer.eksposur, penilaian_medis_igd_primer.ind_ranap " +
                "from reg_periksa " +
                "inner join pasien on reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                "inner join penilaian_medis_igd on reg_periksa.no_rawat = penilaian_medis_igd.no_rawat " +
                "inner join dokter on penilaian_medis_igd.kd_dokter = dokter.kd_dokter " +
                "inner join penilaian_medis_igd_primer on penilaian_medis_igd.no_rawat = penilaian_medis_igd_primer.no_rawat " +
                "where penilaian_medis_igd.no_rawat = '" + tbObat.getValueAt(tbObat.getSelectedRow(),0).toString() + "'", param);

        }
    }//GEN-LAST:event_MnPenilaianMedisActionPerformed

    private void jn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jn2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jn2ActionPerformed

    private void btnsimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpanActionPerformed
        // create multi line Jlabel
        String result= "<html><body style='width: 250px;'>";
        //ambil value(nilai) dari cekbox
        if (jn1.isSelected()){
            result +="Paten, " ;
        }

        if (jn2.isSelected()){
            result +=" Obstruksi Partial:stridor , " ;
        }

        if (jn3.isSelected()){
            result +=" Obstruksi Partial:snoring , " ;
        }

        if (jn4.isSelected()){
            result +=" Obstruksi Partial:gurgling , " ;
        }

        if (jn5.isSelected()){
            result +=" Obstruksi Partial:wheezing , " ;
        }
        if (jn6.isSelected()){
            result +="Obstruksi total, " ;
        }

        if (jn7.isSelected()){
            result +="Trauma jalan nafas:fasial, " ;
        }

        if (jn8.isSelected()){
            result +="Trauma jalan nafas:leher, " ;
        }

        if (jn9.isSelected()){
            result +="Trauma jalan nafas:inhalasi*, " ;
        }

        if (jn10.isSelected()){
            result +="Risiko aspirasi:perdarahan, " ;
        }

        if (jn11.isSelected()){
            result +="Risiko aspirasi:muntahan*, " ;
        }
        
        if (jn13.isSelected()){
            result +="Obstruksi Partial:ronkhi, " ;
        }

        String input = jn12.getText();
        if (!input.isEmpty()) {
            result += input + ", ";
        }

        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2); // Menghapus 2 karakter terakhir (koma dan spasi)
        }
        result +="</body></html>";

        lblJalanNapas.setText(result);
    }//GEN-LAST:event_btnsimpanActionPerformed

    private void KetLokalisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KetLokalisKeyPressed
        Valid.pindah2(evt,KetFisik,EKG);
    }//GEN-LAST:event_KetLokalisKeyPressed

    private void DiagnosisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosisKeyPressed
        Valid.pindah2(evt,Laborat,Tatalaksana);
    }//GEN-LAST:event_DiagnosisKeyPressed

    private void TatalaksanaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TatalaksanaKeyPressed
        Valid.pindah2(evt,Diagnosis,BtnSimpan);
    }//GEN-LAST:event_TatalaksanaKeyPressed

    private void LaboratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LaboratKeyPressed
        Valid.pindah2(evt,Radiologi,Diagnosis);
    }//GEN-LAST:event_LaboratKeyPressed

    private void RadiologiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RadiologiKeyPressed
        Valid.pindah2(evt,EKG,Laborat);
    }//GEN-LAST:event_RadiologiKeyPressed

    private void MataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MataKeyPressed
        Valid.pindah(evt,Kepala,Gigi);
    }//GEN-LAST:event_MataKeyPressed

    private void TglAsuhanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglAsuhanKeyPressed
        Valid.pindah(evt,Tatalaksana,Anamnesis);
    }//GEN-LAST:event_TglAsuhanKeyPressed

    private void EKGKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EKGKeyPressed
        Valid.pindah2(evt,KetLokalis,Radiologi);
    }//GEN-LAST:event_EKGKeyPressed

    private void EkstremitasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EkstremitasKeyPressed
        Valid.pindah(evt,Genital,KetFisik);
    }//GEN-LAST:event_EkstremitasKeyPressed

    private void GenitalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GenitalKeyPressed
        Valid.pindah(evt,Abdomen,Ekstremitas);
    }//GEN-LAST:event_GenitalKeyPressed

    private void AbdomenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AbdomenKeyPressed
        Valid.pindah(evt,Thoraks,Genital);
    }//GEN-LAST:event_AbdomenKeyPressed

    private void LeherKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LeherKeyPressed
        Valid.pindah(evt,Gigi,Thoraks);
    }//GEN-LAST:event_LeherKeyPressed

    private void GigiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GigiKeyPressed
        Valid.pindah(evt,Mata,Leher);
    }//GEN-LAST:event_GigiKeyPressed

    private void KepalaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KepalaKeyPressed
        Valid.pindah(evt,SPO,Mata);
    }//GEN-LAST:event_KepalaKeyPressed

    private void SPOKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SPOKeyPressed
        Valid.pindah(evt,Suhu,Kepala);
    }//GEN-LAST:event_SPOKeyPressed

    private void KesadaranKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KesadaranKeyPressed
        Valid.pindah(evt,Keadaan,GCS);
    }//GEN-LAST:event_KesadaranKeyPressed

    private void KeadaanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeadaanKeyPressed
        Valid.pindah(evt,Alergi,Kesadaran);
    }//GEN-LAST:event_KeadaanKeyPressed

    private void RPSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RPSKeyPressed
        Valid.pindah2(evt,KeluhanUtama,RPK);
    }//GEN-LAST:event_RPSKeyPressed

    private void HubunganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HubunganKeyPressed
        Valid.pindah(evt,Anamnesis,KeluhanUtama);
    }//GEN-LAST:event_HubunganKeyPressed

    private void GCSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GCSKeyPressed
        Valid.pindah(evt,Kesadaran,TB);
    }//GEN-LAST:event_GCSKeyPressed

    private void GCSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GCSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_GCSActionPerformed

    private void KetFisikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KetFisikKeyPressed
        Valid.pindah2(evt,Ekstremitas,KetLokalis);
    }//GEN-LAST:event_KetFisikKeyPressed

    private void RPOKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RPOKeyPressed
        Valid.pindah2(evt,RPD,Alergi);
    }//GEN-LAST:event_RPOKeyPressed

    private void RPKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RPKKeyPressed
        Valid.pindah2(evt,RPS,RPD);
    }//GEN-LAST:event_RPKKeyPressed

    private void RPDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RPDKeyPressed
        Valid.pindah2(evt,RPK,RPO);
    }//GEN-LAST:event_RPDKeyPressed

    private void KeluhanUtamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeluhanUtamaKeyPressed
        Valid.pindah2(evt,Hubungan,RPS);
    }//GEN-LAST:event_KeluhanUtamaKeyPressed

    private void AnamnesisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AnamnesisKeyPressed
        Valid.pindah(evt,TglAsuhan,Hubungan);
    }//GEN-LAST:event_AnamnesisKeyPressed

    private void AlergiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AlergiKeyPressed
        Valid.pindah(evt,RPO,Keadaan);
    }//GEN-LAST:event_AlergiKeyPressed

    private void RRKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RRKeyPressed
        Valid.pindah(evt,Nadi,Suhu);
    }//GEN-LAST:event_RRKeyPressed

    private void TDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TDKeyPressed
        Valid.pindah(evt,BB,Nadi);
    }//GEN-LAST:event_TDKeyPressed

    private void SuhuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuhuKeyPressed
        Valid.pindah(evt,RR,SPO);
    }//GEN-LAST:event_SuhuKeyPressed

    private void NadiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NadiKeyPressed
        Valid.pindah(evt,TD,RR);
    }//GEN-LAST:event_NadiKeyPressed

    private void TBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TBKeyPressed
        Valid.pindah(evt,GCS,BB);
    }//GEN-LAST:event_TBKeyPressed

    private void BBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BBKeyPressed
        Valid.pindah(evt,TB,TD);
    }//GEN-LAST:event_BBKeyPressed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        //Valid.pindah(evt,Monitoring,BtnSimpan);
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void KdDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokterKeyPressed

    }//GEN-LAST:event_KdDokterKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isRawat();
        }else{
            Valid.pindah(evt,TCari,BtnDokter);
        }
    }//GEN-LAST:event_TNoRwKeyPressed

    private void btnsimpan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpan1ActionPerformed
        // create multi line Jlabel
        String result= "<html><body style='width: 250px;'>";
        //ambil value(nilai) dari cekbox
        if (ipcc.isSelected()){
            result +="cerv. collar, " ;
        }

        if (iprjp.isSelected()){
            result +="RJP, " ;
        }

        if (ipdef.isSelected()){
            result +="defibrilasi, " ;
        }
        
        if (ipint.isSelected()){
            result +="intubasi, " ;
        }

        if (ipdcp.isSelected()){
            result +="Dec. jarum, WSD*, " ;
        }

        if (ipvtp.isSelected()){
            result +="VTP , " ;
        }
        if (ipbal.isSelected()){
            result +="Balut/Bidai*, " ;
        }

        if (ipngt.isSelected()){
            result +="NGT, " ;
        }

        if (ipinf.isSelected()){
            result +="Infus, " ;
        }
        
        if (ipktr.isSelected()){
            result +="kateter urin, " ;
        }

        if (ipnn.isSelected()){
            result +="Tidak ada, " ;
        }

        
        String input = ipob.getText();
            if (!input.isEmpty()) {
            result += "Obat: " + input + ", ";
                 }

        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2); // Menghapus 2 karakter terakhir (koma dan spasi)
        }
        result +="</body></html>";

        lblIP.setText(result);
    
    }//GEN-LAST:event_btnsimpan1ActionPerformed

    private void ipobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipobActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ipobActionPerformed

    private void tpnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tpnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tpnActionPerformed

    private void tpkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tpkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tpkActionPerformed

    private void btnsimpan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpan2ActionPerformed
        // create multi line Jlabel
        String result= "<html><body style='width: 250px;'>";
        //ambil value(nilai) dari cekbox
        if (psp.isSelected()){
            result +="pernapasan spontan, " ;
        }

        if (pts.isSelected()){
            result +="pernapasan tidak spontan, " ;
        }

        if (preg.isSelected()){
            result +="reguler, " ;
        }
        
        if (pireg.isSelected()){
            result +="irreguler, " ;
        }

        if (gsim.isSelected()){
            result +="gerakan simetris, " ;
        }

        if (gasim.isSelected()){
            result +="gerakan asimetris, " ;
        }
        if (gkan.isSelected()){
            result +="jejas dinding dada kanan, " ;
        }

        if (gkir.isSelected()){
            result +="jejas dinding dada kiri*, " ;
        }

        if (tpn.isSelected()){
            result +="tipe pernapasan normal, " ;
        }
        
        if (tpk.isSelected()){
            result +="tipe pernapasan kussmaul, " ;
        }

        if (tpb.isSelected()){
            result +="tipe pernapasan biot, " ;
        }
        
        if (tpb.isSelected()){
            result +="tipe pernapasan biot, " ;
        }

        if (tpa.isSelected()){
            result +="tipe pernapasan apneustic, " ;
        }
        
        if (tpr.isSelected()){
            result +="tipe pernapasan reaktif, " ;
        }
        
        if (tph.isSelected()){
            result +="tipe pernapasan hiperventilasi, " ;
        }
        
        if (tpc.isSelected()){
            result +="tipe pernapasan cheyne stoke, " ;
        }
        
        if (tpf.isSelected()){
            result +="tipe pernapasan flare, " ;
        }
   

        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2); // Menghapus 2 karakter terakhir (koma dan spasi)
        }
        result += "</body></html>";

        lblPern.setText(result);
    
    
    }//GEN-LAST:event_btnsimpan2ActionPerformed

    private void sirCRTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sirCRTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sirCRTActionPerformed

    private void btnsimpan3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpan3ActionPerformed
          String result = "<html><body style='width: 250px;'>";
// Array yang berisi pasangan teks dan status isSelected() dari checkbox
                String[][] checkboxes = {
                    {"sirkulasi nadi reguler", String.valueOf(sNr.isSelected())},
                    {"sirkulasi nadi irreguler", String.valueOf(sNir.isSelected())},
                    {"sirkulasi nadi kuat", String.valueOf(sNk.isSelected())},
                    {"sirkulasi nadi lemah", String.valueOf(sNl.isSelected())},
                    {"kulit / mukosa normal", String.valueOf(sKn.isSelected())},
                    {"kulit / mukosa jaundice", String.valueOf(sKj.isSelected())},
                    {"kulit / mukosa berkeringat", String.valueOf(sKb.isSelected())},
                    {"kulit / mukosa pucat", String.valueOf(sKp.isSelected())},
                    {"kulit / mukosa cyanosis", String.valueOf(sKc.isSelected())},
                    {"akral hangat", String.valueOf(sAh.isSelected())},
                    {"akral dingin", String.valueOf(sAd.isSelected())},
                    {"akral kering", String.valueOf(sAk.isSelected())},
                    {"akral basah", String.valueOf(sAb.isSelected())},
                   };
                
                String input = sirCRT.getText();
                    if (!input.isEmpty()) {
                    // Mengganti karakter khusus dengan entitas HTML
                    input = input.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                    result += "CRT: " + input + ", ";
                }

// Menambahkan teks checkbox yang terpilih ke dalam result
                    for (String[] checkbox : checkboxes) {
                        if (Boolean.parseBoolean(checkbox[1])) {
                            result += checkbox[0] + ", ";
                        }
                    }

// Menghapus koma dan spasi terakhir jika ada
                if (result.endsWith(", ")) {
                    result = result.substring(0, result.length() - 2);
                }

                result += "</body></html>";

                lblsir.setText(result);

    }//GEN-LAST:event_btnsimpan3ActionPerformed

    private void btnsimpan4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpan4ActionPerformed
        // create multi line Jlabel
        String result= "<html><body style='width: 250px;'>";
        //ambil value(nilai) dari cekbox
        if (dpis.isSelected()){
            result +="pupil isokor, " ;
        }
        
        if (dpais.isSelected()){
            result +="pupil anisokor, " ;
        }
        
        String input1 = dpdi.getText();
        if (!input1.isEmpty()) {
        result += "Diameter Pupil: " + input1 + ", ";
        }
        
        String input2 = dprc.getText();
        if (!input2.isEmpty()) {
        result += "Reflek Cahaya: " + input2 + ", ";
        }
        
        String input3 = dpms.getText();
        if (!input3.isEmpty()) {
        result += "Meningeal signs: " + input3 + ", ";
        }
        
        if (dplatkir.isSelected()){
            result +="laterasi kiri, " ;
        }
        
        if (dplatkan.isSelected()){
            result +="laterasi kanan, " ;
        }
        
        if (dplattdk.isSelected()){
            result +="laterasi tidak ada, " ;
        }
        
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2); // Menghapus 2 karakter terakhir (koma dan spasi)
        }
        result +="</body></html>";

        lbldis.setText(result);
    
    }//GEN-LAST:event_btnsimpan4ActionPerformed

    private void btnsimpan5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpan5ActionPerformed
        String result = "<html><body style='width: 250px;'>";
// Array yang berisi pasangan teks dan status isSelected() dari checkbox
                String[][] checkboxes = {
                    {"Eksposur Deformitas", String.valueOf(eDe.isSelected())},
                    {"Eksposur Contusio", String.valueOf(eCon.isSelected())},
                    {"Eksposur Penetrasi", String.valueOf(ePe.isSelected())},
                    {"Eksposur Tenderness", String.valueOf(eTe.isSelected())},
                    {"Eksposur Swelling", String.valueOf(eSw.isSelected())},
                    {"Eksposur tidak tampak jelas", String.valueOf(eTtj.isSelected())},
                    {"Eksposur Ekskoriasi", String.valueOf(eAb.isSelected())},
                    {"Eksposur Abrasi", String.valueOf(eAb.isSelected())},
                    {"Eksposur Burn", String.valueOf(eBu.isSelected())},
                    {"Eksposur Laserasi", String.valueOf(eLas.isSelected())},
                    {"Eksposur Tidak Ada", String.valueOf(eTa.isSelected())},
                   };
                

// Menambahkan teks checkbox yang terpilih ke dalam result
                    for (String[] checkbox : checkboxes) {
                        if (Boolean.parseBoolean(checkbox[1])) {
                            result += checkbox[0] + ", ";
                        }
                    }

// Menghapus koma dan spasi terakhir jika ada
                if (result.endsWith(", ")) {
                    result = result.substring(0, result.length() - 2);
                }

                result += "</body></html>";

                lbleks.setText(result);

    }//GEN-LAST:event_btnsimpan5ActionPerformed

    private void ThoraksKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ThoraksKeyPressed
        Valid.pindah(evt,Leher,Abdomen);
    }//GEN-LAST:event_ThoraksKeyPressed

    private void btnsimpan6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpan6ActionPerformed
        // create multi line Jlabel
        String result= "<html><body style='width: 250px;'>";
        //ambil value(nilai) dari cekbox
      
        
        String input1 = pBcb.getText();
        if (!input1.isEmpty()) {
        result += "Cukup Bulan: " + input1 + ", ";
        }
        
        String input2 = pBca.getText();
        if (!input2.isEmpty()) {
        result += "Cairan amnion jernih: " + input2 + ", ";
        }
        
        String input3 = pBpm.getText();
        if (!input3.isEmpty()) {
        result += "Pernapasan / menangis: " + input3 + ", ";
        }
        
        String input4 = pBto.getText();
        if (!input4.isEmpty()) {
        result += "Tonus otot baik " + input4 + ", ";
        }
        
        String input5 = pBto.getText();
        if (!input5.isEmpty()) {
        result += "Tonus otot baik " + input5 + ", ";
        }
        
        String input8 = pSa.getText();
        if (!input8.isEmpty()) {
        result += "Skor APGAR " + input8 + ", ";
        }
        
        String input9 = pSd.getText();
        if (!input9.isEmpty()) {
        result += "Skor DOWNE " + input9 + ", ";
        }
        
        
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2); // Menghapus 2 karakter terakhir (koma dan spasi)
        }
        result +="</body></html>";

        lblBBL.setText(result);
    
    }//GEN-LAST:event_btnsimpan6ActionPerformed

    private void pBcbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pBcbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pBcbActionPerformed

    private void pBpmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pBpmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pBpmActionPerformed

    private void btnsimpan7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpan7ActionPerformed
        // create multi line Jlabel
        String result= "<html><body style='width: 250px;'>";
        //ambil value(nilai) dari cekbox
      
        
        String input1 = pNf.getText();
        if (!input1.isEmpty()) {
        result += "Frekuensi nafas: " + input1 + ", ";
        }
        
        String input2 = pNr.getText();
        if (!input2.isEmpty()) {
        result += "Retraksi: " + input2 + ", ";
        }
        
        String input3 = pNs.getText();
        if (!input3.isEmpty()) {
        result += "Sianosi: " + input3 + ", ";
        }
        
        String input4 = pNa.getText();
        if (!input4.isEmpty()) {
        result += "Air Entry: " + input4 + ", ";
        }
        
        String input5 = pNm.getText();
        if (!input5.isEmpty()) {
        result += "Merintih: " + input5 + ", ";
        }
        
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2); // Menghapus 2 karakter terakhir (koma dan spasi)
        }
        result +="</body></html>";

        lblpmnilai.setText(result);
    
    }//GEN-LAST:event_btnsimpan7ActionPerformed

    private void BtnEdit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEdit2ActionPerformed

        DlgMarkingImageAssMedisIGD form=new DlgMarkingImageAssMedisIGD(null,false);
        form.setNoRw(TNoRw.getText());
        form.setVisible(true);
        form.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                urlImage=Sequel.cariIsi("select url_image from asesmen_medis_igd_image_marking where no_rawat='"+TNoRw.getText()+"' ");
                imageAssesment("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/imagefreehand/"+urlImage+"");
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
    }//GEN-LAST:event_BtnEdit2ActionPerformed

    private void BtnEdit2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEdit2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnEdit2KeyPressed

    private void jn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jn1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jn1ActionPerformed

    private void btnsimpan8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpan8ActionPerformed
      String result = "<html><body style='width: 250px;'>";
        
            //ambil value(nilai) dari cekbox
            if (indrnpya.isSelected()){
                result += "Indikasi Rawat Inap : YA, <br>" ;
            }

            if (prev.isSelected()){
                result += "Kebutuhan Pelayanan : Preventif, <br>" ;
            }
            if (kur.isSelected()){
                result += "Kebutuhan Pelayanan : Kuratif, <br>" ;
            }
            if (rehab.isSelected()){
                result += "Kebutuhan Pelayanan : Rehabilitatif, <br>" ;
            }
            if (pal.isSelected()){
                result += "Kebutuhan Pelayanan : Paliatif, <br>" ;
            }

            if (rb.isSelected()){
                result += "Kebutuhan Ruangan : R. Biasa<br>" ;
            }
            if (rip.isSelected()){
                result += "Kebutuhan Ruangan : R. Isolasi(+)<br>" ;
            }
            if (ripn.isSelected()){
                result += "Kebutuhan Ruangan : R. Isolasi(-)<br>" ;
            }
            if (rint.isSelected()){
                result += "Kebutuhan Ruangan : R. Intensif<br>" ;
            }

            if (indrnpt.isSelected()){
                result += "Indikasi Rawat Inap : TIDAK<br>" ;
            }

            // Jika string diakhiri dengan ", " (tidak lagi diperlukan jika menggunakan <br>)
            if (result.endsWith("<br>")) {
                result = result.substring(0, result.length() - 4); // hapus <br> terakhir
            }

            result += "</body></html>";
            lblindikasiranap.setText(result);
    }//GEN-LAST:event_btnsimpan8ActionPerformed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        tampil();
    }//GEN-LAST:event_TabRawatMouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMPenilaianAwalMedisIGD dialog = new RMPenilaianAwalMedisIGD(new javax.swing.JFrame(), true);
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
    private widget.ComboBox Abdomen;
    private widget.TextBox Alergi;
    private widget.ComboBox Anamnesis;
    private widget.TextBox BB;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnEdit2;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.TextArea Diagnosis;
    private widget.TextArea EKG;
    private widget.ComboBox Ekstremitas;
    private widget.PanelBiasa FormInput;
    private widget.TextBox GCS;
    private widget.ComboBox Genital;
    private widget.ComboBox Gigi;
    private widget.TextBox Hubungan;
    private widget.TextBox Jk;
    private widget.TextBox KdDokter;
    private widget.ComboBox Keadaan;
    private widget.TextArea KeluhanUtama;
    private widget.ComboBox Kepala;
    private widget.ComboBox Kesadaran;
    private widget.TextArea KetFisik;
    private widget.TextArea KetLokalis;
    private widget.Label LCount;
    private widget.TextArea Laborat;
    private widget.ComboBox Leher;
    private widget.editorpane LoadHTML;
    private widget.ComboBox Mata;
    private javax.swing.JMenuItem MnPenilaianMedis;
    private widget.TextBox Nadi;
    private widget.TextBox NmDokter;
    private usu.widget.glass.PanelGlass PanelWall;
    private widget.TextArea RPD;
    private widget.TextArea RPK;
    private widget.TextArea RPO;
    private widget.TextArea RPS;
    private widget.TextBox RR;
    private widget.TextArea Radiologi;
    private widget.TextBox SPO;
    private widget.ScrollPane Scroll;
    private widget.TextBox Suhu;
    private widget.TextBox TB;
    private widget.TextBox TCari;
    private widget.TextBox TD;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private javax.swing.JTabbedPane TabRawat;
    private widget.TextArea Tatalaksana;
    private widget.Tanggal TglAsuhan;
    private widget.TextBox TglLahir;
    private widget.ComboBox Thoraks;
    private javax.swing.JButton btnsimpan;
    private javax.swing.JButton btnsimpan1;
    private javax.swing.JButton btnsimpan2;
    private javax.swing.JButton btnsimpan3;
    private javax.swing.JButton btnsimpan4;
    private javax.swing.JButton btnsimpan5;
    private javax.swing.JButton btnsimpan6;
    private javax.swing.JButton btnsimpan7;
    private javax.swing.JButton btnsimpan8;
    private javax.swing.JCheckBox dpais;
    private javax.swing.JTextField dpdi;
    private javax.swing.JCheckBox dpis;
    private javax.swing.JCheckBox dplatkan;
    private javax.swing.JCheckBox dplatkir;
    private javax.swing.JCheckBox dplattdk;
    private javax.swing.JTextField dpms;
    private javax.swing.JTextField dprc;
    private javax.swing.JCheckBox eAb;
    private javax.swing.JCheckBox eBu;
    private javax.swing.JCheckBox eCon;
    private javax.swing.JCheckBox eDe;
    private javax.swing.JCheckBox eKo;
    private javax.swing.JCheckBox eLas;
    private javax.swing.JCheckBox ePe;
    private javax.swing.JCheckBox eSw;
    private javax.swing.JCheckBox eTa;
    private javax.swing.JCheckBox eTe;
    private javax.swing.JCheckBox eTtj;
    private javax.swing.JCheckBox gasim;
    private javax.swing.JCheckBox gkan;
    private javax.swing.JCheckBox gkir;
    private javax.swing.JCheckBox gsim;
    private javax.swing.JCheckBox indrnpt;
    private javax.swing.JCheckBox indrnpya;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private javax.swing.JCheckBox ipbal;
    private javax.swing.JCheckBox ipcc;
    private javax.swing.JCheckBox ipdcp;
    private javax.swing.JCheckBox ipdef;
    private javax.swing.JCheckBox ipinf;
    private javax.swing.JCheckBox ipint;
    private javax.swing.JCheckBox ipktr;
    private javax.swing.JCheckBox ipngt;
    private javax.swing.JCheckBox ipnn;
    private javax.swing.JTextField ipob;
    private javax.swing.JCheckBox iprjp;
    private javax.swing.JCheckBox ipvtp;
    private javax.swing.JLabel jLabel1;
    private widget.Label jLabel10;
    private widget.Label jLabel100;
    private widget.Label jLabel101;
    private widget.Label jLabel102;
    private widget.Label jLabel103;
    private widget.Label jLabel104;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private javax.swing.JLabel jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private javax.swing.JLabel jLabel2;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private javax.swing.JLabel jLabel27;
    private widget.Label jLabel28;
    private widget.Label jLabel29;
    private javax.swing.JLabel jLabel3;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel33;
    private javax.swing.JLabel jLabel34;
    private widget.Label jLabel35;
    private javax.swing.JLabel jLabel36;
    private widget.Label jLabel37;
    private widget.Label jLabel38;
    private widget.Label jLabel39;
    private javax.swing.JLabel jLabel4;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel42;
    private javax.swing.JLabel jLabel43;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private widget.Label jLabel49;
    private javax.swing.JLabel jLabel5;
    private widget.Label jLabel50;
    private widget.Label jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private widget.Label jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private widget.Label jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private widget.Label jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private widget.Label jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private widget.Label jLabel75;
    private widget.Label jLabel79;
    private widget.Label jLabel8;
    private widget.Label jLabel80;
    private widget.Label jLabel81;
    private widget.Label jLabel82;
    private widget.Label jLabel9;
    private widget.Label jLabel94;
    private widget.Label jLabel99;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel jlabel;
    private javax.swing.JLabel jlabeldawne;
    private javax.swing.JCheckBox jn1;
    private javax.swing.JCheckBox jn10;
    private javax.swing.JCheckBox jn11;
    private javax.swing.JTextField jn12;
    private javax.swing.JCheckBox jn13;
    private javax.swing.JCheckBox jn2;
    private javax.swing.JCheckBox jn3;
    private javax.swing.JCheckBox jn4;
    private javax.swing.JCheckBox jn5;
    private javax.swing.JCheckBox jn6;
    private javax.swing.JCheckBox jn7;
    private javax.swing.JCheckBox jn8;
    private javax.swing.JCheckBox jn9;
    private javax.swing.JCheckBox kur;
    private javax.swing.JLabel label;
    private widget.Label label11;
    private widget.Label label14;
    private javax.swing.JLabel lblBBL;
    private javax.swing.JLabel lblIP;
    private javax.swing.JLabel lblJalanNapas;
    private javax.swing.JLabel lblPern;
    private javax.swing.JLabel lbldis;
    private javax.swing.JLabel lbleks;
    private javax.swing.JLabel lblindikasiranap;
    private javax.swing.JLabel lblpmnilai;
    private javax.swing.JLabel lblsir;
    private javax.swing.JTextField pBca;
    private javax.swing.JTextField pBcb;
    private javax.swing.JTextField pBpm;
    private javax.swing.JTextField pBto;
    private javax.swing.JTextField pNa;
    private javax.swing.JTextField pNf;
    private javax.swing.JTextField pNm;
    private javax.swing.JTextField pNr;
    private javax.swing.JTextField pNs;
    private javax.swing.JTextField pSa;
    private javax.swing.JTextField pSd;
    private javax.swing.JCheckBox pal;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JCheckBox pireg;
    private javax.swing.JCheckBox preg;
    private javax.swing.JCheckBox prev;
    private javax.swing.JCheckBox psp;
    private javax.swing.JCheckBox pts;
    private javax.swing.JCheckBox rb;
    private javax.swing.JCheckBox rehab;
    private javax.swing.JCheckBox rint;
    private javax.swing.JCheckBox rip;
    private javax.swing.JCheckBox ripn;
    private javax.swing.JCheckBox sAb;
    private javax.swing.JCheckBox sAd;
    private javax.swing.JCheckBox sAh;
    private javax.swing.JCheckBox sAh2;
    private javax.swing.JCheckBox sAk;
    private javax.swing.JCheckBox sKb;
    private javax.swing.JCheckBox sKc;
    private javax.swing.JCheckBox sKj;
    private javax.swing.JCheckBox sKn;
    private javax.swing.JCheckBox sKp;
    private javax.swing.JCheckBox sNir;
    private javax.swing.JCheckBox sNk;
    private javax.swing.JCheckBox sNl;
    private javax.swing.JCheckBox sNr;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane10;
    private widget.ScrollPane scrollPane11;
    private widget.ScrollPane scrollPane13;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane3;
    private widget.ScrollPane scrollPane4;
    private widget.ScrollPane scrollPane5;
    private widget.ScrollPane scrollPane7;
    private widget.ScrollPane scrollPane8;
    private widget.ScrollPane scrollPane9;
    private javax.swing.JTextField sirCRT;
    private widget.Table tbObat;
    private javax.swing.JCheckBox tpa;
    private javax.swing.JCheckBox tpb;
    private javax.swing.JCheckBox tpc;
    private javax.swing.JCheckBox tpf;
    private javax.swing.JCheckBox tph;
    private javax.swing.JCheckBox tpk;
    private javax.swing.JCheckBox tpn;
    private javax.swing.JCheckBox tpr;
    private javax.swing.JCheckBox tpt;
    // End of variables declaration//GEN-END:variables

public void tampil() {
    Valid.tabelKosong(tabMode);
    try {
//        Connection koneksi = koneksiDB.condb();// Lakukan inisialisasi koneksi ke database
//        PreparedStatement ps;
        
        if(TCari.getText().trim().equals("")) {
            ps = koneksi.prepareStatement(
                "SELECT reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, IF(pasien.jk='L', 'Laki-Laki', 'Perempuan') AS jk, pasien.tgl_lahir, " +
                "penilaian_medis_igd.tanggal, penilaian_medis_igd.kd_dokter, penilaian_medis_igd.anamnesis, penilaian_medis_igd.hubungan, " +
                "penilaian_medis_igd.keluhan_utama, penilaian_medis_igd.rps, penilaian_medis_igd.rpk, penilaian_medis_igd.rpd, penilaian_medis_igd.rpo, " +
                "penilaian_medis_igd.alergi, penilaian_medis_igd.keadaan, penilaian_medis_igd.gcs, penilaian_medis_igd.kesadaran, penilaian_medis_igd.td, " +
                "penilaian_medis_igd.nadi, penilaian_medis_igd.rr, penilaian_medis_igd.suhu, penilaian_medis_igd.spo, penilaian_medis_igd.bb, " +
                "penilaian_medis_igd.tb, penilaian_medis_igd.kepala, penilaian_medis_igd.mata, penilaian_medis_igd.gigi, penilaian_medis_igd.leher, " +
                "penilaian_medis_igd.thoraks, penilaian_medis_igd.abdomen, penilaian_medis_igd.ekstremitas, penilaian_medis_igd.genital, " +
                "penilaian_medis_igd.ket_fisik, penilaian_medis_igd.ket_lokalis, penilaian_medis_igd.ekg, penilaian_medis_igd.rad, penilaian_medis_igd.lab, " +
                "penilaian_medis_igd.diagnosis, penilaian_medis_igd.tata, dokter.nm_dokter, " +
                "penilaian_medis_igd_primer.int_prehospital, penilaian_medis_igd_primer.jalan_nafas, penilaian_medis_igd_primer.pernapasan, penilaian_medis_igd_primer.sirkulasi, penilaian_medis_igd_primer.disabilitas, penilaian_medis_igd_primer.penilaian_BBL, penilaian_medis_igd_primer.nilai_periksa, penilaian_medis_igd_primer.eksposur, penilaian_medis_igd_primer.ind_ranap " + // Kolom dari tabel penilaian_medis_igd_primer
                "FROM reg_periksa " +
                "INNER JOIN pasien ON reg_periksa.no_rkm_medis=pasien.no_rkm_medis " +
                "INNER JOIN penilaian_medis_igd ON reg_periksa.no_rawat=penilaian_medis_igd.no_rawat " +
                "INNER JOIN dokter ON penilaian_medis_igd.kd_dokter=dokter.kd_dokter " +
                "INNER JOIN penilaian_medis_igd_primer ON reg_periksa.no_rawat=penilaian_medis_igd_primer.no_rawat " + // Join dengan tabel penilaian_medis_igd_primer
                "WHERE penilaian_medis_igd.tanggal BETWEEN ? AND ? " +
                "ORDER BY penilaian_medis_igd.tanggal");
        } else {
            ps = koneksi.prepareStatement(
                "SELECT reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, IF(pasien.jk='L', 'Laki-Laki', 'Perempuan') AS jk, pasien.tgl_lahir, " +
                "penilaian_medis_igd.tanggal, penilaian_medis_igd.kd_dokter, penilaian_medis_igd.anamnesis, penilaian_medis_igd.hubungan, " +
                "penilaian_medis_igd.keluhan_utama, penilaian_medis_igd.rps, penilaian_medis_igd.rpk, penilaian_medis_igd.rpd, penilaian_medis_igd.rpo, " +
                "penilaian_medis_igd.alergi, penilaian_medis_igd.keadaan, penilaian_medis_igd.gcs, penilaian_medis_igd.kesadaran, penilaian_medis_igd.td, " +
                "penilaian_medis_igd.nadi, penilaian_medis_igd.rr, penilaian_medis_igd.suhu, penilaian_medis_igd.spo, penilaian_medis_igd.bb, " +
                "penilaian_medis_igd.tb, penilaian_medis_igd.kepala, penilaian_medis_igd.mata, penilaian_medis_igd.gigi, penilaian_medis_igd.leher, " +
                "penilaian_medis_igd.thoraks, penilaian_medis_igd.abdomen, penilaian_medis_igd.ekstremitas, penilaian_medis_igd.genital, " +
                "penilaian_medis_igd.ket_fisik, penilaian_medis_igd.ket_lokalis, penilaian_medis_igd.ekg, penilaian_medis_igd.rad, penilaian_medis_igd.lab, " +
                "penilaian_medis_igd.diagnosis, penilaian_medis_igd.tata, dokter.nm_dokter, " +
                "penilaian_medis_igd_primer.int_prehospital, penilaian_medis_igd_primer.jalan_nafas, penilaian_medis_igd_primer.pernapasan, penilaian_medis_igd_primer.sirkulasi, penilaian_medis_igd_primer.disabilitas, penilaian_medis_igd_primer.penilaian_bbl, penilaian_medis_igd_primer.nilai_periksa, penilaian_medis_igd_primer.eksposur, penilaian_medis_igd_primer.ind_ranap  " + // Kolom dari tabel penilaian_medis_igd_primer
                "FROM reg_periksa " +
                "INNER JOIN pasien ON reg_periksa.no_rkm_medis=pasien.no_rkm_medis " +
                "INNER JOIN penilaian_medis_igd ON reg_periksa.no_rawat=penilaian_medis_igd.no_rawat " +
                "INNER JOIN dokter ON penilaian_medis_igd.kd_dokter=dokter.kd_dokter " +
                "INNER JOIN penilaian_medis_igd_primer ON reg_periksa.no_rawat=penilaian_medis_igd_primer.no_rawat " + // Join dengan tabel penilaian_medis_igd_primer
                "WHERE penilaian_medis_igd.tanggal BETWEEN ? AND ? " +
                "AND (reg_periksa.no_rawat LIKE ? OR pasien.no_rkm_medis LIKE ? OR pasien.nm_pasien LIKE ? OR " +
                "penilaian_medis_igd.kd_dokter LIKE ? OR dokter.nm_dokter LIKE ?) " +
                "ORDER BY penilaian_medis_igd.tanggal");
        }
        if(TCari.getText().trim().equals("")){
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                }else{
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                }   
//        // Atur parameter sesuai dengan tanggal dan kriteria pencarian
//        ps.setDate(1, new java.sql.Date(tanggalAwal.getTime()));
//        ps.setDate(2, new java.sql.Date(tanggalAkhir.getTime()));
//        if(!TCari.getText().trim().equals("")) {
//            String cari = "%" + TCari.getText().trim() + "%";
//            for (int i = 3; i <= 6; i++) {
//                ps.setString(i, cari);
//            }
//        }
        
        // Eksekusi kueri
        ResultSet rs = ps.executeQuery();
        
        // Tambahkan data ke dalam tabel
        while(rs.next()) {
            tabMode.addRow(new String[] {
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("tgl_lahir"),rs.getString("jk"),rs.getString("kd_dokter"),rs.getString("nm_dokter"),rs.getString("tanggal"),
                        rs.getString("anamnesis"),rs.getString("hubungan"),rs.getString("keluhan_utama"),rs.getString("rps"),rs.getString("rpd"),rs.getString("rpk"),rs.getString("rpo"),rs.getString("alergi"),
                        rs.getString("keadaan"),rs.getString("gcs"),rs.getString("kesadaran"),rs.getString("td"),rs.getString("nadi"),rs.getString("rr"),rs.getString("suhu"),rs.getString("spo"),rs.getString("bb"),
                        rs.getString("tb"),rs.getString("kepala"),rs.getString("mata"),rs.getString("gigi"),rs.getString("leher"),rs.getString("thoraks"),rs.getString("abdomen"),rs.getString("genital"),
                        rs.getString("ekstremitas"),rs.getString("ket_fisik"),rs.getString("ket_lokalis"),rs.getString("ekg"),rs.getString("rad"),rs.getString("lab"),rs.getString("diagnosis"),rs.getString("tata"),
                        rs.getString("int_prehospital"), rs.getString("jalan_nafas"), rs.getString("pernapasan"), rs.getString("sirkulasi"), rs.getString("disabilitas"), rs.getString("penilaian_bbl"), rs.getString("nilai_periksa"), rs.getString("eksposur"),rs.getString("ind_ranap")   // Kolom dari tabel penilaian_medis_igd_primer
                // Tambahkan kolom-kolom lainnya yang diperlukan dari tabel penilaian_medis_igd_primer
            });
        }
        
        // Tutup koneksi dan statement
        rs.close();
        ps.close();
        //koneksi.close();
        
    } catch(Exception e) {
        System.out.println("Error: " + e.getMessage());
    }



//    public void tampil() {
//        Valid.tabelKosong(tabMode);
//        try{
//            if(TCari.getText().trim().equals("")){
//                ps=koneksi.prepareStatement(
//                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,penilaian_medis_igd.tanggal,"+
//                        "penilaian_medis_igd.kd_dokter,penilaian_medis_igd.anamnesis,penilaian_medis_igd.hubungan,penilaian_medis_igd.keluhan_utama,penilaian_medis_igd.rps,penilaian_medis_igd.rpk,penilaian_medis_igd.rpd,penilaian_medis_igd.rpo,penilaian_medis_igd.alergi,"+
//                        "penilaian_medis_igd.keadaan,penilaian_medis_igd.gcs,penilaian_medis_igd.kesadaran,penilaian_medis_igd.td,penilaian_medis_igd.nadi,penilaian_medis_igd.rr,penilaian_medis_igd.suhu,penilaian_medis_igd.spo,penilaian_medis_igd.bb,penilaian_medis_igd.tb,"+
//                        "penilaian_medis_igd.kepala,penilaian_medis_igd.mata,penilaian_medis_igd.gigi,penilaian_medis_igd.leher,penilaian_medis_igd.thoraks,penilaian_medis_igd.abdomen,penilaian_medis_igd.ekstremitas,penilaian_medis_igd.genital,penilaian_medis_igd.ket_fisik,"+
//                        "penilaian_medis_igd.ket_lokalis,penilaian_medis_igd.ekg,penilaian_medis_igd.rad,penilaian_medis_igd.lab,penilaian_medis_igd.diagnosis,penilaian_medis_igd.tata,dokter.nm_dokter "+
//                        "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
//                        "inner join penilaian_medis_igd on reg_periksa.no_rawat=penilaian_medis_igd.no_rawat "+
//                        "inner join dokter on penilaian_medis_igd.kd_dokter=dokter.kd_dokter where "+
//                        "penilaian_medis_igd.tanggal between ? and ? order by penilaian_medis_igd.tanggal");
//            }else{
//                ps=koneksi.prepareStatement(
//                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,penilaian_medis_igd.tanggal,"+
//                        "penilaian_medis_igd.kd_dokter,penilaian_medis_igd.anamnesis,penilaian_medis_igd.hubungan,penilaian_medis_igd.keluhan_utama,penilaian_medis_igd.rps,penilaian_medis_igd.rpk,penilaian_medis_igd.rpd,penilaian_medis_igd.rpo,penilaian_medis_igd.alergi,"+
//                        "penilaian_medis_igd.keadaan,penilaian_medis_igd.gcs,penilaian_medis_igd.kesadaran,penilaian_medis_igd.td,penilaian_medis_igd.nadi,penilaian_medis_igd.rr,penilaian_medis_igd.suhu,penilaian_medis_igd.spo,penilaian_medis_igd.bb,penilaian_medis_igd.tb,"+
//                        "penilaian_medis_igd.kepala,penilaian_medis_igd.mata,penilaian_medis_igd.gigi,penilaian_medis_igd.leher,penilaian_medis_igd.thoraks,penilaian_medis_igd.abdomen,penilaian_medis_igd.ekstremitas,penilaian_medis_igd.genital,penilaian_medis_igd.ket_fisik,"+
//                        "penilaian_medis_igd.ket_lokalis,penilaian_medis_igd.ekg,penilaian_medis_igd.rad,penilaian_medis_igd.lab,penilaian_medis_igd.diagnosis,penilaian_medis_igd.tata,dokter.nm_dokter "+
//                        "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
//                        "inner join penilaian_medis_igd on reg_periksa.no_rawat=penilaian_medis_igd.no_rawat "+
//                        "inner join dokter on penilaian_medis_igd.kd_dokter=dokter.kd_dokter where "+
//                        "penilaian_medis_igd.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or "+
//                        "penilaian_medis_igd.kd_dokter like ? or dokter.nm_dokter like ?) order by penilaian_medis_igd.tanggal");
//            }
//                
//            try {
//                if(TCari.getText().trim().equals("")){
//                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
//                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
//                }else{
//                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
//                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
//                    ps.setString(3,"%"+TCari.getText()+"%");
//                    ps.setString(4,"%"+TCari.getText()+"%");
//                    ps.setString(5,"%"+TCari.getText()+"%");
//                    ps.setString(6,"%"+TCari.getText()+"%");
//                    ps.setString(7,"%"+TCari.getText()+"%");
//                }   
//                rs=ps.executeQuery();
//                while(rs.next()){
//                    tabMode.addRow(new String[]{
//                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("tgl_lahir"),rs.getString("jk"),rs.getString("kd_dokter"),rs.getString("nm_dokter"),rs.getString("tanggal"),
//                        rs.getString("anamnesis"),rs.getString("hubungan"),rs.getString("keluhan_utama"),rs.getString("rps"),rs.getString("rpd"),rs.getString("rpk"),rs.getString("rpo"),rs.getString("alergi"),
//                        rs.getString("keadaan"),rs.getString("gcs"),rs.getString("kesadaran"),rs.getString("td"),rs.getString("nadi"),rs.getString("rr"),rs.getString("suhu"),rs.getString("spo"),rs.getString("bb"),
//                        rs.getString("tb"),rs.getString("kepala"),rs.getString("mata"),rs.getString("gigi"),rs.getString("leher"),rs.getString("thoraks"),rs.getString("abdomen"),rs.getString("genital"),
//                        rs.getString("ekstremitas"),rs.getString("ket_fisik"),rs.getString("ket_lokalis"),rs.getString("ekg"),rs.getString("rad"),rs.getString("lab"),rs.getString("diagnosis"),rs.getString("tata")
//                    });
//                }
//            } catch (Exception e) {
//                System.out.println("Notif : "+e);
//            } finally{
//                if(rs!=null){
//                    rs.close();
//                }
//                if(ps!=null){
//                    ps.close();
//                }
//            }
//            
//        }catch(Exception e){
//            System.out.println("Notifikasi : "+e);
//        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void emptTeks() {
        Anamnesis.setSelectedIndex(0);
        Hubungan.setText("");
        KeluhanUtama.setText("");
        RPS.setText("");
        RPK.setText("");
        RPD.setText("");
        RPO.setText("");
        Alergi.setText("");
        Keadaan.setSelectedIndex(0);
        GCS.setText("");
        Kesadaran.setSelectedIndex(0);
        TD.setText("");
        Nadi.setText("");
        RR.setText("");
        Suhu.setText("");
        BB.setText("");
        TB.setText("");
        Kepala.setSelectedIndex(0);
        Mata.setSelectedIndex(0);
        Gigi.setSelectedIndex(0);
        Leher.setSelectedIndex(0);
        Thoraks.setSelectedIndex(0);
        Abdomen.setSelectedIndex(0);
        Genital.setSelectedIndex(0);
        Ekstremitas.setSelectedIndex(0);
        KetFisik.setText("");
        KetLokalis.setText("");
        EKG.setText("");
        Radiologi.setText("");
        Laborat.setText("");
        Diagnosis.setText("");
        Tatalaksana.setText("");
        TglAsuhan.setDate(new Date());
        TabRawat.setSelectedIndex(0);
        Anamnesis.requestFocus();
    } 
    
    public void emptTeks1() {
    lblIP.setText(""); // Kosongkan nilai di JTextField lblIP
    lblJalanNapas.setText(""); // Kosongkan nilai di JTextField lblJalanNapas
    lblPern.setText(""); // Kosongkan nilai di JTextField lblPern
    lblsir.setText(""); // Kosongkan nilai di JTextField lblsir
    lbldis.setText(""); // Kosongkan nilai di JTextField lbldis
    lblBBL.setText(""); // Kosongkan nilai di JTextField lblBBL
    lblpmnilai.setText(""); // Kosongkan nilai di JTextField lblpmnilai
    lbleks.setText(""); // Kosongkan nilai di JTextField lbleks
    lblindikasiranap.setText(""); // Kosongkan nilai di JTextField lbleks
} 

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()); 
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            Jk.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString()); 
            Anamnesis.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            Hubungan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            KeluhanUtama.setText(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            RPS.setText(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());
            RPD.setText(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            RPK.setText(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            RPO.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            Alergi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            Keadaan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            GCS.setText(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
            Kesadaran.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
            TD.setText(tbObat.getValueAt(tbObat.getSelectedRow(),19).toString());
            Nadi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),20).toString());
            RR.setText(tbObat.getValueAt(tbObat.getSelectedRow(),21).toString());
            Suhu.setText(tbObat.getValueAt(tbObat.getSelectedRow(),22).toString());
            SPO.setText(tbObat.getValueAt(tbObat.getSelectedRow(),23).toString());
            BB.setText(tbObat.getValueAt(tbObat.getSelectedRow(),24).toString());
            TB.setText(tbObat.getValueAt(tbObat.getSelectedRow(),25).toString());
            Kepala.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),26).toString());
            Mata.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),27).toString());
            Gigi.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),28).toString());
            Leher.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),29).toString());
            Thoraks.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),30).toString());
            Abdomen.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),31).toString());
            Genital.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),32).toString());
            Ekstremitas.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),33).toString());
            KetFisik.setText(tbObat.getValueAt(tbObat.getSelectedRow(),34).toString());
            KetLokalis.setText(tbObat.getValueAt(tbObat.getSelectedRow(),35).toString());
            EKG.setText(tbObat.getValueAt(tbObat.getSelectedRow(),36).toString());
            Radiologi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),37).toString());
            Laborat.setText(tbObat.getValueAt(tbObat.getSelectedRow(),38).toString());
            Diagnosis.setText(tbObat.getValueAt(tbObat.getSelectedRow(),39).toString());
            Tatalaksana.setText(tbObat.getValueAt(tbObat.getSelectedRow(),40).toString());
            lblIP.setText(tbObat.getValueAt(tbObat.getSelectedRow(),41).toString());
            lblJalanNapas.setText(tbObat.getValueAt(tbObat.getSelectedRow(),42).toString());
            lblPern.setText(tbObat.getValueAt(tbObat.getSelectedRow(),43).toString());
            lblsir.setText(tbObat.getValueAt(tbObat.getSelectedRow(),44).toString());
            lbldis.setText(tbObat.getValueAt(tbObat.getSelectedRow(),45).toString());
            lblBBL.setText(tbObat.getValueAt(tbObat.getSelectedRow(),46).toString());
            lblpmnilai.setText(tbObat.getValueAt(tbObat.getSelectedRow(),47).toString());
            lbleks.setText(tbObat.getValueAt(tbObat.getSelectedRow(),48).toString());
            lblindikasiranap.setText(tbObat.getValueAt(tbObat.getSelectedRow(),49).toString());
            Valid.SetTgl2(TglAsuhan,tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
        }
    }

    private void isRawat() {
        try {
            ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien, if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi "+
                    "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "where reg_periksa.no_rawat=?");
            try {
                ps.setString(1,TNoRw.getText());
                rs=ps.executeQuery();
                if(rs.next()){
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    Jk.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
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
    }
 
    public void setNoRm(String norwt,Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);    
        isRawat(); 
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getpenilaian_awal_medis_igd());
        BtnHapus.setEnabled(akses.getpenilaian_awal_medis_igd());
        BtnEdit.setEnabled(akses.getpenilaian_awal_medis_igd());
        if(akses.getjml2()>=1){
            KdDokter.setEditable(false);
            BtnDokter.setEnabled(false);
            KdDokter.setText(akses.getkode());
            NmDokter.setText(dokter.tampil3(KdDokter.getText()));
            if(NmDokter.getText().equals("")){
                KdDokter.setText("");
                JOptionPane.showMessageDialog(null,"User login bukan Dokter...!!");
            }
        }            
    }
    
    public void setTampil(){
       TabRawat.setSelectedIndex(1);
    }

    private void hapus() {
    String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();

    // Hapus dari tabel penilaian_medis_igd
    if(Sequel.queryu2tf("delete from penilaian_medis_igd where no_rawat=?", 1, new String[]{ noRawat })) {
        // Hapus dari tabel penilaian_medis_igd_primer
        if (Sequel.queryu2tf("delete from penilaian_medis_igd_primer where no_rawat=?", 1, new String[]{ noRawat })) {
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            TabRawat.setSelectedIndex(1);
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus entri dari tabel penilaian_medis_igd_primer..!!");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Gagal menghapus entri dari tabel penilaian_medis_igd..!!");
    }
}

    private void ganti() {
        if(Sequel.mengedittf("penilaian_medis_igd","no_rawat=?","no_rawat=?,tanggal=?,kd_dokter=?,anamnesis=?,hubungan=?,keluhan_utama=?,rps=?,rpd=?,rpk=?,rpo=?,alergi=?,keadaan=?,gcs=?,kesadaran=?,td=?,nadi=?,rr=?,suhu=?,spo=?,bb=?,tb=?,kepala=?,mata=?,gigi=?,leher=?,thoraks=?,abdomen=?,genital=?,ekstremitas=?,ket_fisik=?,ket_lokalis=?,ekg=?,rad=?,lab=?,diagnosis=?,tata=?",37,new String[]{
                TNoRw.getText(),Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),KdDokter.getText(),Anamnesis.getSelectedItem().toString(),Hubungan.getText(),
                    KeluhanUtama.getText(),RPS.getText(),RPD.getText(),RPK.getText(),RPO.getText(),Alergi.getText(),Keadaan.getSelectedItem().toString(),GCS.getText(),Kesadaran.getSelectedItem().toString(),TD.getText(),
                    Nadi.getText(),RR.getText(),Suhu.getText(),SPO.getText(),BB.getText(),TB.getText(),Kepala.getSelectedItem().toString(),Mata.getSelectedItem().toString(),Gigi.getSelectedItem().toString(),Leher.getSelectedItem().toString(),
                    Thoraks.getSelectedItem().toString(),Abdomen.getSelectedItem().toString(),Genital.getSelectedItem().toString(),Ekstremitas.getSelectedItem().toString(),KetFisik.getText(),KetLokalis.getText(),EKG.getText(),
                    Radiologi.getText(),Laborat.getText(),Diagnosis.getText(),Tatalaksana.getText(),tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()}))
          
        if(Sequel.mengedittf("penilaian_medis_igd_primer","no_rawat=?","int_prehospital=?,jalan_nafas=?,pernapasan=?,sirkulasi=?,disabilitas=?,penilaian_bbl=?,nilai_periksa=?,eksposur=?, ind_ranap=?",10,new String[]{
                TNoRw.getText(), lblIP.getText().replaceAll("\\<.*?\\>", ""), lblJalanNapas.getText().replaceAll("\\<.*?\\>",""), lblPern.getText().replaceAll("\\<.*?\\>",""), lblsir.getText().replaceAll("\\<.*?\\>",""), lbldis.getText().replaceAll("\\<.*?\\>",""), lblBBL.getText().replaceAll("\\<.*?\\>",""), lblpmnilai.getText().replaceAll("\\<.*?\\>",""), lbleks.getText().replaceAll("\\<.*?\\>",""), lblindikasiranap.getText().replaceAll("\\<.*?\\>","")
        
        })==true){
               tbObat.setValueAt(TNoRw.getText(),tbObat.getSelectedRow(),0);
               tbObat.setValueAt(TNoRM.getText(),tbObat.getSelectedRow(),1);
               tbObat.setValueAt(TPasien.getText(),tbObat.getSelectedRow(),2);
               tbObat.setValueAt(TglLahir.getText(),tbObat.getSelectedRow(),3);
               tbObat.setValueAt(Jk.getText().substring(0,1),tbObat.getSelectedRow(),4);
               tbObat.setValueAt(KdDokter.getText(),tbObat.getSelectedRow(),5);
               tbObat.setValueAt(NmDokter.getText(),tbObat.getSelectedRow(),6);
               tbObat.setValueAt(Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),tbObat.getSelectedRow(),7);
               tbObat.setValueAt(Anamnesis.getSelectedItem().toString(),tbObat.getSelectedRow(),8);
               tbObat.setValueAt(Hubungan.getText(),tbObat.getSelectedRow(),9);
               tbObat.setValueAt(KeluhanUtama.getText(),tbObat.getSelectedRow(),10);
               tbObat.setValueAt(RPS.getText(),tbObat.getSelectedRow(),11);
               tbObat.setValueAt(RPD.getText(),tbObat.getSelectedRow(),12);
               tbObat.setValueAt(RPK.getText(),tbObat.getSelectedRow(),13);
               tbObat.setValueAt(RPO.getText(),tbObat.getSelectedRow(),14);
               tbObat.setValueAt(Alergi.getText(),tbObat.getSelectedRow(),15);
               tbObat.setValueAt(Keadaan.getSelectedItem().toString(),tbObat.getSelectedRow(),16);
               tbObat.setValueAt(GCS.getText(),tbObat.getSelectedRow(),17);
               tbObat.setValueAt(Kesadaran.getSelectedItem().toString(),tbObat.getSelectedRow(),18);
               tbObat.setValueAt(TD.getText(),tbObat.getSelectedRow(),19);
               tbObat.setValueAt(Nadi.getText(),tbObat.getSelectedRow(),20);
               tbObat.setValueAt(RR.getText(),tbObat.getSelectedRow(),21);
               tbObat.setValueAt(Suhu.getText(),tbObat.getSelectedRow(),22);
               tbObat.setValueAt(SPO.getText(),tbObat.getSelectedRow(),23);
               tbObat.setValueAt(BB.getText(),tbObat.getSelectedRow(),24);
               tbObat.setValueAt(TB.getText(),tbObat.getSelectedRow(),25);
               tbObat.setValueAt(Kepala.getSelectedItem().toString(),tbObat.getSelectedRow(),26);
               tbObat.setValueAt(Mata.getSelectedItem().toString(),tbObat.getSelectedRow(),27);
               tbObat.setValueAt(Gigi.getSelectedItem().toString(),tbObat.getSelectedRow(),28);
               tbObat.setValueAt(Leher.getSelectedItem().toString(),tbObat.getSelectedRow(),29);
               tbObat.setValueAt(Thoraks.getSelectedItem().toString(),tbObat.getSelectedRow(),30);
               tbObat.setValueAt(Abdomen.getSelectedItem().toString(),tbObat.getSelectedRow(),31);
               tbObat.setValueAt(Genital.getSelectedItem().toString(),tbObat.getSelectedRow(),32);
               tbObat.setValueAt(Ekstremitas.getSelectedItem().toString(),tbObat.getSelectedRow(),33);
               tbObat.setValueAt(KetFisik.getText(),tbObat.getSelectedRow(),34);
               tbObat.setValueAt(KetLokalis.getText(),tbObat.getSelectedRow(),35);
               tbObat.setValueAt(EKG.getText(),tbObat.getSelectedRow(),36);
               tbObat.setValueAt(Radiologi.getText(),tbObat.getSelectedRow(),37);
               tbObat.setValueAt(Laborat.getText(),tbObat.getSelectedRow(),38);
               tbObat.setValueAt(Diagnosis.getText(),tbObat.getSelectedRow(),39);
               tbObat.setValueAt(Tatalaksana.getText(),tbObat.getSelectedRow(),40);
               tbObat.setValueAt(lblIP.getText(),tbObat.getSelectedRow(),41);
               tbObat.setValueAt(lblJalanNapas.getText(),tbObat.getSelectedRow(),42);
               tbObat.setValueAt(lblPern.getText(),tbObat.getSelectedRow(),43);
               tbObat.setValueAt(lblsir.getText(),tbObat.getSelectedRow(),44);
               tbObat.setValueAt(lbldis.getText(),tbObat.getSelectedRow(),45);
               tbObat.setValueAt(lblBBL.getText(),tbObat.getSelectedRow(),46);
               tbObat.setValueAt(lblpmnilai.getText(),tbObat.getSelectedRow(),47);
               tbObat.setValueAt(lbleks.getText(),tbObat.getSelectedRow(),48);
               tbObat.setValueAt(lblindikasiranap.getText(),tbObat.getSelectedRow(),49);
               emptTeks();
               TabRawat.setSelectedIndex(1);
        }
    }
//ubah    
  void imageAssesment(String url){
        try {
            BufferedImage img = ImageIO.read(new URL(url.trim()));
            PanelWall.setBackgroundImage(new javax.swing.ImageIcon(img));
        }
        catch(IOException ex) {

        } 
    } 
    
}

