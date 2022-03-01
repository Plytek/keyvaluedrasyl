package Utility;

import lombok.Getter;
import org.drasyl.node.DrasylNode;

import java.util.*;
import java.util.function.Consumer;


// Sorgt für die Zustellung von Messages mit Bestätigungen(confirm) für eine DrasylNode
// Falls keine Bestätigung kommt, wird nach ein paar Sekunden ein Timeout ausgelöst
// Nach jedem Timeout wird die Nachricht erneut gesendet
// Nach ein paar Timeouts wird aufgehört
public class MessageConfirmer {
    // Der Knoten mit dem gesendet/empfangen wird
    private final DrasylNode node;

    // Map von Token zu Messages, bei denen noch auf ein Confirm gewartet wird
    private final Map<String, MessageConfirmData> messages;

    // Starte den MessageConfirmer für die eigene Adresse
    public MessageConfirmer(DrasylNode node) {
        this.node = node;
        this.messages = Collections.synchronizedMap(new HashMap<>());

        // Timer für die automatische Durchführung
        // timer bleibt am leben, da ein neuer Background-Thread erzeugt wird
        // der background-thread behält eine referenz auf den timer -> wird NIE garbage-collected
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // synchronized muss verwendet werden bei Iteration über synchronized-collection
                // siehe Dokumentation von "synchronizedMap"
                Set<String> tokens = messages.keySet();

                synchronized (messages) {
                    Iterator<String> i = tokens.iterator();
                    while (i.hasNext()) {
                        checkTimeoutMessage(i.next());
                    }
                }
            }
        }, 0, 1000);
    }

    // Sende eine Nachricht die von dem Empfänger bestätigt werden muss
    // Falls keine Bestätigung kommt, gibt es nach ein paar Sekunden einen Timeout und die Nachricht wird erneut gesendet
    // Nach ein paar Timeouts wird aufgegeben
    // Die automatische Prüfung erfolgt in "startMessageConfirmer" bzw. "checkTimeoutMessage"
    // onSuccess wird bei empfangener Bestätigung ausgeführt
    // onError wird bei endgültigem Timeout ausgeführt
    public void sendMessage(Message message, Runnable onSuccess, Runnable onError)
    {
        long currentTime = System.currentTimeMillis();
        message.setSender(node.identity().getAddress().toString());
        message.setTime(currentTime);
        message.setConfirmRequested(true);
        message.generateToken();

        MessageConfirmData data = new MessageConfirmData(message, onSuccess, onError);
        messages.put(message.getToken(), data);
        node.send(message.getRecipient(), Tools.getMessageAsJSONString(message));
    }

    // Muss vom Knoten bei jeder ankommenden Message aufgerufen werden !
    // Hiermit kann geprüft werden, ob Bestätigungen angekommen sind
    public void receiveMessage(Message message){
            String token = message.getToken();

            if(message.isConfirmRequested()) {
                // Sende Confirmation, falls angefordert
                sendConfirmation(token, message.getSender());
            }

            if(messages.containsKey(token))
            {
                // Entferne Nachricht aus Behälter, falls confirm eingetroffen
                // Führe auch onSuccess aus
                messages.get(token).onSuccess.run();
                messages.remove(token);
            }
    }

    // Sende eine Bestätigung auf eine Nachricht
    // Wird von der Klasse automatisch aufgerufen
    private void sendConfirmation(String token, String receiver) {
        String sender =  node.identity().getAddress().toString();

        Message message = new Message("confirm", sender, receiver);
        message.setSender(sender);
        message.setToken(token);
        message.setConfirmRequested(false); // wollen keine Endlos-Schleife!

        node.send(receiver, Tools.getMessageAsJSONString(message));
    }

    // Prüfe ob für eine Nachricht aus confirmMessages ein Timeout besteht
    // Timeout nach ein paar Sekunden
    // Nach jedem Timeout wird Nachricht erneut gesendet bis zu 3mal
    // Wenn nach ein paar Timeouts nicht erfolgreich, wird aufgehört
    private void checkTimeoutMessage(String token)
    {
        long currentTime = System.currentTimeMillis();
        MessageConfirmData data = messages.get(token);

        // nur handeln falls timeout nach 3 Sekunden
        if(currentTime - data.message.getTime() > 3000)
        {
            // timeout!
            // counter zählt wie oft bisher timeout aufgetaucht -> jetzt einmal mehr als counter
            // timer updaten für ggf nächsten timeout
            data.message.tickCounter();
            data.message.updateTimestamp();
            int timeouts = data.message.getCounter();
            System.out.println("Timeout Nummer " + timeouts + " für token = " + token);

            if(timeouts >= 3) {
                // maximal 3 Timeouts
                data.onError.run();
                messages.remove(token);
            } else {
                // ansonsten erneut zustellen
                node.send(data.message.getRecipient(), Tools.getMessageAsJSONString(data.message));
            }
        }
    }




}
