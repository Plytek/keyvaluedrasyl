package Utility;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class NodeResponse extends Message{
    private long checksum;
    private boolean requestupdate = false;
    private Set<String> nodes;
    private Map<Integer, Map<String,String>> datastorage;

    public NodeResponse() {
    }


}
