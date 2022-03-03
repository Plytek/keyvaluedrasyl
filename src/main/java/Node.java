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
public class Node extends DrasylNode
{
    private Timer timer = new Timer();
    private boolean isMaster = false;
    private String previousMaster;
    private String nextMaster;
    private String coordinator;
    private NodeRange range;
    private Map<String, Boolean> localCluster; // bool wer master ist
    private Map<String, Boolean> clusterOnline; // bool wer noch online ist

    private int welchercluster;
    private int hashrange;
    private boolean isOnline;

    private Map<Integer, Map<String,String>> datastorage;

    private Map<String, ConsensData> consensDataCollection = Collections.synchronizedMap(new HashMap<>());
    private MessageConfirmer messageConfirmer;

    protected Node(DrasylConfig config) throws DrasylException {
        super(config);

        messageConfirmer = new MessageConfirmer(this);
    }

    public Node() throws DrasylException {
        super();

        messageConfirmer = new MessageConfirmer(this);
    }

    public String getAddress() {
        return this.identity().getAddress().toString();
    }

    public void handleClusterHeartbeatSuccess(String address){
        clusterOnline.put(address, true);
    }

    public void handleClusterHeartbeatError(String address) {
        System.out.println("cluster-heartbeat error from " + getAddress() + " to " + address);
        clusterOnline.put(address, false);
        if(localCluster.get(address))
        {
            // offline node ist master
            // bestimme nächsten Master als
            String nextMaster = this.getAddress();

            for(int i = 0; i < localCluster.size(); i++) {
                String clusterAddress = (String) localCluster.keySet().toArray()[i];

                if (!clusterAddress.equals(address) && !clusterAddress.equals(this.getAddress()) && clusterOnline.get(clusterAddress)) {
                    if (clusterAddress.hashCode() > nextMaster.hashCode()) {
                        nextMaster = clusterAddress;
                    }
                }
            }

            localCluster.put(address, false);
            localCluster.put(nextMaster, true);

            if(nextMaster.equals(this.getAddress()))
            {
                promote(address);
            }
        }
        else
        {
            // offline node ist secondary
            // erstmal nichts zu tun
        }
    }

    public void sendHeartbeat(long intervall) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Falls nicht online, keine heartbeats mehr senden
                if(!isOnline) return;

