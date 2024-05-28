package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    List<GameData> listGames();

    GameData getGame(int gameID);

    GameData createGame(GameData game);

    GameData joinGame(GameData game);

    void clear();
}
