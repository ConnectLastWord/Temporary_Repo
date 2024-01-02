package lect.chat.client;

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
        createDefaultRooms();
    }

    // 기본 채팅 3개 생성
    private void createDefaultRooms() {
        ArrayList<ChatRoom> rooms = new ArrayList<>();
        rooms.add(new ChatRoom("채팅방- 1"));
        rooms.add(new ChatRoom("채팅방- 2"));
        rooms.add(new ChatRoom("채팅방- 3"));
        addNewRooms(rooms);
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
//
//    public void transferFileDropped(List<File> files) {
//        ChatRoom userToSendFile = (ChatRoom) this.getSelectedValue();
//        try {
//            FileSender sender = new FileSender(userToSendFile.getHost(), files);
//            sender.setListener(StatusBar.getStatusBar());
//            sender.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public class RoomListTransferHandler extends TransferHandler {
//        public boolean canImport(TransferHandler.TransferSupport info) {
//            if (!info.isDrop()) {
//                return false;
//            }
//            JList list = (JList) info.getComponent();
//            JList.DropLocation dLoc = (JList.DropLocation) info.getDropLocation();
//            int idxOverJList = dLoc.getIndex();
//            if (idxOverJList == -1) {
//                return false;
//            }
//            list.setSelectedIndex(dLoc.getIndex());
//            // DataFlavor [] flavors = info.getDataFlavors();
//            return (info.isDataFlavorSupported(DataFlavor.javaFileListFlavor));
//        }
//
//        public int getSourceActions(JComponent c) {
//            return TransferHandler.NONE;
//        }
//
//        @SuppressWarnings("unchecked")
//        public boolean importData(TransferHandler.TransferSupport info) {
//            if (!info.isDrop()) {
//                return false;
//            }
//            Transferable t = info.getTransferable();
//            List<File> data;
//            try {
//                data = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
//                ChatRoomList.this.transferFileDropped(data);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//            return true;
//        }
//    }
}
