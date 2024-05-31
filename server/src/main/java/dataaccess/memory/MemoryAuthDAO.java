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

/**
 * Constructs a new AuthDao instance.
 */
//public AuthDao() {
//}

/**
 * Checks if an authentication token is valid and represents a logged-in user.
 *
 * @param authToken The authentication token to check.
 * @return true if the token is valid, false otherwise
 * @throws DataAccessException if there's an error during data access
 */
//public boolean isAuthTokenValid(String authToken) throws DataAccessException {
//    // Implementation for checking if the authToken is valid
//    return true;
//}

/**
 * Inserts a new AuthToken into the data store.
 *
 * @param authToken The AuthToken to be inserted.
 * @throws DataAccessException if an AuthToken already exists for the provided username.
 */
//public void insertAuthToken(AuthToken authToken) throws DataAccessException {
//    if (!authTokens.containsKey(authToken.getUsername())) {
//        authTokens.put(authToken.getUsername(), authToken);
//    } else {
//        throw new DataAccessException("AuthToken already exists for this user.");
//    }
//}

/**
 * Finds and retrieves an AuthToken by the username associated with it.
 *
 * @param username The username for which to find the AuthToken.
 * @return The AuthToken associated with the username or null if not found.
 */
//public AuthToken findAuthToken(String username) {
//    return authTokens.get(username);
//}

/**
 * Retrieves a list of all AuthTokens in the data store.
 *
 * @return A list of all AuthTokens.
 */
//public List<AuthToken> findAllAuthTokens() {
//    return new ArrayList<>(authTokens.values());
//}

/**
 * Updates an existing AuthToken with a new value.
 *
 * @param authToken The updated AuthToken to replace the existing one.
 * @throws DataAccessException if the username is not found in the data store.
 */
//public void updateAuthToken(AuthToken authToken) throws DataAccessException {
//    if (authTokens.containsKey(authToken.getUsername())) {
//        authTokens.put(authToken.getUsername(), authToken);
//    } else {
//        throw new DataAccessException("Username not found.");
//    }
//}

/**
 * Retrieves the username associated with an authentication token.
 *
 * @param authToken The authentication token.
 * @return The username associated with the token, or null if the token is invalid
 * @throws DataAccessException if there's an error during data retrieval
 */
//public String getUsernameForAuthToken(String authToken) throws DataAccessException {
//    // Implementation for retrieving the username associated with the authToken
//    return authToken;
//}

/**
 * Removes an authentication token from the active sessions.
 *
 * @param authToken The authentication token to be removed.
 * @throws DataAccessException if there's an error during token removal
 */
//public void removeAuthToken(String authToken) throws DataAccessException {
//    if (authTokens.containsKey(authToken)) {
//        authTokens.remove(authToken);
//    } else {
//        throw new DataAccessException("Username not found.");
//    }
//}

/**
 * Clears all data from the data store.
 */
//public void clearData() {
//    authTokens.clear();
//}
