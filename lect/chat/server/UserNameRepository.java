package lect.chat.server;

import java.util.HashSet;
import java.util.Set;

public class UserNameRepository {
    private static final Set<String> userNames = new HashSet<>();

    public static void addUserName(String userName) {
        if(!isin(userName))
            userNames.add(userName);
    }

    public static void removeUserName(String userName) {
        if(isin(userName))
            userNames.remove(userName);
        else
            System.out.println("user name is not exist");
    }

    public static boolean isin(String userName) {
        if(userNames.contains(userName))
            return true;
        return false;
    }
}
