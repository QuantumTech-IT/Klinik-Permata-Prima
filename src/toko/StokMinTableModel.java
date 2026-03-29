package toko;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class StokMinTableModel extends AbstractTableModel {

    private final String[] cols = {
            "Kode", "Nama", "Satuan",
            "Stok", "Stok Min",
            "Kurang", "Saran Order",
            "Expire", "Kondisi"
    };

    private List<TokobarangStokMinDao.StokMinRow> rows = new ArrayList<>();
    private final Map<String, BigDecimal> changedStokMin = new HashMap<>();

    public void setRows(List<TokobarangStokMinDao.StokMinRow> rows) {
        this.rows = (rows == null) ? new ArrayList<>() : rows;
        changedStokMin.clear();
        fireTableDataChanged();
    }

    public boolean hasChanges() {
        return !changedStokMin.isEmpty();
    }

    public List<ChangeStokMin> getChanges() {
        List<ChangeStokMin> out = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : changedStokMin.entrySet()) {
            out.add(new ChangeStokMin(e.getKey(), e.getValue()));
        }
        return out;
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 3:
            case 4:
            case 5: return BigDecimal.class;
            case 6: return Long.class;
            default: return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 4; // Stok Min editable
    }

    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        TokobarangStokMinDao.StokMinRow r = rows.get(rowIndex);
        switch (colIndex) {
            case 0: return r.kodeBrng;
            case 1: return r.namaBrng;
            case 2: return r.satuan;
            case 3: return bd(r.stok);
            case 4: {
                BigDecimal edited = changedStokMin.get(r.kodeBrng);
                return edited != null ? edited : bd(r.stokMin);
            }
            case 5: return bd(r.kurangExact);
            case 6: return r.saranOrder;
            case 7: return (r.expire != null) ? r.expire.toString() : "";
            case 8: return r.kondisi;
            default: return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int colIndex) {
        if (colIndex != 4) return;

        TokobarangStokMinDao.StokMinRow r = rows.get(rowIndex);

        BigDecimal v = parseBD(aValue);
        if (v == null) return;

        if (v.compareTo(BigDecimal.ZERO) < 0) v = BigDecimal.ZERO;
        v = v.setScale(2, RoundingMode.HALF_UP);

        changedStokMin.put(r.kodeBrng, v);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    private static BigDecimal bd(BigDecimal v) {
        return v == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : v;
    }

    private static BigDecimal parseBD(Object v) {
        try {
            if (v == null) return null;
            if (v instanceof BigDecimal) return (BigDecimal) v;
            String s = v.toString().trim().replace(",", ".");
            if (s.isEmpty()) return null;
            return new BigDecimal(s);
        } catch (Exception e) {
            return null;
        }
    }

    public static class ChangeStokMin {
        public final String kodeBrng;
        public final BigDecimal stokMinBaru;
        public ChangeStokMin(String kodeBrng, BigDecimal stokMinBaru) {
            this.kodeBrng = kodeBrng;
            this.stokMinBaru = stokMinBaru;
        }
    }
}
