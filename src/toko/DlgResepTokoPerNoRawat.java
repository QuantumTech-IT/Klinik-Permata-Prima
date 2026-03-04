/*
 * DlgResepTokoPerNoRawat.java (Versi RESEP_TOKO + RESEP_TOKO_DETAIL)
 */
package toko;

import fungsi.koneksiDB;
import fungsi.validasi;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DlgResepTokoPerNoRawat extends JDialog {

    private final Connection koneksi = koneksiDB.condb();

    private final JTextField TNoRw = new JTextField();
    private final JButton BtnCari = new JButton("Cari");
    private final JButton BtnSemua = new JButton("Semua");
    private final JButton BtnTelaah = new JButton("Telaah/Print");
    private final JButton BtnTutup = new JButton("Tutup");

    private final validasi Valid = new validasi();

    private final JLabel LPasien = new JLabel("-");
    private final JLabel LNoRM   = new JLabel("-");
    private final JLabel LDokter = new JLabel("-");
    private final JLabel LPoli   = new JLabel("-");
    private final JLabel LInfo   = new JLabel(" ");

    private final widget.Tanggal DTgl1 = new widget.Tanggal();
    private final widget.Tanggal DTgl2 = new widget.Tanggal();

    private JTable tbResep;
    private DefaultTableModel tabMode;
    private String noNotaFilter = "";

    // kalau file kamu namanya rekaptelaah.php, ganti jadi:
    // private static final String TELAAH_PAGE = "/billing/rekaptelaah.php";
    private static final String TELAAH_PAGE = "/billing/ResepTelaah.php";

    public DlgResepTokoPerNoRawat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Rekap Resep Toko per No. Rawat");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(1150, 600));

        initUI();
        initEvent();
        DTgl1.setDisplayFormat("dd-MM-yyyy");
        DTgl2.setDisplayFormat("dd-MM-yyyy");
        DTgl1.setDate(new java.util.Date());
        DTgl2.setDate(new java.util.Date());

        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        // ===== row1
        JPanel row1 = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));
        row1.add(new JLabel("No. Rawat: "));
        TNoRw.setPreferredSize(new Dimension(180, 26));
        row1.add(TNoRw);
        row1.add(BtnCari);
        row1.add(BtnSemua);
        row1.add(BtnTelaah);
        row1.add(Box.createHorizontalStrut(15));
        row1.add(BtnTutup);

        // ===== row2
        JPanel row2 = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));
        row2.add(new JLabel("Tgl Resep: "));
        DTgl1.setPreferredSize(new Dimension(140, 26));
        DTgl2.setPreferredSize(new Dimension(140, 26));
        row2.add(DTgl1);
        row2.add(new JLabel(" s.d "));
        row2.add(DTgl2);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(row1);
        top.add(row2);

        // ===== info pasien
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(new JLabel("Pasien : "));
        info.add(LPasien);
        info.add(new JLabel("No.RM : "));
        info.add(LNoRM);
        info.add(new JLabel("Poli   : "));
        info.add(LPoli);
        info.add(new JLabel("Dokter : "));
        info.add(LDokter);
        info.add(LInfo);

        JPanel north = new JPanel(new BorderLayout(8, 8));
        north.add(top, BorderLayout.NORTH);
        north.add(info, BorderLayout.CENTER);

        // ===== table (No Resep tampil)
        tabMode = new DefaultTableModel(null, new Object[]{
                "Tgl Resep", "No Resep", "No Nota", "No.Rawat", "Jenis", "Dokter",
                "Nama Obat", "Jml", "Satuan", "Aturan Pakai",
                "Harga", "Subtotal", "Diskon", "Tuslah", "Total"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tbResep = new JTable(tabMode);
        tbResep.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] w = {
                140, 140, 150, 140, 90, 200,
                320, 60, 70, 160,
                90, 90, 80, 80, 90
        };
        for (int i = 0; i < w.length; i++) {
            tbResep.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }

        JScrollPane sp = new JScrollPane(tbResep);

        getContentPane().setLayout(new BorderLayout(8, 8));
        getContentPane().add(north, BorderLayout.NORTH);
        getContentPane().add(sp, BorderLayout.CENTER);
    }

    private void initEvent() {
        BtnCari.addActionListener(e -> tampil());
        BtnTutup.addActionListener(e -> dispose());

        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) tampil();
            }
        });

        BtnSemua.addActionListener(e -> {
            TNoRw.setText("");
            noNotaFilter = "";
            tampil();
        });

        BtnTelaah.addActionListener(e -> bukaResepTelaah(1)); // autoprint=1
    }

    public void setNoRawat(String noRawat) {
        TNoRw.setText(noRawat == null ? "" : noRawat.trim());
    }

    public void setNoNota(String noNota) {
        noNotaFilter = (noNota == null ? "" : noNota.trim());
    }

    public void tampil() {
        String noRawat = TNoRw.getText().trim();

        String tgl1 = toSqlDate(DTgl1.getSelectedItem());
        String tgl2 = toSqlDate(DTgl2.getSelectedItem());
        if (tgl1.isEmpty() || tgl2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tanggal range tidak valid!");
            return;
        }

        Valid.tabelKosong(tabMode);
        resetHeader();

        if (!noRawat.isEmpty()) {
            loadHeaderPasien(noRawat);
            boolean ada = loadResepByNoRawat(noRawat);
            if (!ada) {
                ada = loadPenjualanResepByNoRawat(noRawat);
                if (ada) {
                    LInfo.setText(LInfo.getText() + " (fallback tokopenjualan)");
                }
            }
            if (!ada && !noNotaFilter.isEmpty()) {
                ada = loadResepByNoNota(noNotaFilter);
                if (ada) {
                    LInfo.setText(LInfo.getText() + " (fallback no_nota)");
                }
                if (!ada) {
                    ada = loadPenjualanResepByNoNota(noNotaFilter);
                    if (ada) {
                        LInfo.setText(LInfo.getText() + " (fallback tokopenjualan/no_nota)");
                    }
                }
            }
            if (!ada) {
                JOptionPane.showMessageDialog(this,
                        "Tidak ada data resep untuk No.Rawat/No.Nota ini.");
            }
        } else if (!noNotaFilter.isEmpty()) {
            boolean ada = loadResepByNoNota(noNotaFilter);
            if (!ada) {
                ada = loadPenjualanResepByNoNota(noNotaFilter);
                if (ada) {
                    LInfo.setText(LInfo.getText() + " (fallback tokopenjualan/no_nota)");
                }
            }
            if (!ada) {
                String noRawatDariNota = resolveNoRawatByNota(noNotaFilter);
                if (!noRawatDariNota.isEmpty()) {
                    TNoRw.setText(noRawatDariNota);
                    loadHeaderPasien(noRawatDariNota);
                    ada = loadResepByNoRawat(noRawatDariNota);
                    if (!ada) {
                        ada = loadPenjualanResepByNoRawat(noRawatDariNota);
                    }
                    if (ada) {
                        LInfo.setText(LInfo.getText() + " (fallback no_rawat dari nota)");
                    }
                }
            }
            if (!ada) {
                JOptionPane.showMessageDialog(this,
                        "Tidak ada data resep untuk nota: " + noNotaFilter);
            }
        } else {
            boolean ada = loadResepByTanggal(tgl1, tgl2);
            if (!ada) {
                JOptionPane.showMessageDialog(this,
                        "Tidak ada data resep pada rentang tanggal tersebut.");
            }
        }
    }

    private void resetHeader() {
        LPasien.setText("-");
        LNoRM.setText("-");
        LPoli.setText("-");
        LDokter.setText("-");
        LInfo.setText(" ");
    }

    private void loadHeaderPasien(String noRawat) {
        String sql =
                "SELECT p.no_rkm_medis, p.nm_pasien, IFNULL(pl.nm_poli,'-') AS nm_poli, " +
                "       IFNULL(d.nm_dokter,'-') AS nm_dokter " +
                "FROM reg_periksa rp " +
                "INNER JOIN pasien p ON rp.no_rkm_medis=p.no_rkm_medis " +
                "LEFT JOIN poliklinik pl ON rp.kd_poli=pl.kd_poli " +
                "LEFT JOIN dokter d ON rp.kd_dokter=d.kd_dokter " +
                "WHERE rp.no_rawat=? LIMIT 1";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LPasien.setText(rs.getString("nm_pasien"));
                    LNoRM.setText(rs.getString("no_rkm_medis"));
                    LPoli.setText(rs.getString("nm_poli"));
                    LDokter.setText(rs.getString("nm_dokter"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi loadHeaderPasien : " + e.getMessage());
        }
    }

    private String resolveNoRawatByNota(String noNota) {
        String nota = (noNota == null ? "" : noNota.trim());
        if (nota.isEmpty()) return "";

        String sql =
                "SELECT COALESCE(no_rawat,'') AS no_rawat " +
                "FROM tokopenjualan WHERE nota_jual=? LIMIT 1";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, nota);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String noRawat = rs.getString("no_rawat");
                    return noRawat == null ? "" : noRawat.trim();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi resolveNoRawatByNota(tokopenjualan) : " + e.getMessage());
        }

        sql =
                "SELECT COALESCE(no_rawat,'') AS no_rawat " +
                "FROM resep_toko WHERE TRIM(COALESCE(no_nota,''))=? " +
                "ORDER BY tgl_resep DESC LIMIT 1";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, nota);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String noRawat = rs.getString("no_rawat");
                    return noRawat == null ? "" : noRawat.trim();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi resolveNoRawatByNota(resep_toko) : " + e.getMessage());
        }
        return "";
    }

    // =========================================================
    // SUMBER DATA UTAMA: resep_toko + resep_toko_detail
    // =========================================================
    private boolean loadResepByNoRawat(String noRawat) {
        String sql =
                "SELECT " +
                "  r.tgl_resep, r.no_resep, r.no_rawat, COALESCE(r.no_nota,'') AS no_nota, " +
                "  COALESCE(r.keterangan,'') AS jenis_resep, " +
                "  COALESCE(d.nm_dokter,'-') AS nm_dokter, " +
                "  rd.kode_brng, COALESCE(b.nama_brng,'') AS nama_brng, " +
                "  COALESCE(rd.jml,0) AS jml_resep, COALESCE(rd.satuan,'') AS satuan_resep, " +
                "  COALESCE(rd.aturan_pakai,'') AS aturan_pakai, " +
                "  COALESCE(rd.keterangan,'') AS ket_item, " +              // << kalau kolommu bukan ket, ganti ke rd.keterangan
                "  COALESCE(rd.nama_racikan,'') AS nama_racikan, " +
                "  COALESCE(dj.h_jual,0) AS h_jual, COALESCE(dj.subtotal,0) AS subtotal, " +
                "  COALESCE(dj.bsr_dis,0) AS diskon, COALESCE(dj.tambahan,0) AS tuslah, COALESCE(dj.total,0) AS total " +
                "FROM resep_toko r " +
                "JOIN resep_toko_detail rd ON rd.no_resep=r.no_resep " +
                "LEFT JOIN tokobarang b ON b.kode_brng=rd.kode_brng " +
                "LEFT JOIN dokter d ON d.kd_dokter=r.kd_dokter " +
                "LEFT JOIN toko_detail_jual dj ON dj.nota_jual=r.no_nota AND dj.kode_brng=rd.kode_brng " +
                "WHERE r.no_rawat=? " +
                "ORDER BY r.tgl_resep, r.no_resep, rd.id";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noRawat);

            try (ResultSet rs = ps.executeQuery()) {

                String resepLast = "";
                double totalResep = 0;
                double grand = 0;
                boolean ada = false;

                while (rs.next()) {
                    ada = true;

                    String noResep = rs.getString("no_resep");

                    if (!noResep.equals(resepLast)) {
                        if (!resepLast.isEmpty()) {
                            // total resep sebelumnya
                            tabMode.addRow(new Object[]{
                                    "", "", "", "", "", "",
                                    "", "", "", "",
                                    "", "", "", "TOTAL RESEP :", totalResep
                            });
                            tabMode.addRow(new Object[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});
                        }

                        resepLast = noResep;
                        totalResep = 0;

                        // header group resep
                        tabMode.addRow(new Object[]{
                                rs.getString("tgl_resep"),
                                rs.getString("no_resep"),
                                rs.getString("no_nota"),
                                rs.getString("no_rawat"),
                                rs.getString("jenis_resep"),
                                rs.getString("nm_dokter"),
                                "", "", "", "",
                                "", "", "", "", ""
                        });

                        // sub-header item
                        tabMode.addRow(new Object[]{
                                "", "", "", "", "", "",
                                "Nama Obat", "Jml", "Satuan", "Aturan Pakai",
                                "Harga", "Subtotal", "Diskon", "Tuslah", "Total"
                        });
                    }

                    double total = rs.getDouble("total");
                    totalResep += total;
                    grand += total;

                    String aturan = rs.getString("aturan_pakai");
                    String ketItem = rs.getString("ket_item");
                    String aturanGabung = (aturan == null ? "" : aturan.trim());
                    if (ketItem != null && !ketItem.trim().isEmpty()) {
                        if (!aturanGabung.isEmpty()) aturanGabung += " | ";
                        aturanGabung += ketItem.trim();
                    }

                    tabMode.addRow(new Object[]{
                            "", "", "", "", "", "",
                            rs.getString("nama_brng"),
                            rs.getDouble("jml_resep"),
                            rs.getString("satuan_resep"),
                            aturanGabung,
                            rs.getDouble("h_jual"),
                            rs.getDouble("subtotal"),
                            rs.getDouble("diskon"),
                            rs.getDouble("tuslah"),
                            total
                    });
                }

                if (!resepLast.isEmpty()) {
                    tabMode.addRow(new Object[]{
                            "", "", "", "", "", "",
                            "", "", "", "",
                            "", "", "", "TOTAL RESEP :", totalResep
                    });
                }

                LInfo.setText("Total (berdasarkan nota jika ada): " + grand);
                return ada;
            }

        } catch (Exception e) {
            System.out.println("Notifikasi loadResepByNoRawat : " + e.getMessage());
        }
        return false;
    }

    private boolean loadResepByTanggal(String tgl1, String tgl2) {
        String sql =
                "SELECT " +
                "  r.tgl_resep, r.no_resep, r.no_rawat, COALESCE(r.no_nota,'') AS no_nota, " +
                "  COALESCE(r.keterangan,'') AS jenis_resep, " +
                "  COALESCE(d.nm_dokter,'-') AS nm_dokter, " +
                "  rd.kode_brng, COALESCE(b.nama_brng,'') AS nama_brng, " +
                "  COALESCE(rd.jml,0) AS jml_resep, COALESCE(rd.satuan,'') AS satuan_resep, " +
                "  COALESCE(rd.aturan_pakai,'') AS aturan_pakai, " +
                "  COALESCE(rd.keterangan,'') AS ket_item, " +              // << kalau kolommu bukan ket, ganti ke rd.keterangan
                "  COALESCE(rd.nama_racikan,'') AS nama_racikan, " +
                "  COALESCE(dj.h_jual,0) AS h_jual, COALESCE(dj.subtotal,0) AS subtotal, " +
                "  COALESCE(dj.bsr_dis,0) AS diskon, COALESCE(dj.tambahan,0) AS tuslah, COALESCE(dj.total,0) AS total " +
                "FROM resep_toko r " +
                "JOIN resep_toko_detail rd ON rd.no_resep=r.no_resep " +
                "LEFT JOIN tokobarang b ON b.kode_brng=rd.kode_brng " +
                "LEFT JOIN dokter d ON d.kd_dokter=r.kd_dokter " +
                "LEFT JOIN toko_detail_jual dj ON dj.nota_jual=r.no_nota AND dj.kode_brng=rd.kode_brng " +
                "WHERE DATE(r.tgl_resep) BETWEEN ? AND ? " +
                "ORDER BY r.tgl_resep, r.no_resep, rd.id";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, tgl1);
            ps.setString(2, tgl2);

            try (ResultSet rs = ps.executeQuery()) {

                String resepLast = "";
                double totalResep = 0;
                double grand = 0;
                boolean ada = false;

                while (rs.next()) {
                    ada = true;
                    String noResep = rs.getString("no_resep");

                    if (!noResep.equals(resepLast)) {
                        if (!resepLast.isEmpty()) {
                            tabMode.addRow(new Object[]{
                                    "", "", "", "", "", "",
                                    "", "", "", "",
                                    "", "", "", "TOTAL RESEP :", totalResep
                            });
                            tabMode.addRow(new Object[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});
                        }

                        resepLast = noResep;
                        totalResep = 0;

                        tabMode.addRow(new Object[]{
                                rs.getString("tgl_resep"),
                                rs.getString("no_resep"),
                                rs.getString("no_nota"),
                                rs.getString("no_rawat"),
                                rs.getString("jenis_resep"),
                                rs.getString("nm_dokter"),
                                "", "", "", "",
                                "", "", "", "", ""
                        });

                        tabMode.addRow(new Object[]{
                                "", "", "", "", "", "",
                                "Nama Obat", "Jml", "Satuan", "Aturan Pakai",
                                "Harga", "Subtotal", "Diskon", "Tuslah", "Total"
                        });
                    }

                    double total = rs.getDouble("total");
                    totalResep += total;
                    grand += total;

                    String aturan = rs.getString("aturan_pakai");
                    String ketItem = rs.getString("ket_item");
                    String aturanGabung = (aturan == null ? "" : aturan.trim());
                    if (ketItem != null && !ketItem.trim().isEmpty()) {
                        if (!aturanGabung.isEmpty()) aturanGabung += " | ";
                        aturanGabung += ketItem.trim();
                    }

                    tabMode.addRow(new Object[]{
                            "", "", "", "", "", "",
                            rs.getString("nama_brng"),
                            rs.getDouble("jml_resep"),
                            rs.getString("satuan_resep"),
                            aturanGabung,
                            rs.getDouble("h_jual"),
                            rs.getDouble("subtotal"),
                            rs.getDouble("diskon"),
                            rs.getDouble("tuslah"),
                            total
                    });
                }

                if (!resepLast.isEmpty()) {
                    tabMode.addRow(new Object[]{
                            "", "", "", "", "", "",
                            "", "", "", "",
                            "", "", "", "TOTAL RESEP :", totalResep
                    });
                }

                LDokter.setText("(multi)");
                LInfo.setText("Total (berdasarkan nota jika ada): " + grand);
                return ada;
            }

        } catch (Exception e) {
            System.out.println("Notifikasi loadResepByTanggal : " + e.getMessage());
        }
        return false;
    }

    private boolean loadResepByNoNota(String noNota) {
        String sql =
                "SELECT " +
                "  r.tgl_resep, r.no_resep, r.no_rawat, COALESCE(r.no_nota,'') AS no_nota, " +
                "  COALESCE(r.keterangan,'') AS jenis_resep, " +
                "  COALESCE(d.nm_dokter,'-') AS nm_dokter, " +
                "  rd.kode_brng, COALESCE(b.nama_brng,'') AS nama_brng, " +
                "  COALESCE(rd.jml,0) AS jml_resep, COALESCE(rd.satuan,'') AS satuan_resep, " +
                "  COALESCE(rd.aturan_pakai,'') AS aturan_pakai, " +
                "  COALESCE(rd.keterangan,'') AS ket_item, " +
                "  COALESCE(rd.nama_racikan,'') AS nama_racikan, " +
                "  COALESCE(dj.h_jual,0) AS h_jual, COALESCE(dj.subtotal,0) AS subtotal, " +
                "  COALESCE(dj.bsr_dis,0) AS diskon, COALESCE(dj.tambahan,0) AS tuslah, COALESCE(dj.total,0) AS total " +
                "FROM resep_toko r " +
                "JOIN resep_toko_detail rd ON rd.no_resep=r.no_resep " +
                "LEFT JOIN tokobarang b ON b.kode_brng=rd.kode_brng " +
                "LEFT JOIN dokter d ON d.kd_dokter=r.kd_dokter " +
                "LEFT JOIN toko_detail_jual dj ON dj.nota_jual=r.no_nota AND dj.kode_brng=rd.kode_brng " +
                "WHERE TRIM(COALESCE(r.no_nota,''))=? " +
                "ORDER BY r.tgl_resep, r.no_resep, rd.id";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noNota == null ? "" : noNota.trim());
            try (ResultSet rs = ps.executeQuery()) {
                String resepLast = "";
                double totalResep = 0;
                double grand = 0;
                boolean ada = false;
                boolean headerLoaded = false;

                while (rs.next()) {
                    ada = true;
                    String noResep = rs.getString("no_resep");
                    if (!headerLoaded) {
                        String noRawat = rs.getString("no_rawat");
                        if (noRawat != null && !noRawat.trim().isEmpty()) {
                            loadHeaderPasien(noRawat.trim());
                        }
                        headerLoaded = true;
                    }

                    if (!noResep.equals(resepLast)) {
                        if (!resepLast.isEmpty()) {
                            tabMode.addRow(new Object[]{
                                    "", "", "", "", "", "",
                                    "", "", "", "",
                                    "", "", "", "TOTAL RESEP :", totalResep
                            });
                            tabMode.addRow(new Object[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});
                        }

                        resepLast = noResep;
                        totalResep = 0;

                        tabMode.addRow(new Object[]{
                                rs.getString("tgl_resep"),
                                rs.getString("no_resep"),
                                rs.getString("no_nota"),
                                rs.getString("no_rawat"),
                                rs.getString("jenis_resep"),
                                rs.getString("nm_dokter"),
                                "", "", "", "",
                                "", "", "", "", ""
                        });

                        tabMode.addRow(new Object[]{
                                "", "", "", "", "", "",
                                "Nama Obat", "Jml", "Satuan", "Aturan Pakai",
                                "Harga", "Subtotal", "Diskon", "Tuslah", "Total"
                        });
                    }

                    double total = rs.getDouble("total");
                    totalResep += total;
                    grand += total;

                    String aturan = rs.getString("aturan_pakai");
                    String ketItem = rs.getString("ket_item");
                    String aturanGabung = (aturan == null ? "" : aturan.trim());
                    if (ketItem != null && !ketItem.trim().isEmpty()) {
                        if (!aturanGabung.isEmpty()) aturanGabung += " | ";
                        aturanGabung += ketItem.trim();
                    }

                    tabMode.addRow(new Object[]{
                            "", "", "", "", "", "",
                            rs.getString("nama_brng"),
                            rs.getDouble("jml_resep"),
                            rs.getString("satuan_resep"),
                            aturanGabung,
                            rs.getDouble("h_jual"),
                            rs.getDouble("subtotal"),
                            rs.getDouble("diskon"),
                            rs.getDouble("tuslah"),
                            total
                    });
                }

                if (!resepLast.isEmpty()) {
                    tabMode.addRow(new Object[]{
                            "", "", "", "", "", "",
                            "", "", "", "",
                            "", "", "", "TOTAL RESEP :", totalResep
                    });
                }

                LInfo.setText("Total (berdasarkan nota jika ada): " + grand);
                return ada;
            }
        } catch (Exception e) {
            System.out.println("Notifikasi loadResepByNoNota : " + e.getMessage());
        }
        return false;
    }

    private boolean loadPenjualanResepByNoRawat(String noRawat) {
        String sql =
                "SELECT " +
                "  tp.tgl_jual AS tgl_resep, '' AS no_resep, tp.no_rawat, tp.nota_jual AS no_nota, " +
                "  COALESCE(tp.jns_jual,'Resep') AS jenis_resep, COALESCE(d.nm_dokter,'-') AS nm_dokter, " +
                "  td.kode_brng, COALESCE(b.nama_brng,'') AS nama_brng, " +
                "  COALESCE(td.jumlah,0) AS jml_resep, COALESCE(ks.satuan,'') AS satuan_resep, " +
                "  '' AS aturan_pakai, '' AS ket_item, '' AS nama_racikan, " +
                "  COALESCE(td.h_jual,0) AS h_jual, COALESCE(td.subtotal,0) AS subtotal, " +
                "  COALESCE(td.bsr_dis,0) AS diskon, COALESCE(td.tambahan,0) AS tuslah, COALESCE(td.total,0) AS total " +
                "FROM tokopenjualan tp " +
                "INNER JOIN toko_detail_jual td ON td.nota_jual = tp.nota_jual " +
                "LEFT JOIN tokobarang b ON b.kode_brng = td.kode_brng " +
                "LEFT JOIN kodesatuan ks ON ks.kode_sat = td.kode_sat " +
                "LEFT JOIN dokter d ON d.kd_dokter = tp.kd_dokter " +
                "WHERE tp.no_rawat = ? AND tp.jns_jual IN ('Resep','Resep Luar') " +
                "ORDER BY tp.tgl_jual, tp.nota_jual, td.kode_brng";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            return tampilkanFallbackPenjualan(ps);
        } catch (Exception e) {
            System.out.println("Notifikasi loadPenjualanResepByNoRawat : " + e.getMessage());
        }
        return false;
    }

    private boolean loadPenjualanResepByNoNota(String noNota) {
        String sql =
                "SELECT " +
                "  tp.tgl_jual AS tgl_resep, '' AS no_resep, COALESCE(tp.no_rawat,'') AS no_rawat, tp.nota_jual AS no_nota, " +
                "  COALESCE(tp.jns_jual,'Resep') AS jenis_resep, COALESCE(d.nm_dokter,'-') AS nm_dokter, " +
                "  td.kode_brng, COALESCE(b.nama_brng,'') AS nama_brng, " +
                "  COALESCE(td.jumlah,0) AS jml_resep, COALESCE(ks.satuan,'') AS satuan_resep, " +
                "  '' AS aturan_pakai, '' AS ket_item, '' AS nama_racikan, " +
                "  COALESCE(td.h_jual,0) AS h_jual, COALESCE(td.subtotal,0) AS subtotal, " +
                "  COALESCE(td.bsr_dis,0) AS diskon, COALESCE(td.tambahan,0) AS tuslah, COALESCE(td.total,0) AS total " +
                "FROM tokopenjualan tp " +
                "INNER JOIN toko_detail_jual td ON td.nota_jual = tp.nota_jual " +
                "LEFT JOIN tokobarang b ON b.kode_brng = td.kode_brng " +
                "LEFT JOIN kodesatuan ks ON ks.kode_sat = td.kode_sat " +
                "LEFT JOIN dokter d ON d.kd_dokter = tp.kd_dokter " +
                "WHERE tp.nota_jual = ? AND tp.jns_jual IN ('Resep','Resep Luar') " +
                "ORDER BY tp.tgl_jual, tp.nota_jual, td.kode_brng";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noNota == null ? "" : noNota.trim());
            return tampilkanFallbackPenjualan(ps);
        } catch (Exception e) {
            System.out.println("Notifikasi loadPenjualanResepByNoNota : " + e.getMessage());
        }
        return false;
    }

    private boolean tampilkanFallbackPenjualan(PreparedStatement ps) throws Exception {
        try (ResultSet rs = ps.executeQuery()) {
            String notaLast = "";
            double totalNota = 0;
            double grand = 0;
            boolean ada = false;
            boolean headerLoaded = false;

            while (rs.next()) {
                ada = true;
                String noNota = rs.getString("no_nota");

                if (!headerLoaded) {
                    String noRawat = rs.getString("no_rawat");
                    if (noRawat != null && !noRawat.trim().isEmpty()) {
                        loadHeaderPasien(noRawat.trim());
                    }
                    headerLoaded = true;
                }

                if (!noNota.equals(notaLast)) {
                    if (!notaLast.isEmpty()) {
                        tabMode.addRow(new Object[]{
                                "", "", "", "", "", "",
                                "", "", "", "",
                                "", "", "", "TOTAL NOTA :", totalNota
                        });
                        tabMode.addRow(new Object[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});
                    }

                    notaLast = noNota;
                    totalNota = 0;

                    tabMode.addRow(new Object[]{
                            rs.getString("tgl_resep"),
                            "(langsung)",
                            noNota,
                            rs.getString("no_rawat"),
                            rs.getString("jenis_resep"),
                            rs.getString("nm_dokter"),
                            "", "", "", "",
                            "", "", "", "", ""
                    });

                    tabMode.addRow(new Object[]{
                            "", "", "", "", "", "",
                            "Nama Obat", "Jml", "Satuan", "Aturan Pakai",
                            "Harga", "Subtotal", "Diskon", "Tuslah", "Total"
                    });
                }

                double total = rs.getDouble("total");
                totalNota += total;
                grand += total;

                tabMode.addRow(new Object[]{
                        "", "", "", "", "", "",
                        rs.getString("nama_brng"),
                        rs.getDouble("jml_resep"),
                        rs.getString("satuan_resep"),
                        rs.getString("aturan_pakai"),
                        rs.getDouble("h_jual"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("diskon"),
                        rs.getDouble("tuslah"),
                        total
                });
            }

            if (!notaLast.isEmpty()) {
                tabMode.addRow(new Object[]{
                        "", "", "", "", "", "",
                        "", "", "", "",
                        "", "", "", "TOTAL NOTA :", totalNota
                });
            }

            if (ada) {
                LInfo.setText("Total (fallback tokopenjualan): " + grand);
            }
            return ada;
        }
    }

    private String toSqlDate(Object selected) {
        if (selected == null) return "";

        java.util.Date d = null;

        if (selected instanceof java.util.Date) {
            d = (java.util.Date) selected;
        } else {
            String s = selected.toString().trim();
            String[] p = {
                    "dd-MM-yyyy",
                    "d/M/yy, h:mm a",
                    "d/M/yyyy, h:mm a",
                    "yyyy-MM-dd",
                    "EEE MMM dd HH:mm:ss zzz yyyy"
            };
            for (String pat : p) {
                try {
                    d = new java.text.SimpleDateFormat(pat, java.util.Locale.US).parse(s);
                    break;
                } catch (Exception ignored) {}
            }
            if (d == null && s.contains(",")) {
                try {
                    d = new java.text.SimpleDateFormat("d/M/yy", java.util.Locale.US)
                            .parse(s.split(",")[0].trim());
                } catch (Exception ignored) {}
            }
        }

        if (d == null) return "";
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    // =========================
    // BUKA Telaah (by NO NOTA)
    // =========================
    private void bukaResepTelaah(int autoprint) {
        String nota = getNoNotaAktifDariTabel();
        if (nota == null || nota.trim().isEmpty()) {
            String noresep = getNoResepAktifDariTabel();
            if (noresep == null) noresep = "";
            JOptionPane.showMessageDialog(this,
                    "Nota belum ada untuk baris yang dipilih.\n" +
                    "Biasanya resep ini masih 'baru' dan belum diproses jadi penjualan.\n\n" +
                    "No Resep: " + noresep);
            return;
        }

        try {
            String base = baseWebapps();
            String usere = koneksiDB.USERHYBRIDWEB();
            String pass  = koneksiDB.PASHYBRIDWEB();

            String url = base + TELAAH_PAGE
                    + "?nonota=" + URLEncoder.encode(nota.trim(), "UTF-8")
                    + "&autoprint=" + autoprint
                    + "&usere=" + URLEncoder.encode(usere, "UTF-8")
                    + "&passwordte=" + URLEncoder.encode(pass, "UTF-8");

            System.out.println("[TELAAH] " + url);
            openBrowser(url);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal buka Telaah:\n" + e.getMessage());
        }
    }

    private String getNoNotaAktifDariTabel() {
        int vRow = tbResep.getSelectedRow();
        if (vRow == -1) return "";

        int mRow = tbResep.convertRowIndexToModel(vRow);

        int colNota = 2; // "No Nota"
        for (int r = mRow; r >= 0; r--) {
            Object v = tabMode.getValueAt(r, colNota);
            if (v != null) {
                String s = v.toString().trim();
                if (s.startsWith("TJ") || s.matches("^[A-Z]{1,3}\\d+.*")) { // fleksibel
                    return s;
                }
            }
        }
        return "";
    }

    private String getNoResepAktifDariTabel() {
        int vRow = tbResep.getSelectedRow();
        if (vRow == -1) return "";

        int mRow = tbResep.convertRowIndexToModel(vRow);

        int colResep = 1; // "No Resep"
        for (int r = mRow; r >= 0; r--) {
            Object v = tabMode.getValueAt(r, colResep);
            if (v != null) {
                String s = v.toString().trim();
                if (!s.isEmpty() && s.matches("^\\d+.*")) return s;
            }
        }
        return "";
    }

    private void openBrowser(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            try {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Tidak bisa membuka browser:\n" + url + "\nError: " + ex.getMessage());
            }
        }
    }

    private String baseWebapps() {
        String host = koneksiDB.HOSTHYBRIDWEB();
        String port = koneksiDB.PORTWEB();
        String web  = koneksiDB.HYBRIDWEB();

        host = (host == null) ? "" : host.trim();
        port = (port == null) ? "" : port.trim();
        web  = (web  == null) ? "" : web.trim();

        if (host.isEmpty()) host = "localhost";
        if (port.isEmpty()) port = "8080";
        if (web.isEmpty())  web  = "webapps";

        if (!host.startsWith("http://") && !host.startsWith("https://")) {
            host = "http://" + host;
        }

        if (!host.matches(".*:\\d+(/.*)?$")) {
            host = host + ":" + port;
        }

        while (host.endsWith("/")) host = host.substring(0, host.length() - 1);
        while (web.startsWith("/")) web = web.substring(1);
        while (web.endsWith("/")) web = web.substring(0, web.length() - 1);

        return host + "/" + web;
    }
}
