//package lect.chat.client;
//
//import lect.chat.client.connect.ChatSocketListener;
//import lect.chat.client.event.ChatConnector;
//
//
//import javax.swing.*;
//import java.awt.event.WindowEvent;
//import java.io.IOException;
//import java.net.Socket;
//import java.util.ArrayList;
//
//public class Connector implements ChatConnector {
//    // 싱글톤
//    private static Socket socket;
//    private static String userName;
//    private static String id;
//    private static ArrayList<ChatSocketListener> sListeners = new ArrayList<>();
//    private static Connector instance;
//
//    private Connector() {
//        id = new java.rmi.server.UID().toString();
//    }
//
//    public static Connector getInstance(){
//        if(instance == null)
//            instance = new Connector();
//        return instance;
//    }
//    @Override
//    public boolean connect() {
//        if (socketAvailable()) {
//            return true;
//        }
//        try {
//            socket = new Socket("127.0.0.1", 8081);
//            for (ChatSocketListener lsnr : sListeners) {
//                lsnr.socketConnected(socket); // 초기화만 하고 init message는 보내지 않음
//            }
//            // userName을 ChatClient에게 요청
//            userName = ChatClient.getName();
//            if (userName == null) {
//                return false;
//            }else {
//                // 이름 검사를 한 후에 init message를 보냄
//                sListeners.get(0).checkUserName(socket);
//                return true;
//            }
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(null, "Failed to connect chat server", "Error", JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
//    }
//
//    @Override
//    public void disConnect() {
//        if (socketAvailable()) {
//            try {
//                socket.close();
//            } catch (IOException ex) {
//            }
//        }
//    }
//
//    @Override
//    public Socket getSocket() {
//        return socket;
//    }
//
//    @Override
//    public boolean socketAvailable() {
//        return !(socket == null || socket.isClosed());
//    }
//
//    @Override
//    public void invalidateSocket() {
//        disConnect();
//        for (ChatSocketListener lsnr : sListeners) {
//            lsnr.socketClosed();
//        }
//    }
//
//    @Override
//    public void setName(String userName) {
//        this.userName = userName;
//    }
//
//    @Override
//    public String getId() {
//        return id;
//    }
//
//    @Override
//    public String getName() {
//        return userName;
//    }
//
//    public void addChatSocketListener(ChatSocketListener lsnr) {
//        sListeners.add(lsnr);
//    }
//
//    public void removeChatSocketListener(ChatSocketListener lsnr) {
//        sListeners.remove(lsnr);
//    }
//
//    public void windowClosing(WindowEvent e) {
//        disConnect();
//        System.exit(0);
//    }
//}
