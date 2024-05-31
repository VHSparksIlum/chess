package result;

public record JoinGameResult (String status, String message) {
    public String getMessage() {
        return message;
    }
}
