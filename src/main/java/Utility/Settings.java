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
    private int hashrange;
    private int clusterid;


    public Settings() {
    }

    /**
     * Diese Klasse beinhaltet die Settings für die Nodes.
     * @param low untere Grenze des Hash Ranges, für den eine Node zuständig ist
     * @param high obere Grenze des Hash-Ranges, für den eine Node zuständig ist
     * @param isMaster ob ein Node der MasterNode ist
     * @param localcluster eine Liste von Node-Addressen, die im selben Cluster wie der Node sind
     * @param previousmaster der MasterNode für Hashes kleiner als die Untergrenze dieses Nodes
     * @param nextmaster der MasterNode für Hashes größer als die Obergrenze dieses Nodes
     */
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
