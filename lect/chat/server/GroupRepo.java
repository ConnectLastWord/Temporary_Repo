package lect.chat.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupRepo {
    private static GroupRepo instance;
    private static Map<String, Group> roomsList = new HashMap<>();

    // 싱글톤 패턴 구현
    public static GroupRepo getInstance() {
        if (instance == null) {
            instance = new GroupRepo();
        }
        return instance;
    }

    // 채팅방 추가
    public void add(String roomName) {
        roomsList.put(roomName, new Group(roomName));
    }

    // 채팅방 삭제
    public void remove(String name) {

    }

    // 채팅방 조회
    public Group getGroup(String roomName) {
        return roomsList.get(roomName);
    }

    // 채팅방 사용자 조회
    public List<UserImpl> findAllMessageHandler(String roomName) {
        return getGroup(roomName).getClientList();
    }

    // 포함 여부
    public boolean isContains(String roomName) {
        return roomsList.containsKey(roomName);
    }

    // Group 이름 Set 조회
    public Set<String> getKeySet() {
        return roomsList.keySet();
    }


    // 채팅방 개수 조회
    public int getSize() {
        return roomsList.size();
    }

    // 문자열 채팅방 리스트 return
    public String getRoomList() {
        String[] names = getKeySet().toArray(new String[0]);
        StringBuilder rooms = new StringBuilder(String.join("|", names));
        return rooms.toString();
    }
}
