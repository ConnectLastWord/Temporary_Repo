package lect.chat.server;

public interface BroadCast {
    GroupRepo groupRepo = GroupRepo.getInstance();
    MessageHandlerRepo messageHandlerRepo = MessageHandlerRepo.getInstance();

    void broadcastMessage(String msg);
}
