package lect.chat.client.connect;

import java.net.Socket;

public interface ChatConnector {
    public boolean connect();

    public void disConnect();

    public Socket getSocket();

    public boolean socketAvailable();

    public void invalidateSocket();

    public String getName();

    public void setName(String userName);

    public String getId();
}
