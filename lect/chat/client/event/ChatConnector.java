package lect.chat.client.event;
import java.net.*;
public interface ChatConnector {
	boolean connect();
	void disConnect();
	Socket getSocket();
	boolean socketAvailable();
	void invalidateSocket();
	String getName();
	void setName(String userName);
	String getId();
}
