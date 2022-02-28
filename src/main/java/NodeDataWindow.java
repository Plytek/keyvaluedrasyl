import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NodeDataWindow {
    private JTable nodesDataTable;
    private JPanel panel1;
    private JButton showMoreButton;
    private JButton valueÄndernButton;
    private Node node;
    private Map<Integer, Map<String,String>> nodeData;
    TableModel tableModel;
    JFrame frame;
    NodeOptionsWindow w;
    EditNodeDataWindow ew;
    private Timer timer;

    public NodeDataWindow(Node n)
    {
        node = n;
        nodeData = n.getDatastorage();
        showMoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                w = new NodeOptionsWindow(node);
            }
        });
        tableModel = new NodesDataTableModel();
        nodesDataTable.setModel(tableModel);
        nodesDataTable.setAutoCreateColumnsFromModel(true);
        nodesDataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = nodesDataTable.rowAtPoint(e.getPoint());
                int col= nodesDataTable.columnAtPoint(e.getPoint());
                if (col == 3)
                {
                    refreshData();
                    nodeData.remove(nodesDataTable.getValueAt(row, 0));
                    node.setDatastorage(nodeData);
                }
            }
        });
        valueÄndernButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ew = new EditNodeDataWindow(node);
            }
        });
        frame = new JFrame("Node Data");
        frame.setContentPane(panel1);
        frame.setSize(600, 400);
        frame.setVisible(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshData();
                nodesDataTable.repaint();
            }

        }, 0, 500);
    }

    private void refreshData()
    {
        nodeData = node.getDatastorage();
    }

    public class NodesDataTableModel extends AbstractTableModel
    {

        public NodesDataTableModel()
        {
            refreshData();
        }

        @Override
        public int getRowCount() {
            return nodeData.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int i, int i1) {
            Integer key = 0;
            int x = 0;
            for (Integer k : nodeData.keySet()) {
                if (x == i)
                {
                    key = k;
                    break;
                }
                x++;
            }
            Map<String, String> value = nodeData.get(key);
            if (i1 == 0) {
                return key;
            }

            else if(i1 == 1)
            {
                return value.keySet().toArray()[0];
            }
            else if (i1 == 3)
            {
                return "löschen";
            }
            return value.values().toArray()[0];
        }
    }
}
