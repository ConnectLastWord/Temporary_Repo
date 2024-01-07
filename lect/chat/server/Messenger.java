package lect.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Messenger {
    protected BufferedReader br;
    protected PrintWriter pw;

//    public abstract void println(String msg);
    public abstract void println(Object... objects);
    public abstract String readLine() throws IOException;
}
