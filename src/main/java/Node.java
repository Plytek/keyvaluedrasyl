import Utility.NodeRange;
import lombok.Getter;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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


    @Override
    public void onEvent(Event event) {

    }
}
