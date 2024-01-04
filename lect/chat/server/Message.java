package lect.chat.server;

public class Message {
    public static void sendMessage(MessageHandler handler, char protocol, String msg) {
        handler.sendMessage(createMessage(protocol, msg));
    }

    public static String createMessage(char protocol, String msg) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.delete(0, msgBuilder.length());
        msgBuilder.append("[");
        msgBuilder.append(protocol);
        msgBuilder.append("]");
        msgBuilder.append(msg);
        return msgBuilder.toString();
    }

}
