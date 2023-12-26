package lect.chat.client;

public class ChatRoom {
    // 채팅방 이름
    String name;
    // 채팅방 유저 리스트
    ChatUserList userList;

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ChatUserList getUserList() {
        return userList;
    }

    public String toString() {
        return name;
    }
}
