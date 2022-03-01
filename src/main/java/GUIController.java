import lombok.Getter;

import javax.swing.*;
import java.util.List;

@Getter
public class GUIController
{

    private JFrame jFrame;
    public GUIController(ClientNode clientNode, List<Node> nodes)
    {
        KeyValueGUI keyValueGUI = new KeyValueGUI();
        keyValueGUI.setClientNode(clientNode);
        keyValueGUI.setNodes(nodes);
        jFrame = new JFrame("Bank On It");
        jFrame.setContentPane(keyValueGUI.getKeyValuePanel());
        jFrame.pack();
        jFrame.setVisible(true);

    }
}
