package Utility;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class ClientRequest extends Message{

    private String requestType;
    private String affectedKey;
    private String value;

    public ClientRequest() {
    }

    public ClientRequest(String requestType, String affectedKey, String value) {
        super();
        this.requestType = requestType;
        this.affectedKey = affectedKey;
        this.value = value;
        this.messageType = "clientrequest";
    }

    public ClientRequest(String requestType, String affectedKey) {
        this.requestType = requestType;
        this.affectedKey = affectedKey;
        this.messageType = "clientrequest";
    }

    public int verteilerHash()
    {
        int hash = affectedKey.hashCode();
        return Math.abs(hash*31%(Integer.MAX_VALUE-1));
    }
}
