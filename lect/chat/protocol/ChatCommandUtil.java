package lect.chat.protocol;

public class ChatCommandUtil {
    public static final char CREATE_ROOM ='a';
    public static final char NORMAL = 'b';// 채팅
    public static final char INIT_ALIAS = 'c'; //닉네임 생성
    public static final char USER_LIST = 'd';
    public static final char ENTER_ROOM = 'e';
    public static final char EXIT_ROOM = 'f';
    public static final char UNKNOWN = 'z';
    public static final char ROOM_LIST = 'r';

    private ChatCommandUtil() {
    }

    public static char getCommand(String msg) {
        if (msg.matches("\\[{1}[a-z]\\]{1}.*")) {
            return msg.charAt(1);
        } else {
            return ChatCommandUtil.UNKNOWN;
        }
    }
}
