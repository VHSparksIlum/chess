package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand{

    private final ChessGame.TeamColor playerColor;
    public ConnectCommand(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken, gameID);
        this.commandType = CommandType.CONNECT;
        this.playerColor = playerColor;
    }

    public ConnectCommand(String authToken, int gameID) {
        super(authToken, gameID);
        this.commandType = CommandType.CONNECT;
        playerColor = null;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
