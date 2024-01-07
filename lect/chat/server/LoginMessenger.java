package lect.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class LoginMessenger extends Messenger{
    public LoginMessenger(BufferedReader br, PrintWriter pw) {
        this.br = br;
        this.pw = pw;
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
