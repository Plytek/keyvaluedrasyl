import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CoordinatorRegisteredNodesWindow {
    private JTable InfoTable;
    private JPanel panel;
    private JButton schließenButton;
    private List<Object> nodes;
    private JFrame frame;
    private TableModel tableModel;

    /**
     * Zeigt alle registrierten Nodes eines CoordinatorNodes an.
     * @param list Liste an Node-Addressen. Es werden Strings erwartet, aber jeder andere Datentyp ist ebenfalls zulässig.
     */
    public CoordinatorRegisteredNodesWindow(List<Object> list)
    {
        nodes = list;
        schließenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
            }
        });
        tableModel = new CoordinatorNodesTableModel(nodes);
        InfoTable.setModel(tableModel);
        InfoTable.setAutoCreateColumnsFromModel(true);
        frame = new JFrame("Registered Nodes");
        frame.setContentPane(panel);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

}
