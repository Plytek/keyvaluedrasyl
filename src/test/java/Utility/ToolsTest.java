package Utility;

import org.junit.jupiter.api.Test;

class ToolsTest {

    @Test
    void getMessageContentJSON() {
        Heartbeat heartbeat = new Heartbeat("masterheartbeat");
        System.out.println(Tools.getMessageJSON(heartbeat));
        //Message message = new Message();
        //System.out.println(Utility.getMessageContentJSON(message));
    }
}