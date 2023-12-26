package lect.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import lect.chat.protocol.ChatCommandUtil;

// 사용자 메시지를 전달하기 위한 구현체 = 하나의 클라이언트와 통신하기 위한 객체, 스레드
public class ClientHandler implements Runnable, MessageHandler {
    private Socket socket;
    BufferedReader br = null;
    PrintWriter pw = null;
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
        // addMessageHandler로 사용자 소켓을 전달
//        GroupManager.addMessageHandler(this);
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
                //GroupManager.broadcastMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            GroupManager.removeMessageHandler(chatRoomName, this);
            close();
        }
        System.out.println("Terminating ClientHandler");
    }

    public void sendMessage(String msg) {
        pw.println(msg);
    }

    public String getMessage() throws IOException {
        // msg example : [c]woo|-55cf9d44:18c90bce87e:-8000
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
        char command = ChatCommandUtil.getCommand(msg);
        msg = msg.replaceFirst("\\[{1}[a-z]\\]{1}", "");
        switch (command) {
            case ChatCommandUtil.NORMAL:
                String msg_split[] = msg.split("\\|");
                String roomName = msg_split[0];
                String sendMsg = msg_split[1];
                GroupManager.addChatRoom(roomName);
                GroupManager.broadcastMessage(roomName, String.format("%s: %s", chatName, sendMsg));
                break;
            case ChatCommandUtil.INIT_ALIAS:
                String nameWithId[] = msg.split("\\|");
                chatName = nameWithId[0];
                id = nameWithId[1];
                System.out.println("INIT_AS : " + chatName + " / " + id);
//                GroupManager.broadcastNewChatter(chatRoomName, this);
                break;
            case ChatCommandUtil.WHISPER:
                String toId = msg.substring(0, msg.indexOf('|'));
                String msgToWhisper = msg.substring(msg.indexOf('|') + 1);
                GroupManager.sendWhisper(this, toId, String.format("%s: %s", chatName, msgToWhisper));
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
            default:
                System.out.printf("ChatCommand %c \n", command);
                break;

        }
    }
}
