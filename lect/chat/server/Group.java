package lect.chat.server;

import lect.chat.protocol.ChatCommandUtil;

import java.util.Vector;

// 채팅방 1개
public class Group {
    // 채팅방 이름
    private String groupName;
    // 채팅방 소속된 유저 리스트
    private Vector<MessageHandler> clientList;
    private static BroadCast broadCast;

    public Group(String groupName) {
        this.groupName = groupName;
        clientList = new Vector<>();
    }

    // 채팅방 내 사용자 추가
    public void addUser(MessageHandler handler) {
        clientList.add(handler);
        System.out.println("Active clients count: " + clientList.size());
    }

    // 채팅방 내 사용자 삭제
    public void removeUser(MessageHandler handler) {
        clientList.remove(handler);
        System.out.println("Active clients count: " + clientList.size());
    }

    // 채팅방 내 유저 목록, 입장퇴장 메시지 브로드캐스트
    public void broadCastChatter(char protocol, MessageHandler handler) {
        for (MessageHandler mh : clientList) {
            // 퇴장 브로드캐스팅
            if (protocol == ChatCommandUtil.EXIT_ROOM) {
                Message.sendMessage(mh, protocol, handler.getName() + " has just left [" + groupName + "] room room");
            } else { // 입장 브로드캐스팅
                Message.sendMessage(mh, protocol, handler.getName() + " has entered [" + groupName + "] room room");
            }

            // List Of Users 브로드 캐스팅
            Message.sendMessage(mh, ChatCommandUtil.USER_LIST, getUserList());
        }
    }

    // 채팅 메시지 브로드캐스트
    public void broadcastMessage(String msg) {
        for (MessageHandler handler : clientList) {
            Message.sendMessage(handler, ChatCommandUtil.NORMAL, msg);
        }
    }

    private String getUserList() {
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
        return userList.toString();
    }
}
