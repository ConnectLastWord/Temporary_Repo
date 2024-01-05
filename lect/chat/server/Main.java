package lect.chat.server;

import lect.chat.server.api.ChatServer;

import java.io.IOException;

public class Main {
    static int idx = 0;

    public static void main(String[] args) {
        try {
            Runnable r = new ChatServer(8081);
            new Thread(r).start();
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }
    }
}
