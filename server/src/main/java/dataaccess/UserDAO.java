package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username);

    boolean foundUser(String username);

    void createUser(UserData user);

    void clear();
}
