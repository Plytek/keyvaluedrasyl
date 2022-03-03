import Utility.*;
import lombok.Getter;
import lombok.Setter;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;
import org.drasyl.node.event.MessageEvent;
import org.drasyl.node.event.NodeDownEvent;
import org.drasyl.node.event.NodeOnlineEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class CoordinatorNode extends DrasylNode {
    List<String> registerednodes;
    Set<String> mainnodes = new HashSet<>();
    List<String> clients = new ArrayList<>();
    Map<String, Message> responseWaitMap = new ConcurrentHashMap<>();
    private int maxnodes = 3;
    private int oldmaxnodes;
    private int range = 9998;
    private int clustersize = 3;
    private int number = 0;
    private int dataBasesRecieved = 0;
    private List<Map<Integer, Map<String,String>>> listOfDatabases = new ArrayList<>();
    private boolean isOnline;
    private boolean initialSettings = true;

    private MessageConfirmer messageConfirmer;

    protected CoordinatorNode(DrasylConfig config) throws DrasylException {
        super(config);
        messageConfirmer = new MessageConfirmer(this);
    }

    protected CoordinatorNode() throws DrasylException {
        messageConfirmer = new MessageConfirmer(this);
    }

    /**
     * Nimmt Messages entgegen und registriert den Sender als Node. Wenn die festgelegte maximale Anzahl
     * an Nodes erreicht ist werden für diese Settings erstellt und entsprechend versendet
     */
    private synchronized void registerProcess(Message message)
    {
        number++;
        System.out.println("Node " + number + " hinzugefügt" + " (" + message.getSender() + ")");
        if(registerednodes.size() == maxnodes)
        {
            List<Settings> settingsList = createInitialSettings();
            for(Settings settings : settingsList)
            {
                responseWaitMap.put(settings.getRecipient(), settings);

                messageConfirmer.sendMessage(settings,
                    () -> {
                        synchronized (responseWaitMap) {
                            System.out.println("settings: onSuccess!!!");
                            responseWaitMap.remove(settings.getRecipient());
                            if(responseWaitMap.size() == 0)
                            {
                                notifyClients();
                                initialSettings = false;
                                System.out.println("Erfolg");
                            }
                        }
                    },
                    () -> System.out.println("settings: onError!!!")
                );


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Sobald alle Nodes den Erhalt ihrer Settings bestätigt haben wird diese Methode ausgeführt, um alle Clientnodes
     * zu informieren dass das Netzwork online ist und CRUD-Operationen zur Ausführung freigegeben sind.
     */
    private void notifyClients()
    {
        NodeResponse message = new NodeResponse();
        message.setMessageType("networkonline");
        message.generateToken();
        message.setSender(identity().getAddress().toString());
        message.setBemerkung("All nodes registered, Network online");
        message.setNodes(mainnodes);
        for(String client : clients)
        {
            message.setRecipient(client);
            send(client, Tools.getMessageAsJSONString(message));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Erstellt eine Liste von Settings für alle registrierten Nodes. Es wird eine Aufteilung des Hashbereichs
     * bzw. die Partitionsgröße anhand der gesetzten Clustergröße, Anzahl von Maxknoten und der Größe des
     * Hashbereichs berechnet. Zusätzlich wird gesetzt welcher Knoten initial ein Masterknoten ist und welchem
     * Cluster sie angehören.
     * @return Die Liste der Settings für alle registrierten Nodes.
     */
    public List<Settings> createInitialSettings()
    {
        Map<Integer, List<String>> nodesbycluster = new HashMap<>();
        List<Settings> settingsList = new ArrayList<>();
        int partitionsize = range/(maxnodes/clustersize);

        int counter = 1;
        int cluster = 1;
        for(String address : registerednodes)
        {
            Settings settings = new Settings();
            if(counter == 1)
            {
                settings.setMaster(true);
                mainnodes.add(address);
            }
            else settings.setMaster(false);
            settings.setLow(partitionsize*(cluster-1));
            settings.setHigh(partitionsize*cluster-1);
            settings.setClusterid(cluster);
            settings.setIdentity(address);
            settings.setHashrange((partitionsize*(maxnodes/clustersize))-1);
            settings.setMessageType("settings");
            settings.generateToken();
            settings.setRecipient(address);
            settings.setSender(identity().getAddress().toString());
            settings.setInitialSettings(initialSettings);
            settingsList.add(settings);


            nodesbycluster.computeIfAbsent(cluster, k -> new ArrayList<>()).add(address);
            counter++;
            if(counter%(clustersize+1) == 0)
            {
                cluster++;
                counter = 1;
            }
        }

        for(Settings setting : settingsList)
        {
            List<String> localcl = nodesbycluster.get(setting.getClusterid());

            int tempid;

            if(setting.getClusterid()+1 > nodesbycluster.size()) tempid = 1;
            else tempid = setting.getClusterid()+1;
            String nextm = nodesbycluster.get(tempid).get(0);

            if(setting.getClusterid()-1 < 1) tempid = nodesbycluster.size();
            else tempid = setting.getClusterid()-1;
            String prevm = nodesbycluster.get(tempid).get(0);

            setting.setLocalcluster(localcl);
            setting.setPreviousmaster(prevm);
            setting.setNextmaster(nextm);


        }
        return settingsList;
    }

    public void redistributeData()
    {
        List<ClientRequest> requestList = new ArrayList<>();
        for(Map<Integer, Map<String,String>> database : listOfDatabases)
        {
            for(Integer key : database.keySet())
            {
                Map<String, String> data = database.get(key);
                for(Map.Entry<String, String> entry : data.entrySet())
                {
                    ClientRequest clientRequest = new ClientRequest();
                    clientRequest.setMessageType("clientrequest");
                    clientRequest.setRequestType("create");
                    clientRequest.setAffectedKey(entry.getKey());
                    clientRequest.setValue(entry.getValue());
                    clientRequest.setRecipient(clients.get(0));
                    clientRequest.setSender(identity.getAddress().toString());
                    clientRequest.generateToken();
                    requestList.add(clientRequest);
                }
            }
        }
        for(ClientRequest request : requestList)
        {
            messageConfirmer.sendMessage(request, () -> System.out.print(""), () -> System.out.println("Problemrequest. Repeat?") );
        }
        listOfDatabases.clear();
    }

    public void clearNodes()
    {
        registerednodes.clear();
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageEvent msgevent)
        {
            Message message = null;
            message = Tools.getMessageFromEvent(msgevent);

            //Überprüft ob einkommende Nachrichten bestätigt werden müssen oder Bestätigungen sind
            messageConfirmer.receiveMessage(message);

            switch(message.getMessageType())
            {
                //Registrierungsevent Netzwerkknoten
                case "registernode": {
                    System.out.println("Drasylevent: " + event);
                    if(!registerednodes.contains(message.getSender()) && registerednodes.size() < maxnodes)
                    {
                        registerednodes.add(message.getSender());
                        registerProcess(message);
                    }
                    break;
                }
                //Registrierungsevent Clients
                case "registerclient": {
                    clients.add(message.getSender());
                    break;
                }
                case "noderesponse":
                {
                    NodeResponse backupdata = (NodeResponse) message;
                    dataBasesRecieved++;
                    listOfDatabases.add(backupdata.getDatastorage());
                    if(dataBasesRecieved == oldmaxnodes/clustersize)
                    {
                        dataBasesRecieved = 0;
                        redistributeData();
                    }
                }
            }
            System.out.println(event);
        }
        if(event instanceof NodeOnlineEvent)
        {
            System.out.println("Ich bin: " + identity.getAddress().toString());
            number = 0;
            registerednodes = new ArrayList<>();
            isOnline = true;
        }
        else if (event instanceof NodeDownEvent)
        {
            isOnline = false;
            registerednodes.clear();
            mainnodes.clear();
            clients.clear();
            responseWaitMap.clear();
            messageConfirmer = new MessageConfirmer(this);
        }
    }
}
