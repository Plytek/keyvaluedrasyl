import lombok.Setter;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;

@Setter
public class ApplicationGUI {
    private JTable NodesTable;
    private JPanel NodesWindow;
    private JButton StartNodesButton;
    private JButton StopNodesButton;
    private JButton coordinatorNodeButton;
    private JButton coordinatorNodeAdresseButton;
    private JButton clientNodeUIButton;
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
    private ApplicationGUI myself = this;
    private GUIController clientGUI;
    private ClientNode client;

    /**
     * Erstellt eine GUI, die mit Nodes und CoordinatorNodes arbeiten kann.
     * @param n eine Liste an Nodes
     * @param coord Referenz auf einen Coordinator node. Darf null sein.
     */
    public ApplicationGUI(List<Node> n, CoordinatorNode coord)
    {
        nodes = n;
        coordinator = coord;
        constructUI();
    }

    private void constructUI()
    {
        if (coordinator != null)
        {
            coordinatorAddress = coordinator.identity().getAddress().toString();
        }
        StartNodesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (coordinator == null && (coordinatorAddress == "" || coordinatorAddress == null))
                {
                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    int dialogResult = JOptionPane.showConfirmDialog(frame, "CoordinatorNode wurde noch nicht erstellt und es wurde keine Addresse angegeben. Soll einer erstellt werden?", "Kein CoordinatorNode", dialogButton);
                    if(dialogResult == 0) {
                        createCoordinator();
                    }
                    else {return;}
                }
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
                if (row < 0)
                {
                    return;
                }
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
        clientNodeUIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (clientGUI != null && client != null)
                {
                    clientGUI.getJFrame().setVisible(true);
                }
                else
                {
                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    int dialogResult = JOptionPane.showConfirmDialog(frame, "ClientNode wurde noch nicht erstellt. Soll einer erstellt werden?", "Kein ClientNode", dialogButton);
                    if(dialogResult == 0) {
                        createClient();
                        clientGUI.getJFrame().setVisible(true);
                    }
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

    /**
     * Erstellt eine GUI, die mit Nodes und CoordinatorNodes arbeiten kann.
     * @param n eine Liste an Nodes
     */
    public ApplicationGUI(List<Node> n)
    {
        this(n, null);
    }

    /**
     * Erstellt eine GUI, die mit Nodes und CoordinatorNodes arbeiten kann.
     * Erstellt zuerst ein Fenster, in der die gew??nschte Anzahl an Nodes eingegeben werden kann.
     */
    public ApplicationGUI() {
        CreateNodesWindow cnd = new CreateNodesWindow(this);
    }

    public void recieveCreateDialogResults(int n, boolean coord, boolean client)
    {
        nodes = new ArrayList<>();
        DrasylConfig config;
        for (int i = 0; i < n; i++)
        {
            try {
                config = DrasylConfig.newBuilder().identityPath(Path.of(i + ".identity")).build();
                nodes.add(new Node(config));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Das Erstellen von Node " + i + " ist fehlgeschlagen.");
            }
        }
        if (coord)
        {
            createCoordinator();
        }
        if(client)
        {
            createClient();
        }
        constructUI();
    }

    private void showMoreWindow(int nodeNr)
    {
        optionsWindows.add(new NodeOptionsWindow(nodes.get(nodeNr)));
    }

    private void showDataWindow(int nodeNr)
    {
        dataWindows.add(new NodeDataWindow(nodes.get(nodeNr)));
    }

    private void createCoordinator()
    {
        try {
            coordinator = new CoordinatorNode();
            coordinatorAddress = coordinator.identity().getAddress().toString();
            setCoordinatorAddressInNodes();
            coordinator.start().toCompletableFuture().join();
            CompletableFuture.allOf();
        } catch (DrasylException e) {
            e.printStackTrace();
        }

    }

    private void createClient()
    {
        try {
            DrasylConfig config = DrasylConfig.newBuilder().identityPath(Path.of("client.identity")).build();
            client = new ClientNode(config);
            if (coordinator != null) {
                client.setCoordinator(coordinatorAddress);
            }
            clientGUI = new GUIController(client);
            clientGUI.getJFrame().setVisible(false);
            client.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setCoordinatorAddressInNodes()
    {
        for (Node n : nodes)
        {
            n.setCoordinator(coordinatorAddress);
        }
        if (client != null)
        {
            client.setCoordinator(coordinatorAddress);
        }

    }

    protected class NodesTableModel extends AbstractTableModel {
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

    protected class CoordinatorAddressDialog extends JDialog
    {
        private JFormattedTextField addressTextField;
        private JButton ok;
        private JButton abbrechen;
        private JLabel label;

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
