import org.drasyl.node.DrasylException;

import java.util.ArrayList;
import java.util.List;

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

        List<String> adressen = new ArrayList<>();
        adressen.add("179d3a1c332e5ee2780e0530ccf2c3b009d28243c1bb147ffede52404267a2b3");
        adressen.add("05ecc87c306e7c6d08ad61ddbcc008732d73767720018ebb2b9cba78ddf2d39a");

        clientNode.setMainnodes(adressen);

        clientNode.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(String adresse : adressen)
        {
            clientNode.send(adresse, "registernode");
        }
    }
}
