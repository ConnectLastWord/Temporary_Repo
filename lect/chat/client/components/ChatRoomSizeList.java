package lect.chat.client.components;

import java.awt.Component;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings("serial")
public class ChatRoomSizeList extends JList {
    public ChatRoomSizeList() {
        super(new DefaultListModel());
        this.setCellRenderer(new CellRenderer());
        DefaultListModel model = (DefaultListModel) getModel();
        model.addElement(null);
        this.setDropMode(DropMode.ON);
    }

    public void add(String[] roomSizes) {
        DefaultListModel newModel = new DefaultListModel();
        for (String room : roomSizes) {
            newModel.addElement(room);
        }
        setModel(newModel);
    }

    class CellRenderer extends JLabel implements ListCellRenderer {
        public CellRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            setText(value == null ? "" : value.toString());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }
}
