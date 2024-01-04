package lect.chat.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    // 모든 사용자 조회
    public List<MessageHandler> getValues() {
        return clientList.values().stream().toList();
    }

    public boolean isContains(String userName) {
        if (clientList.containsKey(userName))
            return true;
        return false;
    }
}
