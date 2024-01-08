package lect.chat.server.application.messenger.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepo {
    private static UserRepo instance;
    private static Map<String, UserMessenger> clientList;

    private UserRepo() {
        clientList = new HashMap<>();
    }

    // 싱글톤 패턴 구현
    public static UserRepo getInstance() {
        if (instance == null) {
            instance = new UserRepo();
        }
        return instance;
    }

    // 사용자 추가
    public void add(UserMessenger userMessenger) {
        clientList.put(userMessenger.getChatName(), userMessenger);
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
