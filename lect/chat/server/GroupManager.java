package lect.chat.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// GroupManager roomList객체만 관리 / MessageHandler는 관리 x
public class GroupManager {
    // Duck[] arr;
    private static List<Group> roomList = new ArrayList<>();
    private static Vector<MessageHandler> clientGroup = new Vector<>();

    private GroupManager() {
    }

    // 채팅방 생성 있는 채팅방이면 생성
    // 매개변수 = 채팅방 이름
    public static void addChatRoom(String roomName) {
        if(!isinGroup(roomName))
        // for문에서 존재 여부 값이 변하지 않으면 채팅방 생성 후 roomList 추가
            roomList.add(new Group(roomName));
    }

    // 채팅방 내 사용자 추가
    public static void addMessageHandler(String roomName, MessageHandler handler) {
        for (Group g : roomList) {
            if (g.getGroupName().equals(roomName)) {
                g.addUser(handler);
                return;
            }
        }
    }

    // 채팅방 내 사용자 삭제
    public static void removeMessageHandler(String roomName, MessageHandler handler) {
        for (Group g : roomList) {
            if (g.getGroupName().equals(roomName)) {
                g.removeUser(handler);
                return;
            }
        }
    }

    // 퇴장 메시지 브로드캐스트
    public static void broadcastLeftChatter(String roomName, MessageHandler handler) {
        for (Group g : roomList) {
            if (g.getGroupName().equals(roomName)) {
                g.broadCastLeftChatter(handler);
                return;
            }
        }
    }

    public static void broadcastNewChatter(String roomName, MessageHandler newHandler) {
        for (Group g : roomList) {
            if (g.getGroupName().equals(roomName)) {
                g.broadcastNewChatter(newHandler);
                return;
            }
        }
    }


    // 채팅방 내 브로드캐스트 전파
    public static void broadcastMessage(String roomName, String msg) {
        for (Group g : roomList) {
            if (g.getGroupName().equals(roomName)) {
                g.broadcastMessage(msg);
                return;
            }
        }
    }

    public static void closeAllMessageHandlers() {
        for (MessageHandler handler : clientGroup) {
            handler.close();
        }
        clientGroup.clear();
    }

    public static boolean isinGroup(String roomName) {
        if(roomList.size() == 0)
            return false;
        for(Group g : roomList) {
            if(g.getGroupName().equals(roomName)) {
                return true;
            }
        }
        return false;
    }

    static String createMessage(char command, String msg) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.delete(0, msgBuilder.length());
        msgBuilder.append("[");
        msgBuilder.append(command);
        msgBuilder.append("]");
        msgBuilder.append(msg);
        return msgBuilder.toString();
    }
}
