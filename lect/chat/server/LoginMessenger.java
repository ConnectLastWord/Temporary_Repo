package lect.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginMessenger extends Messenger{
    SocketManager socketManager;

    public LoginMessenger(BufferedReader br, PrintWriter pw) {
        this.br = br;
        this.pw = pw;
    }
    public String readLine() throws IOException {
        return br.readLine();
    }

    public void println(String msg) {
        pw.println(msg);
    }
}
