package lect.chat.protocol;

public class ChatCommandUtil {
    // 회원
    public static final char LOGIN = 'u';
    public static final char LOGOUT = 'l';
    // 채팅
    public static final char NORMAL = 'b';
    // 채팅방
    public static final char CREATE_ROOM = 'a';
    public static final char ENTER_ROOM = 'r';
    public static final char ENTER_ROOM_MESSAGE = 'e';
    public static final char EXIT_ROOM_MESSAGE = 'f';
    //    public static final char SHOW_ROOM_LIST = 'x';
    // 사용자 목록
    public static final char USER_LIST = 'd';
    public static final char UNKNOWN = 'z';

    public static final char ROOM_LIST = 'r';
    public static final char CHECK_USER_NAME = 'u';
    public static final char EXIT_PROGRAM = 'x';
    public static final char REMOVE_ROOM = 'g';
    public static final char LOGIN_ANOYMOUS = 'y';

    public static final char CREATE_ANONYMOUS_USER = 's';
    public static final char CREATE_DEFAULT_USER = 'n';

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
