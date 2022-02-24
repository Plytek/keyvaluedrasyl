import org.drasyl.node.DrasylException;
import org.junit.jupiter.api.Test;

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
      GUIController guiController = new GUIController(clientNode);
  }
}