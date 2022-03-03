import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;

public class CreateNodesWindow {
    private JButton okButton;
    private JFormattedTextField anzahlNodesFeld;
    private JButton upButton;
    private JButton downButton;
    private JPanel innerPanel;
    private JPanel panel;
    private JCheckBox coordinatorNodeErstellenCheckBox;
    private JCheckBox clientNodeErstellenCheckBox;
    private JFormattedTextField maxNodesFeld;
    private JFrame frame;

    /**
     * Über dieses Fenster lassen sich beliebig viele Nodes, ein ClientNode und ein CoordinatorNode erstellen.
     * Wird aufgerufen, falls an den Constructor von ApplicationGUI keine Parameter übergeben wurden.
     * @param gui Eine ApplicationGUI. In der Regel übergibt hier die ApplicationGUI sich selbst.
     */
    public CreateNodesWindow(ApplicationGUI gui)
    {
        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                anzahlNodesFeld.setText(Integer.toString(getIntegerFromField(anzahlNodesFeld) + 1));
            }
        });
        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                anzahlNodesFeld.setText(Integer.toString(getIntegerFromField(anzahlNodesFeld) - 1));
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int anzahl = getIntegerFromField(anzahlNodesFeld);
                if (coordinatorNodeErstellenCheckBox.isSelected()) {
                    int maxNodes = getIntegerFromField(maxNodesFeld);

                    if (!(maxNodes % 3 == 0)) {
                        JOptionPane.showMessageDialog(frame, "Maximale Anzahl der Nodes muss ein vielfaches von 3 sein!");
                        return;
                    }
                    if (anzahl != Integer.MIN_VALUE && maxNodes != Integer.MIN_VALUE)
                    {
                        gui.recieveCreateDialogResults(anzahl, true, clientNodeErstellenCheckBox.isSelected(), maxNodes);
                        frame.dispose();
                    }
                }
                else
                {
                    if (anzahl != Integer.MIN_VALUE)
                    {
                        gui.recieveCreateDialogResults(anzahl, false, clientNodeErstellenCheckBox.isSelected(), Integer.MIN_VALUE);
                        frame.dispose();
                    }
                }

            }
        });
        coordinatorNodeErstellenCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (coordinatorNodeErstellenCheckBox.isSelected())
                {
                    maxNodesFeld.setEnabled(true);
                    maxNodesFeld.setText("9");
                }
                else
                {
                    maxNodesFeld.setEnabled(false);
                    maxNodesFeld.setText("");
                }
            }
        });
        anzahlNodesFeld.setText("0");
        frame = new JFrame("Nodes erstellen");
        frame.setContentPane(panel);
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        maxNodesFeld.setEnabled(false);
        try {
            frame.setIconImage(ImageIO.read(getClass().getClassLoader().getResourceAsStream("logo.png")));
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
        frame.setVisible(true);
    }


    /**
     * Holt sich ein Integer aus dem Textfeld, wenn möglich, und setzt einen Boolean. Falls nicht, wird eine Fehlermeldung gezeigt.
     * @return das Integer aus dem Feld
     */
    public int getIntegerFromField(JFormattedTextField field)
    {
        try
        {
            int i =  Math.abs(Integer.parseInt(field.getText()));
            return i;
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(frame, "Bitte gültige Nummer eingeben");
            return Integer.MIN_VALUE;
        }
    }
}
