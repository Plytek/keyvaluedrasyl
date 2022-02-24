package Utility;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void getMessageContentJSON() {
        Heartbeat heartbeat = new Heartbeat("masterheartbeat");
        System.out.println(Utility.getMessageContentJSON(heartbeat));
        //Message message = new Message();
        //System.out.println(Utility.getMessageContentJSON(message));
    }
}