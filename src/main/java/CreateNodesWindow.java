import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        frame = new JFrame("Nodes erstellen");
        frame.setContentPane(panel);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


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
            JOptionPane.showMessageDialog(frame, "Bitte g??ltige Nummer eingeben");
            return 0;
        }
    }
}
