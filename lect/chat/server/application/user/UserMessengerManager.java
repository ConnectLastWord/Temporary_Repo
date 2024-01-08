package lect.chat.server.application.messenger.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;

public class UserMessengerManager {
    private static UserMessengerManager instance;
    private static UserMessengerRepo userMessengerRepo;

    private UserMessengerManager() {
        userMessengerRepo = UserMessengerRepo.getInstance();
    }

    // 싱글톤 패턴 구현
    public static UserMessengerManager getInstance() {
        if (instance == null) {
            instance = new UserMessengerManager();
        }
        return instance;
    }

    // 사용자 추가
    public String addUser(char protocol, String chatName, String chatId, BufferedReader br, PrintWriter pw, String host) {
        return userMessengerRepo.add(protocol, chatName, chatId, br, pw, host).getChatName();
    }

    // 사용자 삭제
    public void removeUser(UserMessenger userMessenger) {
        userMessengerRepo.remove(userMessenger);
    }
    // 포함 여부
    public boolean isContains(String userName) {
        return userMessengerRepo.isContains(userName);
    }

    public List<UserMessenger> findAllMessageHandler() {
        return userMessengerRepo.getValues();
    }

    public UserMessenger getUserMessenger(String chatName) {
        return userMessengerRepo.getValue(chatName);
    }

    public String getChatName(String chatName) {
        return chatName;
    }

    public String getChatRoomName(String chatName) {
        UserMessenger userMessenger = userMessengerRepo.getValue(chatName);
        return userMessenger.getChatRoomName();
    }
}
