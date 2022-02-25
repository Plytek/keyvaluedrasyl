package Utility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientResponse extends Message
{
    private String response;

    public ClientResponse() {
    }

    public ClientResponse(String response) {
        this.response = response;
        this.messageType = "clientresponse";
    }

    public ClientResponse(String messageType, String sender, String recipient, String response) {
        super(messageType, sender, recipient);
        this.response = response;
        this.messageType = "clientresponse";
    }

}
