package Utility;


// Daten für die Versendung einer Nachricht mit dem MessageConfirmer
public class MessageConfirmData {
    public Message message;
    public Runnable onSuccess;
    public Runnable onError;
    public int timeoutMaxCount;
    public int timeoutMilliseconds;

    public MessageConfirmData(Message message, Runnable onSuccess, Runnable onError, int timeoutMaxCount, int timeoutMilliseconds) {
        this.message = message;
        this.onSuccess = onSuccess;
        this.onError = onError;
        this.timeoutMaxCount = timeoutMaxCount;
        this.timeoutMilliseconds = timeoutMilliseconds;
    }
}
