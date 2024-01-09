package lect.chat.client.components;

import lect.chat.client.model.ChatRoom;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ChatRoomList extends JList {
    public ChatRoomList() {
        super(new DefaultListModel());
        this.setCellRenderer(new CellRenderer());
        DefaultListModel model = (DefaultListModel) getModel();
        model.addElement(null);
        this.setDropMode(DropMode.ON);
    }

    // 채팅방 초기화
    public void removeAllChatRoom() {
        DefaultListModel model = (DefaultListModel) getModel();
        model.removeAllElements();
    }

    // 새로운 채팅방 추가
    public void addNewRooms(ArrayList<ChatRoom> rooms) {
        DefaultListModel newModel = new DefaultListModel();
        for (ChatRoom room : rooms) {
            newModel.addElement(room);
        }
        setModel(newModel);
    }

    // Inner Class ( CellRenderer )
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
