package dataaccess.memory;

import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private int gameID = 0;
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public List<GameData> listGames() {
        List<GameData> listGames = new ArrayList<>();
        for (int i = 1; i <= gameID; i++) {
            listGames.add(games.get(i));
        }
        return listGames;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public GameData createGame(GameData game) {
        return null;
    }

    @Override
    public GameData joinGame(GameData game) {
        return null;
    }

    @Override
    public void clear() {
        gameID = 0;
        games.clear();
    }
}
