package lect.chat.server.application.controller;

public interface Controller {
    void handleController(char command, String msg);

    void processMessage();

    default String createMessage(char protocol, String msg) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.delete(0, msgBuilder.length());
        msgBuilder.append("[");
        msgBuilder.append(protocol);
        msgBuilder.append("]");
        msgBuilder.append(msg);
        return msgBuilder.toString();
    }
}
