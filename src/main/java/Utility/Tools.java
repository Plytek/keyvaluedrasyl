package Utility;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.drasyl.node.event.MessageEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Tools {
    private static JSONParser PARSER = new JSONParser();
    protected static ObjectMapper mapper = new ObjectMapper();

    /**
     * JSON-String parsen
     * @param json ein gültiger JSON String
     * @return ein JSONObject, aus dem die Werte mittels .get("key") ausgelesen werden können
     */
    public static JSONObject parseJSON(String json) {
        try {
            PARSER = new JSONParser();
            return (JSONObject) PARSER.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * eine Message in einen gültigen JSON-String umwandeln
     * @param message ein Objekt vom Typ Message
     * @return ein gültiges JSON String
     */
    public static String getMessageAsJSONString(Message message)
    {
        try
        {
            return mapper.writeValueAsString(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "{}";
        }
    }

    /**
     * Ein MessageEvent in ein Objekt vom Typ Message umwandeln
     * @param event ein Drasyl MessageEvent
     * @return ein Objekt des richtigen Typs (erbt von Message)
     */
    public static Message getMessageFromEvent(MessageEvent event)
    {
        try {
            String payload = event.getPayload().toString();
            JSONObject j = parseJSON(payload);
            if(j == null)
            {
                System.out.println();
            }
            Class javaType = null;
            switch (j.get("messageType").toString()) {
                case "clientrequest": {
                    javaType = ClientRequest.class;
                    break;
                }
                case "heartbeat": {
                    javaType = Heartbeat.class;
                    break;
                }
                case "clientresponse":
                {
                    javaType = ClientResponse.class;
                    break;
                }
                case "settings":
                {
                    javaType = Settings.class;
                    break;
                }
                case "registernode":
                {
                    javaType = Message.class;
                    break;
                }
                case "confirm":
                {
                    javaType = Message.class;
                    break;
                }
            }
            return (Message) mapper.readValue(payload.toString(), javaType);
        }
        catch (Exception x)
        {
            x.printStackTrace();
            return null;
        }
    }


    /**
     * Eine Instanz der Klasse Tools ist nicht vorgesehen
     */
    private Tools()
    {
    }

    /**
     * CRC32-Checksum berechnen
     * @param bytes ein Array an bites, etwa von einem String
     * @return eine CRC32-Checksum
     */
    public static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

    /*public static Message getMessageObject(MessageEvent message)
    {
        try {
            String payload = message.getPayload().toString();
            JSONObject j = parseJSON(payload);
            Class javaType = null;
            String messageType = j.get("messageType").toString();
            switch (messageType)
            {
                case("heartbeat"):
                {
                    javaType = Heartbeat.class;
                    break;
                }
                case("clientRequest"):
                {
                    javaType = ClientRequest.class;
                    break;
                }
                default:
                    return null;
            }
            DrasylAddress recipient = new DrasylAddress() {
                @Override
                public byte[] toByteArray() {
                    return j.get("recipient").toString().getBytes(StandardCharsets.UTF_8);
                }
                @Override
                public String toString()
                {
                    return j.get("recipient").toString();
                }
            };;
            Message msg = new Message(j.get("messageType").toString(), j.get("token").toString(), (MessageContent) mapper.readValue(j.get("content").toString(), javaType), message.getSender().toString(), j.get("recipient").toString());
            return msg;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }*/
}
