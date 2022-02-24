package Utility;

import lombok.Getter;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;

@Getter
@Setter
public class Heartbeat extends Message
{
    private String heartbeat;

    public Heartbeat(String heartbeat) {
        super();
        this.heartbeat = heartbeat;
        this._messageType = "heartbeat";
    }

    public Heartbeat() {
        super();
        this._messageType = "heartbeat";
    }
}
