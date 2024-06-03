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

/**
 * Creates a new User and inserts it into the data store.
 *
 * @param user The User to be created and inserted.
 * @throws DataAccessException if the username is already taken by another user.
 */
//public void createUser(User user) throws DataAccessException {
//    if (!users.containsKey(user.getUsername())) {
//        users.put(user.getUsername(), user);
//    } else {
//        throw new DataAccessException("Username already taken.");
//    }
//}

/**
 * Finds and retrieves a User by username.
 *
 * @param username The username of the User to find.
 * @return The User with the specified username or null if not found.
 */
//public boolean findUser(String username) {
//    return false;
//}

/**
 * Retrieves a list of all User objects in the data store.
 *
 * @return A list of all User objects.
 */
//public List<User> findAllUsers() {
//    return new ArrayList<>(users.values());
//}

/**
 * Updates the information of an existing User.
 *
 * @param updatedUser The User object with updated information.
 * @throws DataAccessException if the username is not found in the data store.
 */
//public void updateUser(User updatedUser) throws DataAccessException {
//    if (users.containsKey(updatedUser.getUsername())) {
//        users.put(updatedUser.getUsername(), updatedUser);
//    } else {
//        throw new DataAccessException("Username not found.");
//    }
//}

/**
 * Deletes a User by username.
 *
 * @param username The username of the User to delete.
 * @throws DataAccessException if the username is not found in the data store.
 */
//public void deleteUser(String username) throws DataAccessException {
//    if (users.containsKey(username)) {
//        users.remove(username);
//    } else {
//        throw new DataAccessException("Username not found.");
//    }
//}

/**
 * Clears all data from the data store.
 */
//public void clearData() {
//    users.clear();
//}