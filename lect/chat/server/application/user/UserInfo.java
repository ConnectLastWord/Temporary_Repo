package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class UserInfo {
    Socket socket;
    BufferedReader br;
    PrintWriter pw;
    private String chatName;
    private String id;
    private String host;
    private String chatRoomName;

    public UserInfo(Socket socket, BufferedReader br, PrintWriter pw, String host) {
        this.socket = socket;
        this.br = br;
        this.pw = pw;
        this.host = host;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setChatName(String name) {
        this.chatName = name;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String readLine() throws IOException {
        return br.readLine();
    }

//    public void close() {
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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

    public PrintWriter getPw() {
        return pw;
    }

    public BufferedReader getBr() {
        return br;
    }

    public Socket getSocket() {
        return socket;
    }
}
