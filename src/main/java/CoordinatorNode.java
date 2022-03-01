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

@Getter
@Setter
public class CoordinatorNode extends DrasylNode {
    List<String> registerednodes;
    List<String> mainnodes = new ArrayList<>();
    List<String> clients = new ArrayList<>();
    Map<String, Message> responseWaitMap = new HashMap<>();
    private int maxnodes = 9;
    //int range = Integer.MAX_VALUE-1;
    private int range = 9998;
    private int clustersize = 3;
    private int number = 1;
    private boolean isOnline;

    private MessageConfirmer messageConfirmer;

    protected CoordinatorNode(DrasylConfig config) throws DrasylException {
        super(config);
        messageConfirmer = new MessageConfirmer(this);
    }

    protected CoordinatorNode() throws DrasylException {
        messageConfirmer = new MessageConfirmer(this);
    }


    private void registerProcess(Message message)
    {
        registerednodes.add(message.getSender());
        System.out.println("Node " + number + " hinzugefügt" + " (" + message.getSender() + ")");
        number++;
        if(registerednodes.size() == maxnodes)
        {
            List<Settings> settingsList = createInitialSettings();
            for(Settings settings : settingsList)
            {
                responseWaitMap.put(settings.getRecipient(), settings);

                messageConfirmer.sendMessage(settings,
                        (Message m) -> {
                            responseWaitMap.remove(m.getRecipient());

                            if(responseWaitMap.size() == 0)
                            {
                                notifyClients();
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
            settings.setHashrange(range);
            settings.setMessageType("settings");
            settings.generateToken();
            settings.setRecipient(address);
            settings.setSender(identity().getAddress().toString());
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
            messageConfirmer.receiveMessage(message);

            switch(message.getMessageType())
            {
                case "registernode": {
                    System.out.println("Drasylevent: " + event);
                    registerProcess(message);
                    break;
                }
                case "registerclient": {
                    clients.add(message.getSender());
                    break;
                }
            }
            System.out.println(event);
        }
        if(event instanceof NodeOnlineEvent)
        {
            System.out.println("Ich bin: " + identity.getAddress().toString());
            number = 1;
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
