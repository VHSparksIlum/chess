package service;

import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;
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
        try {
            userDAO = new SqlDataAccess();
            authDAO = new SqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
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
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            writeHashedPasswordToDatabase(username, hashedPassword);
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

    private static void writeHashedPasswordToDatabase(String username, String hashedPassword) {

    }

    private static void readHashedPasswordFromDatabase(String username){
        
    }

    boolean verifyUser(String username, String providedClearTextPassword) {
//        var hashedPassword = readHashedPasswordFromDatabase(username);
//
//        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
        return true;
    }

    private static AuthData createNewAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        return authDAO.createAuth(authToken, username);
    }
}
