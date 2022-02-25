import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class KeyvalueStoreApplication {

    public static void main(String[] args){

        try {

            DrasylConfig config;
            config = DrasylConfig.newBuilder().identityPath(Path.of("second.identity")).build();
            CoordinatorNode coordinatorNode = new CoordinatorNode(config);
            config = DrasylConfig.newBuilder().identityPath(Path.of("third.identity")).build();
            Node node2 = new Node(config);
            node2.setCoordinator("b1a6343a59f723efb93807b2f71727063bde5469a92a9124d7d0906e767c7899");
            config = DrasylConfig.newBuilder().identityPath(Path.of("fourth.identity")).build();
            Node node3 = new Node(config);
            node3.setCoordinator("b1a6343a59f723efb93807b2f71727063bde5469a92a9124d7d0906e767c7899");
            config = DrasylConfig.newBuilder().identityPath(Path.of("fifth.identity")).build();
            Node node4 = new Node(config);
            node4.setCoordinator("b1a6343a59f723efb93807b2f71727063bde5469a92a9124d7d0906e767c7899");
            config = DrasylConfig.newBuilder().identityPath(Path.of("sixth.identity")).build();
            Node node5 = new Node(config);
            node5.setCoordinator("2daf0e96db01cfbd8575ea645877158b03075bcbd7517166381bdab3019d1f72");
            config = DrasylConfig.newBuilder().identityPath(Path.of("seventh.identity")).build();
            Node node6 = new Node(config);
            node6.setCoordinator("2daf0e96db01cfbd8575ea645877158b03075bcbd7517166381bdab3019d1f72");
            config = DrasylConfig.newBuilder().identityPath(Path.of("eigth.identity")).build();
            Node node7 = new Node(config);
            node7.setCoordinator("2daf0e96db01cfbd8575ea645877158b03075bcbd7517166381bdab3019d1f72");
            config = DrasylConfig.newBuilder().identityPath(Path.of("nineth.identity")).build();
            Node node8 = new Node(config);
            node8.setCoordinator("2daf0e96db01cfbd8575ea645877158b03075bcbd7517166381bdab3019d1f72");
            config = DrasylConfig.newBuilder().identityPath(Path.of("tenth.identity")).build();
            Node node9 = new Node(config);
            node9.setCoordinator("2daf0e96db01cfbd8575ea645877158b03075bcbd7517166381bdab3019d1f72");

            coordinatorNode.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
/*            node1.start();
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
            }*/
           /* node4.start();
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
            node6.start();
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
            ClientNode clientNode = null;
            try {
                clientNode = new ClientNode();
            } catch (DrasylException e) {
                e.printStackTrace();
            }
            clientNode.setMainnodes(coordinatorNode.getMainnodes());
            testUI(clientNode);

            node2.start();
            node3.start();
            node4.start();


        } catch (DrasylException e) {
            e.printStackTrace();
        }
    }


    public static void testUI(ClientNode clientNode)
    {

        clientNode.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GUIController guiController = new GUIController(clientNode);




    }
}
