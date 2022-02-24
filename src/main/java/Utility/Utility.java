package Utility;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Utility {
    private static final JSONParser PARSER = new JSONParser();

    public static JSONObject parseJSON(String json) {
        try {
            return (JSONObject) PARSER.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
