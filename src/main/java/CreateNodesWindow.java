import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class CreateNodesWindow {
    private JButton okButton;
    private JFormattedTextField formattedTextField1;
    private JButton upButton;
    private JButton downButton;
    private JPanel innerPanel;
    private JPanel panel;
    private JCheckBox coordinatorNodeErstellenCheckBox;
    private JCheckBox clientNodeErstellenCheckBox;
    private JFrame frame;
    private int anzahl;
    private ApplicationGUI gui;
    private boolean validResult = false;

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
                formattedTextField1.setText(Integer.toString(getIntegerFromField() + 1));
            }
        });
        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                formattedTextField1.setText(Integer.toString(getIntegerFromField() - 1));
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                anzahl = getIntegerFromField();
                if (validResult)
                {
                    gui.recieveCreateDialogResults(anzahl, coordinatorNodeErstellenCheckBox.isSelected(), clientNodeErstellenCheckBox.isSelected());
                    frame.dispose();
                }
            }
        });
        formattedTextField1.setText("0");
        frame = new JFrame("Nodes erstellen");
        frame.setContentPane(panel);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            frame.setIconImage(ImageIO.read(new File("logo.png")));
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
    public int getIntegerFromField()
    {
        try
        {
            int i =  Math.abs(Integer.parseInt(formattedTextField1.getText()));
            validResult = true;
            return i;
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(frame, "Bitte gültige Nummer eingeben");
            return 0;
        }
    }
}
