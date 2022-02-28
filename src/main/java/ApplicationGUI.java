import lombok.Setter;
import org.drasyl.node.DrasylException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
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
    private JButton coordinatorNodeButton;
    private JButton coordinatorNodeAdresseButton;
    private List<Node> nodes;
    private NodesTableModel tableModel;
    private JFrame frame;
    private List<NodeDataWindow> dataWindows = new LinkedList<>();
    private List<NodeOptionsWindow> optionsWindows = new LinkedList<>();
    private Timer timer;
    private CoordinatorNode coordinator;
    private String coordinatorAddress;
    private CoordinatorNodeWindow corw;
    private CoordinatorAddressDialog cadrw;
    private ApplicationGUI myself;

    public ApplicationGUI(List<Node> n, CoordinatorNode coord)
    {
        myself = this;
        nodes = n;
        coordinator = coord;
        if (coordinator != null)
        {
            coordinatorAddress = coordinator.identity().getAddress().toString();
        }
        StartNodesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (Node n : nodes)
                {
                    coordinator.clearNodes();
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
        coordinatorNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (coordinator == null)
                {
                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    int dialogResult = JOptionPane.showConfirmDialog(frame, "CoordinatorNode wurde noch nicht erstellt. Soll einer erstellt werden?", "Kein CoordinatorNode", dialogButton);
                    if(dialogResult == 0) {
                        createCoordinator();
                    }
                    else
                    {
                        return;
                    }
                }
                corw = new CoordinatorNodeWindow(coordinator);
            }
        });
        coordinatorNodeAdresseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cadrw = new CoordinatorAddressDialog();
            }
        });
        tableModel = new NodesTableModel();
        NodesTable.setModel(tableModel);
        NodesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = NodesTable.rowAtPoint(e.getPoint());
                int col= NodesTable.columnAtPoint(e.getPoint());
                if (col == 3)
                {
                    showMoreWindow(row);
                }
                else if (col == 4)
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

    private void createCoordinator()
    {
        try {
            coordinator = new CoordinatorNode();
            coordinatorAddress = coordinator.identity().getAddress().toString();
            setCoordinatorAddressInNodes();
        } catch (DrasylException e) {
            e.printStackTrace();
        }

    }

    private void setCoordinatorAddressInNodes()
    {
        for (Node n : nodes)
        {
            n.setCoordinator(coordinatorAddress);
        }

    }

    public class NodesTableModel extends AbstractTableModel {
        private final String[] headers = {
                "Cluster",
                "Master",
                "Online",
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
                v[2] = n.isOnline();
                v[3] = "mehr...";
                v[4] = "Daten";
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

    public class CoordinatorAddressDialog extends JDialog
    {
        private JFormattedTextField addressTextField;
        private JButton ok;
        private JButton abbrechen;
        private JLabel label;
        private JFrame frame;

        public CoordinatorAddressDialog()
        {
            super(new JFrame("CoordinatorNode Addresse"), "CoordinatorNode Addresse");
            label = new JLabel("Addresse:");
            addressTextField = new JFormattedTextField();
            addressTextField.setText(coordinatorAddress);
            ok = new JButton("Ok");
            ok.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    coordinatorAddress = addressTextField.getText();
                    setCoordinatorAddressInNodes();
                    dispose();
                }
            });
            abbrechen = new JButton("Abbrechen");
            abbrechen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    dispose();
                }
            });
            setLayout(new GridLayout(2, 2, 5, 5));
            add(label);
            add(addressTextField);
            add(ok);
            add(abbrechen);
            pack();
            setVisible(true);
        }
    }

}
