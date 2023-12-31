package lect.chat.server.application.user;

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
    public void addUser(UserInfo userInfo) {
        userRepo.add(userInfo);
    }

    // 사용자 삭제
    public void removeUser(UserInfo userInfo) {
        userRepo.remove(userInfo);
    }

    // 포함 여부
    public boolean isContains(String userName) {
        return userRepo.isContains(userName);
    }

    public List<UserInfo> findAllMessageHandler() {
        return userRepo.getValues();
    }
}