                for(int i = 0; i < localCluster.size(); i++)
                {
                    Heartbeat heartbeat = new Heartbeat();
                    String currentnode = (String) localCluster.keySet().toArray()[i];

                    //boolean isMaster = localCluster.get(currentnode);
                    heartbeat.setSender(identity.getAddress().toString());
                    heartbeat.setRecipient(currentnode);
                    heartbeat.updateTimestamp();

                    if(!identity().getAddress().toString().equals(currentnode))
                    {
                        heartbeat.setHeartbeat("heartbeat");
                        heartbeat.setBemerkung("cluster hearbeat");

                        messageConfirmer.sendMessage(heartbeat,
                            () -> handleClusterHeartbeatSuccess(currentnode),
                            () -> handleClusterHeartbeatError(currentnode)
                        );
                    }
                }
            }

        }, 0, intervall);
    }

    public void promote(String oldMaster)
    {
         isMaster = true;

         Set<String> ringMasters = new HashSet<>();
         ringMasters.add(previousMaster);
         ringMasters.add(nextMaster);

         for(String ringMaster : ringMasters)
         {
             // skippe falls Liste auf sich selber zeigt
             if(ringMaster.equals(oldMaster)) continue;

             Message message = new Message("newmaster", getAddress(), ringMaster);
             message.generateToken();
             message.setBemerkung(oldMaster);

             messageConfirmer.sendMessage(
                 message,
                 () -> System.out.println("newmaster onSuccess"),
                 () -> System.out.println("newmaster onError")
             );
         }
    }

    public void handleClientRequest(ClientRequest clientRequest)
    {
        int requesthash = clientRequest.verteilerHash();
        boolean sonderfall = (range.getLow() == 0 || range.getHigh() == hashrange);

        System.out.println("Hashcode: " + requesthash);
        if(requesthash >= range.getLow() && requesthash <= range.getHigh())
        {

            if(isMaster)
            {
                clientRequest.setRecipient(identity.getAddress().toString());
                ClientResponse clientResponse;
                switch (clientRequest.getRequestType()) {
                    case "create": {
                        clientResponse = createClientResponse(requesthash, clientRequest);
                        break;
                    }
                    case "read": {
                        clientResponse = readClientResponse(requesthash, clientRequest);
                        break;
                    }
                    case "delete": {
                        clientResponse = deleteClientResponse(requesthash, clientRequest);
                        break;
                    }
                    case "update": {
                        clientResponse = updateClientResponse(requesthash, clientRequest);
                        break;
                    }
                    default:
                        clientResponse = new ClientResponse();
                        clientResponse.setBemerkung("You fucked up");
                        break;
                }

                ConsensData consensData = new ConsensData();
                consensData.setClientRequest(clientRequest);
                consensDataCollection.put(clientRequest.getToken(), consensData);
                handleClientResponse(clientRequest.getToken(), clientResponse);

                for(int i = 0; i < localCluster.size(); i++)
                {
                    String adresse = (String) localCluster.keySet().toArray()[i];
                    if(!localCluster.get(adresse))
                    {
                        clientRequest.setBemerkung("Von " + welchercluster + " an secondary gesendet");
                        send(adresse, Tools.getMessageAsJSONString(clientRequest));
                    }
                }

                Timer clientRequestTimer = new Timer();

                clientRequestTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handleClientResponse(clientRequest.getToken(), null);
                    }
                }, 5000); // 5s delay


            }
            else
            {
                ClientResponse clientResponse;
                switch (clientRequest.getRequestType()) {
                    case "create": {
                        clientResponse = createClientResponse(requesthash, clientRequest);
                        returnRequest(clientResponse);
                        break;
                    }
                    case "read": {
                        clientResponse = readClientResponse(requesthash, clientRequest);
                        returnRequest(clientResponse);
                        break;
                    }
                    case "delete": {
                        clientResponse = deleteClientResponse(requesthash, clientRequest);
                        returnRequest(clientResponse);
                        break;
                    }
                    case "update": {
                        clientResponse = updateClientResponse(requesthash, clientRequest);
                        returnRequest(clientResponse);
                        break;
                    }
                }
            }

        }
        else if(requesthash > range.getHigh() && !sonderfall)
        {
            clientRequest.setBemerkung("Von " + welchercluster + " an next gesendet");
            send(nextMaster, Tools.getMessageAsJSONString(clientRequest));
        }
        else if(sonderfall)
        {
            if(range.getLow() == 0 && requesthash > hashrange-range.getHigh())
            {
                clientRequest.setBemerkung("Von " + welchercluster + " an next gesendet");
                send(previousMaster, Tools.getMessageAsJSONString(clientRequest));
            }
            else if(range.getLow() == 0)
            {
                clientRequest.setBemerkung("Von " + welchercluster + " an next gesendet");
                send(nextMaster, Tools.getMessageAsJSONString(clientRequest));
            }
            else if(range.getHigh() == hashrange && requesthash < range.getLow() && requesthash > (range.getHigh()-range.getLow()))
            {
                clientRequest.setBemerkung("Von " + welchercluster + " an next gesendet");
                send(previousMaster, Tools.getMessageAsJSONString(clientRequest));
            }
            else
            {
                clientRequest.setBemerkung("Von " + welchercluster + " an next gesendet");
                send(nextMaster, Tools.getMessageAsJSONString(clientRequest));
            }
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

    public ClientResponse readClientResponse(int requesthash, ClientRequest clientRequest)
    {
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setRecipient(clientRequest.getRecipient());
        clientResponse.setMessageType("clientresponse");
        if(doesKeyExist(requesthash, clientRequest.getAffectedKey()))
        {
            clientResponse.setResponse(datastorage.get(requesthash).get(clientRequest.getAffectedKey()));
        }
        else
        {
            clientResponse.setResponse("Keine Daten für Key: " + clientRequest.getAffectedKey() + " gefunden.");
        }
        return clientResponse;
    }

    public ClientResponse updateClientResponse(int requesthash, ClientRequest clientRequest)
    {
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setMessageType("clientresponse");
        clientResponse.setRecipient(clientRequest.getRecipient());
        if(doesKeyExist(requesthash, clientRequest.getAffectedKey()))
        {
            clientResponse.setOldvalue(datastorage.get(requesthash).get(clientRequest.getAffectedKey()));
            clientResponse.setResponse("Daten erfolgreich geupdatet");
            handleUpdate(requesthash, clientRequest.getAffectedKey(), clientRequest.getValue());
        }
        else
        {
            clientResponse.setResponse("Keine Daten für Key: " + clientRequest.getAffectedKey() + " gefunden.");
        }
        return clientResponse;
    }

    public ClientResponse deleteClientResponse(int requesthash, ClientRequest clientRequest)
    {
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setRecipient(clientRequest.getRecipient());
        clientResponse.setMessageType("clientresponse");
        if(doesKeyExist(requesthash, clientRequest.getAffectedKey()))
        {
            clientResponse.setOldvalue(datastorage.get(requesthash).get(clientRequest.getAffectedKey()));
            clientResponse.setResponse("Daten erfolgreich gelöscht");
            handleDelete(requesthash, clientRequest.getAffectedKey());
        }
        else
        {
            clientResponse.setResponse("Keine Daten für Key: " + clientRequest.getAffectedKey() + " gefunden.");
        }
        return clientResponse;
    }

    public ClientResponse createClientResponse(int requesthash, ClientRequest clientRequest)
    {
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setRecipient(clientRequest.getRecipient());
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
        return clientResponse;
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

    // synchronized flag -> sind hier gleichzeitig nur einmal drin!
    private synchronized void handleClientResponse(String token, ClientResponse clientResponse) {
        boolean timeoutHappened = clientResponse == null;
        // nur handeln falls Abstimmung noch nich beendet
        if(consensDataCollection.containsKey(token)) {
            ConsensData consensData = consensDataCollection.get(token);
            ClientRequest clientRequest = consensData.getClientRequest();
            if(clientResponse != null) consensData.getClientResponses().add(clientResponse);

            // Gibt es schon Konsens?
            ClientResponse consensResponse = consensData.findConsens();

            // Falls es einen Konsens gibt, den Konsens an Clienten schicken
            if(consensResponse != null)
            {
                consensResponse.setRecipient(consensData.getClientRequest().getSender());

                consensDataCollection.remove(token);
                returnRequest(consensResponse);
            }
            // Falls es keinen Konsens gibt und (ein Timeout passiert ist oder schon alle Antworten da sind)
            // Dann schicke "Failed Consens!" an Clienten
            // Außerdem reverse MainNode-Entscheidung, die dann als einziges reversed werden muss
            else if(timeoutHappened || (consensData.getClientResponses().size() >= 3)){
                ClientResponse mainResponse = consensData.getClientResponses().get(0);
                switch (clientRequest.getRequestType()){
                    case "create":
                        handleDelete(clientRequest.verteilerHash(), clientRequest.getAffectedKey());
                        break;
                    case "delete":
                        handleCreate(clientRequest.verteilerHash(), clientRequest.getAffectedKey(), mainResponse.getOldvalue());
                        break;
                    case "update":
                        if(mainResponse.getOldvalue() != null)
                        {
                            handleUpdate(clientRequest.verteilerHash(), clientRequest.getAffectedKey(), mainResponse.getOldvalue());
                        }
                }
                ClientResponse failedConsensResponse = new ClientResponse();
                failedConsensResponse.setRecipient(clientRequest.getSender());
                failedConsensResponse.setMessageType("clientresponse");
                failedConsensResponse.setResponse("Failed to reach Consensus!");

                consensDataCollection.remove(token);
                returnRequest(failedConsensResponse);
            }
        }
    }
    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageEvent messageEvent)
        {
            //System.out.println("messageevent: " + messageEvent);
            String sender = messageEvent.getSender().toString();
            Message message = Tools.getMessageFromEvent(messageEvent);
            String messageType = message.getMessageType();

            // Lasse MessageConfirmer wissen, dass Nachricht eingetroffen
            // Ansonsten weiß MessageConfirmer nicht, ob Nachrichten bestätigt wurden!
            messageConfirmer.receiveMessage(message);

            switch(messageType)
            {
                case "newmaster":
                    String oldMaster = message.getBemerkung();
                    if(previousMaster.equals(oldMaster)){
                        previousMaster = message.getSender();
                    }
                    else if(nextMaster.equals(oldMaster)){
                        nextMaster = message.getSender();
                    }
                    else {
                        System.out.println("newmaster: oldMaster unbekannt");
                    }
                    break;
                case "heartbeat":
                    Heartbeat heartbeat = (Heartbeat) message;
                    if(heartbeat.getHeartbeat().equals("clientheartbeat") && isMaster)
                    {
                        NodeResponse nodeResponse = new NodeResponse();
                        nodeResponse.setMessageType("noderesponse");
                        nodeResponse.setSender(identity.getAddress().toString());
                        nodeResponse.setRecipient(message.getSender());
                        nodeResponse.setNodes(Set.of(previousMaster, nextMaster));
                        send(nodeResponse.getRecipient(), Tools.getMessageAsJSONString(nodeResponse));
                    }
                    break;
                case "settings":
                    Settings settings = (Settings) message;

                        if(!settings.isInitialSettings() && datastorage != null)
                        {
                            if(isMaster)
                            {
                                NodeResponse backupdata = new NodeResponse();
                                backupdata.setMessageType("noderesponse");
                                backupdata.setDatastorage(datastorage);
                                backupdata.setSender(getAddress());
                                backupdata.setRecipient(settings.getSender());
                                backupdata.generateToken();
                                messageConfirmer.sendMessage(backupdata, () -> {
                                    System.out.print("");
                                }, () -> System.out.println("Fehler bei Dateübertragung"));
                            }
                            datastorage.clear();
                        }
                        isMaster = settings.isMaster();
                        List<String> cluster = settings.getLocalcluster();
                        localCluster = new HashMap<>();
                        clusterOnline = new HashMap<>();
                        for(int i = 0; i < cluster.size(); i++)
                        {
                            boolean master;
                            if(i == 0) master = true;
                            else master = false;
                            localCluster.put(cluster.get(i), master);
                            clusterOnline.put(cluster.get(i), true);
                        }
                        previousMaster = settings.getPreviousmaster();
                        nextMaster = settings.getNextmaster();
                        welchercluster = settings.getClusterid();
                        range = new NodeRange(settings.getLow(), settings.getHigh());
                        hashrange = settings.getHashrange();
                        if(datastorage == null) datastorage = new HashMap<>();

                        sendHeartbeat(5000);
                        System.out.println(localCluster.toString() + "\n" + isMaster + "\n" + previousMaster + "\n" + nextMaster + "\n" + range.toString() + "\n" + settings.getClusterid());
                    break;
                case "clientrequest":
                    ClientRequest request = (ClientRequest) message;
                    System.out.println("Bemerkung: " + request.getBemerkung() + " Cluster: " +welchercluster);
                    handleClientRequest((ClientRequest) message);
                    break;
                case "clientresponse":
                    ClientResponse clientResponse = (ClientResponse)message;
                    handleClientResponse(clientResponse.getToken(), clientResponse);
                    break;
                default:
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
                isMaster = false;
                if(clusterOnline!= null) clusterOnline.put(getAddress(), true);
            }
            else if(event instanceof NodeOfflineEvent)
            {
                System.out.println("NodeOfflineEvent");
            }
            else if (event instanceof NodeDownEvent)
            {
                System.out.println("NodeDownEvent");
                isOnline = false;
                isMaster = false;
                localCluster.put(getAddress(), false);
                clusterOnline.put(getAddress(), false);
            }
            else
            {
                //System.out.println("unknown drasyl-event: " + event);
            }
        }
    }
}
