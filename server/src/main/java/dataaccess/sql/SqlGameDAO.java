package dataaccess.sql;

import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.List;

public class SqlGameDAO implements GameDAO {
    @Override
    public List<GameData> listGames() {
        return List.of();
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public GameData createGame(GameData game) {
        return null;
    }

    @Override
    public void joinGame(int gameID, String playerColor, AuthData auth) {

    }

    @Override
    public void clear() {

    }
}
