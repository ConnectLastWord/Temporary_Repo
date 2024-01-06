package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class User {
    Socket socket;
    BufferedReader br;
    PrintWriter pw;
    private String chatName;
    private String id;
    private String host;
    private String chatRoomName;

    public User(Socket socket, String userId, BufferedReader br, PrintWriter pw, String host) {
        this.id = userId;
        this.socket = socket;
        this.br = br;
        this.pw = pw;
        this.host = host;
    }
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public String getChatName() {
        return chatName;
    }
    public void println(String msg) {
        pw.println(msg);
    }
    public String readLine() throws IOException {
        return br.readLine();
    }
}
