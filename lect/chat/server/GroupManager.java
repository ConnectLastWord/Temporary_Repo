package lect.chat.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import lect.chat.protocol.ChatCommandUtil;

public class GroupManager {
    private static Map<String, Vector<MessageHandler>> roomGroup = new HashMap<String, Vector<MessageHandler>>();
    private static Vector<MessageHandler> clientGroup = new Vector<MessageHandler>();

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
        Vector<MessageHandler> room = roomGroup.get(roomName);
        room.add(handler);
        System.out.println("Active clients count: " + room.size());
    }

    // 채팅방 내 사용자 삭제
    public static void removeMessageHandler(String chatRoomName, MessageHandler newHandler) {
        StringBuilder users = new StringBuilder();
        Vector<MessageHandler> rooms = roomGroup.get(chatRoomName);
        rooms.remove(newHandler);
        System.out.println("Active clients count: " + rooms.size());
    }

    public static void broadcastLeftChatter(String roomName, MessageHandler newHandler) {
        StringBuilder users = new StringBuilder();
        // 해당 채팅방 (room)불러오기
        Vector<MessageHandler> rooms = roomGroup.get(roomName);
        //  사용자 한명
        MessageHandler handler;
        // 채팅방 인원수 파악
        int size = rooms.size();
        // 채팅방 내 사용자 반복수
        for (int i = 0; i < size; i++) {
            // 채팅방 내 사용자 1명 불러오기
            handler = rooms.get(i);
            users.append(handler.getName()).append(",")
                    .append(handler.getId()).append(",")
                    .append(handler.getFrom());
            System.out.println(users);
            if (i < (size - 1)) {
                users.append("|");
            }
        }
        for (MessageHandler mh : rooms) {
            if (mh != newHandler) {
                // 퇴장 브로드캐스팅
                mh.sendMessage(createMessage(ChatCommandUtil.EXIT_ROOM,
                        newHandler.getName() + " has just left [" + roomName + "] room room"));
            }
            // List Of Users 브로드 캐스팅
            mh.sendMessage(createMessage(ChatCommandUtil.USER_LIST, users.toString()));
        }
    }

    public static void broadcastNewChatter(String roomName, MessageHandler newHandler) {
        StringBuilder userInfoStringBuilder = new StringBuilder();
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
            userInfoStringBuilder.append(handler.getName()).append(",")
                    .append(handler.getId()).append(",")
                    .append(handler.getFrom());
            System.out.println(userInfoStringBuilder);
            if (i < (size - 1)) {
                userInfoStringBuilder.append("|");
            }
        }
        for (MessageHandler mh : users) {
            if (mh != newHandler) {
                // 입장 브로드캐스팅
                mh.sendMessage(createMessage(ChatCommandUtil.ENTER_ROOM,
                        (newHandler.getName() + " has just entered [" + roomName + "] room")));
            }
            // List Of Users 브로드 캐스팅
            // 서버에게 전송 ex) [d] userList
            mh.sendMessage(createMessage(ChatCommandUtil.USER_LIST, userInfoStringBuilder.toString()));
        }
    }


    // 채팅방 내 브로드캐스트 전파
    public static void broadcastMessage(String roomName, String msg) {
        Vector<MessageHandler> room = roomGroup.get(roomName);
        for (MessageHandler handler : room) {
            handler.sendMessage(createMessage(ChatCommandUtil.NORMAL, msg));
        }
    }

    public static void closeAllMessageHandlers() {
        for (MessageHandler handler : clientGroup) {
            handler.close();
        }
        clientGroup.clear();
    }

    public static void sendWhisper(MessageHandler from, String to, String msg) {
        msg = createMessage(ChatCommandUtil.WHISPER, msg);
        for (MessageHandler handler : clientGroup) {
            if (handler.getId().equals(to)) {
                handler.sendMessage(msg);
                break;
            }
        }
        from.sendMessage(msg);
    }

    static String createMessage(char command, String msg) {
        StringBuilder msgBuilder = new StringBuilder();
        // msgBuilder.delete(0, msgBuilder.length());
        msgBuilder.append("[");
        msgBuilder.append(command);
        msgBuilder.append("]");
        msgBuilder.append(msg);
        return msgBuilder.toString();
    }
}
