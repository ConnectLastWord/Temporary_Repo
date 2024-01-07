package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DefaultUser extends User {
    public DefaultUser(String chatName, String userId, String host) {
        super(userId, host);
        setChatName(chatName);
        System.out.println("Default User 생성");
    }
}
