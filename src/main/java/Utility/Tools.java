package Utility;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drasyl.node.event.MessageEvent;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Tools {
    private static ObjectMapper MAPPER = new ObjectMapper();
    //Diese statische Map enthält die Message-Klassen, die für jeden Typ vorgesehen sind.
    private static Map<String, Class> messageTypeClasses = Map.of("clientrequest", ClientRequest.class, "heartbeat", Heartbeat.class, "clientresponse", ClientResponse.class, "settings", Settings.class, "registernode", Message.class, "confirm", Message.class,
             "registerclient", Message.class, "networkonline", NodeResponse.class, "newmaster", Message.class, "noderesponse", NodeResponse.class);

    /**
     * JSON-String parsen
     * @param json ein gültiger JSON String
     * @return ein JsonNode, aus dem die Werte mittels .get("key") ausgelesen werden können. Für Strings bitte .get("key").asText() benutzen!
     */
    public static JsonNode parseJSON(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
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
            return MAPPER.writeValueAsString(message);
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
            JsonNode j = parseJSON(payload);
            Class javaType = messageTypeClasses.get(j.get("messageType").asText());
            try {
                return (Message) MAPPER.readValue(payload, javaType);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
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

}
