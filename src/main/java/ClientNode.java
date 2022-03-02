import Utility.*;
import lombok.Getter;
import lombok.Setter;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.*;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Getter
@Setter
public class ClientNode extends DrasylNode
{
    Set<String> mainnodes = new HashSet<>();
    String responsevalue = "";
    String coordinator;
    private boolean networkonline = false;
    MessageConfirmer messageConfirmer;
    Timer timer;

    protected ClientNode() throws DrasylException {
        messageConfirmer = new MessageConfirmer(this);
    }


    public ClientNode(DrasylConfig config) throws DrasylException {
        super(config);
        messageConfirmer = new MessageConfirmer(this);
    }

    /**
     * Sendet eine Create-Anfrage an eine zufällige bekannte Mainnode, wenn das Netzwerk online ist.
     * @param key Der Schlüsselwert mit dem die Daten verknüpft werden sollen
     * @param value Der mit dem Key assoziierte Wert
     */
    public void create(String key, String value)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = (String) mainnodes.toArray()[rand.nextInt(0, mainnodes.size())];
            ClientRequest request = new ClientRequest("create", key, value);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            responsevalue = "Auf Antwort vom Server warten...";
            send(address, Tools.getMessageAsJSONString(request));
        } else { responsevalue = "NETZWERK OFFLINE!";
        }
    }

    /**
     * Sendet eine Delete-Anfrage an eine zufällige bekannte Mainnode, wenn das Netzwerk online ist.
     * @param key Der Schlüssel, welcher inklusive Daten gelöscht werden soll
     */
    public void delete(String key)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = (String) mainnodes.toArray()[rand.nextInt(0, mainnodes.size())];
            ClientRequest request = new ClientRequest("delete", key);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            responsevalue = "Auf Antwort vom Server warten...";
            send(address, Tools.getMessageAsJSONString(request));
        }
        else { responsevalue = "NETZWERK OFFLINE!";
    }

    }

    /**
     * Sendet eine Update-Anfrage an eine zufällige bekannte Mainnode, wenn das Netzwerk online ist.
     * @param key Der Schlüsselwert auf dem die Daten geupdated werden sollen
     * @param value Die Daten, welche das Update sind
     */
    public void update(String key, String value)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = (String) mainnodes.toArray()[rand.nextInt(0, mainnodes.size())];
            ClientRequest request = new ClientRequest("update", key, value);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            responsevalue = "Auf Antwort vom Server warten...";
            send(address, Tools.getMessageAsJSONString(request));
        }
        else { responsevalue = "NETZWERK OFFLINE!";
    }

    }

    /**
     * Sendet eine Read-Anfrage an eine zufällige bekannte Mainnode, wenn das Netzwerk online ist.
     * @param key Schlüssel, für den die Werte ausgelesen werden sollen
     */
    public void read(String key)
    {
        if (networkonline) {
            Random rand = new Random();
            String address = (String) mainnodes.toArray()[rand.nextInt(0, mainnodes.size())];
            ClientRequest request = new ClientRequest("read", key);
            request.setRecipient(address);
            request.setSender(identity().getAddress().toString());
            responsevalue = "Auf Antwort vom Server warten...";
            send(address, Tools.getMessageAsJSONString(request));
        }
        else { responsevalue = "NETZWERK OFFLINE!";
    }
    }

    /**
     * Sendet an alle bekannten Mainnodes einen Heartbeat und erwartet eine Antwort zurück.
     * Sollte diese nicht eintreffen wird der Mainnode aus der Liste derer gelöscht, an die Anfragen verteilt werden.
     * @param intervall Heartbeat-Intervall in Millisekunden
     */
    public synchronized void sendHeartbeat(int intervall) {
        if(timer == null) {

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (mainnodes != null) {
                    for (String node : mainnodes) {
                        Heartbeat heartbeat = new Heartbeat();
                        heartbeat.setSender(identity.getAddress().toString());
                        heartbeat.setRecipient(node);
                        heartbeat.updateTimestamp();
                        heartbeat.setMessageType("heartbeat");
                        heartbeat.setHeartbeat("clientheartbeat");
                        messageConfirmer.sendMessage(
                            heartbeat,
                            () -> {},
                            () -> removeMaster(node),
              2,
             2000
                        );
                    }
                    }
                }
            },0 , intervall);
        }
    }

    /**
     * Verbindet sich mit dem gesetzten Coordinator Node
     */
    public void connectToCoordinator()
    {
        Message message = new Message();
        message.setMessageType("registerclient");
        message.setRecipient(coordinator);
        message.setSender(identity.getAddress().toString());
        message.generateToken();

        send(message.getRecipient(), Tools.getMessageAsJSONString(message));
    }

    /**
     * Entfernen eines Masters
     * @param address Drasyladdresse des Masternodes als String
     */
    public synchronized void removeMaster(String address) {
        if(mainnodes.contains(address)) {
            mainnodes.remove(address);
            System.out.println("client removed master:" + mainnodes);
        }
    }

    /**
     * Hinzufügen eines Masters
     * @param address Drasyladdresse des Masternodes als String
     */
    public synchronized void addMaster(String address) {
        if(!mainnodes.contains(address)) {
            mainnodes.add(address);
            System.out.println("client added master:" + mainnodes);
            sendHeartbeat(5000);
        }
    }

    @Override
    public void onEvent(Event event) {

        if(event instanceof MessageEvent e)
        {
            Message message = null;
            message = Tools.getMessageFromEvent(e);
            messageConfirmer.receiveMessage(message);

            switch (message.getMessageType())
            {
                //Antwortnachricht des Netzwerks auf Requests. Setzt den in der UI Anzuzeigenden Antwortwert
                case "clientresponse":
                {
                    ClientResponse response = (ClientResponse) message;
                    responsevalue = response.getResponse();
                    System.out.println("Clientresponse: " + message);
                    break;
                }
                //Benachrichtigung darüber, dass das Netzwork online ist
                case "networkonline":
                {
                    NodeResponse response = (NodeResponse) message;
                    mainnodes = response.getNodes();
                    networkonline = true;
                    responsevalue = "NETWORK ONLINE!";
                    Runnable soundThread = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Clip clip = AudioSystem.getClip();
                                InputStream audioSrc = getClass().getResourceAsStream("oxp1.wav");
                                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                                clip.open(audioStream);
                                clip.start();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (UnsupportedAudioFileException ex) {
                                ex.printStackTrace();
                            } catch (LineUnavailableException ex) {
                                ex.printStackTrace();
                            }
                        }
                    };
                    soundThread.run();
                    System.out.println("client network online");
                    sendHeartbeat(5000);
                    break;
                }
                /*
                 * Benachrichtigungsnachricht von Mainnodes, die ihnen bekannte andere Mainnodes mitteilen
                 * und fügt diese zur Verteilerliste hinzu falls sie dem Client unbekannt sind.
                 */
                case "noderesponse": {
                    NodeResponse response = (NodeResponse) message;
                    for(String address : response.getNodes()) {
                        addMaster(address);
                    }
                    break;
                }
                default:
                {
                    break;
                }
            }

        }
        else if(event instanceof NodeOnlineEvent e)
        {
            connectToCoordinator();
        }
        else if(event instanceof NodeDownEvent e)
        {
            if(timer != null){
                timer.cancel();
                timer = null;
            }
            mainnodes.clear();
        }
    }
}
