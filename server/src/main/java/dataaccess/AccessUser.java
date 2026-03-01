package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;

public class AccessUser implements UserAccess {
    private final ArrayList<UserData> users = new ArrayList<>();

    public void createUser(UserData user){
        users.add(user);
    }

    public UserData getUser(String username) throws DataAccessException{
        for (UserData x: users){
            if (x.username().equals(username)){
                return x;
            }
        }
        return null;
    }

    public void clearUsers(){
        users.clear();
    }
}
