import Utility.Heartbeat;
import Utility.Tools;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;

@Setter
public class ApplicationGUI {
    private JTable NodesTable;
    private JPanel NodesWindow;
    private JButton StartNodesButton;
    private JButton StopNodesButton;
    private List<Node> nodes;
    private NodesTableModel tableModel;
    private JFrame frame;
    private List<NodeDataWindow> dataWindows = new LinkedList<>();
    private List<NodeOptionsWindow> optionsWindows = new LinkedList<>();
    private Timer timer;

    public ApplicationGUI(List<Node> n)
    {
        nodes = n;
        StartNodesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (Node n : nodes)
                {
                    n.start().toCompletableFuture().join();
                }
            }
        });
        StopNodesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (Node n : nodes)
                {
                    n.shutdown().toCompletableFuture().join();
                }
            }
        });
        tableModel = new NodesTableModel();
        NodesTable.setModel(tableModel);
        NodesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = NodesTable.rowAtPoint(e.getPoint());
                int col= NodesTable.columnAtPoint(e.getPoint());
                if (col == 2)
                {
                    showMoreWindow(row);
                }
                else if (col == 3)
                {
                    showDataWindow(row);
                }
            }
        });
        NodesTable.setAutoCreateColumnsFromModel(true);
        frame = new JFrame("Bank on It Node Controller");
        frame.setContentPane(NodesWindow);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tableModel.fireTableDataChanged();
                NodesTable.repaint();
            }

        }, 0, 500);
    }
    public void showMoreWindow(int nodeNr)
    {
        optionsWindows.add(new NodeOptionsWindow(nodes.get(nodeNr)));
    }

    public void showDataWindow(int nodeNr)
    {
        dataWindows.add(new NodeDataWindow(nodes.get(nodeNr)));
    }


    public class NodesTableModel extends AbstractTableModel {
        private final String[] headers = {
                "Cluster",
                "Master",
                "",
                ""
        };
        private List<Object[]> values;

        public NodesTableModel()
        {
            insertValues();
        }
        @Override
        public int getRowCount() {
            return nodes.size();
        }

        private void insertValues()
        {
            values = new LinkedList<>();
            for (Node n : nodes)
            {
                Object[] v = new Object[5];
                v[0] = n.getWelchercluster();
                v[1] = n.isMaster();
                //v[2] = n.isConnected();
                v[2] = "mehr...";
                v[3] = "Daten";
                values.add(v);
            }
        }

        public String getColumnName(int column) {
            return headers[column];
        }

        @Override
        public int getColumnCount() {
            return headers.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return values.get(rowIndex)[columnIndex];
        }

        @Override
        public void fireTableDataChanged()
        {
            insertValues();
        }


    }
}
