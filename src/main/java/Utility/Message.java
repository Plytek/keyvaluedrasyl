package Utility;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class Message
{
    protected String _messageType;
    protected long _time;
    protected String _token;
    protected String _sender;
    protected String _recipient;
    protected int _counter;


    public Message() {
        _time = System.currentTimeMillis();
        _token = UUID.randomUUID().toString();
    }

    public Message(String messageType, String sender, String recipient)
    {
        _token = UUID.randomUUID().toString();
        _messageType = messageType;
        _time = System.currentTimeMillis();
        _sender = sender;
        _recipient = recipient;
    }

    public void tickCounter()
    {
        _counter += 1;
    }

}
