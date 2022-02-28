import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class CoordinatorNodeWindow {
    private JButton coordniatorNodeHerunterfahrenButton;
    private JButton coordinatorNodeStartenButton;
    private JTable InfoTable;
    private JButton zeigeRegistrierteNodesButton;
    private JButton zeigeMainNodesButton;
    private JButton zeigeResponseWaitMessagesButton;
    private JPanel panel;
    private CoordinatorNode node;
    private CoordinatorNodeInfoTableModel tableModel;
    private JFrame frame;
    private Timer timer;

    public CoordinatorNodeWindow(CoordinatorNode c)
    {
        node = c;
        coordniatorNodeHerunterfahrenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                node.shutdown().toCompletableFuture().join();
            }
        });
        coordinatorNodeStartenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                node.start().toCompletableFuture().join();
            }
        });
        tableModel = new CoordinatorNodeInfoTableModel();
        InfoTable.setModel(tableModel);
        InfoTable.setAutoCreateColumnsFromModel(true);
        frame = new JFrame("Node Options");
        frame.setContentPane(panel);
        frame.setSize(600, 400);
        frame.setVisible(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tableModel.insertValues();
                InfoTable.repaint();
            }

        }, 0, 500);

    }

    public class CoordinatorNodeInfoTableModel extends AbstractTableModel
    {
        String[][] values;

        public CoordinatorNodeInfoTableModel()
        {
            insertValues();
        }

        public void insertValues()
        {
            values = new String[6][];
            values[0] = new String[]{"Adresse", node.identity().getAddress().toString()};
            values[1] = new String[]{"Maximale Nodes", Integer.toString(node.getMaxnodes())};
            values[2] = new String[]{"Range", Integer.toString(node.getRange())};
            values[3] = new String[]{"Cluster size", Integer.toString(node.getClustersize())};
            values[4] = new String[]{"Number", Integer.toString(node.getNumber())};
            values[5] = new String[]{"is Online", Boolean.toString(node.isOnline())};
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
