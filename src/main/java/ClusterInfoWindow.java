import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.Map;

public class ClusterInfoWindow {
    private JTable InfoTable;
    private JPanel panel1;
    private JLabel clusterLabel;
    private Node node;
    private TableModel tableModel;
    private Map<String, Boolean> cluster;
    private JFrame frame;

    public ClusterInfoWindow(Node n)
    {
        node = n;
        cluster = n.getLocalCluster();
        tableModel = new ClusterInfoTableModel();
        InfoTable.setModel(tableModel);
        InfoTable.setAutoCreateColumnsFromModel(true);
        clusterLabel.setText(Integer.toString(node.getWelchercluster()));
        frame = new JFrame("Cluster " +  Integer.toString(node.getWelchercluster()) + " Info");
        frame.setContentPane(panel1);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    public class ClusterInfoTableModel extends AbstractTableModel
    {

        @Override
        public int getRowCount() {
            return cluster.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int i, int i1) {
            int x = 0;
            for (String k : cluster.keySet())
            {
                if (x == i)
                {
                    if (i1 == 0)
                    {
                        return k;
                    }
                    return cluster.get(k);
                }
                x++;
            }
            return null;
        }
    }
}
