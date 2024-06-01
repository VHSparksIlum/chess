package service;

import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private static AuthDAO authDAO;
    private static UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    static {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
    }

    public static AuthData register(UserData user) throws DataAccessException {
        if(user.username()==null || user.password()==null || user.email()==null){
            throw new IllegalArgumentException("Missing required fields");
        }
        String username = user.username();
        if(userDAO.getUser(username)==null && !userDAO.foundUser(username)){
            userDAO.createUser(user);
            return createNewAuth(username);
        } else {
            throw new DataAccessException("Username already taken");
        }
    }

    public static AuthData login(UserData user) throws DataAccessException {
        String username = user.username();
        String password = user.password();

        UserData getUser = userDAO.getUser(username);

        if((getUser!=null) && (getUser.password().equals(password))) {
            return createNewAuth(username);
        } else {
            throw new DataAccessException("Unauthorized");
        }
    }

    public static void logout(String authToken) throws DataAccessException {
        if(authDAO.getAuth(authToken)!=null){
            authDAO.deleteAuth(authToken);
        } else{
            throw new DataAccessException("Unauthorized");
        }
    }

    private static AuthData createNewAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        return authDAO.createAuth(authToken, username);
    }
}
