package lect.chat.protocol;

public class ChatCommandUtil {
    public static final char CREATE_ROOM ='a';
    public static final char NORMAL = 'b';// 채팅
    public static final char USER_LIST = 'd';
    public static final char ENTER_ROOM = 'e';
    public static final char EXIT_ROOM = 'f';
    public static final char UNKNOWN = 'z';
    public static final char ROOM_LIST = 'r';
    public static final char CHECK_USER_NAME = 'u';
    public static final char EXIT_PROGRAM = 'x';

    public static final char REMOVE_ROOM = 'g';

    public static final char ROOM_SIZE='s';

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
