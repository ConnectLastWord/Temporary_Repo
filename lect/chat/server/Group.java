package lect.chat.server;

import java.util.ArrayList;
import java.util.List;

// 채팅방 1개
public class Group {
    // 채팅방 이름
    private String groupName;
    // 채팅방 소속된 유저 리스트
    private List<MessageHandler> clientList;

    public Group(String groupName) {
        this.groupName = groupName;
        clientList = new ArrayList<>();
    }

    // 채팅방 내 사용자 추가
    public List<MessageHandler> addUser(MessageHandler handler) {
        clientList.add(handler);
        System.out.println("Active clients count: " + clientList.size());
        return clientList;
    }

    public List<MessageHandler> getClientList() {
        return clientList;
    }

    // 채팅방 내 사용자 삭제
    public List<MessageHandler> removeUser(MessageHandler handler) {
        clientList.remove(handler);
        System.out.println("Active clients count: " + clientList.size());
        return clientList;
    }

    public String getUserList() {
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
