package Utility;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drasyl.node.event.MessageEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Utility {
    private static final JSONParser PARSER = new JSONParser();

    protected static final ObjectMapper mapper = new ObjectMapper();

    public static JSONObject parseJSON(String json) {
        try {
            return (JSONObject) PARSER.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Message getMessageObject(MessageEvent message)
    {
        try {
            String payload = message.getPayload().toString();
            JSONObject j = parseJSON(payload);
            Class javaType = null;
            String messageType = (String) j.get("messageType");
            if (messageType.equals("heartbeat")) {
                javaType = Heartbeat.class;
            } else if (messageType.equals("clientRequest")) {
                javaType = ClientRequest.class;
            }
            Message msg = new Message(j.get("messageType").toString(), Long.parseLong(j.get("time").toString()), j.get("token").toString(), (MessageContent) mapper.readValue(j.get("content").toString(), javaType), message.getSender(), j.get("recipient").toString());
            return msg;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMessageContentJSON(Object content)
    {
        try
        {
            return mapper.writeValueAsString(content);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "{}";
        }
    }



    private Utility()
    {
    }

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }
}
