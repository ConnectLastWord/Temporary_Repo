package lect.chat.server.application.messenger.user;

import lect.chat.server.application.messenger.Messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class UserMessenger extends Messenger {
    private String chatName;
    private String id;
    private String host;
    private String chatRoomName;

    public UserMessenger(String userId, BufferedReader br, PrintWriter pw, String host) {
        this.id = userId;
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

    @Override
    public void println(String msg) {
        pw.println(msg);
    }

    @Override
    public String readLine() throws IOException {
        return br.readLine();
    }
}
