import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NodeDataWindow {
    private JTable nodesDataTable;
    private JPanel panel1;
    private JButton showMoreButton;
    private Node node;
    private Map<Integer, Map<String,String>> nodeData;
    TableModel tableModel;
    JFrame frame;
    NodeOptionsWindow w;
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
        frame = new JFrame("Node Data Window");
        frame.setContentPane(panel1);
        frame.setSize(600, 400);
        frame.setVisible(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
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
            return 3;
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
            return value.values().toArray()[0];
        }
    }
}
