package result;

import model.GameData;

import java.util.Collection;

public class ListGamesResult extends ErrorResponse {

    Collection<GameData> games;

    public ListGamesResult(String message, Collection<GameData> games) {
        super(message);
        this.games = games;
    }
}
