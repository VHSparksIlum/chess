package result;

public class LoginResult extends ErrorResponse {
    private final String username;
    private final String authToken;

    public LoginResult(String username, String authToken, String message) {
        super(message);
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}

