import javax.swing.table.AbstractTableModel;
import java.util.List;

public class EffectTableModel extends AbstractTableModel {
    private final List<EffectResult> results;
    private final String[] columns = {"Probe ID", "Cohen's d", "Category"};

    public EffectTableModel(List<EffectResult> results) {
        this.results = results;
    }

    @Override public int getRowCount() { return results.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        EffectResult r = results.get(row);
        switch (col) {
            case 0: return r.getProbeID();
            case 1: return String.format("%.5f", r.getCohensD());
            case 2: return r.getCategory();
            default: return "";
        }
    }
}

