package lect.chat.server.application.messageHandler;

import java.io.IOException;

// ClientHandler가 구현체
public interface MessageHandler {
    public String getId();

    public String getName();

    public String getRoomName();

    public String getFrom();

    public void run();

    public void sendMessage(String msg);

    public String getMessage() throws IOException;

    public void close(String chatName);

    public String createMessage(char protocol, String s);
}
