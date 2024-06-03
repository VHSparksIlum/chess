package dataaccess.memory;

import dataaccess.AuthDAO;
import model.AuthData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public AuthData createAuth(String authToken, String username) {
        AuthData auth = new AuthData(authToken, username);
        auths.put(authToken, auth);
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public void clear() {
        auths.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryAuthDAO that = (MemoryAuthDAO) o;
        return Objects.equals(auths, that.auths);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(auths);
    }
}