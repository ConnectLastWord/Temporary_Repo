package lect.chat.client;

import lect.chat.client.event.ChatConnector;
import lect.chat.client.event.ChatSocketListener;
import lect.chat.client.event.MessageReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

// 채팅 기반 신호 리스너
public class ChatMessageReceiver implements Runnable, ChatSocketListener {
    private BufferedReader reader;
    private MessageReceiver receiver;
    ChatConnector connector;

    public ChatMessageReceiver(ChatConnector c) {
        connector = c;
    }

    public void setMessageReceiver(MessageReceiver r) {
        receiver = r;
    }

    // 채팅 기능을 위한 스레드
    public void run() {
        String msg;
        try {
            while (connector.socketAvailable()) { //소켓이 null이나 closed가 아닌 경우
                msg = reader.readLine();
                System.out.println(msg);
                if (msg == null) {
                    System.out.println("Terminating ChatMessageReceiver: message received is null");
                    break;
                }
                if (receiver != null) {
                    receiver.messageArrived(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Terminating ChatMessageReceiver: " + e.getMessage());
        } finally {
            connector.invalidateSocket();
        }

    }

    public void socketClosed() {
    }

    public void socketConnected(Socket s) throws IOException {
        reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        new Thread(this).start();
    }

    public void checkUserName(Socket s){
    }
    public void initConnect(Socket s) {
    }
}
