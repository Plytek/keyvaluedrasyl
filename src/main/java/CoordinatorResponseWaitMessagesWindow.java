import Utility.Message;
import Utility.Tools;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CoordinatorResponseWaitMessagesWindow {
    private JTable messageTable;
    private JPanel panel;
    private JButton schließenButton;
    private Map<String, Message> messages;
    private ResponseWaitTableModel tableModel;
    private JFrame frame;
    private CoordinatorNode node;
    private ShowMessageJSONDialog d;
    private Timer timer;

    public CoordinatorResponseWaitMessagesWindow(CoordinatorNode n)
    {
        node = n;
        messages = node.getResponseWaitMap();
        tableModel = new ResponseWaitTableModel();
        schließenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
            }
        });
        messageTable.setModel(tableModel);
        messageTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = messageTable.rowAtPoint(e.getPoint());
                if (row < 0){return;}
                int col= messageTable.columnAtPoint(e.getPoint());
                if (col == 1)
                {
                    d = new ShowMessageJSONDialog(messages.get(messageTable.getValueAt(row, 0)));
                }
            }
        });
        frame = new JFrame("Response Wait Messages of CoordinatorNode");
        frame.setContentPane(panel);
        frame.setSize(600, 400);
        frame.setVisible(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }

        }, 0, 500);

    }

    public void refresh()
    {
        messages = node.getResponseWaitMap();
        tableModel.insertValues();
        messageTable.repaint();
    }

    public class ResponseWaitTableModel extends AbstractTableModel
    {
        Object[] keys;

        public ResponseWaitTableModel()
        {
            insertValues();
        }

        public void insertValues()
        {
            keys = messages.keySet().toArray();
        }

        @Override
        public int getRowCount() {
            return messages.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int i, int i1) {
            if (i1 == 1)
            {
                return "anzeigen";
            }
            return keys[i];
        }
    }

    public class ShowMessageJSONDialog extends JDialog
    {
        private JTextArea textArea;
        private JButton schließen;

        public ShowMessageJSONDialog(Message message)
        {
            super(new JFrame("Message"), "Message");
            textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setText(Tools.getMessageAsJSONString(message));
            schließen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    dispose();
                }
            });
            setLayout(new GridLayout(2, 1, 5, 5));
            add(textArea);
            add(schließen);
            pack();
            setVisible(true);
        }
    }
}
