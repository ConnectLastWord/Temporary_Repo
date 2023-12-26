package lect.chat.client.p2p;
import java.io.*;
import java.net.*;
public class P2P implements Runnable {
	ServerSocket ss;
	public static int PORT = 1224;
	private static P2P instance;
	
	private P2P () {}
	public static P2P getInstance() {
		if(instance == null) instance = new P2P();
		return instance;
	}
	public void startService() throws IOException {
		if(ss != null && ss.isBound()) return;
		ss = new ServerSocket(PORT);
		new Thread(this).start();
	}
	public void stopService() {
		try {
			ss.close();
			ss = null;
		} catch(IOException e) {
			
		}
	}
	public void run () {
		Socket s;
		try {
			while(true) {
				s = ss.accept();
				new Thread(new FileReceiver(s)).start();
			}
		} catch(IOException e) {
			
		} finally {
			try { if(ss != null) ss.close(); } catch(Exception e) {}
		}
	}
}
