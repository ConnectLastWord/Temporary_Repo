package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class UserImpl {
    private Socket socket;
    BufferedReader br;
    PrintWriter pw;
    private String chatName;
    private String id;
    private String host;
    private String chatRoomName;

    public UserImpl(Socket socket, BufferedReader br, PrintWriter pw, String host) {
        this.socket = socket;
        this.br = br;
        this.pw = pw;
        this.host = host;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String readLine() throws IOException {
        return br.readLine();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void println(String msg) {
        pw.println(msg);
    }
}
