package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class UserManager {
    private static UserManager instance;
    private static UserRepo userRepo;
    private static UserFactory userFactory;

    private UserManager() {
        userRepo = UserRepo.getInstance();
        userFactory = UserFactory.getInstance();
    }

    // 싱글톤 패턴 구현
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // 사용자 추가
    public String addUser(char protocol, Socket socket, String chatName, String chatId, BufferedReader br, PrintWriter pw, String host) {
        User user = userFactory.getUser(protocol, socket, chatName, chatId, br, pw, host);
        userRepo.add(user);

        return user.getChatName();
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

    public User getUser(String chatName) {
        return userRepo.getValue(chatName);
    }

    public String getChatName(String chatName) {
        return chatName;
    }

    public String getChatRoomName(String chatName) {
        User user = userRepo.getValue(chatName);
        return user.getChatRoomName();
    }
}
