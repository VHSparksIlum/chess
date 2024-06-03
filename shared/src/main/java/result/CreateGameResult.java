package result;

public class CreateGameResult extends ErrorResponse {
    private final Integer gameID;


    public CreateGameResult(String message, Integer gameID) {
        super(message);
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
