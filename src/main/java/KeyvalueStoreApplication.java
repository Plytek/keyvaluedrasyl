import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class KeyvalueStoreApplication {
    static String coordinatorAddress = "ffc753dc5c344fc6d3fc2f0cab66337e9c05884362fa695363f1507c07870cc6";

    public static void main(String[] args) throws DrasylException {

        //try {
            ApplicationGUI a = new ApplicationGUI();
            /*List<Node> nodes = new LinkedList<Node>();
            CoordinatorNode coordinatorNode = new CoordinatorNode();
            DrasylConfig config;
            config = DrasylConfig.newBuilder().identityPath(Path.of("second.identity")).build();
            Node node1 = new Node(config);
            node1.setCoordinator(coordinatorAddress);
            config = DrasylConfig.newBuilder().identityPath(Path.of("third.identity")).build();
            Node node2 = new Node(config);
            node2.setCoordinator(coordinatorAddress);
            config = DrasylConfig.newBuilder().identityPath(Path.of("fourth.identity")).build();
            Node node3 = new Node(config);
            node3.setCoordinator(coordinatorAddress);


            config = DrasylConfig.newBuilder().identityPath(Path.of("fifth.identity")).build();

            Node node4 = new Node(config);
            node4.setCoordinator(coordinatorAddress);
            config = DrasylConfig.newBuilder().identityPath(Path.of("sixth.identity")).build();
            Node node5 = new Node(config);
            node5.setCoordinator(coordinatorAddress);
            config = DrasylConfig.newBuilder().identityPath(Path.of("seventh.identity")).build();
            Node node6 = new Node(config);
            node6.setCoordinator(coordinatorAddress);
            config = DrasylConfig.newBuilder().identityPath(Path.of("eigth.identity")).build();
            Node node7 = new Node(config);
            node7.setCoordinator(coordinatorAddress);
            config = DrasylConfig.newBuilder().identityPath(Path.of("nineth.identity")).build();
            Node node8 = new Node(config);
            node8.setCoordinator(coordinatorAddress);
            config = DrasylConfig.newBuilder().identityPath(Path.of("tenth.identity")).build();
            Node node9 = new Node(config);
            node9.setCoordinator(coordinatorAddress);

            nodes.add(node1);
            nodes.add(node2);
            nodes.add(node3);
            nodes.add(node4);
            nodes.add(node5);
            nodes.add(node6);
            nodes.add(node7);
            nodes.add(node8);
            nodes.add(node9);
            coordinatorNode.start();
            ApplicationGUI a = new ApplicationGUI(nodes, coordinatorNode);
            */
            /*DrasylConfig config = DrasylConfig.newBuilder().identityPath(Path.of("client.identity")).build();
            ClientNode c = new ClientNode(config);
            c.setCoordinator(coordinatorAddress);
            c.start();
            GUIController g = new GUIController(c);



            /*try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            node1.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            node2.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            node3.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            node4.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            node5.start();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            node6.start();*/
            /*
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            node7.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            node8.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            node9.start();*/



       /* } catch (DrasylException e) {
            e.printStackTrace();
        }*/
    }


    public static void testUI()
    {
        ClientNode clientNode = null;
        try {
            clientNode = new ClientNode();
        } catch (DrasylException e) {
            e.printStackTrace();
        }
        clientNode.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //GUIController guiController = new GUIController(clientNode);




    }
}
