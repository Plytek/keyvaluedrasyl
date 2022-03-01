package Utility;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class ClientResponse extends Message
{
    private String response;
    private String oldvalue;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientResponse that = (ClientResponse) o;
        return Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(response);
    }
}
