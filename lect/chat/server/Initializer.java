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
    MessageHandler messageHandler;

    public Initializer(Socket socket) throws IOException {
        socketManager = new SocketManager(socket);
    }
    @Override
    public void run() {
        try {
            messageHandler = new MessageHandlerImpl(socketManager);
            messageHandler.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}