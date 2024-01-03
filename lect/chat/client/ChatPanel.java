package lect.chat.client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import lect.chat.client.event.ChatConnector;
import lect.chat.client.event.ChatSocketListener;
import lect.chat.client.event.MessageReceiver;
import lect.chat.protocol.ChatCommandUtil;

// 컴포넌트 기반 신호 리스너
@SuppressWarnings("serial")
public class ChatPanel extends JPanel implements MessageReceiver, ActionListener, ChatSocketListener {
    JTextField chatTextField;
    ChatTextPane chatDispArea;
    ChatUserList userList;
    ChatRoomList roomList;
    ChatRoom room;
    CommandButton connectDisconnect;
    JButton clearChat;
    // 방 생성 버튼
    JButton makeRoom;

    // 입장 버튼
    JButton enterChat;
    PrintWriter writer;
    ChatConnector connector;
    StringBuilder msgBuilder = new StringBuilder();

    public ChatPanel(ChatConnector c) {
        super(new GridBagLayout());
        initUI();
        connector = c;
        chatTextField.addActionListener(this);
        connectDisconnect.addActionListener(this);
        clearChat.addActionListener(this);
        enterChat.addActionListener(this);
        makeRoom.addActionListener(this);
    }

    private void initUI() {
        chatTextField = new JTextField();
        chatDispArea = new ChatTextPane();//new ChatTextArea();
        userList = new ChatUserList();
        roomList = new ChatRoomList();

        connectDisconnect = new CommandButton();
        clearChat = new JButton("ClearChat");
        // 입장 버튼
        enterChat = new JButton("EnterChat");
        // 방생성 버튼
        makeRoom = new JButton("MakeRoom");

        chatTextField.setEnabled(false);
        chatDispArea.setEditable(false);
        clearChat.setEnabled(false);
        enterChat.setEnabled(false);
        makeRoom.setEnabled(false);
        userList.setEnabled(false);
        roomList.setEnabled(false);

        GridBagConstraints c = new GridBagConstraints();
        JLabel titleLabel = new JLabel("Message Received", JLabel.CENTER);
        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(2, 2, 2, 2);
        add(titleLabel, c);

        c = new GridBagConstraints();
        titleLabel = new JLabel("List of Users", JLabel.CENTER);
        c.gridy = 0;
        c.gridx = 1;
        c.gridwidth = 2;
        c.insets = new Insets(2, 2, 2, 2);
        add(titleLabel, c);

        c = new GridBagConstraints();
        titleLabel = new JLabel("List of Room", JLabel.CENTER);
        c.gridy = 0;
        c.gridx = 5;
        c.insets = new Insets(2, 2, 2, 2);
        add(titleLabel, c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 0;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.9;
        c.insets = new Insets(1, 2, 0, 2);
        JScrollPane scrollPane = new JScrollPane(chatDispArea);
        add(scrollPane, c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 0.1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(1, 2, 0, 2);
        scrollPane = new JScrollPane(userList);
        add(scrollPane, c);

        // List Of Room
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 4;
        c.gridwidth = 2;
        c.weightx = 0.1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(1, 2, 0, 2);
        scrollPane = new JScrollPane(roomList);
        add(scrollPane, c);

        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 1, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        add(chatTextField, c);

        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(connectDisconnect, c);

        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridx = 2;
        c.anchor = GridBagConstraints.CENTER;
        add(clearChat, c);

        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridx = 4;
        c.anchor = GridBagConstraints.CENTER;
        add(enterChat, c);

        c = new GridBagConstraints();
        c.gridy = 2;
        c.gridx = 5;
        c.anchor = GridBagConstraints.CENTER;
        add(makeRoom, c);
    }

    // 서버로부터 받은 응답 처리
    public void messageArrived(String msg) {
        char command = ChatCommandUtil.getCommand(msg);
        msg = msg.replaceFirst("\\[{1}[a-z]\\]{1}", "");
        switch (command) {
            case ChatCommandUtil.NORMAL:
            case ChatCommandUtil.ENTER_ROOM:
            case ChatCommandUtil.EXIT_ROOM:
                chatDispArea.append(msg + "\n", command);
                break;
            case ChatCommandUtil.USER_LIST:
                displayUserList(msg);
                break;
            case ChatCommandUtil.ROOM_LIST:
                displayRoomList(msg);
                break;
            default:
                break;
        }
    }

    // 클라이언트(컴포넌트 신호)로부터 신호가 들어왔을 때
    public void actionPerformed(ActionEvent e) {
        Object sourceObj = e.getSource();
        if (sourceObj == chatTextField) {
            String msgToSend = chatTextField.getText();
            if (msgToSend.trim().isEmpty()) {
                return;
            }
            if (connector.socketAvailable()) {
                String roomName = room.getName();
                sendMessage(ChatCommandUtil.NORMAL, String.format("%s|%s", roomName, msgToSend));
            }
            chatTextField.setText("");
        } else if (sourceObj == connectDisconnect) {
            // 신호가 Connect일때
            if (e.getActionCommand().equals(CommandButton.CMD_CONNECT)) {
                if (connector.connect()) {
                    connectDisconnect.toButton(CommandButton.CMD_DISCONNECT);
                }
                // 컴포넌트 비활성화
                userList.setEnabled(false);
                chatDispArea.setEnabled(false);
                chatTextField.setEnabled(false);

            } else {// 신호가 Disconnect 일때
                connector.disConnect();
                connectDisconnect.toButton(CommandButton.CMD_CONNECT);
                room = null;
            }
        } else {
            // 챗방 클리어 버튼일때
            if (e.getActionCommand().equals("ClearChat")) {
                chatDispArea.initDisplay();
            } else if (e.getActionCommand().equals("EnterChat"))  {  //  채팅방 입장 버튼일때
                if (roomList.getSelectedValue() != room) {
                    room = (ChatRoom) roomList.getSelectedValue();
                    if (room == null) {
                        JOptionPane.showMessageDialog(this, "Room to Enter to must be selected", "EnterChat",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    sendMessage(ChatCommandUtil.ROOM_LIST, room.getName());
                    // 컴포넌트 활성화
                    userList.setEnabled(true);
                    chatDispArea.setEnabled(true);
                    chatTextField.setEnabled(true);
                    chatDispArea.initDisplay();
                    return;
                }
                JOptionPane.showMessageDialog(this, "이미 접속해있는 방입니다.", "ChatRoom",
                        JOptionPane.WARNING_MESSAGE);
            }
            // 채팅방 생성 버튼
            else{
               String chatName = JOptionPane.showInputDialog("Enter create chatRoom name:");
               if (chatName==null){
                   return;
               }
               if (chatName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "방 이름은 공백이 안됩니다", "Faild Create ChatRoom",
                            JOptionPane.WARNING_MESSAGE);
               }
               if (connector.socketAvailable()) {
                    sendMessage(ChatCommandUtil.CREATE_ROOM, chatName);
                }
            }
        }

    }

    public void socketClosed() {
        chatTextField.setEnabled(false);
        chatDispArea.setEnabled(false);
        clearChat.setEnabled(false);
        // 입장 버튼 비활성화
        enterChat.setEnabled(false);
        userList.setEnabled(false);
        //채팅방 생성 버튼 비활성화
        makeRoom.setEnabled(false);
        // 채팅방 목록 비활성화
        roomList.setEnabled(false);
        // 모든 유저 삭제
        userList.removeAllUsers();
        // 채팅창 초기화
        chatDispArea.initDisplay();
        connectDisconnect.toButton(CommandButton.CMD_CONNECT);
    }

    //  ChatServer에게 사용자 이름과 , uuid값 전달하고, 채팅 기능을 위한 Component 활성화
    public void socketConnected(Socket s) throws IOException {
        writer = new PrintWriter(s.getOutputStream(), true);
        // ChatCommandUtil - 서버와 통신할때 이벤트 식별자?
        writer.println(createMessage(ChatCommandUtil.INIT_ALIAS,
                String.format("%s|%s", connector.getName(), connector.getId())));
        chatTextField.setEnabled(true);
        chatDispArea.setEnabled(true);
        clearChat.setEnabled(true);
        // 입장 버튼 활성화
        enterChat.setEnabled(true);
        userList.setEnabled(true);
        //채팅방 생성 활성화
        makeRoom.setEnabled(true);
        // 채팅방 목록 활성화
        roomList.setEnabled(true);
    }

    private void displayUserList(String users) {
        //format should be like 'name1,id1,host1|name2,id2,host2|...'
        //System.out.println(users);
        String[] strUsers = users.split("\\|");
        String[] nameWithIdHost;
        ArrayList<ChatUser> list = new ArrayList<>();
        for (String strUser : strUsers) {
            nameWithIdHost = strUser.split(",");
            if (connector.getId().equals(nameWithIdHost[1])) {
                continue;
            }
            list.add(new ChatUser(nameWithIdHost[0], nameWithIdHost[1], nameWithIdHost[2]));
        }
        userList.addNewUsers(list);
    }

    private void displayRoomList(String rooms) {
        String[] strRooms = rooms.split("\\|");
        String[] nameWithIdHost;
        ArrayList<ChatRoom> list = new ArrayList<>();
        for (String strRoom : strRooms) {
            nameWithIdHost = strRoom.split(",");
            if (connector.getId().equals(nameWithIdHost[1])) {
                continue;
            }
            list.add(new ChatRoom(nameWithIdHost[0]));
        }
        roomList.addNewRooms(list);
    }

    private void sendMessage(char command, String msg) {
        writer.println(createMessage(command, msg));
    }

    private String createMessage(char command, String msg) {
        msgBuilder.delete(0, msgBuilder.length());
        msgBuilder.append("[");
        msgBuilder.append(command);
        msgBuilder.append("]");
        msgBuilder.append(msg);
        return msgBuilder.toString();
    }
}
