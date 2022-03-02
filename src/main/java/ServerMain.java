import org.drasyl.node.DrasylConfig;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class ServerMain {
    static List<Node> nodes;

    /**
     * Main-Methode ohne GUI für Betrieb auf dem Server.
     * Parameter-Struktur: <Number of Nodes> <Address of Coordinator>
     * @param args Argumente aus dem CLI
     */
    public static void main(String args[])
    {
        nodes = new LinkedList<>();
        if (args[0].equals("help"))
        {
            System.out.println("Please use as follows\n[COMMAND] <number_of_nodes> <address_of_coordinator>");
            return;
        }
        if (args.length != 2)
        {
            System.out.println("Invalid number of arguments. Please use as follows\n[COMMAND] <number_of_nodes> <address_of_coordinator>");
            return;
        }
        int anzahlNodes;
        try {
            anzahlNodes = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Please give a valid Integer as number of nodes.");
            return;
        }
        if (!args[1].matches("[0-9A-F]+"))
        {
            System.out.println("Please give a valid Drasyl address");
            return;
        }

        System.out.println("Creating " + args[0] + " nodes with coordinator address " + args[1]);
        DrasylConfig config;

        for (int i = 0; i < anzahlNodes; i++)
        {
            try {
                config = DrasylConfig.newBuilder().identityPath(Path.of(i + ".identity")).build();
                Node n = new Node(config);
                n.setCoordinator(args[1]);
                n.start();
                nodes.add(n);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Creation of node " + i + " failed");
            }
        }

    }
}
