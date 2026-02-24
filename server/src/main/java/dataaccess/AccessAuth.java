package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class AccessAuth {
    private final Collection<AuthData> auths = new ArrayList<>();
    AccessAuth(){}

    public void createAuth(String username){
        auths.add(new AuthData("1", username));
    }
}
