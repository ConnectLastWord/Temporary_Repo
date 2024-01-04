package lect.chat.server;

import lect.chat.protocol.ChatCommandUtil;

public class MessageHandlerManager {
    private static MessageHandlerRepo messageHandleRepo = MessageHandlerRepo.getInstance();

    // 사용자 추가 책임 위임
    public static void addMessageHandler(MessageHandler handler) {
        messageHandleRepo.add(handler);
    }

    // 사용자 삭제 책임 위임
    public static void removeMessageHandler(MessageHandler handler) {
        messageHandleRepo.remove(handler);
    }

    // 사용자 브로드 캐스트
    public static void broadcastMessage(String msg) {
        messageHandleRepo.getKeySet();
        for (String name : messageHandleRepo.getKeySet()) {
            MessageHandler handler = messageHandleRepo.getMessageHandler(name);
            Message.sendMessage(handler, ChatCommandUtil.ROOM_LIST, msg);
        }
    }
}
