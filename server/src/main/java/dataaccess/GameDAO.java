package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    List<GameData> listGames();

    GameData getGame(int gameID);

    GameData createGame(GameData game);

    //may need to be passed new instance rather than GameData game
    GameData updateGame(String username, Integer gameID, String playerColor, GameData game);

    void clear();
}
