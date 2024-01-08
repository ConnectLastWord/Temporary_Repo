package lect.chat.server;

import lect.chat.server.application.messenger.LoginMessenger;
import lect.chat.server.application.messageHandler.MessageHandler;
import lect.chat.server.application.messageHandler.MessageHandlerImpl;
import lect.chat.server.application.socket.SocketManager;

import java.io.IOException;
import java.net.Socket;

public class Initializer implements Runnable{
    SocketManager socketManager;
    LoginMessenger loginMessenger;

    public Initializer(Socket socket) throws IOException {
        socketManager = new SocketManager(socket);
    }
    @Override
    public void run() {
        MessageHandler messageHandler = null;
        try {
            messageHandler = new MessageHandlerImpl(socketManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        messageHandler.run();
    }
}