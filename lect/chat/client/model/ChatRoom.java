package lect.chat.client.model;

public class ChatRoom {
    // 채팅방 이름
    String name;

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
