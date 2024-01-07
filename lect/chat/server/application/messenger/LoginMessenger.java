package lect.chat.server.application.messenger;

import lect.chat.server.application.socket.SocketManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginMessenger extends Messenger{
    public LoginMessenger(BufferedReader br, PrintWriter pw) {
        this.br = br;
        this.pw = pw;
    }

    @Override
    public void println(String msg) {
        pw.println(msg);
    }

    @Override
    public String readLine() throws IOException {
        return br.readLine();
    }


}
