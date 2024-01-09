package lect.chat.server.application.group;

import lect.chat.server.application.user.UserInfo;

import java.util.ArrayList;
import java.util.List;

// 채팅방 1개
public class Group {
    // 채팅방 이름
    private String groupName;
    // 채팅방 소속된 유저 리스트
    private List<UserInfo> clientList;

    public Group(String groupName) {
        this.groupName = groupName;
        clientList = new ArrayList<>();
    }

    // 채팅방 내 사용자 추가
    public List<UserInfo> addUser(UserInfo userInfo) {
        clientList.add(userInfo);
        System.out.println("Active clients count: " + clientList.size());
        return clientList;
    }

    public List<UserInfo> getClientList() {
        return clientList;
    }

    // 채팅방 내 사용자 삭제
    public List<UserInfo> removeUser(UserInfo userInfo) {
        clientList.remove(userInfo);
        System.out.println("Active clients count: " + clientList.size());
        return clientList;
    }

    public String getUserList() {
        StringBuilder userList = new StringBuilder();
        //  사용자 한명
        UserInfo userInfo;
        // 채팅방 인원수 파악
        int size = clientList.size();
        // 채팅방 내 사용자 반복수
        for (int i = 0; i < size; i++) {
            // 채팅방 내 사용자 1명 불러오기
            userInfo = clientList.get(i);
            userList.append(userInfo.getChatName()).append(",")
                    .append(userInfo.getId()).append(",")
                    .append(userInfo.getHost());
            System.out.println(userList);
            if (i < (size - 1)) {
                userList.append("|");
            }
        }
        return userList.toString();
    }
}
