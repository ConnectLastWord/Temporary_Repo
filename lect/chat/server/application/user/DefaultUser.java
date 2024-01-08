package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class DefaultUser extends User {
    public DefaultUser(String chatName, String userId, BufferedReader br, PrintWriter pw, String host) {
        super(userId, br, pw, host);
        setChatName(chatName);
        System.out.println("Default User 생성");
    }
}
