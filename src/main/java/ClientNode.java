import Utility.*;
import lombok.Getter;
import lombok.Setter;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;
import org.drasyl.node.event.MessageEvent;
import org.drasyl.node.event.NodeOnlineEvent;

import java.util.List;
import java.util.Random;

@Getter
@Setter
public class ClientNode extends DrasylNode
{
    List<String> mainnodes;
    String responsevalue = "";
    String coordinator;
    boolean networkonline = false;

    protected ClientNode() throws DrasylException {
    }

    public void connect(String initialAddresse)
    {

    }

    public void create(String key, String value)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = mainnodes.get(rand.nextInt(mainnodes.size()));
            ClientRequest request = new ClientRequest("create", key, value);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            send(address, Tools.getMessageAsJSONString(request));
        } else { responsevalue = "NETWORK OFFLINE!\nWait for it to start or contact the Administrator";
        }
    }

    public void delete(String key)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = mainnodes.get(rand.nextInt(mainnodes.size()));
            ClientRequest request = new ClientRequest("delete", key);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            send(address, Tools.getMessageAsJSONString(request));
        }
        else { responsevalue = "NETWORK OFFLINE!\n Wait for it to start or contact the Administrator";
    }

    }

    public void update(String key, String value)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = mainnodes.get(rand.nextInt(mainnodes.size()));
            ClientRequest request = new ClientRequest("update", key, value);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            send(address, Tools.getMessageAsJSONString(request));
        }
        else { responsevalue = "NETWORK OFFLINE!\n Wait for it to start or contact the Administrator";
    }

    }

    public void read(String key)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = mainnodes.get(rand.nextInt(mainnodes.size()));
            ClientRequest request = new ClientRequest("read", key);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            send(address, Tools.getMessageAsJSONString(request));
        }
        else { responsevalue = "NETWORK OFFLINE!\n Wait for it to start or contact the Administrator";
    }
    }

    @Override
    public void onEvent(Event event) {
        System.out.println("Event received: " + event);
        if(event instanceof MessageEvent e)
        {
            Message message = null;
            message = Tools.getMessageFromEvent(e);

            switch (message.getMessageType())
            {
                case "clientresponse":
                {
                    ClientResponse response = (ClientResponse) message;
                    responsevalue = response.getResponse();
                    break;
                }
                case "networkonline":
                {
                    NodeResponse response = (NodeResponse) message;
                    mainnodes = response.getNodes();
                    networkonline = true;
                    responsevalue = "NETWORK ONLINE!";
                    break;
                }
                default:
                {
                    System.out.println("Hier gibts nix zu sehen");
                }
            }

        }
        else if(event instanceof NodeOnlineEvent e)
        {
            Message message = new Message();
            message.setMessageType("registerclient");
            message.setRecipient(coordinator);
            message.setSender(identity.getAddress().toString());
            message.generateToken();

            send(message.getRecipient(), Tools.getMessageAsJSONString(message));
        }
    }
}
