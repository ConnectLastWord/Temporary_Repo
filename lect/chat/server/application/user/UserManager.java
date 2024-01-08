package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;

public class UserManager {
    private static UserManager instance;
    private static UserRepo userRepo;

    private UserManager() {
        userRepo = UserRepo.getInstance();
    }

    // 싱글톤 패턴 구현
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // 사용자 추가
    public String addUser(char protocol, String chatName, String chatId, BufferedReader br, PrintWriter pw, String host) {
        return userRepo.add(protocol, chatName, chatId, br, pw, host).getChatName();
    }

    // 사용자 삭제
    public void removeUser(User user) {
        userRepo.remove(user);
    }
    // 포함 여부
    public boolean isContains(String userName) {
        return userRepo.isContains(userName);
    }

    public List<User> findAllMessageHandler() {
        return userRepo.getValues();
    }

    public String getChatName(String chatName) {
        return chatName;
    }

    public String getChatRoomName(String chatName) {
        User user = userRepo.getValue(chatName);
        return user.getChatRoomName();
    }

    public User getUser(String chatName) {
        return userRepo.getValue(chatName);
    }
}
