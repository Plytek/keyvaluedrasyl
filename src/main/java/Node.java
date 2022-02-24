import Utility.*;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;
import org.drasyl.node.event.MessageEvent;
import org.drasyl.node.event.NodeOfflineEvent;
import org.drasyl.node.event.NodeOnlineEvent;

import java.util.*;

@Getter
@Setter
public class Node extends DrasylNode
{
    private Timer timer;
    private boolean isMaster = false;
    private DrasylAddress previousMaster;
    private DrasylAddress nextMaster;
    private NodeRange range;
    private Map<DrasylAddress, Boolean> localCluster;

    private Timer confirmTimer;
    private Map<String, Message> confirmMessages = new HashMap<>();

    private Map<Integer, Map<String,String>> datastorage;

    protected Node(DrasylConfig config) throws DrasylException {
        super(config);
    }

    public Node() throws DrasylException {
        super();
    }

    public void registriereNodes()
    {

    }

    public void anfrageVerteilen(ClientRequest clientRequest)
    {
        //Muss eventuell noch modifziert werden wenn zB next master Range 0-X hat nach 6666-10000
        int verteilerhash = clientRequest.hashCode();
        if(verteilerhash < range.getLow())
        {
            send(previousMaster, Tools.getMessageAsJSONString(clientRequest));
        }
        else if(verteilerhash > range.getHigh())
        {
            send(nextMaster, Tools.getMessageAsJSONString(clientRequest));
        }
    }

    public String getAddress() {
        return this.identity().getAddress().toString();
    }

    public void sendHeartbeat(long intervall) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Heartbeat heartbeat = new Heartbeat();
                for(int i = 0; i < localCluster.size(); i++)
                {
                    DrasylAddress currentnode = (DrasylAddress) localCluster.keySet().toArray()[i];
                    boolean isMaster = localCluster.get(currentnode);
                    if(isMaster && !identity().getAddress().equals(currentnode))
                    {
                        heartbeat.setHeartbeat("masterheartbeat");
                        heartbeat.updateTimestamp();
                        send(currentnode, Tools.getMessageAsJSONString(heartbeat));
                    }
                    else if(!isMaster && !identity().getAddress().equals(currentnode))
                    {
                        heartbeat.setHeartbeat("secondaryheartbeat");
                        heartbeat.updateTimestamp();
                        send(currentnode, Tools.getMessageAsJSONString(heartbeat));
                    }
                }
                if(isMaster)
                {
                    heartbeat.setHeartbeat("masterheartbeat");
                    heartbeat.updateTimestamp();
                    send(previousMaster, Tools.getMessageAsJSONString(heartbeat));
                    send(nextMaster, Tools.getMessageAsJSONString(heartbeat));
                }
                }

        }, 0, intervall);
    }

    public void reportNewMaster()
    {

    }

    public void promote()
    {
        isMaster = true;
    }

    public void buildConsent()
    {

    }

    public void returnRequest(ClientResponse clientResponse)
    {
        send(clientResponse.get_recipient(), Tools.getMessageAsJSONString(clientResponse));
    }

    // nicht mit retrys bei confirmation, gehe von erfolg aus
    // falls confirmation nicht ankommt, so kommt nochmal eine Nachricht
    public void sendConfirmation(String token, String receiver) {
        Message confirmMessage = new Message(
                "confirm",
                this.identity.toString(),
                receiver
        );
        confirmMessage.set_token(token);


        this.send(receiver, Tools.getMessageAsJSONString(confirmMessage));
    }

    public void sendConfirmedMessage(Message message)
    {
        long currentTime = System.currentTimeMillis();
        message.set_time(currentTime);
        confirmMessages.put(message.get_token(), message);
        if(confirmTimer == null)
        {
            // start MessageConfirmer, falls noch nicht aktiv
            startMessageConfirmer(1000);
        }

        this.send(message.get_recipient(), Tools.getMessageAsJSONString(message));
    }

    public void checkTimeoutMessage(String token)
    {
        long currentTime = System.currentTimeMillis();
        Message message = confirmMessages.get(token);

        // nur handeln falls timeout  nach 5 Sekunden
        if(currentTime - message.get_time() > 5000)
        {
            // maximal 3 Timeouts
            if(message.get_counter() >= 3) {
                System.out.println("TODO: Dreimal Timeout bei Message Delivery!");
            } else {
                System.out.println("Timeout Nummer " + (message.get_counter()+1));
                // counter & time aktualisieren
                // erneut zustellen
                message.tickCounter();
                message.set_time(currentTime);
                this.send(message.get_recipient(), Tools.getMessageAsJSONString(message));
            }
        }
    }

    public void startMessageConfirmer(long intervall)
    {
        confirmTimer = new Timer();
        confirmTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for(String token : confirmMessages.keySet()) {
                    checkTimeoutMessage(token);
                }
            }

        }, 0, intervall);
    }


    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageEvent messageEvent)
        {
            String sender = messageEvent.getSender().toString();

            Message message = Tools.getMessageFromEvent(messageEvent);

            String messageType = message.get_messageType();

            String token = message.get_token();
            if(confirmMessages.containsKey(token))
            {
                // falls confirmation auf gesendete Message, so entferne aus confirm-Queue
                confirmMessages.remove(token);
            }
            else {
                // ansonsten sende selber confirmation
                sendConfirmation(token, sender);
            }

            switch(messageType)
            {
                case "heartbeat":
                    System.out.println("heartbeat");
                    break;
                case "confirmation":
                    System.out.println("confirmation");
                    break;
                default:
                    System.out.println("unknown message-type:" + messageType);
                    break;
            }
        }
        else {
            if(event instanceof NodeOnlineEvent)
            {
                System.out.println("NodeOnlineEvent");
            }
            else if(event instanceof NodeOfflineEvent)
            {
                System.out.println("NodeOfflineEvent");
            }
            else
            {
                System.out.println("unknown drasyl-event: " + event);
            }
        }
    }
}
