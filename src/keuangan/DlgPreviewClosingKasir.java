/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keuangan;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author User
 */
class DlgPreviewClosingKasir extends JDialog {
    private boolean approved = false;

      public DlgPreviewClosingKasir(Window owner,
      String tgl, String shift, String user,
      double modal, double uangMasuk, double pengeluaran,
      double totalSistem, double setoran, double selisih,
      String tAwal, String tAkhir) {
    super(owner, "Preview Tutup Kasir", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel pnl = new JPanel(new BorderLayout(12,12));
        pnl.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // header info
        JPanel head = new JPanel(new GridLayout(2,1));
        head.add(new JLabel("Tanggal: "+tgl+"   Shift: "+shift));
        head.add(new JLabel("User: "+user+(tAwal!=null && tAkhir!=null ? 
            "   Rentang: "+tAwal+" s.d. "+tAkhir : "")));
        pnl.add(head, BorderLayout.NORTH);

        // ringkasan
        JPanel grid = new JPanel(new GridLayout(0,3,6,6));
        grid.add(new JLabel("Modal Awal"));   grid.add(new JLabel(":")); grid.add(new JLabel(rupiah(modal)));
        grid.add(new JLabel("Uang Masuk"));   grid.add(new JLabel(":")); grid.add(new JLabel(rupiah(uangMasuk)));
        grid.add(new JLabel("Pengeluaran"));  grid.add(new JLabel(":")); grid.add(new JLabel(rupiah(pengeluaran)));
        grid.add(new JLabel(">> Total (Modal + Masuk – Pengeluaran)"));
        grid.add(new JLabel(":"));            grid.add(new JLabel("<html><b>"+rupiah(totalSistem)+"</b></html>"));
        grid.add(new JLabel("Tutup Kasir"));  grid.add(new JLabel(":")); grid.add(new JLabel(rupiah(setoran)));
        grid.add(new JLabel("Selisih"));      grid.add(new JLabel(":")); grid.add(new JLabel("<html><b>"+rupiah(selisih)+"</b></html>"));
        pnl.add(grid, BorderLayout.CENTER);

        // tombol
        JButton btnOk = new JButton("Konfirmasi & Simpan");
        JButton btnCancel = new JButton("Batal");
        btnOk.addActionListener(e -> { approved = true; dispose(); });
        btnCancel.addActionListener(e -> dispose());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnCancel);
        south.add(btnOk);
        pnl.add(south, BorderLayout.SOUTH);

        setContentPane(pnl);
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isApproved(){ return approved; }
    private static String rupiah(double n){
    java.text.NumberFormat f = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id","ID"));
    return f.format(n);
}
}
