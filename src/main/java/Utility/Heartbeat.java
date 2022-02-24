package Utility;

import lombok.Getter;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;

@Getter
@Setter
public class Heartbeat extends MessageContent
{
    private String heartbeat;
    private long timestamp;

    public Heartbeat(String heartbeat) {
        this.heartbeat = heartbeat;
        timestamp = System.currentTimeMillis();
    }


}
