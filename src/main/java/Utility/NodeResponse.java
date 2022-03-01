package Utility;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NodeResponse extends Message{
    private long checksum;
    private boolean needsResponse = false;
    private List<String> nodes;

    public NodeResponse() {
    }


}
