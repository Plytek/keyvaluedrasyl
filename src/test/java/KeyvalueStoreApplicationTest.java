import org.drasyl.node.DrasylException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

class KeyvalueStoreApplicationTest
{
    @Test
    void completionStageTest()
    {
        CompletableFuture<Void> online = new CompletableFuture<>();
        Node node = null;
        try {
            node = new Node();
        } catch (DrasylException e) {
            e.printStackTrace();
        }
        node.start().toCompletableFuture().join();
        System.out.println("We are here");

    }

}