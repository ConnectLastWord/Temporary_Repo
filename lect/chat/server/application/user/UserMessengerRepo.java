package lect.chat.server.application.messenger.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMessengerRepo {
    UserMessengerFactory userMessengerFactory;
    private static UserMessengerRepo instance;
    private static Map<String, UserMessenger> clientList;

    private UserMessengerRepo() {
        clientList = new HashMap<>();
    }

    // 싱글톤 패턴 구현
    public static UserMessengerRepo getInstance() {
        if (instance == null) {
            instance = new UserMessengerRepo();
        }
        return instance;
    }

    // 사용자 추가
    public UserMessenger add(char protocol, String chatName, String chatId, BufferedReader br, PrintWriter pw, String host) {
        UserMessenger userMessenger = userMessengerFactory.getUser(protocol, chatName, chatId, br, pw, host);
        clientList.put(userMessenger.getChatName(), userMessenger);
        return userMessenger;
    }

    // 사용자 삭제
    public void remove(UserMessenger userMessenger) {
        clientList.remove(userMessenger.getChatName());
    }

    // 모든 사용자 조회
    public List<UserMessenger> getValues() {
        return clientList.values().stream().toList();
    }

    public UserMessenger getValue(String chatName) {
        return clientList.get(chatName);
    }

    public boolean isContains(String userName) {
        if (clientList.containsKey(userName))
            return true;
        return false;
    }
}
