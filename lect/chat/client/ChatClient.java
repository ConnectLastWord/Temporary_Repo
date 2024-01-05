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

public class ChatClient extends WindowAdapter {
    private static JFrame chatWindow;

    ChatClient() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        // Panel 설정
        ChatPanel chatPanel = new ChatPanel();
        ChatMessageReceiver chatReceiver = new ChatMessageReceiver();
        chatReceiver.setMessageReceiver(chatPanel);
        contentPane.add(chatPanel);

        // 연결 설정
        Connector connector = Connector.getInstance();
        connector.addChatSocketListener(chatPanel);
        connector.addChatSocketListener(chatReceiver);


        // 상태 창 설정 - 사용자 이름
        StatusBar statusBar = StatusBar.getStatusBar();
        statusBar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(1, 2, 2, 2)));
        contentPane.add(statusBar, BorderLayout.SOUTH);

        // 윈도우 설정
        chatWindow = new JFrame("Minimal Chat - Concept Proof");
        chatWindow.setContentPane(contentPane);
        chatWindow.setSize(750, 350);
        chatWindow.setLocationRelativeTo(null);
        chatWindow.setVisible(true);
        chatWindow.addWindowListener(this);
    }

    public static String getUserName() {
        String userName;
        userName = JOptionPane.showInputDialog(chatWindow, "Enter user name:");
        return userName;
    }

    public static void main(String[] args) throws Exception {
        new ChatClient();
    }
}
