import Utility.ClientResponse;
import Utility.Tools;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GUIControllerTest
{
  @Test
  public void testUI()
  {
      ClientNode clientNode = null;
      try {
          clientNode = new ClientNode();
      } catch (DrasylException e) {
          e.printStackTrace();
      }
      //GUIController guiController = new GUIController(clientNode);
  }

  @Test
    public void doesResponseShowUp()
  {
      DrasylConfig config = DrasylConfig.newBuilder().identityPath(Path.of("second.identity")).build();
      Node node1 = null;
      try {
          node1 = new Node(config);
      } catch (DrasylException e) {
          e.printStackTrace();
      }
      ClientResponse clientResponse = new ClientResponse("Testresponse");
      node1.start();
      try {
          Thread.sleep(1000);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      node1.send("2daf0e96db01cfbd8575ea645877158b03075bcbd7517166381bdab3019d1f72", Tools.getMessageAsJSONString(clientResponse)).toCompletableFuture().join();

      node1.start();
      try {
          Thread.sleep(10000);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }

  }
}