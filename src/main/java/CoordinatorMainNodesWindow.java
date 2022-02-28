import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CoordinatorMainNodesWindow {
    private JTable InfoTable;
    private JPanel panel;
    private JButton schließenButton;
    List<String> nodes;
    TableModel tableModel;
    JFrame frame;

    public CoordinatorMainNodesWindow(List<String> list)
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
        frame = new JFrame("Main Nodes");
        frame.setContentPane(panel);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
