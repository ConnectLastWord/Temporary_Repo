package lect.chat.server;

import lect.chat.protocol.ChatCommandUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Handler;

import lect.chat.protocol.ChatCommandUtil;

// 사용자 메시지를 전달하기 위한 구현체 = 하나의 클라이언트와 통신하기 위한 객체, 스레드
public class ClientHandler implements Runnable, MessageHandler {
    private Socket socket;
    BufferedReader br;
    PrintWriter pw;
    private String chatName;
    private String id;
    private String host;
    private String chatRoomName;

    public ClientHandler(Socket s) throws IOException {
        // 소켓 필드에 대입
        socket = s;
        // ip주소 저장
        host = socket.getInetAddress().getHostAddress();
        // 사용자 입력을 받기 위한 빨대 생성
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // 사용자 출력을 위한 빨대 생성
        pw = new PrintWriter(socket.getOutputStream(), true);
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
            Group g = GroupManager.findByName(chatRoomName);
            g.removeUser(this);
            g.broadCastChatter(ChatCommandUtil.EXIT_ROOM, this);
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
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(String userName) {
        try {
            socket.close();
            MessageHandlerManager.removeMessageHandler(this);
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

    public String getRoomName() {
        return chatRoomName;
    }

    public void processMessage(String msg) {
        char command = ChatCommandUtil.getCommand(msg);// 첫번째 글자 떼옴
        //msg = [b]채팅방- 2|massage
        msg = msg.replaceFirst("\\[{1}[a-z]\\]{1}", "");// 첫번쨰 글자 없앰
        switch (command) {
            case ChatCommandUtil.CHECK_USER_NAME:
                String[] nameWithId = msg.split("\\|");
                chatName = nameWithId[0];
                id = nameWithId[1];

                // user 이름이 이미 존재하는 경우
                if(MessageHandlerManager.getInstance().isContains(chatName)) {
                    System.out.println(msg);
                    Message.sendMessage(this, ChatCommandUtil.CHECK_USER_NAME, "false");
                }else {
                    Message.sendMessage(this, ChatCommandUtil.CHECK_USER_NAME, chatName);
                    MessageHandlerManager.addMessageHandler(this);
//                    GroupManager.allBroadcastChatRoomList();
                }
                break;
            // 채팅방 접속
            case ChatCommandUtil.ROOM_LIST:
                if (chatRoomName != null) {     // 기존 채팅방의 내 정보 삭제 후, 새로운 채팅방 생성
                    Group gr = GroupManager.findByName(chatRoomName);
                    gr.removeUser(this);
                    gr.broadCastChatter(ChatCommandUtil.EXIT_ROOM, this);
                }
                chatRoomName = msg;
                //  접속 채팅방에 내 정보 등록
                Group gr = GroupManager.findByName(chatRoomName);
                gr.addUser(this);
                // 접속 채팅방에 입장 메시지 브로드캐스팅
                gr.broadCastChatter(ChatCommandUtil.ENTER_ROOM, this);
                break;
            // 채팅방 생성
            case ChatCommandUtil.CREATE_ROOM:
                if (GroupManager.isinGroup(msg)) {
                    Message.sendMessage(this, ChatCommandUtil.CREATE_ROOM, "이미 존재하는 채팅방");
                } else {
                    GroupManager.addChatRoom(msg);
                    MessageHandlerManager.getInstance().broadcastMessage(GroupManager.getRoomsToString());
                }
                break;
            // 채팅방 메시지
            case ChatCommandUtil.NORMAL:
                String[] msgSplit = msg.split("\\|");
                String sendMsg = msgSplit[1];
                Group g = GroupManager.findByName(chatRoomName);
                g.broadcastMessage(String.format("%s: %s", chatName, sendMsg));
                break;
            case ChatCommandUtil.REMOVE_ROOM:
                GroupManager.removeChatRoom(chatRoomName,this );
            case ChatCommandUtil.EXIT_PROGRAM:
                MessageHandlerManager.removeMessageHandler(this);
            default:
                System.out.printf("ChatCommand %c \n", command);
                break;

        }
    }
}
