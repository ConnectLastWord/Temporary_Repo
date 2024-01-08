package lect.chat.server.application.messenger.user;

import lect.chat.protocol.ChatCommandUtil;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserMessengerFactory {
    private static UserMessengerFactory instance;
    private UserMessengerFactory() {
    }

    public static UserMessengerFactory getInstance() {
        if(instance == null)
            instance = new UserMessengerFactory();
        return instance;
    }

    public UserMessenger getUser(char protocol, Socket socket, String userName, String userId, BufferedReader br, PrintWriter pw, String host) {
        if(protocol == ChatCommandUtil.CREATE_DEFAULT_USER)
            return new DefaultUserMessenger(userName, userId, br, pw, host);
        else if(protocol == ChatCommandUtil.CREATE_ANONYMOUS_USER)
            return new AnonymousUserMessenger(userId, br, pw, host);
        return null;
    }
}
