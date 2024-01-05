package lect.chat.server;

import java.util.List;

public class UserManager {
    private static UserManager instance;
    private static UserRepo messageHandleRepo = UserRepo.getInstance();

    // 싱글톤 패턴 구현
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // 사용자 추가
    public void addMessageHandler(UserImpl user) {
        messageHandleRepo.add(user);
    }

    // 사용자 삭제
    public void removeMessageHandler(UserImpl user) {
        messageHandleRepo.remove(user);
    }

    // 포함 여부
    public boolean isContains(String userName) {
        return messageHandleRepo.isContains(userName);
    }

    public List<UserImpl> findAllMessageHandler() {
        return messageHandleRepo.getValues();
    }
}
