package lect.chat.server.application.messageHandler;

import lect.chat.server.LoginMessenger;
import lect.chat.server.Messenger;
import lect.chat.server.SocketManager;
import lect.chat.server.application.group.GroupManager;
import lect.chat.server.application.user.User;
import lect.chat.server.application.user.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static lect.chat.protocol.ChatCommandUtil.*;

// 사용자 메시지를 전달하기 위한 구현체 = 하나의 클라이언트와 통신하기 위한 객체
public class MessageHandlerImpl implements MessageHandler {
//    private User user;
    String chatName;
    // 모든 사용자 관리자
    private final UserManager userManager;
    // 채팅방 관리자
    private final GroupManager groupManger;
    // 소켓 관리자
    private final SocketManager socketManager;
    // 메시지 처리 전략
    LoginMessenger loginMessenger;
//    UserMessenger userMessenger;
    public MessageHandlerImpl(SocketManager socketManager) throws IOException {
        userManager = UserManager.getInstance();
        groupManger = GroupManager.getInstance();
        this.socketManager = socketManager;
        Socket socket = this.socketManager.getSocket();

        // login 전략 초기화
        loginMessenger = new LoginMessenger(
                new BufferedReader(new InputStreamReader(socket.getInputStream())),
                new PrintWriter(socket.getOutputStream(), true)
        );
    }

    public void run() {
        String msg;
        try {
            msg = getMessage(loginMessenger);
            processMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            User user = userManager.getUser(chatName);
            while (true) {
                msg = getMessage(user);
                if (msg == null) {
                    break;
                }
                processMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            User user = userManager.getUser(chatName);
            // 삭제할 채팅방 정보 조회지
            List<User> targetList = groupManger.removeUserByChatRoom(user.getChatRoomName(), user);
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

    public String getMessage(Messenger messenger) throws IOException {
        return messenger.readLine();
    }

    public void sendMessage(Messenger messenger, String msg) {
        messenger.println(msg);
    }

    // 브로드 캐스트
    private <T extends User> void broadcastMessage(List<T> targetList, String msg) {
        for (User user : targetList) {
            user.println(msg);
        }
    }

    public void close() {
        User user = userManager.getUser(chatName);
        user.close();
    }

    public void close(String chatName) {
        User user = userManager.getUser(chatName);
        user.close();
        userManager.removeUser(user);
    }

    public String getId() {
        User user = userManager.getUser(chatName);
        return user.getId();//socket.getRemoteSocketAddress().toString();
    }

    public String getFrom() {
        User user = userManager.getUser(chatName);
        return user.getHost();
    }

    public String getName() {
        User user = userManager.getUser(chatName);
        return user.getChatName();
    }

    public String getRoomName() {
        User user = userManager.getUser(chatName);
        return user.getChatRoomName();
    }

    public void processMessage(String msg) throws IOException {
        User user;
        char command = getCommand(msg);// 첫번째 글자 떼옴
        //msg = [b]채팅방- 2|massage
        msg = msg.replaceFirst("\\[{1}[a-z]\\]{1}", "");// 첫번쨰 글자 없앰
        System.out.println("msg = " + msg);
        switch (command) {
            // 유저 이름 유효성 검사
            case CHECK_USER_NAME:
                System.out.println("Check user 실행");
                String[] nameWithId = msg.split("\\|");
                if(userManager.isContains(chatName)) {
                    System.out.println("fail");
                    sendMessage(loginMessenger, createMessage(CHECK_USER_NAME, "false"));
                } else {
                    // user가 존재하지 않는 경우에는 로그인
                    sendMessage(loginMessenger, createMessage(CHECK_USER_NAME, userManager.getChatName(chatName)));
                    sendMessage(loginMessenger, createMessage(ROOM_LIST, groupManger.getRoomsToString()));
                    Socket socket = socketManager.getSocket();
                    chatName = userManager.addUser(CREATE_DEFAULT_USER, socket, nameWithId[0], nameWithId[1], new BufferedReader(new InputStreamReader(socket.getInputStream())),
                            new PrintWriter(socket.getOutputStream(), true), socket.getInetAddress().getHostAddress());
                    user = userManager.getUser(chatName);
                    sendMessage(user, createMessage(CHECK_USER_NAME, userManager.getChatName(chatName)));
                }
                break;
            // 채팅방 접속
            case ROOM_LIST:
                user = userManager.getUser(chatName);
                if(user.getChatRoomName() != null) {
                    // 삭제할 채팅방 정보 조회
                    List<User> targetList = groupManger.removeUserByChatRoom(user.getChatRoomName(), user);
                    // 퇴장 메시지 브로드 캐스트
                    broadcastMessage(targetList, createMessage(EXIT_ROOM, user.getChatName() + " has just left [" + user.getChatRoomName() + "] room"));
                    // 유저 리스트 브로드 캐스트
                    broadcastMessage(targetList, createMessage(USER_LIST, groupManger.getUserByChatRoomToString(user.getChatRoomName())));
                }
                user.setChatRoomName(msg);
                // 생성할 채팅방 정보 조회
                List<User> targetList = groupManger.addUserByChatRoom(user.getChatRoomName(), user);
                // 입장 메시지 브로드 캐스트
                broadcastMessage(targetList, createMessage(ENTER_ROOM, user.getChatName() + " has entered [" + user.getChatRoomName() + "] room"));
                // 유저 리스트 브로드 캐스트
                broadcastMessage(targetList, createMessage(USER_LIST, groupManger.getUserByChatRoomToString(user.getChatRoomName())));
                break;
            // 채팅방 생성
            case CREATE_ROOM:
                if (groupManger.isContains(msg)) {
                    user = userManager.getUser(chatName);
                    sendMessage(user, createMessage(CREATE_ROOM, "이미 존재하는 채팅방"));
                } else {
                    groupManger.addChatRoom(msg);
                    broadcastMessage(userManager.findAllMessageHandler(), createMessage(ROOM_LIST, groupManger.getRoomsToString()));
                }
                break;
            // 채팅방 메시지
            case NORMAL:
                user = userManager.getUser(chatName);
                String[] msgSplit = msg.split("\\|");
                String sendMsg = msgSplit[1];
                targetList = groupManger.findAllMessageHandler(user.getChatRoomName());
                broadcastMessage(targetList, createMessage(NORMAL, String.format("%s: %s", user.getChatName(), sendMsg)));
                break;
            case REMOVE_ROOM:
                user = userManager.getUser(chatName);
                groupManger.removeChatRoom(user.getChatRoomName());
            case EXIT_PROGRAM:
                user = userManager.getUser(chatName);
                userManager.removeUser(user);
            default:
                System.out.printf("ChatCommand %c \n", command);
                break;
        }
    }
}
