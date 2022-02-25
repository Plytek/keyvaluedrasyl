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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientRequest that = (ClientRequest) o;
        return Objects.equals(requestType, that.requestType) && affectedKey.equals(that.affectedKey) && Objects.equals(value, that.value);
    }


    @Override
    public int hashCode() {
        return Math.abs(Objects.hash(affectedKey))%(Integer.MAX_VALUE-1);
    }
}
