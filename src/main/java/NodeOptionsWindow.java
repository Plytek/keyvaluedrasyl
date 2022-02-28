import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NodeOptionsWindow {
    private JButton ShutdownButton;
    private JButton StartButton;
    private JTable NodeTable;
    private JButton zeigeClusterButton;
    private JButton zeigeDatenButton;
    private JPanel panel;
    private Node node;
    private TableModel tableModel;
    private JFrame frame;
    NodeDataWindow w;
    ClusterInfoWindow cw;

    public NodeOptionsWindow(Node n)
    {
        node = n;
        ShutdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                node.shutdown().toCompletableFuture().join();
            }
        });
        StartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                node.start().toCompletableFuture().join();
            }
        });
        zeigeClusterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cw = new ClusterInfoWindow(node);
            }
        });
        zeigeDatenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                w = new NodeDataWindow(node);
            }
        });
        tableModel = new NodeOptionsTableModel();
        NodeTable.setModel(tableModel);
        NodeTable.setAutoCreateColumnsFromModel(true);
        frame = new JFrame("Node Options");
        frame.setContentPane(panel);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    public class NodeOptionsTableModel extends AbstractTableModel
    {
        private String[][] values;

        public NodeOptionsTableModel()
        {
            values = new String[7][];
            values[0] = new String[]{"Adresse", node.getAddress()};
            values[1] = new String[]{"Cluster", Integer.toString(node.getWelchercluster())};
            values[2] = new String[]{"Hashrange", Integer.toString(node.getHashrange())};
            values[3] = new String[]{"Coordinator", node.getCoordinator()};
            values[4] = new String[]{"Previous Master", node.getPreviousMaster()};
            values[5] = new String[]{"Next Master", node.getNextMaster()};
            values[6] = new String[]{"is Master", Boolean.toString(node.isMaster())};
        }

        @Override
        public int getRowCount() {
            return values.length;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int i, int i1) {
            return values[i][i1];
        }
    }
}
