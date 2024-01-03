package lect.chat.server;

import lect.chat.protocol.ChatCommandUtil;

import java.util.Vector;

import static lect.chat.server.GroupManager.createMessage;

// 채팅방 1개
public class Group {
    // 채팅방 이름
    private String groupName;
    // 채팅방 소속된 유저 리스트
    private Vector<MessageHandler> clientList;

    public Group(String groupName) {
        this.groupName = groupName;
        clientList = new Vector<>();
    }

    public String getGroupName() {
        return this.groupName;
    }

    // 채팅방 내 사용자 추가
    public void addUser(MessageHandler handler) {
        clientList.add(handler);
        System.out.println("Active clients count: " + clientList.size());
    }

    public void removeUser(MessageHandler handler) {
        clientList.remove(handler);
        System.out.println("Active clients count: " + clientList.size());
    }

    public void broadCastLeftChatter(MessageHandler handler) {
        StringBuilder userList = new StringBuilder();
        //  사용자 한명
        MessageHandler user;
        // 채팅방 인원수 파악
        int size = clientList.size();
        // 채팅방 내 사용자 반복수
        for (int i = 0; i < size; i++) {
            // 채팅방 내 사용자 1명 불러오기
            user = clientList.get(i);
            userList.append(user.getName()).append(",")
                    .append(user.getId()).append(",")
                    .append(user.getFrom());
            System.out.println(userList);
            if (i < (size - 1)) {
                userList.append("|");
            }
        }
        for (MessageHandler mh : clientList) {
            // 퇴장 브로드캐스팅
            mh.sendMessage(createMessage(ChatCommandUtil.EXIT_ROOM,
                    handler.getName() + " has just left [" + groupName + "] room room"));
            // List Of Users 브로드 캐스팅
            mh.sendMessage(createMessage(ChatCommandUtil.USER_LIST, userList.toString()));
        }
    }

    public void broadcastNewChatter(MessageHandler handler) {
        StringBuilder userList = new StringBuilder();
        //  사용자 한명
        MessageHandler user;
        // 채팅방 인원수 파악
        int size = clientList.size();
        // 채팅방 내 사용자 반복수
        for (int i = 0; i < size; i++) {
            // 채팅방 내 사용자 1명 불러오기
            user = clientList.get(i);
            userList.append(user.getName()).append(",")
                    .append(user.getId()).append(",")
                    .append(user.getFrom());

            if (i < (size - 1)) {
                userList.append("|");
            }
        }
        System.out.println("userList" + userList);
        for (MessageHandler mh : clientList) {
            // 입장 브로드캐스팅
            mh.sendMessage(createMessage(ChatCommandUtil.ENTER_ROOM,
                    (handler.getName() + " has just entered [" + groupName + "] room")));
            // List Of Users 브로드 캐스팅
            mh.sendMessage(createMessage(ChatCommandUtil.USER_LIST, userList.toString()));
        }
    }

    public void broadcastMessage(String msg) {
        for (MessageHandler handler : clientList) {
            handler.sendMessage(createMessage(ChatCommandUtil.NORMAL, msg));
        }
    }

    public void broadcastChatRoom(String msg) {
        for (MessageHandler handler : clientList) {
            handler.sendMessage(createMessage(ChatCommandUtil.ROOM_LIST, msg));
        }
    }
}
