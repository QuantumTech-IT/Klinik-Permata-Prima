/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
/**
 *
 * @author ALI-PC
 */
public class WarnaTableNota extends DefaultTableCellRenderer {
    private final int colNota; // index kolom No.Nota (MODEL)

    public WarnaTableNota(int colNota) {
        this.colNota = colNota;
        setOpaque(true);
    }

    private String s(Object o){ return (o==null) ? "" : o.toString().trim(); }

// ✅ hanya anggap NOTA kalau polanya cocok (silakan sesuaikan)
private boolean isNota(String v){
    v = (v == null) ? "" : v.trim();
    if (v.isEmpty()) return false;
    return v.matches("(?i)^TJ\\d{6,}$");  // contoh: TJ20260108000001
    // kalau prefix nota kamu beda, ganti regex-nya
}

// Ambil nota aktif untuk baris r: cari ke atas BARIS yang isinya memang NOTA
private String notaAt(javax.swing.table.TableModel m, int r){
    for(int i=r; i>=0; i--){
        String v = s(m.getValueAt(i, colNota));
        if(isNota(v)) return v;
    }
    return "";
}

@Override
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {

    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (isSelected) return c;

    // zebra
    c.setBackground((row % 2 == 1) ? new Color(255,244,244) : Color.white);
    c.setForeground(new Color(70,70,70));

    int mr = table.convertRowIndexToModel(row);
    javax.swing.table.TableModel m = table.getModel();

    String cur  = notaAt(m, mr);
    String prev = (mr > 0) ? notaAt(m, mr-1) : "";
    String next = (mr < m.getRowCount()-1) ? notaAt(m, mr+1) : "";

    boolean startGroup = !cur.isEmpty() && (mr == 0 || !cur.equals(prev));
    boolean endGroup   = !cur.isEmpty() && (mr == m.getRowCount()-1 || !cur.equals(next));

    if (c instanceof javax.swing.JComponent) {
        javax.swing.JComponent jc = (javax.swing.JComponent) c;

        if (startGroup || endGroup) {
            Color garis = new Color(16, 185, 129);
            int tebal = 3;
            int top = startGroup ? tebal : 0;
            int bot = endGroup ? tebal : 0;

            javax.swing.border.Border line = javax.swing.BorderFactory
                    .createMatteBorder(top, 0, bot, 0, garis);
            javax.swing.border.Border pad = javax.swing.BorderFactory
                    .createEmptyBorder(startGroup ? 4 : 0, 0, endGroup ? 4 : 0, 0);

            jc.setBorder(javax.swing.BorderFactory.createCompoundBorder(line, pad));
        } else {
            // ✅ penting: reset border supaya gak “nempel” ke baris lain
            jc.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
        }
    }

    return c;
}
}