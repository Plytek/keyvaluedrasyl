package Utility;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class Settings extends Message
{
    private String identity;
    private int low;
    private int high;
    private boolean isMaster;
    private List<String> localcluster;
    private String previousmaster;
    private String nextmaster;
    private int clusterid;


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

    @Override
    public String toString() {
        return "Settings{" +
                "identity='" + identity + '\'' +
                ", low=" + low +
                ", high=" + high +
                ", isMaster=" + isMaster +
                ", localcluster=" + localcluster +
                ", previousmaster='" + previousmaster + '\'' +
                ", nextmaster='" + nextmaster + '\'' +
                ", clusterid=" + clusterid +
                '}';
    }
}
