package lect.chat.client.connect;

import java.io.IOException;
import java.net.Socket;

public interface ChatSocketListener {
    public void socketClosed();

    public void socketConnected(Socket s) throws IOException;

    public void checkUserName(Socket s);

    public void loginByAnoymous(Socket s);
}
