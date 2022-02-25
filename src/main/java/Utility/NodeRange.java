package Utility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeRange
{
    private int low;
    private int high;

    public NodeRange(int low, int high) {
        this.low = low;
        this.high = high;
    }

    @Override
    public String toString() {
        return "NodeRange{" +
                "low=" + low +
                ", high=" + high +
                '}';
    }
}
