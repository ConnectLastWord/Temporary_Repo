package lect.chat.server;

import lect.chat.protocol.ChatCommandUtil;

public class ClientBroadCast implements BroadCast {
    @Override
    public void broadcastMessage(String msg) {
        messageHandlerRepo.getKeySet();
        for (String name : messageHandlerRepo.getKeySet()) {
            MessageHandler handler = messageHandlerRepo.getMessageHandler(name);
            Message.sendMessage(handler, ChatCommandUtil.ROOM_LIST, msg);
        }
    }
}
