package dataaccess.sql;

import dataaccess.UserDAO;
import model.UserData;

public class SqlUserDAO implements UserDAO {
    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public boolean foundUser(String username) {
        return false;
    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public void clear() {

    }
}
