package Utility;

import org.drasyl.node.DrasylNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

// Sorgt für die Zustellung von Messages mit Bestätigungen(confirm) für eine DrasylNode
// Falls keine Bestätigung kommt, wird nach 5s ein Timeout ausgelöst
// Nach jedem Timeout wird die Nachricht erneut gesendet
// Nach 3 Timeouts wird aufgehört -> TODO: Error-Handling nach 3 Timeouts
public class MessageConfirmer {
    // Der Knoten mit dem gesendet/empfangen wird
    private DrasylNode node;

    // Map von Token zu Messages, bei denen noch auf ein Confirm gewartet wird
    private Map<String, Message> messages = new HashMap<>();
    private Map<String, Consumer<Message>> onSuccesses = new HashMap<>();
    private Map<String, Consumer<Message>> onErrors = new HashMap<>();

    // Timer für die automatische Durchführung
    private Timer timer;

    // Starte den MessageConfirmer für die eigene Adresse
    public MessageConfirmer(DrasylNode node) {
        this.node = node;
        // timer für jede Sekunde
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for(String token : messages.keySet()) {
                    checkTimeoutMessage(token);
                }
            }
        }, 0, 1000);
    }

    // Sende eine Nachricht die von dem Empfänger bestätigt werden muss
    // Falls keine Bestätigung kommt, gibt es nach 5s einen Timeout und die Nachricht wird erneut gesendet
    // Nach 3 Timeouts wird aufgegeben
    // Die automatische Prüfung erfolgt in "startMessageConfirmer" bzw. "checkTimeoutMessage"
    public void sendMessage(Message message, Consumer<Message> onSuccess, Consumer<Message> onError)
    {
        long currentTime = System.currentTimeMillis();
        message.setSender(node.identity().getAddress().toString());
        message.setTime(currentTime);
        message.setConfirmRequested(true);
        messages.put(message.getToken(), message);
        onSuccesses.put(message.getToken(), onSuccess);
        onErrors.put(message.getToken(), onError);
        node.send(message.getRecipient(), Tools.getMessageAsJSONString(message));
    }

    // Muss vom Knoten bei jeder ankommenden Message aufgerufen werden !
    // Hiermit kann geprüft werden, ob Bestätigungen angekommen sind
    public void receiveMessage(Message message){
            String token = message.getToken();

            // Entferne Nachricht aus Behälter, falls confirm eingetroffen
            if(messages.containsKey(token))
            {
                onSuccesses.get(token).accept(messages.get(message.token));

                messages.remove(token);
                onSuccesses.remove(token);
                onErrors.remove(token);
            }

            // Sende Confirmation, falls angefordert
            if(message.isConfirmRequested())
            {
                sendConfirmation(token, message.getSender());
            }
    }


    // Sende eine Bestätigung auf eine Nachricht
    // Wird von der Klasse automatisch aufgerufen
    private void sendConfirmation(String token, String receiver) {
        Message message = new Message(
                "confirm",
                node.identity().getAddress().toString(),
                receiver
        );
        message.setSender(node.identity().getAddress().toString());
        message.setToken(token);
        message.setConfirmRequested(false); // wollen keine Endlos-Schleife!
        node.send(receiver, Tools.getMessageAsJSONString(message));
    }

    // Prüfe ob für eine Nachricht aus confirmMessages ein Timeout besteht
    // Timeout nach 5 Sekunden
    // Nach jedem Timeout wird Nachricht erneut gesendet bis zu 3mal
    // Wenn nach 3 Timeouts nicht erfolgreich, wird aufgehört -> TODO: Error-Handling bei 3 Timeouts
    private void checkTimeoutMessage(String token)
    {
        long currentTime = System.currentTimeMillis();
        Message message = messages.get(token);

        // nur handeln falls timeout  nach 5 Sekunden
        if(currentTime - message.getTime() > 5000)
        {
            // timeout!
            // counter zählt wie oft bisher timeout aufgetaucht -> jetzt einmal mehr als counter
            // timer updaten für ggf nächsten timeout
            message.tickCounter();
            message.updateTimestamp();
            int timeouts = message.getCounter();

            // maximal 3 Timeouts
            if(timeouts >= 3) {
                System.out.println("Dreimal Timeout bei Message Delivery -> onError wird aufgerufen!");
                onErrors.get(token).accept(message);

                messages.remove(token);
                onSuccesses.remove(token);
                onErrors.remove(token);
            } else {
                System.out.println("Timeout Nummer " + timeouts + " für token = " + token);
                // erneut zustellen
                node.send(message.getRecipient(), Tools.getMessageAsJSONString(message));
            }
        }
    }




}
