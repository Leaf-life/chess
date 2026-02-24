package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.ArrayList;

public class AccessUser {
    private final ArrayList<UserData> users = new ArrayList<>();
    AccessUser(){}

    public void createUser(String username, String password, String email){
        users.add(new UserData(username, password, email));
    }

    public UserData getUser(String username){
        for (UserData x: users){
            if (x.username().equals(username)){
                return x;
            }
        }
        return null;
    }
}
