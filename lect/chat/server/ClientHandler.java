package lect.chat.server;

import lect.chat.protocol.ChatCommandUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

// 사용자 메시지를 전달하기 위한 구현체 = 하나의 클라이언트와 통신하기 위한 객체, 스레드
public class ClientHandler implements Runnable, MessageHandler {
    private Socket socket;
    BufferedReader br;
    PrintWriter pw;
    private String chatName;
    private String id;
    private String host;
    private String chatRoomName;
    // 모든 사용자 관리자
    private MessageHandlerManager mM;
    // 채팅방 관리자
    private GroupManager gM;

    public ClientHandler(Socket s) throws IOException {
        // 소켓 필드에 대입
        socket = s;
        // ip주소 저장
        host = socket.getInetAddress().getHostAddress();
        // 사용자 입력을 받기 위한 빨대 생성
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // 사용자 출력을 위한 빨대 생성
        pw = new PrintWriter(socket.getOutputStream(), true);
        mM = MessageHandlerManager.getInstance();
        gM = GroupManager.getInstance();
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
            // 삭제할 채팅방 정보 조회
            List<MessageHandler> targetList = gM.removeUserByChatRoom(chatRoomName, this);
            // 퇴장 메시지 브로드 캐스트
            broadcastMessage(targetList, createMessage(ChatCommandUtil.EXIT_ROOM, chatName + " has just left [" + chatRoomName + "] room"));
            close();
        }
        System.out.println("Terminating ClientHandler");
    }

    public String createMessage(char protocol, String msg) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.delete(0, msgBuilder.length());
        msgBuilder.append("[");
        msgBuilder.append(protocol);
        msgBuilder.append("]");
        msgBuilder.append(msg);
        return msgBuilder.toString();
    }
    
    public void sendMessage(String msg) {
        pw.println(msg);
    }

    // 브로드 캐스트
    private <T extends MessageHandler> void broadcastMessage(List<T> targetList, String msg) {
        for (MessageHandler handler : targetList) {
            handler.sendMessage(msg);
        }
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

    public void close(String userName) {
        try {
            socket.close();
            mM.removeMessageHandler(this);
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
                if (mM.isContains(chatName)) {
                    sendMessage(createMessage(ChatCommandUtil.CHECK_USER_NAME, "false"));
                } else {
                    sendMessage(createMessage(ChatCommandUtil.CHECK_USER_NAME, chatName));
                    mM.addMessageHandler(this);
                }
                break;
            // 채팅방 접속
            case ChatCommandUtil.ROOM_LIST:
                if (chatRoomName != null) {
                    // 삭제할 채팅방 정보 조회
                    List<MessageHandler> targetList = gM.removeUserByChatRoom(chatRoomName, this);
                    // 퇴장 메시지 브로드 캐스트
                    broadcastMessage(targetList, createMessage(ChatCommandUtil.EXIT_ROOM, chatName + " has just left [" + chatRoomName + "] room"));
                    // 유저 리스트 브로드 캐스트
                    broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(chatRoomName)));
                }
                chatRoomName = msg;
                // 생성할 채팅방 정보 조회
                List<MessageHandler> targetList = gM.addUserByChatRoom(chatRoomName, this);
                // 입장 메시지 브로드 캐스트
                broadcastMessage(targetList, createMessage(ChatCommandUtil.ENTER_ROOM, chatName + " has entered [" + chatRoomName + "] room"));
                // 유저 리스트 브로드 캐스트
                broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(chatRoomName)));
                break;
            // 채팅방 생성
            case ChatCommandUtil.CREATE_ROOM:
                if (gM.isContains(msg)) {
                    sendMessage(createMessage(ChatCommandUtil.CREATE_ROOM, "이미 존재하는 채팅방"));
                } else {
                    gM.addChatRoom(msg);
                    broadcastMessage(mM.findAllMessageHandler(), createMessage(ChatCommandUtil.ROOM_LIST, gM.getRoomsToString()));
                }
                break;
            // 채팅방 메시지
            case ChatCommandUtil.NORMAL:
                String[] msgSplit = msg.split("\\|");
                String sendMsg = msgSplit[1];
                targetList = gM.findAllMessageHandler(chatRoomName);
                broadcastMessage(targetList, createMessage(ChatCommandUtil.NORMAL, String.format("%s: %s", chatName, sendMsg)));
                break;
            case ChatCommandUtil.REMOVE_ROOM:
                gM.removeChatRoom(chatRoomName);
            case ChatCommandUtil.EXIT_PROGRAM:
                mM.removeMessageHandler(this);
            default:
                System.out.printf("ChatCommand %c \n", command);
                break;
        }
    }
}
