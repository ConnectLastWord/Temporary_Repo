package lect.chat.server.application.group;

import lect.chat.protocol.ChatCommandUtil;
import lect.chat.server.application.controller.Controller;
import lect.chat.server.application.messageHandler.MessageHandlerImpl;
import lect.chat.server.application.user.UserInfo;
import lect.chat.server.application.user.UserManager;

import java.io.IOException;
import java.util.List;

public class GroupController implements Controller {
    private GroupManager gM;
    private UserManager uM;

    public GroupController() throws IOException {
        this.gM = GroupManager.getInstance();
        this.uM = UserManager.getInstance();
    }

    @Override
    public void handleController(char command, String msg) {
        switch (command) {
            case ChatCommandUtil.ENTER_ROOM:
                if (MessageHandlerImpl.req.get().getChatRoomName() != null) {
                    // 내 정보 삭제할 채팅방 이름
                    String removeChatRoomName = MessageHandlerImpl.req.get().getChatRoomName();
                    // 삭제할 채팅방 정보 조회
                    List<UserInfo> targetList = gM.removeUserByChatRoom(MessageHandlerImpl.req.get().getChatRoomName(), MessageHandlerImpl.req.get());
                    if (targetList.size() == 0) {
                        gM.removeChatRoom(removeChatRoomName);
                        broadcastMessage(uM.findAllMessageHandler(), createMessage(ChatCommandUtil.ENTER_ROOM, gM.getRoomsToString()));
                        MessageHandlerImpl.req.get().setChatRoomName(msg);
                        // 입장할 채팅방 유저 리스트 조회
                        targetList = gM.addUserByChatRoom(MessageHandlerImpl.req.get().getChatRoomName(), MessageHandlerImpl.req.get());
                        // 입장 메시지 브로드 캐스트
                        broadcastMessage(targetList, createMessage(ChatCommandUtil.ENTER_ROOM_MESSAGE, MessageHandlerImpl.req.get().getChatName() + " has entered [" + MessageHandlerImpl.req.get().getChatRoomName() + "] room"));
                        // 유저 리스트 브로드 캐스트
                        broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(MessageHandlerImpl.req.get().getChatRoomName())));
                        break;
                    } else {
                        // 퇴장 메시지 브로드 캐스트
                        broadcastMessage(targetList, createMessage(ChatCommandUtil.EXIT_ROOM_MESSAGE, MessageHandlerImpl.req.get().getChatName() + " has just left [" + MessageHandlerImpl.req.get().getChatRoomName() + "] room"));
                        // 유저 리스트 브로드 캐스트
                        broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(removeChatRoomName)));
                    }
                }
                MessageHandlerImpl.req.get().setChatRoomName(msg);
                // 생성할 채팅방 정보 조회
                List<UserInfo> targetList = gM.addUserByChatRoom(MessageHandlerImpl.req.get().getChatRoomName(), MessageHandlerImpl.req.get());
                // 입장 메시지 브로드 캐스트
                broadcastMessage(targetList, createMessage(ChatCommandUtil.ENTER_ROOM_MESSAGE, MessageHandlerImpl.req.get().getChatName() + " has entered [" + MessageHandlerImpl.req.get().getChatRoomName() + "] room"));
                // 유저 리스트 브로드 캐스트
                broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(MessageHandlerImpl.req.get().getChatRoomName())));
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
                targetList = gM.findAllMessageHandler(MessageHandlerImpl.req.get().getChatRoomName());
                broadcastMessage(targetList, createMessage(ChatCommandUtil.NORMAL, String.format("%s: %s", MessageHandlerImpl.req.get().getChatName(), sendMsg)));
                break;
            case ChatCommandUtil.REMOVE_ROOM:
                gM.removeChatRoom(MessageHandlerImpl.req.get().getChatRoomName());
                break;
        }

    }

    public void sendMessage(String msg) {
        MessageHandlerImpl.req.get().println(msg);
    }

    // 브로드 캐스트
    public <T extends UserInfo> void broadcastMessage(List<T> targetList, String msg) {
        for (UserInfo userInfo : targetList) {
            userInfo.println(msg);
        }
    }
}
