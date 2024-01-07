package lect.chat.server.application.user;

import lect.chat.protocol.ChatCommandUtil;
import lect.chat.server.application.controller.Controller;
import lect.chat.server.application.group.GroupManager;

import java.util.List;

public class UserController implements Controller {
    private UserManager uM;
    private GroupManager gM;
    private User user;
    private char command;
    private String msg;

    public UserController() {
        this.uM = UserManager.getInstance();
        this.gM = GroupManager.getInstance();
    }

    @Override
    public void handleController(User user, char command, String msg) {
        this.user = user;
        this.command = command;
        this.msg = msg;
        processMessage();
    }

    @Override
    public void processMessage() {
        switch (command) {
            // 로그인
            case ChatCommandUtil.LOGIN:
                String[] nameWithId = msg.split("\\|");
                user.setChatName(nameWithId[0]);
                user.setId(nameWithId[1]);
                // user 이름이 이미 존재하는 경우
                if (uM.isContains(user.getChatName())) {
                    sendMessage(createMessage(ChatCommandUtil.LOGIN, "false"));
                } else {
                    sendMessage(createMessage(ChatCommandUtil.LOGIN, user.getChatName()));
                    uM.addUser(user);
                    broadcastMessage(uM.findAllMessageHandler(), createMessage(ChatCommandUtil.ENTER_ROOM, gM.getRoomsToString()));
                }
                break;
            // 로그아웃
            case ChatCommandUtil.LOGOUT:
                // 채팅방 입장 전 로그아웃 시, 퇴장 메시지 브로드 캐스트 필터
                if (user.getChatRoomName() != null) {
                    // 삭제할 채팅방 정보 조회
                    List<User> targetList = gM.removeUserByChatRoom(user.getChatRoomName(), user);
                    uM.removeUser(user);
                    // 퇴장 메시지 브로드 캐스트
                    broadcastMessage(targetList, createMessage(ChatCommandUtil.EXIT_ROOM_MESSAGE, user.getChatName() + " has just left [" + user.getChatRoomName() + "] room"));
                    // 유저 목록 브로드 캐스트
                    broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(user.getChatRoomName())));
                }
                break;

        }
    }

    public void sendMessage(String msg) {
        user.println(msg);
    }

    // 브로드 캐스트
    private <T extends User> void broadcastMessage(List<T> targetList, String msg) {
        for (User user : targetList) {
            user.println(msg);
        }
    }
}
