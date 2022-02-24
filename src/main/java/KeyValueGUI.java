import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    java.util.Timer timer;

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

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                outputFeld.setText(clientNode.getResponsevalue());
            }
        },0, 1000);

    }
}
