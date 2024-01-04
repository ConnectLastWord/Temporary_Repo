package lect.chat.server;

import lect.chat.protocol.ChatCommandUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageHandlerRepo {
    private static MessageHandlerRepo instance;
    private static Map<String, MessageHandler> clientList = new HashMap<>();

    // 싱글톤 패턴 구현
    public static MessageHandlerRepo getInstance() {
        if (instance == null) {
            instance = new MessageHandlerRepo();
        }
        return instance;
    }

    // 사용자 추가
    public void add(MessageHandler handler) {
        clientList.put(handler.getName(), handler);
    }

    // 사용자 삭제
    public void remove(MessageHandler handler) {
        clientList.remove(handler.getName());
    }

    // Group 이름 Set 조회
    public Set<String> getKeySet() {
        return clientList.keySet();
    }

    // 모든 사용자 브로드캐스트
    public void broadcastMessage(String msg) {
        for (String name : clientList.keySet()) {
            MessageHandler handler = clientList.get(name);
            Message.sendMessage(handler, ChatCommandUtil.ROOM_LIST, msg);
        }
    }

    public MessageHandler getMessageHandler(String name) {
        return clientList.get(name);
    }

    public boolean isContains(String userName) {
        if(clientList.containsKey(userName))
            return true;
        return false;
    }
}
