package lect.chat.client.event;
import java.io.*;
import java.net.Socket;

public interface ChatSocketListener {
	public void socketClosed();
	public void socketConnected(Socket s) throws IOException ;
	public void checkUserName(Socket s, String userName);
	public void initConnect(Socket s);
}
