import Utility.Message;
import Utility.Settings;
import Utility.Tools;
import lombok.Getter;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;
import org.drasyl.node.event.MessageEvent;
import org.drasyl.node.event.NodeOnlineEvent;

import java.util.*;

@Getter
@Setter
public class CoordinatorNode extends DrasylNode {
    List<String> registerednodes = new ArrayList<>();
    List<String> mainnodes = new ArrayList<>();
    int maxnodes = 6;
    //int range = Integer.MAX_VALUE-1;
    int range = 10000;
    int clustersize = 3;
    int number = 1;

    protected CoordinatorNode(DrasylConfig config) throws DrasylException {
        super(config);
    }

    protected CoordinatorNode() throws DrasylException {
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
                send(settings.getIdentity(), Tools.getMessageAsJSONString(settings));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            settings.setMessageType("settings");
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

    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageEvent msgevent)
        {
            System.out.println();
            Message message = null;
            message = Tools.getMessageFromEvent(msgevent);

            switch(message.getMessageType())
            {
                case "registernode": {
                    System.out.println("Drasylevent: " + event);
                    registerProcess(message);
                }
            }
        }
        if(event instanceof NodeOnlineEvent)
        {
            System.out.println("Ich bin: " + identity.getAddress().toString());
        }
    }
}
