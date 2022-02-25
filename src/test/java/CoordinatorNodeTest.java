import org.drasyl.node.DrasylException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatorNodeTest {

    @Test
    void calculateRange() {
        List<String> adressen = new ArrayList<>();
        adressen.add("First");
        adressen.add("Second");
        adressen.add("Third");
        adressen.add("Fourth");
        adressen.add("Fifth");
        adressen.add("Sixth");
        adressen.add("Seventh");
        adressen.add("Eigth");
        adressen.add("Nineth");
        CoordinatorNode node = null;
        try {
            node = new CoordinatorNode();
        } catch (DrasylException e) {
            e.printStackTrace();
        }

        node.setRegisterednodes(adressen);
        node.createInitialSettings();

    }
}