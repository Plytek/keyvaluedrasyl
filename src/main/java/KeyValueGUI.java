import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Getter
@Setter
public class KeyValueGUI {

    private ClientNode clientNode;
    private JPanel keyValuePanel;
    private JButton createButton;
    private JButton updateButton;
    private JButton readButton;
    private JButton deleteButton;
    private JTextArea outputFeld;
    private JTextField keyField;
    private JTextField valueField;
    private JLabel schluesselLabel;
    private JLabel valueLabel;
    private JButton connectToCoordinatorButton;
    private JButton connectToLocalMasterButton;
    private JButton connectToRemoteMasterButton;
    java.util.Timer timer;
    private List<Node> nodes;

    public KeyValueGUI() {
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientNode.create(keyField.getText(), valueField.getText());
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientNode.update(keyField.getText(), valueField.getText());
            }
        });
        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientNode.read(keyField.getText());
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientNode.delete(keyField.getText());
            }
        });
        connectToCoordinatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clientNode.connectToCoordinator();
            }
        });
        connectToLocalMasterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (Node n : nodes)
                {
                    if (n.isMaster())
                    {
                        clientNode.addMaster(n.identity().getAddress().toString());
                        clientNode.setNetworkonline(true);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(keyValuePanel, "Kein lokaler Node ist Master");
            }
        });
        connectToRemoteMasterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MasterAddressDialog m = new MasterAddressDialog();
            }
        });


        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                outputFeld.setText(clientNode.getResponsevalue());
            }
        },0, 1000);

    }

    protected class MasterAddressDialog extends JDialog
    {
        private JFormattedTextField addressTextField;
        private JButton ok;
        private JButton abbrechen;
        private JLabel label;

        public MasterAddressDialog()
        {
            super(new JFrame("MasterNode Addresse"), "MasterNode Addresse");
            label = new JLabel("Addresse:");
            addressTextField = new JFormattedTextField();
            ok = new JButton("Ok");
            ok.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    clientNode.addMaster(addressTextField.getText());
                    clientNode.setNetworkonline(true);
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
