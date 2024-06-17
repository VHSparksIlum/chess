package websocket.commands;

public class JoinObserverCommand extends UserGameCommand{

    public JoinObserverCommand(String authToken, int gameID) {
        super(authToken, gameID);
        this.commandType = CommandType.JOIN_OBSERVER;
    }

}
