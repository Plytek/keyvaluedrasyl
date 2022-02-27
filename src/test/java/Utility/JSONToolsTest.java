package Utility;

import org.drasyl.identity.DrasylAddress;
import org.drasyl.node.event.MessageEvent;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

public class JSONToolsTest {

    @Test
    public void JSONConversionWorks()
    {
        List<String> l = new ArrayList<String>();
        l.add("12345");
        l.add("67890");
        Settings s = new Settings(0, 3000, true, l, "Test", "Test2");
        s.setClusterid(2);
        s.setSender("Sender");
        String json = Tools.getMessageAsJSONString(s);
        MessageEvent e = new MessageEvent() {
            @Override
            public DrasylAddress getSender() {
                return null;
            }

            @Override
            public Object getPayload() {
                return json;
            }
        };
        assertEquals(json, Tools.getMessageAsJSONString(Tools.getMessageFromEvent(e)));
    }

}
