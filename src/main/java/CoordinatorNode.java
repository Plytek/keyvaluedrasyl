import Utility.Message;
import Utility.Tools;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;
import org.drasyl.node.event.MessageEvent;

import java.util.List;

public class CoordinatorNode extends DrasylNode {
    List<Node> registerednodes;

    protected CoordinatorNode(DrasylConfig config) throws DrasylException {
        super(config);
    }

    protected CoordinatorNode() throws DrasylException {
    }


    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageEvent msgevent)
        {
            Message message = Tools.getMessageFromEvent(msgevent);

        }
    }
}
