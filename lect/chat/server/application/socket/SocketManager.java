package lect.chat.server.application.socket;

import java.net.Socket;

public class SocketManager {
    private Socket socket;

    public SocketManager(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
