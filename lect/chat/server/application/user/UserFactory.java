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

    public User getUser(char protocol, String userName, String userId, String host) {
        if(protocol == ChatCommandUtil.CREATE_DEFAULT_USER)
            return new DefaultUser(userName, userId, host);
        else if(protocol == ChatCommandUtil.CREATE_ANONYMOUS_USER)
            return new AnonymousUser(userId, host);
        return null;
    }
}
