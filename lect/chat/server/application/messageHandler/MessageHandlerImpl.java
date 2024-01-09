package lect.chat.server.application.messageHandler;

import lect.chat.protocol.ChatCommandUtil;
import lect.chat.server.application.group.GroupController;
import lect.chat.server.application.user.DefaultUserInfo;
import lect.chat.server.application.user.UserController;
import lect.chat.server.application.user.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// 사용자 메시지를 전달하기 위한 구현체 = 하나의 클라이언트와 통신하기 위한 객체, 스레드
public class MessageHandlerImpl implements Runnable, MessageHandler {
    public static ThreadLocal<UserInfo> req = new ThreadLocal<UserInfo>();
    private Socket s;
    private GroupController gC;
    private UserController uC;

    public MessageHandlerImpl(Socket s) throws IOException {
        this.s = s;
        gC = new GroupController();
        uC = new UserController();
    }

    public void run() {
        String msg;
        try {
            req.set(new DefaultUserInfo(s,
                    new BufferedReader(new InputStreamReader(s.getInputStream())),
                    new PrintWriter(s.getOutputStream(), true), s.getInetAddress().getHostAddress()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            while (true) {
                msg = getMessage();
                if (msg == null) {
                    break;
                }
                handleRequest(msg);
                System.out.println("lineRead: " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            handleRequest(String.valueOf(ChatCommandUtil.LOGOUT));
            close();
        }
        System.out.println("Terminating ClientHandler");
    }

    public String getMessage() throws IOException {
        return MessageHandlerImpl.req.get().readLine();
    }

    public void close() {
        MessageHandlerImpl.req
                .get().close();
    }

    // 핸들러는 해당 요청이 어느 컨트롤러로 가야하는 지?
    @Override
    public void handleRequest(String msg) {
        char command = ChatCommandUtil.getCommand(msg);// 첫번째 글자 떼옴
        //msg = [b]채팅방- 2|massage
        msg = msg.replaceFirst("\\[{1}[a-z]\\]{1}", "");// 첫번쨰 글자 없앰
        switch (command) {
            // 로그인
            case ChatCommandUtil.LOGIN:
                uC.handleController(command, msg);
                break;
            case ChatCommandUtil.LOGIN_ANOYMOUS:
                uC.handleController(command, msg);
                break;
            //  로그아웃
            case ChatCommandUtil.LOGOUT:
                uC.handleController(command, msg);
                break;
            // 채팅방 메시지
            case ChatCommandUtil.NORMAL:
                gC.handleController(command, msg);
                break;
            // 채팅방 생성
            case ChatCommandUtil.CREATE_ROOM:
                gC.handleController(command, msg);
                break;
            // 채팅방 삭제
            case ChatCommandUtil.REMOVE_ROOM:
                gC.handleController(command, msg);
                break;
            // 채팅방 접속
            case ChatCommandUtil.ENTER_ROOM:
                gC.handleController(command, msg);
                break;
            default:
                System.out.printf("ChatCommand %c \n", command);
                break;
        }
    }
}
