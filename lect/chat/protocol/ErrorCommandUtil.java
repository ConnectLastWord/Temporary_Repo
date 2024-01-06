package lect.chat.protocol;

public class ErrorCommandUtil {
    public static final String LOG_IN_FAIL = "로그인 실패";
    public static final String ROOM_CREATE_FAIL = "채팅방 생성 실패";
    public static final String ROOM_ENTER_FAIL = "채팅방 입장 실패";

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
    public static final char CREATE_ANONYMOUS_USER = 's';
    public static final char CREATE_DEFAULT_USER = 'n';
}

