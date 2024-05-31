package dataaccess.memory;

import chess.ChessGame;
import dataaccess.GameDAO;
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
        gameID++;
        GameData createGame = new GameData(gameID,game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), game.getGame());
        games.put(gameID,createGame);
        return createGame;
    }

    @Override
    public GameData updateGame(String username, Integer gameID, String playerColor, GameData game) {
        GameData newGame = games.get(gameID);
        if(playerColor == null){
            newGame = new GameData(gameID,newGame.getWhiteUsername(),newGame.getBlackUsername(),newGame.getGameName(),game.getGame());
        }else {
            switch (playerColor) {
                case "WHITE" -> newGame = new GameData(gameID, username, newGame.getBlackUsername(), newGame.getGameName(), game.getGame());
                case "BLACK" -> newGame = new GameData(gameID, newGame.getWhiteUsername(), username, newGame.getGameName(), game.getGame());
            }
        }
        games.remove(gameID);
        games.put(gameID,newGame);
        return newGame;
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

/**
 * Creates a new game in the database and returns the gameID.
 *
 * @param game The Game object to be created.
 * @return The gameID of the created game
 * @throws DataAccessException if there's an error during game creation
 */
//public int createGame(Game game) throws DataAccessException {
//    // Implementation for creating a game in the database
//    return 0;
//}

/**
 * Inserts a new Game into the data store and assigns it a unique gameID.
 *
 * @param game The Game to be inserted.
 * @throws DataAccessException if there is an issue inserting the game.
 */
//public void insertGame(Game game) throws DataAccessException {
//    game.setGameID(nextGameID++);
//    games.put(game.getGameID(), game);
//}

/**
 * Finds and retrieves a Game by its unique gameID.
 *
 * @param gameID The unique gameID for the game to be found.
 * @return The Game with the specified gameID or null if not found.
 */
//public Game findGame(int gameID) {
//    return games.get(gameID);
//}

/**
 * Retrieves a list of all Game objects in the data store.
 *
 * @return A list of all Game objects.
 */
//public List<Game> getAllGames() {
//    return new ArrayList<>(games.values());
//}

/**
 * Claims a spot in the specified game by setting the username as either the whitePlayer or blackPlayer.
 *
 * @param gameID     The unique gameID for the game.
 * @param username   The username of the player claiming a spot.
 * @param playerType The player type to claim (WHITE/BLACK).
 * @throws DataAccessException if the game doesn't exist or if the spot is already taken.
 */
//public void claimSpot(int gameID, String username, String playerType) throws DataAccessException {
//    Game game = games.get(gameID);
//    if (game != null) {
//        if (playerType.equals("WHITE") && game.getWhiteUsername() == null) {
//            game.setWhiteUsername(username);
//        } else if (playerType.equals("BLACK") && game.getBlackUsername() == null) {
//            game.setBlackUsername(username);
//        } else {
//            throw new DataAccessException("Spot already taken.");
//        }
//    } else {
//        throw new DataAccessException("Game not found.");
//    }
//}

/**
 * Updates the ChessGame implementation for the specified game.
 *
 * @param gameID     The unique gameID for the game to be updated.
 * @param newGame    The new ChessGame implementation for the game.
 * @throws DataAccessException if the game doesn't exist.
 */
//public void updateGame(int gameID, ChessGame newGame) throws DataAccessException {
//    Game game = games.get(gameID);
//    if (game != null) {
//        game.setGame(newGame);
//    } else {
//        throw new DataAccessException("Game not found.");
//    }
//}

/**
 * Removes a game from the data store by its unique gameID.
 *
 * @param gameID The unique gameID for the game to be removed.
 * @throws DataAccessException if the game doesn't exist.
 */
//public void removeGame(int gameID) throws DataAccessException {
//    if (games.remove(gameID) == null) {
//        throw new DataAccessException("Game not found.");
//    }
//}

/**
 * Clears all data from the data store.
 */
//public void clearData() {
//    games.clear();
//    nextGameID = 1;
//}
