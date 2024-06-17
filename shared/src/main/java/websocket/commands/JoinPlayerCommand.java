package websocket.commands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand{

    private final ChessGame.TeamColor playerColor;
    public JoinPlayerCommand(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken, gameID);
        this.commandType = CommandType.JOIN_PLAYER;
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
