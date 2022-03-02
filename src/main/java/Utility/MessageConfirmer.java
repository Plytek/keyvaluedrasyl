package Utility;

import org.drasyl.node.DrasylNode;

import java.util.*;

// Umsetzung eines Sendeprotokolls + Empfangsprotokolls mit Bestätigungsnachrichten
// Sendeprotokoll:
// Versuche eine Nachricht zuzustellen und warte auf eine Bestätigung
// Falls Bestätigung eintrifft, so rufe eine onSuccess Funktion auf
// Falls Timeout für Bestätigung, so sende Nachricht erneut, bis zu einer maximalen Anzahl von Timeouts
// Falls Maximale Anzahl an Timeouts, so rufe eine onError Funktion auf
// Empfangsprotokoll:
// Falls eine Bestätigung angefordert wird, so sende diese Bestätigung
// Falls eine Bestätigung für eine Nachricht ankommt, so gebe Senderprotokoll Bescheid
public class MessageConfirmer {
    // Der Knoten mit dem gesendet/empfangen wird
    private DrasylNode node;

    // Die Daten für eine Nachricht im Sendeprotokoll, identifiziert mit Token
    private Map<String, MessageConfirmData> messages;

    // Timer für die Prüfung von Timeouts im Sendeprotokoll
    private Timer timer;

    // Starte den MessageConfirmer für die eigene Adresse
    public MessageConfirmer(DrasylNode node) {
        this.node = node;
        this.messages = new HashMap<>();
    }

    // Versende eine Nachricht mit dem SendeProtokoll
    // onSuccess wird aufgerufen, sobald die Zustellung bestätigt wurde
    // onError wird aufgerufen, sobald die Zustellung die maximale Anzahl an Timeouts erreicht hat
    public synchronized void sendMessage(Message message, Runnable onSuccess, Runnable onError) {
        sendMessage(message, onSuccess, onError, 3, 3000);
    }

    // sendMessage mit Anzahl der m
    public synchronized void sendMessage(Message message, Runnable onSuccess, Runnable onError,
                                         int timeoutMaxCount, int timeoutMilliseconds) {
        long currentTime = System.currentTimeMillis();
        message.setSender(node.identity().getAddress().toString());
        message.setTime(currentTime);
        message.setConfirmRequested(true);
        message.generateToken();

        MessageConfirmData data = new MessageConfirmData(message, onSuccess, onError, timeoutMaxCount, timeoutMilliseconds);
        messages.put(message.getToken(), data);
        node.send(message.getRecipient(), Tools.getMessageAsJSONString(message));

        if(timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    checkAllTimeouts();
                }
            }, 0, 1000);
        }
    }

    // Führe das Empfangsprotokoll für eine eingehende Nachricht aus
    // Muss vom Benutzer dieser Klasse in DrasylNode.onEvent manuell aufgerufen werden!
    public synchronized void receiveMessage(Message message){
        String token = message.getToken();

        // Falls Bestätigung angefordert wird, so sende diese
        if (message.isConfirmRequested()) {
            sendConfirmation(token, message.getSender());
        }

        // Falls Bestätigung eingetroffen, so führe onSuccess aus und entferne bestätigte Nachricht
        if (messages.containsKey(token)) {
            messages.get(token).onSuccess.run();
            messages.remove(token);
        }
    }

    // Sende eine Bestätigung
    private void sendConfirmation(String token, String receiver) {
        String sender =  node.identity().getAddress().toString();

        Message message = new Message("confirm", sender, receiver);
        message.setSender(sender);
        message.setToken(token);
        message.setConfirmRequested(false); // wollen keine Endlos-Schleife!

        node.send(receiver, Tools.getMessageAsJSONString(message));
    }

    // Prüfe Timeouts für alle Nachrichten
    private synchronized void checkAllTimeouts() {
        // Kopieren hier Tokens
        // Sonst gibt es Probleme bei Mutation der messages im for-loop
        Set<String> tokens = new HashSet<>(messages.keySet());

        for(String token : tokens)
        {
            checkTimeout(token);
        }
    }

    // Prüfe einen Timeout für eine Nachricht
    private void checkTimeout(String token)
    {
        long currentTime = System.currentTimeMillis();
        MessageConfirmData data = messages.get(token);

        // Timeout tritt nach "timeoutMilliseconds" ein
        if(currentTime - data.message.getTime() > data.timeoutMilliseconds)
        {
            // In counter sind die bisherigen Timeouts gespeichert
            // counter erhöhen und timestamp aktualisieren
            data.message.tickCounter();
            data.message.updateTimestamp();
            int timeouts = data.message.getCounter();
            System.out.println("Timeout Nummer " + timeouts + " für message = " + data.message);

            if(timeouts >= data.timeoutMaxCount) {
                // höchstens "timeoutMaxCount" Timeouts
                // Führe onError aus und entferne fehlgeschlagene Nachricht
                data.onError.run();
                messages.remove(token);
            } else {
                // ansonsten erneut senden
                node.send(data.message.getRecipient(), Tools.getMessageAsJSONString(data.message));
            }
        }
    }


}
