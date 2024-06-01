package service;

import model.*;
import dataaccess.*;

import java.util.List;
import java.util.Objects;


public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public GameData createGame(String authToken, GameData game) throws DataAccessException {
        checkAuthToken(authToken);
        return gameDAO.createGame(game);
    }

    public void joinGame(String authToken, String playerColor, Integer gameID) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if(auth != null) {
            GameData foundGame = gameDAO.getGame(gameID);
            if(foundGame != null) {
                if(((Objects.equals(playerColor, "WHITE") && (Objects.equals(foundGame.getWhiteUsername(), "") || foundGame.getWhiteUsername() == null))) ||
                        ((Objects.equals(playerColor, "BLACK") && (Objects.equals(foundGame.getBlackUsername(), "") || foundGame.getBlackUsername() == null))) ||
                        ((!Objects.equals(playerColor, "WHITE") && !Objects.equals(playerColor, "BLACK")))) {
                    gameDAO.updateGame(auth.username(), foundGame.getGameID(), playerColor, foundGame);
                } else {
                    throw new DataAccessException("team taken");
                }
            } else {
                throw new DataAccessException("bad request");
            }
        } else {
            throw new DataAccessException("unauthorized");
        }
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        checkAuthToken(authToken);
        return gameDAO.listGames();
    }

    private void checkAuthToken(String authToken) throws DataAccessException {
        if(authDAO.getAuth(authToken)==null){
            throw new DataAccessException("unauthorized");
        }
    }
}
