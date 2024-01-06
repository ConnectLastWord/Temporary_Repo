package lect.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;

public class ConnectManager {
    private Socket socket;
    BufferedReader br;
    private PrintWriter pw;
    private static ConnectManager instance;
    private ConnectManager() {
    }

    public static ConnectManager getInstance() {
        if(instance == null)
            instance = new ConnectManager();
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
