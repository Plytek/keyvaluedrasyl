import javax.swing.*;

public class GUIController
{
    public GUIController(ClientNode clientNode)
    {
        KeyValueGUI keyValueGUI = new KeyValueGUI();
        keyValueGUI.setClientNode(clientNode);
        JFrame jFrame = new JFrame("Bank On It");
        jFrame.setContentPane(keyValueGUI.getKeyValuePanel());
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);

    }
}
