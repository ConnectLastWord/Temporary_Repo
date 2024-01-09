package lect.chat.server.application.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginManager {
    BufferedReader br;
    PrintWriter pw;
    public LoginManager(BufferedReader br, PrintWriter pw) {
        this.br = br;
        this.pw = pw;
    }

    public void println(String msg) {
        pw.println(msg);
    }
    public String readLine() throws IOException {
        return br.readLine();
    }
}
