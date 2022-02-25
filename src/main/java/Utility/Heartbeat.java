package Utility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Heartbeat extends Message
{
    private String heartbeat;

    public Heartbeat(String heartbeat) {
        super();
        this.heartbeat = heartbeat;
        this.messageType = "heartbeat";
    }

    public Heartbeat() {
        super();
        this.messageType = "heartbeat";
    }
}
