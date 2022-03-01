package Utility;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class NodeResponse extends Message{
    private long checksum;
    private boolean needsResponse = false;
    private Set<String> nodes;

    public NodeResponse() {
    }


}
