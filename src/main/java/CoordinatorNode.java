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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CoordinatorNode extends DrasylNode {
    List<String> registerednodes = new ArrayList<>();
    int maxnodes = 9;
    int range;
    int clustersize = 3;

    protected CoordinatorNode(DrasylConfig config) throws DrasylException {
        super(config);
    }

    protected CoordinatorNode() throws DrasylException {
    }

    private void registerProcess(Message message)
    {
        registerednodes.add(message.getSender());
        if(registerednodes.size() == maxnodes)
        {
            calculateRange();
        }

    }

    public void calculateRange()
    {
        Map<Integer, List<String>> nodesbycluster = new HashMap<>();
        int partitionsize = range/maxnodes;

        int counter = 1;
        int cluster = 1;
        for(String address : registerednodes)
        {
            Settings settings = new Settings();
            if(counter == 1) settings.setMaster(true);
            else settings.setMaster(false);
            settings.setLow(partitionsize*(cluster-1));
            settings.setHigh(partitionsize*cluster-1);

            nodesbycluster.computeIfAbsent(cluster, k -> new ArrayList<>()).add(address);

            counter++;
            if(counter%4 == 0)
            {
                cluster++;
                counter = 1;
            }
        }

        System.out.println(nodesbycluster);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageEvent msgevent)
        {
            Message message = Tools.getMessageFromEvent(msgevent);
            switch(message.getMessageType())
            {
                case "noderegistered": {
                    registerProcess(message);
                }
            }
        }
    }
}
