package Utility;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Settings extends Message
{
    private int low;
    private int high;
    private boolean isMaster;
    private List<String> localcluster;
    private String previousmaster;
    private String nextmaster;


    public Settings() {
    }

    public Settings(int low, int high, boolean isMaster, List<String> localcluster, String previousmaster, String nextmaster) {
        messageType = "settings";
        this.low = low;
        this.high = high;
        this.isMaster = isMaster;
        this.localcluster = localcluster;
        this.previousmaster = previousmaster;
        this.nextmaster = nextmaster;
    }
}
