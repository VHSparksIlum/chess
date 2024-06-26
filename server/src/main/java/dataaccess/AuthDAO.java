package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String authToken, String username);

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken);

    void clear();
}
