import Utility.*;
import lombok.Getter;
import lombok.Setter;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.*;

import java.util.*;

@Getter
@Setter
public class ClientNode extends DrasylNode
{
    Set<String> mainnodes = new HashSet<>();
    String responsevalue = "";
    String coordinator;
    private boolean networkonline = false;
    MessageConfirmer messageConfirmer;
    Timer timer;

    protected ClientNode() throws DrasylException {
        messageConfirmer = new MessageConfirmer(this);
    }


    public ClientNode(DrasylConfig config) throws DrasylException {
        super(config);
        messageConfirmer = new MessageConfirmer(this);
    }

    public void create(String key, String value)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = (String) mainnodes.toArray()[rand.nextInt(0, mainnodes.size()-1)];
            ClientRequest request = new ClientRequest("create", key, value);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            responsevalue = "Waiting for Server response...";
            send(address, Tools.getMessageAsJSONString(request));
        } else { responsevalue = "NETWORK OFFLINE!\nWait for it to start or contact the Administrator";
        }
    }

    public void delete(String key)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = (String) mainnodes.toArray()[rand.nextInt(0, mainnodes.size()-1)];
            ClientRequest request = new ClientRequest("delete", key);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            responsevalue = "Waiting for Server response...";
            send(address, Tools.getMessageAsJSONString(request));
        }
        else { responsevalue = "NETWORK OFFLINE!\nWait for it to start or contact the Administrator";
    }

    }

    public void update(String key, String value)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = (String) mainnodes.toArray()[rand.nextInt(0, mainnodes.size()-1)];
            ClientRequest request = new ClientRequest("update", key, value);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            responsevalue = "Waiting for Server response...";
            send(address, Tools.getMessageAsJSONString(request));
        }
        else { responsevalue = "NETWORK OFFLINE!\nWait for it to start or contact the Administrator";
    }

    }

    public void read(String key)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = (String) mainnodes.toArray()[rand.nextInt(0, mainnodes.size()-1)];
            ClientRequest request = new ClientRequest("read", key);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            responsevalue = "Waiting for Server response...";
            send(address, Tools.getMessageAsJSONString(request));
        }
        else { responsevalue = "NETWORK OFFLINE!\nWait for it to start or contact the Administrator";
    }
    }

    public void sendHeartbeat(int intervall) {
        timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (mainnodes != null) {
                    for (String node : mainnodes) {
                        Heartbeat heartbeat = new Heartbeat();
                        heartbeat.setSender(identity.getAddress().toString());
                        heartbeat.setRecipient(node);
                        heartbeat.updateTimestamp();
                        heartbeat.setMessageType("heartbeat");
                        heartbeat.setHeartbeat("clientheartbeat");
                        messageConfirmer.sendMessage(heartbeat, m -> {
                            System.out.println(mainnodes);
                        }, () -> {
                            mainnodes.remove(node);

                        });
                    }
                    }
                }
            },0 , intervall);


    }


    @Override
    public void onEvent(Event event) {

        if(event instanceof MessageEvent e)
        {
            Message message = null;
            message = Tools.getMessageFromEvent(e);
            messageConfirmer.receiveMessage(message);

            switch (message.getMessageType())
            {
                case "clientresponse":
                {
                    ClientResponse response = (ClientResponse) message;
                    responsevalue = response.getResponse();
                    System.out.println("Event received: " + event);
                    break;
                }
                case "networkonline":
                {
                    NodeResponse response = (NodeResponse) message;
                    mainnodes = response.getNodes();
                    networkonline = true;
                    responsevalue = "NETWORK ONLINE!";
                    System.out.println("Event received: " + event);
                    sendHeartbeat(5000);
                    break;
                }
                case "noderesponse": {
                    NodeResponse response = (NodeResponse) message;
                    for(String address : response.getNodes()) {
                        if (!mainnodes.contains(address)) {
                            mainnodes.add(address);
                            System.out.println(mainnodes);
                        }
                    }
                    break;
                }
                default:
                {
                    break;
                }
            }

        }
        else if(event instanceof NodeOnlineEvent e)
        {
            sendHeartbeat(5000);
            Message message = new Message();
            message.setMessageType("registerclient");
            message.setRecipient(coordinator);
            message.setSender(identity.getAddress().toString());
            message.generateToken();

            send(message.getRecipient(), Tools.getMessageAsJSONString(message));
        }
        else if(event instanceof NodeDownEvent e)
        {
            timer.cancel();
        }
    }
}
