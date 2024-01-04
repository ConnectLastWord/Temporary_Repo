package lect.chat.server;

public class MessageHandlerManager implements BroadCast {
    private static MessageHandlerRepo messageHandleRepo = MessageHandlerRepo.getInstance();
    private static BroadCast broadCast = new ClientBroadCast();
    private static MessageHandlerManager instance;

    // 싱글톤 패턴 구현
    public static MessageHandlerManager getInstance() {
        if (instance == null) {
            instance = new MessageHandlerManager();
        }
        return instance;
    }

    // 사용자 추가 책임 위임
    public static void addMessageHandler(MessageHandler handler) {
        messageHandleRepo.add(handler);
    }

    // 사용자 삭제 책임 위임
    public static void removeMessageHandler(MessageHandler handler) {
        messageHandleRepo.remove(handler);
    }

    // 사용자 브로드 캐스트 책임 위임
    public void broadcastMessage(String msg) {
        broadCast.broadcastMessage(msg);
    }

    public static boolean isContains(String userName) {
        return messageHandleRepo.isContains(userName);
    }
}
