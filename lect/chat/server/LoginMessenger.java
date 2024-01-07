package lect.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginMessenger extends Messenger{

    private static LoginMessenger instance;
    private LoginMessenger() {
    }

    public static LoginMessenger getInstance() {
        if(instance == null)
            instance = new LoginMessenger();
        return instance;
    }
    public String readLine() throws IOException {
        return br.readLine();
    }

    public void println(String msg) {
        pw.println(msg);
    }

    public void init(Socket socket, BufferedReader br, PrintWriter pw) {
        this.socket = socket;
        this.br = br;
        this.pw = pw;
    }

    public Socket getSocket() {
        return socket;
    }
}
