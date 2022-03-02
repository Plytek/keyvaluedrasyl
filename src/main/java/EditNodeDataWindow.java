import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;


public class EditNodeDataWindow {
    private JFormattedTextField formattedTextField1;
    private JFormattedTextField formattedTextField2;
    private JButton übernehmenButton;
    private JPanel panel;
    private Node node;
    private Map<Integer, Map<String, String>> data;
    private JFrame frame;

    public EditNodeDataWindow(Node n)
    {
        node = n;
        data = node.getDatastorage();
        übernehmenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (Integer key : data.keySet()) {
                    if (data.get(key).containsKey(formattedTextField1.getText())) {
                        data.get(key).put(formattedTextField1.getText(), formattedTextField2.getText());
                        frame.dispose();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(frame, "Key not in Dataset");
            }
        });
        frame = new JFrame("Datenpunkt bearbeiten");
        frame.setContentPane(panel);
        frame.setSize(300, 200);
        frame.setVisible(true);
    }

    public EditNodeDataWindow(Node n, String selectedKey, String selectedValue)
    {
        this(n);
        formattedTextField1.setText(selectedKey);
        formattedTextField2.setText(selectedValue);
    }
}
