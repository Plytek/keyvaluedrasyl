import lombok.Getter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Getter
public class GUIController
{

    private JFrame jFrame;

    /**
     * Diese Klasse packt ein KeyValueGUI in einen JFrame und registriert den ClientNode. Die Liste an Nodes kann übergeben werden,
     * damit der Client sich einfacher mit einem lokalen Master Node verbinden kann.
     * @param clientNode ein ClientNode
     * @param nodes die Liste an lokalen Nodes.
     */
    public GUIController(ClientNode clientNode, List<Node> nodes)
    {
        KeyValueGUI keyValueGUI = new KeyValueGUI();
        keyValueGUI.setClientNode(clientNode);
        keyValueGUI.setNodes(nodes);
        jFrame = new JFrame("Bank On It");
        jFrame.setContentPane(keyValueGUI.getKeyValuePanel());
        jFrame.pack();
        jFrame.setVisible(true);
        try {
            jFrame.setIconImage(ImageIO.read(new File(getClass().getClassLoader().getResource("/resources/logo.png").getFile())));
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }

    }
}
