package lect.chat.server;

import lect.chat.server.application.messageHandler.MessageHandler;
import lect.chat.server.application.messageHandler.MessageHandlerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Initializer implements Runnable{
    SocketManager socketManager;
    LoginMessenger loginMessenger;

    public Initializer(Socket socket) throws IOException {
        socketManager = new SocketManager();
        socketManager.setSocket(socket);
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