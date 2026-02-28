package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class AccessAuth implements AuthAccess {
    private final Collection<AuthData> auths = new ArrayList<>();

    public void createAuth(AuthData authorization){
        auths.add(authorization);
    }

    public AuthData getAuth(String authToken){
        for (AuthData x: auths){
            if (x.authToken().equals(authToken)){
                return x;
            }
        }
        return null;
    }

    public void deleteAuth(String authToken){
        auths.removeIf(x -> x.authToken().equals(authToken));
    }

    public void clearAuths(){
        auths.clear();
    }
}
