package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if(user.getUsername()==null || user.getPassword()==null || user.getEmail()==null){
            throw new DataAccessException("Missing required fields");
        }
        String username = user.getUsername();
        if(userDAO.getUser(username)==null){
            userDAO.createUser(user);
            return createNewAuth(username);
        } else {
            throw new DataAccessException("Username already taken");
        }
    }

    public AuthData login(UserData user) throws DataAccessException {
        String username = user.getUsername();
        UserData getUser = userDAO.getUser(username);
        if((getUser!=null) && (Objects.equals(user.getPassword(), getUser.getPassword()))){
            return createNewAuth(username);
        } else {
            throw new DataAccessException("Unauthorized");
        }
    }

    public void logout(String authToken) throws DataAccessException {
        if(authDAO.getAuth(authToken)!=null){
            authDAO.deleteAuth(authToken);
        } else{
            throw new DataAccessException("Unauthorized");
        }
    }

    private AuthData createNewAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        return authDAO.createAuth(authToken, username);
    }
}
