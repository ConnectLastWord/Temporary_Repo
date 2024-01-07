package lect.chat.server;

import java.net.Socket;

public class SocketManager {
    private Socket socket;

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
