package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DefaultUserMessenger extends UserMessenger {
    public DefaultUserMessenger(Socket socket, String chatName, String userId, BufferedReader br, PrintWriter pw, String host) {
        super(socket, userId, br, pw, host);
        setChatName(chatName);
        System.out.println("Default User 생성");
    }
}
