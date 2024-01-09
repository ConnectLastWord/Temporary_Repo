package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DefaultUserInfo extends UserInfo {
    public DefaultUserInfo(Socket socket, BufferedReader br, PrintWriter pw, String host) {
        super(socket, br, pw, host);
    }
}
