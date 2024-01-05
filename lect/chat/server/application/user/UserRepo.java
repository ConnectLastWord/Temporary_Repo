package lect.chat.server.application.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepo {
    private static UserRepo instance;
    private static Map<String, UserImpl> clientList = new HashMap<>();

    // 싱글톤 패턴 구현
    public static UserRepo getInstance() {
        if (instance == null) {
            instance = new UserRepo();
        }
        return instance;
    }

    // 사용자 추가
    public void add(UserImpl user) {
        clientList.put(user.getChatName(), user);
    }

    // 사용자 삭제
    public void remove(UserImpl user) {
        clientList.remove(user.getChatName());
    }


    // 모든 사용자 조회
    public List<UserImpl> getValues() {
        return clientList.values().stream().toList();
    }

    public boolean isContains(String userName) {
        if (clientList.containsKey(userName))
            return true;
        return false;
    }
}
