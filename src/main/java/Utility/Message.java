package Utility;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class Message
{
    protected String messageType;
    // Timestamp zum Sende-Zeitpunkt
    protected long time;
    // Token für Identifikation der Nachricht
    protected String token;
    protected String sender;
    protected String recipient;
    // Anzahl der Timeouts bei Zustellung mit MessageConfirmer, vor dem ersten Timeout gleich 0
    protected int counter = 0;
    protected String bemerkung;
    // Flag die vom MessageConfirmer gesetzt wird, falls eine Bestätigung erwartet wird
    protected boolean confirmRequested = false;

    public Message() {
    }

    public Message(String messageType, String sender, String recipient)
    {
        token = UUID.randomUUID().toString();
        this.messageType = messageType;
        time = System.currentTimeMillis();
        this.sender = sender;
        this.recipient = recipient;
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

    // Token ist zufälliger String
    // Kollisionen sind praktisch ausgeschlossen
    public String generateToken()
    {
        token = UUID.randomUUID().toString();
        return token;
    }

}
