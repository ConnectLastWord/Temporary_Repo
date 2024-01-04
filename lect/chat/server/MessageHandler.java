package lect.chat.server;

import java.io.IOException;

// ClientHandler가 구현체
public interface MessageHandler {
    public String getId();

    public String getName();

    public String getRoomName();

    public String getFrom();

    public void sendMessage(String msg);

    public String getMessage() throws IOException;

    public void close();
}
