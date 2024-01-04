package lect.chat.client;

import lect.chat.client.event.ChatConnector;
import lect.chat.client.event.ChatSocketListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient extends WindowAdapter implements ChatConnector {
    private Socket socket;
    private String userName;
    private String id;
    private ArrayList<ChatSocketListener> sListeners = new ArrayList<>();
    private JFrame chatWindow;

    ChatClient() {
        id = new java.rmi.server.UID().toString();
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        ChatPanel chatPanel = new ChatPanel(this);
        chatPanel.setBorder(BorderFactory.createEtchedBorder());
        StatusBar statusBar = StatusBar.getStatusBar();
        statusBar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(1, 2, 2, 2)));
        contentPane.add(statusBar, BorderLayout.SOUTH);
        ChatMessageReceiver chatReceiver = new ChatMessageReceiver(this);
        chatReceiver.setMessageReceiver(chatPanel);

        chatWindow = new JFrame("Minimal Chat - Concept Proof");
        contentPane.add(chatPanel);

        chatWindow.setContentPane(contentPane);
        // 프로그램 프레임 사이즈
        chatWindow.setSize(750, 350);

        chatWindow.setLocationRelativeTo(null);
        chatWindow.setVisible(true);
        chatWindow.addWindowListener(this);

        this.addChatSocketListener(chatPanel);
        this.addChatSocketListener(chatReceiver);
    }

    @Override
    public boolean connect() {
        if (socketAvailable()) {
            return true;
        }
        try {
            socket = new Socket("127.0.0.1", 8081);
            for (ChatSocketListener lsnr : sListeners) {
                lsnr.socketConnected(socket); // 초기화만 하고 init message는 보내지 않음
            }
            userName = JOptionPane.showInputDialog(chatWindow, "Enter user name:");
            if (userName == null) {
                return false;
            }else {
                // 이름 검사를 한 후에 init message를 보냄
                sListeners.get(0).checkUserName(socket);
                return true;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect chat server", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public void disConnect() {
        if (socketAvailable()) {
            try {
                socket.close();
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public boolean socketAvailable() {
        return !(socket == null || socket.isClosed());
    }

    @Override
    public void invalidateSocket() {
        disConnect();
        for (ChatSocketListener lsnr : sListeners) {
            lsnr.socketClosed();
        }
    }

    @Override
    public String getName() {
        return userName;
    }

    @Override
    public void setName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getId() {
        return id;
    }

    public void addChatSocketListener(ChatSocketListener lsnr) {
        sListeners.add(lsnr);
    }

    public void removeChatSocketListener(ChatSocketListener lsnr) {
        sListeners.remove(lsnr);
    }

    public void windowClosing(WindowEvent e) {
        disConnect();
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        new ChatClient();
    }
}
