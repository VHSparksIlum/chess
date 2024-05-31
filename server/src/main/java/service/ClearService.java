package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public ClearService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void clear(){
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }

//    try {
//        game.ClearGames();
//        user.clearUsers();
//        auth.clearAuth();
//        return new ClearResult();
//    }
//        catch (DataAccessException e) {
//        var response = new ClearResult();
//        response.setMessage(String.format("Error: %s", e.getMessage()));
//        return response;
//    }
}
