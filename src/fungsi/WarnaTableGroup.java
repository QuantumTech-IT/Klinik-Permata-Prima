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
/**
 *
 * @author ALI-PC
 */
public class WarnaTableGroup extends DefaultTableCellRenderer {
    private final int colGroup; // kolom penentu grup (No.Nota)

    public WarnaTableGroup(int colGroup) {
        this.colGroup = colGroup;
        setOpaque(true);
    }

    private String s(Object o){ return (o==null) ? "" : o.toString().trim(); }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // kalau sedang selected, biarkan warna default selection
        if (isSelected) return c;

        // zebra
        c.setBackground((row % 2 == 1) ? new Color(255,244,244) : Color.white);
        c.setForeground(new Color(70,70,70));

        // deteksi awal blok (No.Nota berubah)
        int mr = table.convertRowIndexToModel(row);
        String cur  = s(table.getModel().getValueAt(mr, colGroup));
        String prev = (mr > 0) ? s(table.getModel().getValueAt(mr - 1, colGroup)) : "";

        boolean newGroup = !cur.isEmpty() && (mr == 0 || !cur.equals(prev));

        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;

            if (newGroup) {
                Border line = BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(200,200,200)); // garis tebal
                Border pad  = BorderFactory.createEmptyBorder(6, 0, 0, 0); // “pace”/jarak atas
                jc.setBorder(BorderFactory.createCompoundBorder(line, pad));
            } else {
                jc.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
            }
        }

        return c;
    }
}

