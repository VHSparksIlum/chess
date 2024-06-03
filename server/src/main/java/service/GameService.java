package service;

import model.*;
import dataaccess.*;

import java.util.List;
import java.util.Objects;


public class GameService {
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        GameService.authDAO = authDAO;
        GameService.gameDAO = gameDAO;
    }

    public GameData createGame(String authToken, GameData game) throws DataAccessException {
        if (!checkAuthToken(authToken)) {
            throw new DataAccessException("Unauthorized");
        }
        if (Objects.equals(game.gameName(), "")) {
            throw new DataAccessException("Bad Request");
        }
        return gameDAO.createGame(game);
    }

    public void joinGame(String authToken, String playerColor, Integer gameID) throws DataAccessException {
        GameData currentGame = gameDAO.getGame(gameID);
            if (playerColor != null) {
                playerColor = playerColor.toUpperCase();
            }

            if (gameDAO.getGame(gameID) == null) {
                throw new IllegalArgumentException("No game with that ID"); // change message
            }

            if (gameID == 0) {
                throw new IllegalArgumentException("No gameID entered");
            }
            if (!checkAuthToken(authToken)) {
                throw new DataAccessException("Unauthorized");
            }
            AuthData auth = authDAO.getAuth(authToken);

            if (!(Objects.equals(playerColor, "WHITE") || Objects.equals(playerColor, "BLACK"))) {
                throw new IllegalArgumentException("Bad playerColor request");
            }
            if (Objects.equals(playerColor, "WHITE") && (currentGame.whiteUsername() != null))
            {
                throw new IllegalAccessError("Already taken");
            }
            if (Objects.equals(playerColor, "BLACK") && (currentGame.blackUsername() != null))
            {
                throw new IllegalAccessError("Already taken");
            }
            gameDAO.joinGame(gameID, playerColor, auth);
    }

    public static List<GameData> listGames(String authToken) throws DataAccessException {
        if(!checkAuthToken(authToken)) {
            throw new DataAccessException("unauthorized");
        }
        return gameDAO.listGames();
    }

    private static boolean checkAuthToken(String authToken) throws DataAccessException {
        if(authDAO.getAuth(authToken)==null){
            throw new DataAccessException("unauthorized");
        }
        return true;
    }
}
