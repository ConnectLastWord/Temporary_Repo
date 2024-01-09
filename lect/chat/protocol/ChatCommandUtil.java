package lect.chat.protocol;

public class ChatCommandUtil {
    // 회원
    public static final char LOGIN = 'a';
    public static final char LOGOUT = 'b';
    // 채팅
    public static final char NORMAL = 'c';
    // 채팅방
    public static final char CREATE_ROOM = 'd';
    public static final char ENTER_ROOM = 'e';
    public static final char ENTER_ROOM_MESSAGE = 'f';
    public static final char EXIT_ROOM_MESSAGE = 'g';
    //    public static final char SHOW_ROOM_LIST = 'x';
    // 사용자 목록
    public static final char USER_LIST = 'h';
    public static final char UNKNOWN = 'i';

    public static final char ROOM_LIST = 'j';
    public static final char CHECK_USER_NAME = 'k';
    public static final char EXIT_PROGRAM = 'l';
    public static final char REMOVE_ROOM = 'm';
    public static final char LOGIN_ANOYMOUS = 'n';

    public static final char CREATE_ANONYMOUS_USER = 'o';
    public static final char CREATE_DEFAULT_USER = 'p';
    public static final char ROOM_SIZE = 'q';

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
