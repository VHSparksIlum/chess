package dataaccess.memory;

import dataaccess.UserDAO;
import model.UserData;

import java.util.HashSet;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    private static final HashSet<UserData> USERS = new HashSet<>();

    @Override
    public UserData getUser(String username) {
        for (UserData user : USERS) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }

    public boolean foundUser(String username) {
        for (UserData user : USERS) {
            if (Objects.equals(user.username(), username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void createUser(UserData user) {
        USERS.add(user);
    }

    @Override
    public void clear() {
        USERS.clear();
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}