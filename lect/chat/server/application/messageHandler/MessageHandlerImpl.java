package lect.chat.server.application.messageHandler;

import lect.chat.server.application.group.GroupManager;
import lect.chat.server.application.socket.LoginManager;
import lect.chat.server.application.user.User;
import lect.chat.server.application.user.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static lect.chat.protocol.ChatCommandUtil.*;

// 사용자 메시지를 전달하기 위한 구현체 = 하나의 클라이언트와 통신하기 위한 객체, 스레드
public class MessageHandlerImpl implements Runnable, MessageHandler {
//    private User user;
    String chatName;
    Socket socket;
    // 모든 사용자 관리자
    private UserManager mM;
    // 채팅방 관리자
    private GroupManager gM;
    // 로그인 관리자
    private LoginManager loginManager;
    public MessageHandlerImpl(Socket socket) throws IOException {
        mM = UserManager.getInstance();
        gM = GroupManager.getInstance();
        this.socket = socket;
//        loginManager.init(new BufferedReader(new InputStreamReader(s.getInputStream())),
//                new PrintWriter(s.getOutputStream(), true));
        System.out.println("message handler 생성");
    }

    public void run() {
        String msg;
        try {
            msg = getLoginMessage();
            processMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
        try {
            while (true) {
                msg = getMessage();
                if (msg == null) {
                    break;
                }
                processMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            User user = mM.getUser(chatName);
            // 삭제할 채팅방 정보 조회지
            List<User> targetList = gM.removeUserByChatRoom(user.getChatRoomName(), user);
            // 퇴장 메시지 브로드 캐스트
            broadcastMessage(targetList, createMessage(EXIT_ROOM, user.getChatName() + " has just left [" + user.getChatRoomName() + "] room"));
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

    public void sendMessage(String chatName, String msg) {
        User user = mM.getUser(chatName);
        user.println(msg);
    }

    // 브로드 캐스트
    private <T extends User> void broadcastMessage(List<T> targetList, String msg) {
        for (User user : targetList) {
            user.println(msg);
        }
    }

    public String getLoginMessage() throws IOException {
        loginManager = new LoginManager(new BufferedReader(new InputStreamReader(socket.getInputStream())),
                new PrintWriter(socket.getOutputStream(), true));
        return loginManager.readLine();
    }

    public void sendLoginMessage(String msg) {
        loginManager.println(msg);
    }

    public String getMessage() throws IOException {
        User user = mM.getUser(chatName);
        return user.readLine();
    }

    public void close() {
        User user = mM.getUser(chatName);
        user.close();
    }

    public void close(String chatName) {
        User user = mM.getUser(chatName);
        user.close();
        mM.removeUser(user);
    }

    public String getId() {
        User user = mM.getUser(chatName);
        return user.getId();//socket.getRemoteSocketAddress().toString();
    }

    public String getFrom() {
        User user = mM.getUser(chatName);
        return user.getHost();
    }

    public String getName() {
        User user = mM.getUser(chatName);
        return user.getChatName();
    }

    public String getRoomName() {
        User user = mM.getUser(chatName);
        return user.getChatRoomName();
    }

    public void processMessage(String msg) throws IOException {
        User user;
        char command = getCommand(msg);// 첫번째 글자 떼옴
        //msg = [b]채팅방- 2|massage
        msg = msg.replaceFirst("\\[{1}[a-z]\\]{1}", "");// 첫번쨰 글자 없앰
        switch (command) {
            // 유저 이름 유효성 검사
            case CHECK_USER_NAME:
                String[] nameWithId = msg.split("\\|");
                if(mM.isContains(chatName)) {
                    System.out.println("fail");
                    sendLoginMessage(createMessage(CHECK_USER_NAME, "false"));
                } else if(msg.startsWith("anonymous")) {
                    System.out.println("fail");
                    sendLoginMessage(createMessage(CHECK_USER_NAME, "false"));
                }
                else {
                    // user가 존재하지 않는 경우에는 로그인
                    chatName = mM.addUser(CREATE_DEFAULT_USER,
                            nameWithId[0], nameWithId[1],
                            new BufferedReader(new InputStreamReader(socket.getInputStream())),
                            new PrintWriter(socket.getOutputStream(), true),
                            socket.getInetAddress().getHostAddress());
                    sendLoginMessage(createMessage(CHECK_USER_NAME, mM.getChatName(chatName)));
                    sendLoginMessage(createMessage(ROOM_LIST, gM.getRoomsToString()));
                    sendMessage(chatName, createMessage(CHECK_USER_NAME, mM.getChatName(chatName)));
                }
                break;
            case CREATE_ANONYMOUS_USER:
                chatName = mM.addUser(CREATE_ANONYMOUS_USER,
                        "",
                        msg,
                        new BufferedReader(new InputStreamReader(socket.getInputStream())),
                        new PrintWriter(socket.getOutputStream(), true),
                        socket.getInetAddress().getHostAddress());
                sendMessage(chatName, createMessage(ROOM_LIST, gM.getRoomsToString()));
                sendMessage(chatName, createMessage(ROOM_LIST, gM.getRoomsToString()));
                break;
            // 채팅방 접속
            case ROOM_LIST:
                user = mM.getUser(chatName);
                if(user.getChatRoomName() != null) {
                    // 삭제할 채팅방 정보 조회
                    List<User> targetList = gM.removeUserByChatRoom(user.getChatRoomName(), user);
                    // 퇴장 메시지 브로드 캐스트
                    broadcastMessage(targetList, createMessage(EXIT_ROOM, user.getChatName() + " has just left [" + user.getChatRoomName() + "] room"));
                    // 유저 리스트 브로드 캐스트
                    broadcastMessage(targetList, createMessage(USER_LIST, gM.getUserByChatRoomToString(user.getChatRoomName())));
                }
                user.setChatRoomName(msg);
                // 생성할 채팅방 정보 조회
                List<User> targetList = gM.addUserByChatRoom(user.getChatRoomName(), user);
                // 입장 메시지 브로드 캐스트
                broadcastMessage(targetList, createMessage(ENTER_ROOM, user.getChatName() + " has entered [" + user.getChatRoomName() + "] room"));
                // 유저 리스트 브로드 캐스트
                broadcastMessage(targetList, createMessage(USER_LIST, gM.getUserByChatRoomToString(user.getChatRoomName())));
                break;
            // 채팅방 생성
            case CREATE_ROOM:
                user = mM.getUser(chatName);
                if (gM.isContains(msg)) {
                    sendMessage(chatName, createMessage(CREATE_ROOM, "이미 존재하는 채팅방"));
                } else {
                    gM.addChatRoom(msg);
                    broadcastMessage(mM.findAllMessageHandler(), createMessage(ROOM_LIST, gM.getRoomsToString()));
                }
                break;
            // 채팅방 메시지
            case NORMAL:
                user = mM.getUser(chatName);
                String[] msgSplit = msg.split("\\|");
                String sendMsg = msgSplit[1];
                targetList = gM.findAllMessageHandler(user.getChatRoomName());
                broadcastMessage(targetList, createMessage(NORMAL, String.format("%s: %s", user.getChatName(), sendMsg)));
                break;
            case REMOVE_ROOM:
                user = mM.getUser(chatName);
                gM.removeChatRoom(user.getChatRoomName());
            case EXIT_PROGRAM:
                user = mM.getUser(chatName);
                mM.removeUser(user);
            default:
                System.out.printf("ChatCommand %c \n", command);
                break;
        }
    }
}
