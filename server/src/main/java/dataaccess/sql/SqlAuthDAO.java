package dataaccess.sql;

import dataaccess.AuthDAO;
import model.AuthData;

public class SqlAuthDAO implements AuthDAO {
    @Override
    public AuthData createAuth(String authToken, String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() {

    }
}
