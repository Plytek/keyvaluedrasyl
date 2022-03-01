import lombok.Getter;

import javax.swing.*;

@Getter
public class GUIController
{

    private JFrame jFrame;
    public GUIController(ClientNode clientNode)
    {
        KeyValueGUI keyValueGUI = new KeyValueGUI();
        keyValueGUI.setClientNode(clientNode);
        jFrame = new JFrame("Bank On It");
        jFrame.setContentPane(keyValueGUI.getKeyValuePanel());
        jFrame.pack();
        jFrame.setVisible(true);

    }
}
