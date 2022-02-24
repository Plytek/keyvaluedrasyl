package Utility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequest extends Message{

    private String requestType;
    private String affectedKey;
    private String value;

    public ClientRequest(String requestType, String affectedKey, String value) {
        super();
        this.requestType = requestType;
        this.affectedKey = affectedKey;
        this.value = value;
        this._messageType = "clientrequest";
    }

    public ClientRequest(String requestType, String affectedKey) {
        this.requestType = requestType;
        this.affectedKey = affectedKey;
        this._messageType = "clientrequest";
    }
}
