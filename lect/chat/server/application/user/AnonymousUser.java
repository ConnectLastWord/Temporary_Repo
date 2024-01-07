package lect.chat.server.application.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Stack;

public class AnonymousUser extends User {
//    static long idx = 1;
    private final static Stack<Integer> idxManager = new Stack<>();
    static{
        // anonymous user 인덱스 관리자
        int maxAnonymousSize = 100;
        for(int idx=maxAnonymousSize;idx>0;idx--)
            idxManager.push(idx);
    }

    public AnonymousUser(String userId, String host) {
        super(userId, host);
        setChatName(String.format("Anonymous %d", idxManager.pop()));
    }

    public void close() {
        char userIdx = getChatName().charAt(getChatName().length()-1);
        int idx = Character.getNumericValue(userIdx);
        idxManager.push(idx);
//        try {
//            socket.close();
//            char userIdx = getChatName().charAt(getChatName().length()-1);
//            int idx = Character.getNumericValue(userIdx);
//            idxManager.push(idx);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
