package lect.chat.server;

import java.util.Vector;

// GroupManager roomList객체만 관리 / MessageHandler는 관리 x
public class GroupManager {
    // Duck[] arr;
    private static GroupRepo groupRepo = GroupRepo.getInstance();
    private static Vector<MessageHandler> clientGroup = new Vector<>();
    private static BroadCast broadCast = new ChatBroadCast();

    // 채팅방 추가 책임 위임
    public static void addChatRoom(String roomName) {
        groupRepo.add(roomName);
    }

    // 채팅방 삭제 책임 위임
    public static void removeChatRoom(String roomName) {
    }

    // 특정 채팅방 조회 책임 위임
    public static Group findByName(String roomName) {
        return groupRepo.getGroup(roomName);
    }

    // 채팅방 브로드캐스트 책임 위임
    public static void broadcastMessage(String msg) {
    }

    // 포함 여부
    public static boolean isinGroup(String roomName) {
        if (groupRepo.getSize() == 0)
            return false;
        if (groupRepo.isContains(roomName)) {
            return true;
        }
        return false;
    }

    // 채팅방 이름 조회
    public static String getRoomsToString() {
        return groupRepo.getRoomList();
    }

    public static void closeAllMessageHandlers() {
        for (MessageHandler handler : clientGroup) {
            handler.close();
        }
        clientGroup.clear();
    }
}
