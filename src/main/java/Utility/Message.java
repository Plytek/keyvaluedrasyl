package Utility;

import lombok.Getter;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Getter
public class Message
{
    private String _messageType;
    private long _time;
    private MessageContent _content;
    private String _token;
    private String _sender;
    private String _recipient;
    private int _counter;


    public Message(String messageType, long time, String token, MessageContent content, String sender, String recipient)
    {
        _token = token;
        _messageType = messageType;
        _time = time;
        _content = content;
        _sender = sender;
        _recipient = recipient;
    }

    public void tickCounter()
    {
        _counter += 1;
    }

    public void set_time(long time)
    {
        _time = time;
    }

    public String getJSON()
    {
        String str = "{\"messageType:\"" + _messageType + "\", \"time\":\"" + _time + "\", \"sender\":\"" + _sender.toString() + "\", \"recipient\":\"" + _recipient.toString() + "\", \"content\":" + Utility.getMessageContentJSON(_content) + "}";
        return str;
    }
}
