package lect.chat.server.application.user;

import lect.chat.protocol.ChatCommandUtil;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserFactory {
    private static UserFactory instance;
    private UserFactory() {
    }

    public static UserFactory getInstance() {
        if(instance == null)
            instance = new UserFactory();
        return instance;
    }

    public User getUser(char protocol, Socket socket, String userName, String userId, BufferedReader br, PrintWriter pw, String host) {
        if(protocol == ChatCommandUtil.CREATE_DEFAULT_USER)
            return new DefaultUser(socket, userName, userId, br, pw, host);
//        else if(protocol == ChatCommandUtil.CREATE_ANONYMOUS_USER)
//            return new AnonymousUser();
        return null;
    }
}
