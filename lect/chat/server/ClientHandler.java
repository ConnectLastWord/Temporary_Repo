package lect.chat.server;

import lect.chat.protocol.ChatCommandUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// 사용자 메시지를 전달하기 위한 구현체 = 하나의 클라이언트와 통신하기 위한 객체, 스레드
public class ClientHandler implements Runnable, MessageHandler {
    private Socket socket;
    BufferedReader br;
    PrintWriter pw;
    private String chatName, id, host, chatRoomName;

    public ClientHandler(Socket s) throws IOException {
        // 소켓 필드에 대입
        socket = s;
        // ip주소 저장
        host = socket.getInetAddress().getHostAddress();
        // 사용자 입력을 받기 위한 빨대 생성
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // 사용자 출력을 위한 빨대 생성
        pw = new PrintWriter(socket.getOutputStream(), true);
        ChatServer.clients.add(pw);
    }

    public void run() {
        String msg;
        try {
            while (true) {
                msg = getMessage();
                if (msg == null) {
                    break;
                }
                processMessage(msg);
                System.out.println("lineRead: " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            GroupManager.removeMessageHandler(chatRoomName, this);
            GroupManager.broadcastLeftChatter(chatRoomName, this);
            close();
        }
        System.out.println("Terminating ClientHandler");
    }

    public void sendMessage(String msg) {
        pw.println(msg);
    }

    public String getMessage() throws IOException {
        return br.readLine();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;//socket.getRemoteSocketAddress().toString();
    }

    public String getFrom() {
        return host;
    }

    public String getName() {
        return chatName;
    }

    public void processMessage(String msg) {
        char command = ChatCommandUtil.getCommand(msg);// 첫번째 글자 떼옴
        //msg = [b]채팅방- 2|massage
        msg = msg.replaceFirst("\\[{1}[a-z]\\]{1}", "");// 첫번쨰 글자 없앰
        switch (command) {
            case ChatCommandUtil.NORMAL:
                String[] msgSplit = msg.split("\\|");
                String roomName = msgSplit[0];
                String sendMsg = msgSplit[1];
                GroupManager.addChatRoom(roomName);
                GroupManager.broadcastMessage(roomName, String.format("%s: %s", chatName, sendMsg));
                break;
            case ChatCommandUtil.INIT_ALIAS:
                String[] nameWithId = msg.split("\\|");
                chatName = nameWithId[0];
                id = nameWithId[1];
                System.out.println("INIT_AS : " + chatName + " / " + id);
                GroupManager.allBroadcastChatRoomList();
                break;
            case ChatCommandUtil.ROOM_LIST:
                if (chatRoomName != null) {     // 기존 채팅방의 내 정보 삭제 후, 새로운 채팅방 생성
                    GroupManager.removeMessageHandler(chatRoomName, this); // left// list user
                    GroupManager.broadcastLeftChatter(chatRoomName, this);
                }
                chatRoomName = msg;
                GroupManager.addChatRoom(chatRoomName);
                //  생성한 채팅방에 내 정보 등록
                GroupManager.addMessageHandler(chatRoomName, this);
                // 생성한 채팅방에 브로드캐스팅
                GroupManager.broadcastNewChatter(chatRoomName, this);
                break;
            case ChatCommandUtil.CREATE_ROOM:
                if (GroupManager.isinGroup(msg)) {
                    this.sendMessage(GroupManager.createMessage(ChatCommandUtil.ROOM_LIST, "이미 존재하는 채팅방"));
                } else {
                    GroupManager.addChatRoom(msg);
                    GroupManager.allBroadcastChatRoomList();
                }

                break;
            default:
                System.out.printf("ChatCommand %c \n", command);
                break;

        }
    }
}
