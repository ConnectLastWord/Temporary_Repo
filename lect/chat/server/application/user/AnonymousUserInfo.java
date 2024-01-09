package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AnonymousUserInfo extends UserInfo {
    static long idx = 1;

    public AnonymousUserInfo(Socket socket, BufferedReader br, PrintWriter pw, String host) {
        super(socket, br, pw, host);
        setChatName(String.format("Anonymous %d", idx));
        idx++;
    }
}
