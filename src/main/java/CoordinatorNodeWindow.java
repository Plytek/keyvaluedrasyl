import javax.crypto.MacSpi;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
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
    private JButton setzeMaximaleAnzahlNodesButton;
    private CoordinatorNode node;
    private CoordinatorNodeInfoTableModel tableModel;
    private JFrame frame;
    private Timer timer;
    private CoordinatorRegisteredNodesWindow regnw;
    private CoordinatorMainNodesWindow mainnw;
    private CoordinatorResponseWaitMessagesWindow rwmw;
    private MaxNodesDialog dialog;

    /**
     * Dieses Fenster zeigt Informationen des CoordinatorNodes und bietet einige Optionen an.
     * @param c ein CoordinatorNode
     */
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
        zeigeRegistrierteNodesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                regnw = new CoordinatorRegisteredNodesWindow(Collections.singletonList(node.getRegisterednodes()));
            }
        });
        zeigeMainNodesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainnw = new CoordinatorMainNodesWindow(node.getMainnodes());
            }
        });
        zeigeResponseWaitMessagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                rwmw = new CoordinatorResponseWaitMessagesWindow(node);
            }
        });
        setzeMaximaleAnzahlNodesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dialog = new MaxNodesDialog();
            }
        });
        tableModel = new CoordinatorNodeInfoTableModel();
        InfoTable.setModel(tableModel);
        InfoTable.setAutoCreateColumnsFromModel(true);
        frame = new JFrame("Coordinator Node");
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

    /**
     * TableModel für das CoordinatorNode Fenster
     */
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

    protected class MaxNodesDialog extends JDialog
    {
        private JFormattedTextField anzahlFeld;
        private JButton ok;
        private JButton abbrechen;
        private JLabel label;

        public MaxNodesDialog()
        {
            super(new JFrame("Maximale Anzahl an Nodes festlegen"), "Maximale Anzahl an Nodes festlegen");
            label = new JLabel("Maximale Anzahl Nodes: ");
            anzahlFeld = new JFormattedTextField();
            ok = new JButton("Ok");
            abbrechen = new JButton("Abbrechen");
            ok.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try
                    {
                        node.setMaxnodes(Integer.parseInt(anzahlFeld.getText()));
                        dispose();
                    }
                    catch (NumberFormatException e)
                    {
                        JOptionPane.showMessageDialog(frame, "Bitte eine gültige Nummer eingeben");
                    }
                }
            });
            abbrechen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    dispose();
                }
            });
            setLayout(new GridLayout(2, 2, 5, 5));
            add(label);
            add(anzahlFeld);
            add(ok);
            add(abbrechen);
            pack();
            setVisible(true);

        }
    }
}
