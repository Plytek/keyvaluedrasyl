package Utility;


// Daten für die Versendung einer Nachricht mit dem MessageConfirmer
public class MessageConfirmData {
    public Message message;
    public Runnable onSuccess;
    public Runnable onError;

    public MessageConfirmData(Message message, Runnable onSuccess, Runnable onError) {
        this.message = message;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }
}
