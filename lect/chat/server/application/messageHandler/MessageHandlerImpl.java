package lect.chat.server.application.messageHandler;

import lect.chat.server.application.messenger.LoginMessenger;
import lect.chat.server.application.messenger.Messenger;
import lect.chat.server.application.socket.SocketManager;
import lect.chat.server.application.group.GroupManager;
import lect.chat.server.application.messenger.user.UserMessenger;
import lect.chat.server.application.messenger.user.UserMessengerManager;

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
    private final UserMessengerManager userMessengerManager;
    // 채팅방 관리자
    private final GroupManager groupManger;
    // 소켓 관리자
    private final SocketManager socketManager;
    // 메시지 처리 전략
    Messenger strategy;
    // login 전략
    LoginMessenger loginMessenger;
    // user 전략
    UserMessenger userMessenger;

    public MessageHandlerImpl(SocketManager socketManager) throws IOException {
        userMessengerManager = UserMessengerManager.getInstance();
        groupManger = GroupManager.getInstance();
        this.socketManager = socketManager;
        // login 전략 초기화
        loginMessenger = new LoginMessenger(
                new BufferedReader(new InputStreamReader(socketManager.getSocket().getInputStream())),
                new PrintWriter(socketManager.getSocket().getOutputStream(), true));
        // user 전략 초기화
        userMessenger = null;
    }

    public void run() {
        String msg;
        try {
            setStrategy(loginMessenger);
            msg = getMessage();
            processMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            UserMessenger userMessenger = userMessengerManager.getUserMessenger(chatName);
            while (true) {
                setStrategy(userMessenger);
                msg = getMessage();
                if (msg == null) {
                    break;
                }
                processMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            UserMessenger userMessenger = userMessengerManager.getUserMessenger(chatName);
            // 삭제할 채팅방 정보 조회지
            List<UserMessenger> targetList = groupManger.removeUserByChatRoom(userMessenger.getChatRoomName(), userMessenger);
            // 퇴장 메시지 브로드 캐스트
            broadcastMessage(targetList, createMessage(EXIT_ROOM, userMessenger.getChatName() + " has just left [" + userMessenger.getChatRoomName() + "] room"));
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

    private void setStrategy(Messenger strategy) {
        this.strategy = strategy;
    }

    public String getMessage() throws IOException {
        return strategy.readLine();
    }

    public void sendMessage(String msg) {
        strategy.println(msg);
    }

    // 브로드 캐스트
    private <T extends UserMessenger> void broadcastMessage(List<T> targetList, String msg) {
        for (UserMessenger userMessenger : targetList) {
            userMessenger.println(msg);
        }
    }

    public void close() {
        UserMessenger userMessenger = userMessengerManager.getUserMessenger(chatName);
        userMessenger.close();
    }

    public void close(String chatName) {
        UserMessenger userMessenger = userMessengerManager.getUserMessenger(chatName);
        userMessenger.close();
        userMessengerManager.removeUser(userMessenger);
    }

    public String getId() {
        UserMessenger userMessenger = userMessengerManager.getUserMessenger(chatName);
        return userMessenger.getId(); //socket.getRemoteSocketAddress().toString();
    }

    public String getFrom() {
        UserMessenger userMessenger = userMessengerManager.getUserMessenger(chatName);
        return userMessenger.getHost();
    }

    public String getName() {
        UserMessenger userMessenger = userMessengerManager.getUserMessenger(chatName);
        return userMessenger.getChatName();
    }

    public String getRoomName() {
        UserMessenger userMessenger = userMessengerManager.getUserMessenger(chatName);
        return userMessenger.getChatRoomName();
    }

    public void processMessage(String msg) throws IOException {
        UserMessenger userMessenger;
        char command = getCommand(msg);// 첫번째 글자 떼옴
        //msg = [b]채팅방- 2|massage
        msg = msg.replaceFirst("\\[{1}[a-z]\\]{1}", "");// 첫번쨰 글자 없앰
        switch (command) {
            // 유저 이름 유효성 검사
            case CHECK_USER_NAME:
                String[] nameWithId = msg.split("\\|");
                setStrategy(loginMessenger);
                if(userMessengerManager.isContains(chatName)) {
                    System.out.println("fail");
                    sendMessage(createMessage(CHECK_USER_NAME, "false"));
                } else {
                    // user가 존재하지 않는 경우에는 로그인
                    sendMessage(createMessage(CHECK_USER_NAME, userMessengerManager.getChatName(chatName)));
                    sendMessage(createMessage(ROOM_LIST, groupManger.getRoomsToString()));

                    Socket socket = socketManager.getSocket();
                    chatName = userMessengerManager.addUser(CREATE_DEFAULT_USER, socket, nameWithId[0], nameWithId[1], new BufferedReader(new InputStreamReader(socket.getInputStream())),
                            new PrintWriter(socket.getOutputStream(), true), socket.getInetAddress().getHostAddress());
                    userMessenger = userMessengerManager.getUserMessenger(chatName);
                    setStrategy(userMessenger);
                    sendMessage(createMessage(CHECK_USER_NAME, userMessengerManager.getChatName(chatName)));
                }
                break;
            // 채팅방 접속
            case ROOM_LIST:
                userMessenger = userMessengerManager.getUserMessenger(chatName);
                if(userMessenger.getChatRoomName() != null) {
                    // 삭제할 채팅방 정보 조회
                    List<UserMessenger> targetList = groupManger.removeUserByChatRoom(userMessenger.getChatRoomName(), userMessenger);
                    // 퇴장 메시지 브로드 캐스트
                    broadcastMessage(targetList, createMessage(EXIT_ROOM, userMessenger.getChatName() + " has just left [" + userMessenger.getChatRoomName() + "] room"));
                    // 유저 리스트 브로드 캐스트
                    broadcastMessage(targetList, createMessage(USER_LIST, groupManger.getUserByChatRoomToString(userMessenger.getChatRoomName())));
                }
                userMessenger.setChatRoomName(msg);
                // 생성할 채팅방 정보 조회
                List<UserMessenger> targetList = groupManger.addUserByChatRoom(userMessenger.getChatRoomName(), userMessenger);
                // 입장 메시지 브로드 캐스트
                broadcastMessage(targetList, createMessage(ENTER_ROOM, userMessenger.getChatName() + " has entered [" + userMessenger.getChatRoomName() + "] room"));
                // 유저 리스트 브로드 캐스트
                broadcastMessage(targetList, createMessage(USER_LIST, groupManger.getUserByChatRoomToString(userMessenger.getChatRoomName())));
                break;
            // 채팅방 생성
            case CREATE_ROOM:
                if (groupManger.isContains(msg)) {
                    userMessenger = userMessengerManager.getUserMessenger(chatName);
                    setStrategy(userMessenger);
                    sendMessage(createMessage(CREATE_ROOM, "이미 존재하는 채팅방"));
                } else {
                    groupManger.addChatRoom(msg);
                    broadcastMessage(userMessengerManager.findAllMessageHandler(), createMessage(ROOM_LIST, groupManger.getRoomsToString()));
                }
                break;
            // 채팅방 메시지
            case NORMAL:
                userMessenger = userMessengerManager.getUserMessenger(chatName);
                String[] msgSplit = msg.split("\\|");
                String sendMsg = msgSplit[1];
                targetList = groupManger.findAllMessageHandler(userMessenger.getChatRoomName());
                broadcastMessage(targetList, createMessage(NORMAL, String.format("%s: %s", userMessenger.getChatName(), sendMsg)));
                break;
            case REMOVE_ROOM:
                userMessenger = userMessengerManager.getUserMessenger(chatName);
                groupManger.removeChatRoom(userMessenger.getChatRoomName());
            case EXIT_PROGRAM:
                userMessenger = userMessengerManager.getUserMessenger(chatName);
                userMessengerManager.removeUser(userMessenger);
            default:
                System.out.printf("ChatCommand %c \n", command);
                break;
        }
    }
}
