import javax.swing.table.AbstractTableModel;
import java.util.List;

public class CoordinatorNodesTableModel extends AbstractTableModel
{
    List<String> nodes;

    public CoordinatorNodesTableModel(List<String> n)
    {
        nodes = n;
    }
    @Override
    public int getRowCount() {
        return nodes.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int i, int i1) {
        return nodes.get(i);
    }
}