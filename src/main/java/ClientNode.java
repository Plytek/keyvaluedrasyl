import org.drasyl.identity.DrasylAddress;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;

import java.util.List;

public class ClientNode extends DrasylNode
{
    List<DrasylAddress> mainnodes;

    protected ClientNode() throws DrasylException {
    }

    public void connect(String initialAddresse)
    {

    }

    public void create(String key, String value)
    {

    }

    public void delete(String key)
    {

    }

    public void update(String key, String value)
    {

    }

    public void read(String key)
    {

    }

    @Override
    public void onEvent(Event event) {

    }
}
