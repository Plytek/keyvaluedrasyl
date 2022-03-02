import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;

public class CoordinatorMainNodesWindow {
    private JTable InfoTable;
    private JPanel panel;
    private JButton schließenButton;
    List<Object> nodes;
    TableModel tableModel;
    JFrame frame;

    /**
     * Dieses Fenster zeigt die registierten Main Nodes eines CoordinatorNodes an.
     * @param list Liste an Node-Addressen
     */
    public CoordinatorMainNodesWindow(Set<String> list)
    {

        nodes = List.of(list.toArray());
        schließenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
            }
        });
        tableModel = new CoordinatorNodesTableModel(nodes);
        InfoTable.setModel(tableModel);
        InfoTable.setAutoCreateColumnsFromModel(true);
        frame = new JFrame("Main Nodes");
        frame.setContentPane(panel);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
