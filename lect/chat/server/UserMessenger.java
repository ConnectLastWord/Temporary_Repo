package lect.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class UserMessenger extends Messenger{
    String chatName;
    public UserMessenger(BufferedReader br, PrintWriter pw) {
        this.br = br;
        this.pw = pw;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    @Override
    public String readLine() throws IOException {
        return br.readLine();
    }

    @Override
    public void println(Object... objects) {
        pw.println(objects[0]);
    }

}
