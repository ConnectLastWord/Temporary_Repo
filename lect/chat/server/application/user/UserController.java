package lect.chat.server.application.user;

import lect.chat.protocol.ChatCommandUtil;
import lect.chat.server.application.controller.Controller;
import lect.chat.server.application.group.GroupManager;
import lect.chat.server.application.messageHandler.MessageHandlerImpl;

import java.util.List;

public class UserController implements Controller {
    private UserManager uM;
    private GroupManager gM;

    public UserController() {
        this.uM = UserManager.getInstance();
        this.gM = GroupManager.getInstance();
    }

    @Override
    public void handleController(char command, String msg) {
        switch (command) {
            // 로그인
            case ChatCommandUtil.LOGIN:
                String[] nameWithId = msg.split("\\|");
                // user 이름이 이미 존재하는 경우
                if (uM.isContains(nameWithId[0])) {
                    sendMessage(createMessage(ChatCommandUtil.LOGIN, "duplicated"));
                } else if(nameWithId[0].startsWith("anonymous") || nameWithId[0].startsWith("Anonymous")) {
                    sendMessage(createMessage(ChatCommandUtil.LOGIN, "anonymous"));
                }
                else{
                    MessageHandlerImpl.req.get().setChatName(nameWithId[0]);
                    MessageHandlerImpl.req.get().setId(nameWithId[1]);
                    System.out.println(MessageHandlerImpl.req
                            .get().getChatName());
                    sendMessage(createMessage(ChatCommandUtil.LOGIN, nameWithId[0]));
                    uM.addUser(MessageHandlerImpl.req.get());
                    broadcastMessage(uM.findAllMessageHandler(), createMessage(ChatCommandUtil.ENTER_ROOM, gM.getRoomsToString()));
                }
                break;
            case ChatCommandUtil.LOGIN_ANOYMOUS:
                nameWithId = msg.split("\\|");
                System.out.println("LOGIN_ANOYMOUS");
                MessageHandlerImpl.req.set(new AnonymousUserInfo(
                        MessageHandlerImpl.req.get().socket,
                        MessageHandlerImpl.req.get().br,
                        MessageHandlerImpl.req.get().pw,
                        MessageHandlerImpl.req.get().getHost()
                ));
                MessageHandlerImpl.req.get().setId(nameWithId[1]);
                System.out.println(nameWithId[1]);
                sendMessage(createMessage(ChatCommandUtil.LOGIN, MessageHandlerImpl.req.get().getChatName()));
                uM.addUser(MessageHandlerImpl.req.get());
                broadcastMessage(uM.findAllMessageHandler(), createMessage(ChatCommandUtil.ENTER_ROOM, gM.getRoomsToString()));
                String roomSize = gM.getGroupSize();
                if (roomSize != null) {
                    broadcastMessage(uM.findAllMessageHandler(), createMessage(ChatCommandUtil.ROOM_SIZE, roomSize));
                }
                break;
            // 로그아웃
            case ChatCommandUtil.LOGOUT:
                // 채팅방 입장 전 로그아웃 시, 퇴장 메시지 브로드 캐스트 필터
                if (MessageHandlerImpl.req.get().getChatRoomName() != null) {
                    // 삭제할 채팅방 정보 조회
                    List<UserInfo> targetList = gM.removeUserByChatRoom(MessageHandlerImpl.req.get().getChatRoomName(), MessageHandlerImpl.req.get());
                    // 현재 회원 정보 삭제
                    uM.removeUser(MessageHandlerImpl.req.get());
                    if (targetList.size() == 0) {
                        gM.removeChatRoom(MessageHandlerImpl.req.get().getChatRoomName());
                        broadcastMessage(uM.findAllMessageHandler(), createMessage(ChatCommandUtil.ENTER_ROOM, gM.getRoomsToString()));
                        broadcastMessage(uM.findAllMessageHandler(), createMessage(ChatCommandUtil.ROOM_SIZE, gM.getGroupSize()));
                        return;
                    }
                    // 퇴장 메시지 브로드 캐스트
                    broadcastMessage(targetList, createMessage(ChatCommandUtil.EXIT_ROOM_MESSAGE, MessageHandlerImpl.req.get().getChatName() + " has just left [" + MessageHandlerImpl.req.get().getChatRoomName() + "] room"));
                    // 유저 목록 브로드 캐스트
                    broadcastMessage(targetList, createMessage(ChatCommandUtil.USER_LIST, gM.getUserByChatRoomToString(MessageHandlerImpl.req.get().getChatRoomName())));
                    broadcastMessage(uM.findAllMessageHandler(), createMessage(ChatCommandUtil.ROOM_SIZE, gM.getGroupSize()));
                    break;
                }
                uM.removeUser(MessageHandlerImpl.req.get());
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
