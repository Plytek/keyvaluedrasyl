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
    private DrasylAddress _sender;
    private DrasylAddress _recipient;
    private int _counter;


    public Message(String messageType, long time, String token, MessageContent content, DrasylAddress sender, String recipient)
    {
        _token = token;
        _messageType = messageType;
        _time = time;
        _content = content;
        _sender = sender;
        _recipient = new DrasylAddress() {
            @Override
            public byte[] toByteArray() {
                return recipient.getBytes(StandardCharsets.UTF_8);
            }
        };
    }

    public Message(String messageType, long time, MessageContent content, String sender, DrasylAddress recipient)
    {
        _token = UUID.randomUUID().toString();
        _messageType = messageType;
        _time = time;
        _content = content;
        _sender = new DrasylAddress() {
            @Override
            public byte[] toByteArray() {
                return sender.getBytes(StandardCharsets.UTF_8);
            }
        };;
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
        return null;
    }
}
