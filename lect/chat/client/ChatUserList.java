package lect.chat.client;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.TransferHandler;
import lect.chat.client.p2p.FileSender;

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

    public void transferFileDropped(List<File> files) {
        ChatUser userToSendFile = (ChatUser) this.getSelectedValue();
        try {
            FileSender sender = new FileSender(userToSendFile.getHost(), files);
            sender.setListener(StatusBar.getStatusBar());
            sender.execute();
        } catch (IOException e) {
            e.printStackTrace();
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

        @SuppressWarnings("unchecked")
        public boolean importData(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }
            Transferable t = info.getTransferable();
            List<File> data;
            try {
                data = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                ChatUserList.this.transferFileDropped(data);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
