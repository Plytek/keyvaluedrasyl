package Utility;

import lombok.Getter;
import lombok.Setter;
import org.drasyl.identity.DrasylAddress;

import static Utility.Utility.getMessageContentJSON;

@Getter
public class ClientRequest extends MessageContent{

    private String requestType;
    private String affectedKey;
    private String value;

}
