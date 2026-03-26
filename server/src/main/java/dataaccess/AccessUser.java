package dataaccess;

import model.UserData;

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

    public String listUsers(){
        if (users.isEmpty()){
            return null;
        }
        return "has users";
    }
}
