package lect.chat.server;

import lect.chat.protocol.ChatCommandUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

// 사용자 메시지를 전달하기 위한 구현체 = 하나의 클라이언트와 통신하기 위한 객체, 스레드
public class MessageHandlerImpl implements Runnable, MessageHandler {
    private UserImpl userImpl;
    // 모든 사용자 관리자
    private UserManager mM;
    // 채팅방 관리자
    private GroupManager gM;

    public MessageHandlerImpl(Socket s) throws IOException {
        userImpl = new UserImpl(s,
                new BufferedReader(new InputStreamReader(s.getInputStream())),
                new PrintWriter(s.getOutputStream(), true), s.getInetAddress().getHostAddress());
        mM = UserManager.getInstance();
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
            List<UserImpl> targetList = gM.removeUserByChatRoom(userImpl.getChatRoomName(), userImpl);
            // 퇴장 메시지 브로드 캐스트
            broadcastMessage(targetList, createMessage(ChatCommandUtil.EXIT_ROOM, userImpl.getChatName() + " has just left [" + userImpl.getChatRoomName() + "] room"));
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
        userImpl.println(msg);
    }

    // 브로드 캐스트
    private <T extends UserImpl> void broadcastMessage(List<T> targetList, String msg) {
        for (UserImpl user : targetList) {
            user.println(msg);
        }
    }

    public String getMessage() throws IOException {
        return userImpl.readLine();
    }

    public void close() {
        userImpl.close();
    }

    public void close(String userName) {
        userImpl.close();
        mM.removeMessageHandler(userImpl);
    }

    public String getId() {
        return userImpl.getId();//socket.getRemoteSocketAddress().toString();
    }

    public String getFrom() {
        return userImpl.getHost();
    }

    public String getName() {
        return userImpl.getChatName();
    }

    public String getRoomName() {
        return userImpl.getChatRoomName();
    }

    public void processMessage(String msg) {
        char command = ChatCommandUtil.getCommand(msg);// 첫번째 글자 떼옴
        //msg = [b]채팅방- 2|massage
        msg = msg.replaceFirst("\\[{1}[a-z]\\]{1}", "");// 첫번쨰 글자 없앰
        switch (command) {
            case ChatCommandUtil.CHECK_USER_NAME:
                String[] nameWithId = msg.split("\\|");
                userImpl.setChatName(nameWithId[0]);
//                chatName = nameWithId[0];
                userImpl.setId(nameWithId[1]);
//                id = nameWithId[1];
                // user 이름이 이미 존재하는 경우
                if (mM.isContains(userImpl.getChatName())) {
                    sendMessage(createMessage(ChatCommandUtil.CHECK_USER_NAME, "false"));
                } else {
                    sendMessage(createMessage(ChatCommandUtil.CHECK_USER_NAME, userImpl.getChatName()));
                    mM.addMessageHandler(userImpl);
                }
                break;
            // 채팅방 접속
            case ChatCommandUtil.ROOM_LIST:
                if (userImpl.getChatRoomName() != null) {
                    // 삭제할 채팅방 정보 조회
                    List<UserImpl> targetList = gM.removeUserByChatRoom(userImpl.getChatRoomName(), userImpl);
                    // 퇴장 메시지 브로드 캐스트
                    broadcastMessage(targetList, createMessage(ChatCommandUtil.EXIT_ROOM, userImpl.getChatName() + " has just left [" + userImpl.getChatRoomName() + "] room"));
                    // 유저 리스트 브로드 캐스트
                    broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(userImpl.getChatRoomName())));
                }
                userImpl.setChatRoomName(msg);
                // 생성할 채팅방 정보 조회
                List<UserImpl> targetList = gM.addUserByChatRoom(userImpl.getChatRoomName(), userImpl);
                // 입장 메시지 브로드 캐스트
                broadcastMessage(targetList, createMessage(ChatCommandUtil.ENTER_ROOM, userImpl.getChatName() + " has entered [" + userImpl.getChatRoomName() + "] room"));
                // 유저 리스트 브로드 캐스트
                broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(userImpl.getChatRoomName())));
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
                targetList = gM.findAllMessageHandler(userImpl.getChatRoomName());
                broadcastMessage(targetList, createMessage(ChatCommandUtil.NORMAL, String.format("%s: %s", userImpl.getChatName(), sendMsg)));
                break;
            case ChatCommandUtil.REMOVE_ROOM:
                gM.removeChatRoom(userImpl.getChatRoomName());
            case ChatCommandUtil.EXIT_PROGRAM:
                mM.removeMessageHandler(userImpl);
            default:
                System.out.printf("ChatCommand %c \n", command);
                break;
        }
    }
}
