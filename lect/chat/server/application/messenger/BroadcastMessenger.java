package lect.chat.server.application.messenger;

import java.io.IOException;

public class BroadcastMessenger extends Messenger{
    @Override
    public void println(String msg) {

    }

    @Override
    public String readLine() throws IOException {
        return null;
    }
}
