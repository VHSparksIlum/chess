package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.List;

public interface GameDAO {
    List<GameData> listGames();

    GameData getGame(int gameID);

    GameData createGame(GameData game);

    //may need to be passed new instance rather than GameData game
    void joinGame(int gameID, String playerColor, AuthData auth);
//    GameData joinGame(String username, Integer gameID, String playerColor, GameData game);

    void makeMove(int gameID, ChessGame game);

    void clear();
}
