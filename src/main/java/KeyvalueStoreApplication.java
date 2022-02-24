import Utility.Heartbeat;
import Utility.Message;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNodeSharedEventLoopGroupHolder;

import java.nio.file.Path;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

public class KeyvalueStoreApplication {

    public static void main(String[] args) throws DrasylException, InterruptedException {
        //testUI();
        DrasylConfig config1 = DrasylConfig.newBuilder().identityPath(Path.of("D:\\Studium\\Informatik-BSc\\Module\\Prak\\identity\\primary.identity")).build();
        DrasylConfig config2 = DrasylConfig.newBuilder().identityPath(Path.of("D:\\Studium\\Informatik-BSc\\Module\\Prak\\identity\\secondary.identity")).build();

        Node v  = new Node(config1);
        Node w  = new Node(config2);

        v.start();
        w.start();

        Thread.sleep(5000);
        v.sendConfirmedMessage(
                new Message(
                        "heartbeat",
                        UUID.randomUUID().toString(),
                        new Heartbeat(
                                "hello world!"
                        ),
                        v.getAddress(),
                        w.getAddress()
                )
        );


        //DrasylNodeSharedEventLoopGroupHolder.shutdown();
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
