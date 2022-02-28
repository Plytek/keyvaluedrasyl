import Utility.*;
import lombok.Getter;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.*;

import java.util.*;

@Getter
@Setter
public class Node extends DrasylNode
{
    private Timer timer = new Timer();
    private boolean isMaster = false;
    private String previousMaster;
    private String nextMaster;
    private String coordinator;
    private NodeRange range;
    private Map<String, Boolean> localCluster;
    private int welchercluster;
    private int hashrange;
    private boolean isOnline;

    private Map<Integer, Map<String,String>> datastorage = new HashMap<>();
    private MessageConfirmer messageConfirmer;

    protected Node(DrasylConfig config) throws DrasylException {
        super(config);

        messageConfirmer = new MessageConfirmer(this);
    }

    public Node() throws DrasylException {
        super();

        messageConfirmer = new MessageConfirmer(this);
    }

    public void registriereNodes()
    {

    }

     public String getAddress() {
        return this.identity().getAddress().toString();
    }

    public void sendHeartbeat(long intervall) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Heartbeat heartbeat = new Heartbeat();
                for(int i = 0; i < localCluster.size(); i++)
                {
                    String currentnode = (String) localCluster.keySet().toArray()[i];
                    boolean isMaster = localCluster.get(currentnode);
                    heartbeat.setSender(identity.getAddress().toString());
                    heartbeat.setRecipient(currentnode);
                    heartbeat.updateTimestamp();

                    if(isMaster && !identity().getAddress().toString().equals(currentnode))
                    {
                        heartbeat.setHeartbeat("masterheartbeat");
                        heartbeat.setBemerkung("Von Master an Secondary");
                        send(currentnode, Tools.getMessageAsJSONString(heartbeat));
                    }
                    else if(!isMaster && !identity().getAddress().toString().equals(currentnode))
                    {
                        heartbeat.setHeartbeat("secondaryheartbeat");
                        heartbeat.setBemerkung("Von Secondary an Master");
                        send(currentnode, Tools.getMessageAsJSONString(heartbeat));
                    }
                }
                if(isMaster)
                {
                    heartbeat.setHeartbeat("masterheartbeat");
                    heartbeat.updateTimestamp();
                    heartbeat.setBemerkung("Von Cluster " + welchercluster + " an andere Master");
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

    public void handleClientRequest(ClientRequest clientRequest)
    {
        int requesthash = clientRequest.verteilerHash();
        System.out.println("Hashcode: " + requesthash);
        if(requesthash >= range.getLow() && requesthash <= range.getHigh())
        {
            if(isMaster)
            {
                for(int i = 0; i < localCluster.size(); i++)
                {
                    String adresse = (String) localCluster.keySet().toArray()[i];
                    if(!localCluster.get(adresse))
                    {
                        clientRequest.setBemerkung("Von " + welchercluster + " an secondary gesendet");
                        send(adresse, Tools.getMessageAsJSONString(clientRequest));
                    }
                }
            }
            switch (clientRequest.getRequestType()){
                case "create":
                {

                    ClientResponse clientResponse = new ClientResponse();
                    clientResponse.setRecipient(clientRequest.getSender());
                    clientResponse.setMessageType("clientresponse");
                    if(doesKeyExist(requesthash, clientRequest.getAffectedKey()))
                    {
                        clientResponse.setResponse("Key: " + clientRequest.getAffectedKey() + " in Benutzung, bitte update verwenden.");
                    }
                    else
                    {
                        handleCreate(requesthash, clientRequest.getAffectedKey(), clientRequest.getValue());
                        clientResponse.setResponse("Daten erfolgreich gespeichert!");
                    }

                    returnRequest(clientResponse);
                    break;
                }
                case "read":
                {
                    ClientResponse clientResponse = new ClientResponse();
                    clientResponse.setRecipient(clientRequest.getSender());
                    clientResponse.setMessageType("clientresponse");
                    if(doesKeyExist(requesthash, clientRequest.getAffectedKey()))
                    {
                        clientResponse.setResponse(datastorage.get(requesthash).get(clientRequest.getAffectedKey()));
                    }
                    else
                    {
                        clientResponse.setResponse("Keine Daten für Key: " + clientRequest.getAffectedKey() + " gefunden.");
                    }
                    returnRequest(clientResponse);
                    break;
                }
                case "delete":
                {
                    ClientResponse clientResponse = new ClientResponse();
                    clientResponse.setRecipient(clientRequest.getSender());
                    clientResponse.setMessageType("clientresponse");
                    if(doesKeyExist(requesthash, clientRequest.getAffectedKey()))
                    {
                        clientResponse.setResponse("Key: " + clientRequest.getAffectedKey() + " mit Daten: " +
                                datastorage.get(requesthash).get(clientRequest.getAffectedKey()) + " gelöscht");
                        handleDelete(requesthash, clientRequest.getAffectedKey());
                    }
                    else
                    {
                        clientResponse.setResponse("Keine Daten für Key: " + clientRequest.getAffectedKey() + " gefunden.");
                    }
                    returnRequest(clientResponse);
                    break;
                }
                case "update":
                {
                    ClientResponse clientResponse = new ClientResponse();
                    clientResponse.setMessageType("clientresponse");
                    clientResponse.setRecipient(clientRequest.getSender());

                    if(doesKeyExist(requesthash, clientRequest.getAffectedKey()))
                    {
                        clientResponse.setResponse("Key: " + clientRequest.getAffectedKey() + " Daten von: " +
                                datastorage.get(requesthash).get(clientRequest.getAffectedKey()) + " auf " + clientRequest.getValue() + " verändert!");
                        handleUpdate(requesthash, clientRequest.getAffectedKey(), clientRequest.getValue());
                    }
                    else
                    {
                        clientResponse.setResponse("Keine Daten für Key: " + clientRequest.getAffectedKey() + " gefunden.");
                    }

                    returnRequest(clientResponse);
                    break;
                }


            }

        }
        else if(requesthash > range.getHigh())
        {
            clientRequest.setBemerkung("Von " + welchercluster + " an next gesendet");
            send(nextMaster, Tools.getMessageAsJSONString(clientRequest));
        }
        else
        {
            clientRequest.setBemerkung("Von " + welchercluster + " an prev gesendet");
            send(previousMaster, Tools.getMessageAsJSONString(clientRequest));
        }
        for(Integer key : datastorage.keySet())
        {
            Map<String, String> data = datastorage.get(key);
            for(Map.Entry<String, String> entry : data.entrySet())
            {
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
        }
    }

    public boolean doesKeyExist(int requesthash, String key)
    {
        try {
            datastorage.get(requesthash).get(key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void handleDelete(int requesthash, String key)
    {
        datastorage.get(requesthash).remove(key);
        datastorage.remove(requesthash);
    }

    public void handleUpdate(int requesthash, String key, String value)
    {
        handleDelete(requesthash, key);
        handleCreate(requesthash, key, value);
    }

    public void handleCreate(int requesthash, String key, String value)
    {
        datastorage.computeIfAbsent(requesthash, k -> new HashMap<>()).put(key, value);

    }

    public void returnRequest(ClientResponse clientResponse)
    {
        send(clientResponse.getRecipient(), Tools.getMessageAsJSONString(clientResponse));
    }




    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageEvent messageEvent)
        {
            System.out.println("messageevent: " + messageEvent);
            String sender = messageEvent.getSender().toString();
            Message message = Tools.getMessageFromEvent(messageEvent);
            String messageType = message.getMessageType();

            // Lasse MessageConfirmer wissen, dass Nachricht eingetroffen
            // Ansonsten weiß MessageConfirmer nicht, ob Nachrichten bestätigt wurden!
            messageConfirmer.receiveMessage(message);

            switch(messageType)
            {
                case "heartbeat":
                    Heartbeat heartbeat = (Heartbeat) message;
                    System.out.println("Heartbeat: " + heartbeat.getBemerkung());
                    break;
                case "confirmation":
                    System.out.println("confirmation");
                    break;
                case "settings":
                    Settings settings = (Settings) message;

                        isMaster = settings.isMaster();
                        List<String> cluster = settings.getLocalcluster();
                        if(localCluster == null) localCluster = new HashMap<>();
                        for(int i = 0; i < cluster.size(); i++)
                        {
                            boolean master;
                            if(i == 0) master = true;
                            else master = false;
                            localCluster.put(cluster.get(i), master);
                        }
                        previousMaster = settings.getPreviousmaster();
                        nextMaster = settings.getNextmaster();
                        welchercluster = settings.getClusterid();
                        range = new NodeRange(settings.getLow(), settings.getHigh());
                        hashrange = settings.getHashrange();

                        sendHeartbeat(5000);
                        System.out.println(localCluster.toString() + "\n" + isMaster + "\n" + previousMaster + "\n" + nextMaster + "\n" + range.toString() + "\n" + settings.getClusterid());
                    break;
                case "clientrequest":
                    ClientRequest request = (ClientRequest) message;
                    System.out.println("Bemerkung: " + request.getBemerkung() + " Cluster: " +welchercluster);
                    handleClientRequest((ClientRequest) message);

                default:
                    //System.out.println("unknown message-type:" + messageType);
                    break;
            }
        }
        else {
            if(event instanceof NodeOnlineEvent)
            {
                System.out.println("Drasylevent: " + event);
                Message message = new Message();
                message.setMessageType("registernode");
                message.setSender(identity.getAddress().toString());
                message.setRecipient(coordinator);
                send(coordinator, Tools.getMessageAsJSONString(message));
                isOnline = true;
            }
            else if(event instanceof NodeOfflineEvent)
            {
                System.out.println("NodeOfflineEvent");

            }
            else if (event instanceof NodeDownEvent)
            {
                isOnline = false;
            }
            else
            {
                //System.out.println("unknown drasyl-event: " + event);
            }
        }
    }
}
