package lect.chat.server;

import java.util.List;

public class MessageHandlerManager {
    private static MessageHandlerManager instance;
    private static MessageHandlerRepo messageHandleRepo = MessageHandlerRepo.getInstance();

    // 싱글톤 패턴 구현
    public static MessageHandlerManager getInstance() {
        if (instance == null) {
            instance = new MessageHandlerManager();
        }
        return instance;
    }

    // 사용자 추가
    public void addMessageHandler(MessageHandler handler) {
        messageHandleRepo.add(handler);
    }

    // 사용자 삭제
    public void removeMessageHandler(MessageHandler handler) {
        messageHandleRepo.remove(handler);
    }

    // 포함 여부
    public boolean isContains(String userName) {
        return messageHandleRepo.isContains(userName);
    }

    public List<MessageHandler> findAllMessageHandler() {
        return messageHandleRepo.getValues();
    }
}
