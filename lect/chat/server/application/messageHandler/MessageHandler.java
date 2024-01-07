package lect.chat.server.application.messageHandler;

import java.io.IOException;

// ClientHandler가 구현체
public interface MessageHandler {
    public String getMessage() throws IOException;

    public void handleRequest(String msg);

    public void close() throws IOException;
}
