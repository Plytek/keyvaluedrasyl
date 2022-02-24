import Utility.Message;
import Utility.NodeRange;
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
    private List<Map<DrasylAddress, Boolean>> localCluster;

    private Timer confirmTimer;
    private Map<String, Message> confirmMessages;

    protected Node(DrasylConfig config) throws DrasylException {
        super(config);
    }

    public Node() throws DrasylException {
        super();
    }

    public void registriereNodes()
    {

    }

    public void anfrageVerteilen()
    {

    }

    public void sendHeartbeat(long intervall) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                        System.out.println("Placeholder");
                }

        }, 0, intervall);
    }

    public void reportNewMaster()
    {

    }

    public void promote()
    {

    }

    public void buildConsent()
    {

    }

    public void returnRequest()
    {

    }

    // nicht mit retrys bei confirmation, gehe von erfolg aus
    // falls confirmation nicht ankommt, so kommt nochmal eine Nachricht
    public void sendConfirmation(String token, String receiver) {
        Message confirmMessage = new Message(token, "Communication");

        this.send(receiver, confirmMessage.toString());
    }

    public void sendConfirmedMessage(Message message)
    {
        long currentTime = System.currentTimeMillis();
        message.setTime(currentTime);
        confirmMessages.put(message.getToken(), message);
        if(confirmTimer == null)
        {
            // start MessageConfirmer, falls noch nicht aktiv
            startMessageConfirmer(1000);
        }

        this.send(message.getReceiver(), message.toString());
    }

    public void checkTimeoutMessage(String token)
    {
        long currentTime = System.currentTimeMillis();
        Message message = confirmMessages.get(token);

        // nur handeln falls timeout  nach 5 Sekunden
        if(currentTime - message.getTime() > 5000)
        {
            // maximal 3 Timeouts
            if(message.getCounter() >= 3){
                System.out.println("TODO: Dreimal Timeout bei Message Delivery!");
            }
            else
            {
                // counter & time aktualisieren
                // erneut zustellen
                message.increaseCounter();
                message.setTime(currentTime);
                this.send(message.getReceiver(), message.toString());
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
            Object payload = messageEvent.getPayload();

            Message message = Message.fromPayload(payload);
            String messageType = message.getType();

            String token = Message.getToken();
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
                case "Heartbeat":
                    System.out.println("Heartbeat");
                    break;
                case "Confirmation":
                    System.out.println("Confirmation");
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
