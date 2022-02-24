import Utility.ClientRequest;
import Utility.Utility;
import lombok.Getter;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;

import java.util.List;
import java.util.Random;

@Getter
@Setter
public class ClientNode extends DrasylNode
{
    List<String> mainnodes;

    protected ClientNode() throws DrasylException {
    }

    public void connect(String initialAddresse)
    {

    }

    public void create(String key, String value)
    {
        Random rand = new Random();
        String address = mainnodes.get(rand.nextInt(mainnodes.size()));
        ClientRequest request = new ClientRequest("create", key, value);
        request.set_recipient(address);
        request.set_sender(identity().getAddress().toString());
        send(address, Utility.getMessageContentJSON(request));
    }

    public void delete(String key)
    {
        Random rand = new Random();
        String address = mainnodes.get(rand.nextInt(mainnodes.size()));
        ClientRequest request = new ClientRequest("delete", key);
        send(address, Utility.getMessageContentJSON(request));
    }

    public void update(String key, String value)
    {
        Random rand = new Random();
        String address = mainnodes.get(rand.nextInt(mainnodes.size()));
        ClientRequest request = new ClientRequest("update", key, value);
        send(address, Utility.getMessageContentJSON(request));
    }

    public void read(String key)
    {
        Random rand = new Random();
        String address = mainnodes.get(rand.nextInt(mainnodes.size()));
        ClientRequest request = new ClientRequest("read", key);
        send(address, Utility.getMessageContentJSON(request));
    }

    @Override
    public void onEvent(Event event) {
        System.out.println("Event received: " + event);
    }
}
