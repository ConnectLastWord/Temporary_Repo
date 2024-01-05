package lect.chat.client.components;

import lect.chat.client.model.ChatUser;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ChatUserList extends JList {
    public ChatUserList() {
        super(new DefaultListModel());
        this.setCellRenderer(new CellRenderer());
        DefaultListModel model = (DefaultListModel) getModel();
        model.addElement(null);

        this.setDropMode(DropMode.ON);
        this.setTransferHandler(new UserListTransferHandler());
    }

    public void addNewUsers(ArrayList<ChatUser> users) {
        DefaultListModel newModel = new DefaultListModel();
        for (ChatUser user : users) {
            newModel.addElement(user);
        }
        setModel(newModel);
    }

    // 모든 userList 삭제
    public void removeAllUsers() {
        DefaultListModel model = (DefaultListModel) getModel();
        model.removeAllElements();
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

    public class UserListTransferHandler extends TransferHandler {
        public boolean canImport(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }
            JList list = (JList) info.getComponent();
            JList.DropLocation dLoc = (JList.DropLocation) info.getDropLocation();
            int idxOverJList = dLoc.getIndex();
            if (idxOverJList == -1) {
                return false;
            }
            list.setSelectedIndex(dLoc.getIndex());
            // DataFlavor [] flavors = info.getDataFlavors();
            return (info.isDataFlavorSupported(DataFlavor.javaFileListFlavor));
        }

        public int getSourceActions(JComponent c) {
            return TransferHandler.NONE;
        }
    }
}
