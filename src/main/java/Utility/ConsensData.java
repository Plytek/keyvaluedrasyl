package Utility;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ConsensData {
    private ClientRequest clientRequest = null;
    private List<ClientResponse> clientResponses = new ArrayList<>();

    public ClientResponse findConsens() {
        for(int i=0; i < clientResponses.size(); i++) {
            int firsthash = clientResponses.get(i).hashCode();

            for(int j=0; j < clientResponses.size(); j++) {
                if(i==j) continue;
                int secondhash = clientResponses.get(j).hashCode();

                if(firsthash == secondhash) {
                    return clientResponses.get(i);
                }
            }
        }

        return null;
    }
}
