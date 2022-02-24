import org.drasyl.node.DrasylException;

public class KeyvalueStoreApplication {

    public static void main(String[] args) {
        testUI();
    }


    public static void testUI()
    {
        ClientNode clientNode = null;
        try {
            clientNode = new ClientNode();
        } catch (DrasylException e) {
            e.printStackTrace();
        }

        GUIController guiController = new GUIController(clientNode);

    }
}
