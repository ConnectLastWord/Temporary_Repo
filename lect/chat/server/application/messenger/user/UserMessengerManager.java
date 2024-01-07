package lect.chat.server.application.messenger.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class UserMessengerManager {
    private static UserMessengerManager instance;
    private static UserRepo userRepo;
    private static UserMessengerFactory userMessengerFactory;

    private UserMessengerManager() {
        userRepo = UserRepo.getInstance();
        userMessengerFactory = UserMessengerFactory.getInstance();
    }

    // 싱글톤 패턴 구현
    public static UserMessengerManager getInstance() {
        if (instance == null) {
            instance = new UserMessengerManager();
        }
        return instance;
    }

    // 사용자 추가
    public String addUser(char protocol, Socket socket, String chatName, String chatId, BufferedReader br, PrintWriter pw, String host) {
        UserMessenger userMessenger = userMessengerFactory.getUser(protocol, socket, chatName, chatId, br, pw, host);
        userRepo.add(userMessenger);

        return userMessenger.getChatName();
    }

    // 사용자 삭제
    public void removeUser(UserMessenger userMessenger) {
        userRepo.remove(userMessenger);
    }
    // 포함 여부
    public boolean isContains(String userName) {
        return userRepo.isContains(userName);
    }

    public List<UserMessenger> findAllMessageHandler() {
        return userRepo.getValues();
    }

    public UserMessenger getUserMessenger(String chatName) {
        return userRepo.getValue(chatName);
    }

    public String getChatName(String chatName) {
        return chatName;
    }

    public String getChatRoomName(String chatName) {
        UserMessenger userMessenger = userRepo.getValue(chatName);
        return userMessenger.getChatRoomName();
    }
}
