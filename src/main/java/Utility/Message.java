package Utility;

import lombok.Getter;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Getter
@Setter
public class Message
{
    private String _messageType;
    private long _time;
    private MessageContent _content;
    private String _token;
    private String _sender;
    private String _recipient;
    private int _counter;


    public Message(String messageType, String token, MessageContent content, String sender, String recipient)
    {
        _token = token;
        _messageType = messageType;
        _time = System.currentTimeMillis();
        _content = content;
        _sender = sender;
        _recipient = recipient;
    }

    public void tickCounter()
    {
        _counter += 1;
    }


}
