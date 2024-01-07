package lect.chat.server.application.group;

import lect.chat.protocol.ChatCommandUtil;
import lect.chat.server.application.controller.Controller;
import lect.chat.server.application.user.User;
import lect.chat.server.application.user.UserManager;

import java.io.IOException;
import java.util.List;

public class GroupController implements Controller {
    private GroupManager gM;
    private UserManager uM;
    private User user;
    private char command;
    private String msg;

    public GroupController(User user) throws IOException {
        this.user = user;
        this.gM = GroupManager.getInstance();
        this.uM = UserManager.getInstance();
    }

    @Override
    public void handleController(char command, String msg) {
        this.command = command;
        this.msg = msg;
        processMessage();
    }

    @Override
    public void processMessage() {
        switch (command) {
            case ChatCommandUtil.ENTER_ROOM:
                if (user.getChatRoomName() != null) {
                    // 내 정보 삭제할 채팅방 이름
                    String removeChatRoomName = user.getChatRoomName();
                    // 삭제할 채팅방 정보 조회
                    List<User> targetList = gM.removeUserByChatRoom(user.getChatRoomName(), user);
                    // 퇴장 메시지 브로드 캐스트
                    broadcastMessage(targetList, createMessage(ChatCommandUtil.EXIT_ROOM_MESSAGE, user.getChatName() + " has just left [" + user.getChatRoomName() + "] room"));
                    // 유저 리스트 브로드 캐스트
                    broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(removeChatRoomName)));
                }
                user.setChatRoomName(msg);
                // 생성할 채팅방 정보 조회
                List<User> targetList = gM.addUserByChatRoom(user.getChatRoomName(), user);
                // 입장 메시지 브로드 캐스트
                broadcastMessage(targetList, createMessage(ChatCommandUtil.ENTER_ROOM_MESSAGE, user.getChatName() + " has entered [" + user.getChatRoomName() + "] room"));
                // 유저 리스트 브로드 캐스트
                broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(user.getChatRoomName())));
                break;
            case ChatCommandUtil.CREATE_ROOM:
                if (gM.isContains(msg)) {
                    sendMessage(createMessage(ChatCommandUtil.CREATE_ROOM, "이미 존재하는 채팅방"));
                } else {
                    gM.addChatRoom(msg);
                    broadcastMessage(uM.findAllMessageHandler(), createMessage(ChatCommandUtil.ENTER_ROOM, gM.getRoomsToString()));
                }
                break;
            case ChatCommandUtil.NORMAL:
                String[] msgSplit = msg.split("\\|");
                String sendMsg = msgSplit[1];
                targetList = gM.findAllMessageHandler(user.getChatRoomName());
                broadcastMessage(targetList, createMessage(ChatCommandUtil.NORMAL, String.format("%s: %s", user.getChatName(), sendMsg)));
                break;
            case ChatCommandUtil.REMOVE_ROOM:
                gM.removeChatRoom(user.getChatRoomName());
                break;
        }

    }

    public void sendMessage(String msg) {
        user.println(msg);
    }

    // 브로드 캐스트
    public <T extends User> void broadcastMessage(List<T> targetList, String msg) {
        for (User user : targetList) {
            user.println(msg);
        }
    }
}
