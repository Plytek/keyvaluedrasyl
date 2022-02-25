package Utility;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class Message
{
    protected String messageType;
    protected long time;
    protected String token;
    protected String sender;
    protected String recipient;
    protected int counter;


    public Message() {
    }

    public Message(String messageType, String sender, String recipient)
    {
        token = UUID.randomUUID().toString();
        this.messageType = messageType;
        time = System.currentTimeMillis();
        sender = sender;
        recipient = recipient;
    }

    /**
     * Den Zustellversuchs-Counter um 1 erhöhen
     */
    public void tickCounter()
    {
        counter += 1;
    }

    /**
     * Zeitstempel auf aktuelle Zeit setzen
     */
    public void updateTimestamp() {
        time = System.currentTimeMillis();
    }

}
