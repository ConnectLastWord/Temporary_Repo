package lect.chat.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer implements Runnable {
    ServerSocket ss;

    public ChatServer(int port) throws IOException {
        ss = new ServerSocket(port);
        System.out.printf("ChatServer[%s] is listening on port %d\n", InetAddress.getLocalHost().getHostAddress(),
                port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    cleanup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void run() {
        Socket s = null;
        try {
            while (true) {
                s = ss.accept();
                System.out.format("Client[%s] accepted\n", s.getInetAddress().getHostName());
                // 클라이언트 연결 소켓을 ClientHandler 생성자에게 전달
                new Thread(new MessageHandlerImpl(s)).start();
            }
        } catch (IOException e) {
            System.out.println("Terminating ChatServer: " + e.getMessage());
        }
        System.out.println("ChatServer shut down");
    }

    public void cleanup() throws IOException {
        ss.close();
    }
}
