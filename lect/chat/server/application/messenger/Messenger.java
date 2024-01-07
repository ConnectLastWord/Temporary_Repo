package lect.chat.server.application.messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Messenger {
    protected Socket socket;
    protected BufferedReader br;
    protected PrintWriter pw;

    public abstract void println(String msg);
    public abstract String readLine() throws IOException;
}
