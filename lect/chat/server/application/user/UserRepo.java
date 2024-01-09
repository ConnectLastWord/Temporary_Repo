package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepo {
    UserFactory userFactory;
    private static UserRepo instance;
    private static Map<String, User> clientList;

    private UserRepo() {
        clientList = new HashMap<>();
        userFactory = UserFactory.getInstance();
    }

    // 싱글톤 패턴 구현
    public static UserRepo getInstance() {
        if (instance == null) {
            instance = new UserRepo();
        }
        return instance;
    }

    // 사용자 추가
    public User add(char protocol, String chatName, String chatId, BufferedReader br, PrintWriter pw, String host) {
        User user = userFactory.getUser(protocol, chatName, chatId, br, pw, host);
        clientList.put(user.getChatName(), user);
        return user;
    }

    // 사용자 삭제
    public void remove(User user) {
        clientList.remove(user.getChatName());
    }

    // 모든 사용자 조회
    public List<User> getValues() {
        return clientList.values().stream().toList();
    }

    public User getValue(String chatName) {
        return clientList.get(chatName);
    }

    public boolean isContains(String userName) {
        if (clientList.containsKey(userName))
            return true;
        return false;
    }
}
