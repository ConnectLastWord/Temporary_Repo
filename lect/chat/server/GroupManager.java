package lect.chat.server;

import lect.chat.protocol.ChatCommandUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GroupManager {
    private static Map<String, Vector<MessageHandler>> roomGroup = new HashMap<>();
    private static Vector<MessageHandler> clientGroup = new Vector<>();

    private GroupManager() {
    }

    // 채팅방 생성 있는 채팅방이면 생성
    public static void addChatRoom(String msg) {
        if (!roomGroup.keySet().contains(msg)) {
            roomGroup.put(msg, new Vector<MessageHandler>());
        }
    }

    // 채팅방 내 사용자 추가
    public static void addMessageHandler(String roomName, MessageHandler handler) {
        Vector<MessageHandler> users = roomGroup.get(roomName);
        users.add(handler);
        System.out.println("Active clients count: " + users.size());
    }

    // 채팅방 내 사용자 삭제
    public static void removeMessageHandler(String chatRoomName, MessageHandler newHandler) {
        Vector<MessageHandler> users = roomGroup.get(chatRoomName);
        // null 유효성 검사
        if (users != null) {
            users.remove(newHandler);
            System.out.println("Active clients count: " + users.size());
        }
    }

    public static void broadcastLeftChatter(String roomName, MessageHandler newHandler) {
        StringBuilder userList = new StringBuilder();
        // 해당 채팅방 (room)불러오기
        Vector<MessageHandler> users = roomGroup.get(roomName);
        //  사용자 한명
        MessageHandler handler;
        // 채팅방 인원수 파악
        int size = users.size();
        // 채팅방 내 사용자 반복수
        for (int i = 0; i < size; i++) {
            // 채팅방 내 사용자 1명 불러오기
            handler = users.get(i);
            userList.append(handler.getName()).append(",")
                    .append(handler.getId()).append(",")
                    .append(handler.getFrom());
            System.out.println(userList);
            if (i < (size - 1)) {
                userList.append("|");
            }
        }
        for (MessageHandler mh : users) {
            if (mh != newHandler) {
                // 퇴장 브로드캐스팅
                mh.sendMessage(createMessage(ChatCommandUtil.EXIT_ROOM,
                        newHandler.getName() + " has just left [" + roomName + "] room room"));
            }
            // List Of Users 브로드 캐스팅
            mh.sendMessage(createMessage(ChatCommandUtil.USER_LIST, userList.toString()));
        }
    }

    public static void broadcastNewChatter(String roomName, MessageHandler newHandler) {
        StringBuilder userList = new StringBuilder();
        // 해당 채팅방 (room)불러오기
        Vector<MessageHandler> users = roomGroup.get(roomName);
        //  사용자 한명
        MessageHandler handler;
        // 채팅방 인원수 파악
        int size = users.size();
        // 채팅방 내 사용자 반복수
        for (int i = 0; i < size; i++) {
            // 채팅방 내 사용자 1명 불러오기
            handler = users.get(i);
            userList.append(handler.getName()).append(",")
                    .append(handler.getId()).append(",")
                    .append(handler.getFrom());

            if (i < (size - 1)) {
                userList.append("|");
            }
        }
        for (MessageHandler mh : users) {
            if (mh != newHandler) {
                // 입장 브로드캐스팅
                mh.sendMessage(createMessage(ChatCommandUtil.ENTER_ROOM,
                        (newHandler.getName() + " has just entered [" + roomName + "] room")));
            }
            // List Of Users 브로드 캐스팅
            mh.sendMessage(createMessage(ChatCommandUtil.USER_LIST, userList.toString()));
        }
    }


    // 채팅방 내 브로드캐스트 전파
    public static void broadcastMessage(String roomName, String msg) {
        Vector<MessageHandler> users = roomGroup.get(roomName);
        for (MessageHandler handler : users) {
            handler.sendMessage(createMessage(ChatCommandUtil.NORMAL, msg));
        }
    }

    public static void closeAllMessageHandlers() {
        for (MessageHandler handler : clientGroup) {
            handler.close();
        }
        clientGroup.clear();
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
