package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class AnonymousUser extends User {
//    static long idx = 1;
    private final static Stack<Integer> idxManager = new Stack<>();
    static{
        int maxAnonymousSize = 100;
        for(int idx=maxAnonymousSize;idx>0;idx--)
            idxManager.push(idx);
    }

    public AnonymousUser(Socket socket, String userId, BufferedReader br, PrintWriter pw, String host) {
        super(socket, userId, br, pw, host);
        setChatName(String.format("Anonymous %d", idxManager.pop()));
    }

    public void close() {
        try {
            socket.close();
            char userIdx = getChatName().charAt(getChatName().length()-1);
            int idx = Character.getNumericValue(userIdx);
            idxManager.push(idx);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
