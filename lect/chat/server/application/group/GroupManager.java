package lect.chat.server.application.group;

import lect.chat.server.application.user.UserImpl;

import java.util.List;

// GroupManager roomList객체만 관리 / MessageHandler는 관리 x
public class GroupManager {
    private static GroupManager instance;
    private static GroupRepo groupRepo = GroupRepo.getInstance();

    // 싱글톤 패턴 구현
    public static GroupManager getInstance() {
        if (instance == null) {
            instance = new GroupManager();
        }
        return instance;
    }

    // 채팅방 추가
    public void addChatRoom(String roomName) {
        groupRepo.add(roomName);
    }

    // 채팅방 삭제
    public void removeChatRoom(String roomName) {
        groupRepo.remove(roomName);
    }

    // 채팅방 유저 추가
    public List<UserImpl> addUserByChatRoom(String roomName, UserImpl user) {
        Group g = findByName(roomName);
        return g.addUser(user);
    }

    // 채팅방 유저 삭제
    public List<UserImpl> removeUserByChatRoom(String roomName, UserImpl user) {
        Group g = findByName(roomName);
        return g.removeUser(user);
    }

    // 특정 채팅방 조회
    public static Group findByName(String roomName) {
        return groupRepo.getGroup(roomName);
    }

    // 채팅방 이름 문자열 조회
    public String getRoomsToString() {
        return groupRepo.getRoomList();
    }

    // 채팅방 사용자 문자열 조회
    public String getUserByChatRoomToString(String roomName) {
        return findByName(roomName).getUserList();
    }

    // 포함 여부
    public boolean isContains(String roomName) {
        if (groupRepo.getSize() == 0)
            return false;
        if (groupRepo.isContains(roomName)) {
            return true;
        }
        return false;
    }

    public List<UserImpl> findAllMessageHandler(String name) {
        return groupRepo.findAllMessageHandler(name);
    }
}
