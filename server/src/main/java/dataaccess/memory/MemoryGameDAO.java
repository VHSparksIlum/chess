package dataaccess.memory;

import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    private int gameID = 0;
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public GameData createGame(GameData game) {
        gameID++;
        GameData createGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(gameID, createGame);
        return createGame;
    }

    @Override
    public void joinGame(int gameID, String playerColor, AuthData auth) {
        String username = auth.username();
        GameData game = getGame(gameID);

        GameData updatedGame = getGameData(playerColor, game, username);

        games.put(gameID, updatedGame);
    }

    private static GameData getGameData(String playerColor, GameData game, String username) {
        GameData updatedGame;
        if (Objects.equals(playerColor, "WHITE")) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if (Objects.equals(playerColor, "BLACK")) {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            // Handle invalid playerColor
            throw new IllegalArgumentException("Invalid player color: " + playerColor);
        }
        return updatedGame;
    }

    @Override
    public void clear() {
        gameID = 0;
        games.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryGameDAO that = (MemoryGameDAO) o;
        return gameID == that.gameID && Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, games);
    }
}

