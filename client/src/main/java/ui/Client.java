package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import request.*;
import result.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class Client {
    private String auth = null;
    private AuthData authData;
    private final ServerFacade server;
    private final String serverUrl;
    private int state = 0;
    private int gameID = 0;

    public Client(String serverURL) {
        this.server = new ServerFacade(serverURL);
        this.serverUrl = serverURL;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> logIn(params);
                case "logout" -> logOut(params);
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "list" -> listGames(params);
                case "quit" -> "quit";
                case "help" -> help();
                default -> "";
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username, password, email);
            LoginResult res = server.register(user);
            this.auth = res.getAuthToken();
            state = 1;
            return String.format("Registered user %s", username);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String logIn(String... params) throws ResponseException {
        if (params.length >= 1) {
            String username = params[0];
            String password = params[1];
            LoginRequest info = new LoginRequest(username, password);
            LoginResult res = server.login(info);
            this.auth = res.getAuthToken();
            state = 1;
            return String.format("You signed in as %s.", username);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String logOut (String...params) throws ResponseException {
        if (params.length == 0) {
            AuthData info = new AuthData(auth, authData.username());
            server.logout(info);
            state = 0;
            return "Logged out successfully";
        }
        throw new ResponseException(400, "Expected: logout");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            {
                AuthData info = new AuthData(auth, authData.username());
                String gameName = params[0];
                CreateGameRequest req = new CreateGameRequest(auth, gameName);
                CreateGameResult res = server.createGame(req, info);
                int gameID = res.getGameID();
                return String.format("Created Game: %s (id: %s)", gameName, gameID);
            }
            //throw new ResponseException(400, "Expected: create <game_name>");
        }
        throw new ResponseException(400, "Expected: create <game_name>");
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length == 2 || params.length == 1) {
            AuthData info = new AuthData(auth, authData.username());
            String playerColor = null;
            int gameID = 0;
            if (params.length == 2) {
                playerColor = params[0];
                gameID = Integer.parseInt(params[1]);

                ChessGame.TeamColor teamColor = null;
                if (Objects.equals(playerColor, "black"))
                    teamColor = ChessGame.TeamColor.BLACK;
                else if (Objects.equals(playerColor, "white")) {
                    teamColor = ChessGame.TeamColor.WHITE;
                }
                JoinGameRequest req = new JoinGameRequest(playerColor, gameID);
                server.joinGame(info, req);

            } else {
                gameID = Integer.parseInt(params[0]);
            }
            this.state = 2;
//            this.gameID = gameID;
            if (playerColor == null) {
                return "Joined as observer";
            }
            return String.format("Joined as team %s", playerColor);
        }
        throw new ResponseException(400, "Expected: join <black or white> <game_id>");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length == 0) {
            StringBuilder result = new StringBuilder("GAMES LIST:\n");
            AuthData info = new AuthData(auth, authData.username());
            ListGamesResult res = server.listGames(info);
            Collection<GameData> gamesList = res.getGames();
            for (GameData game : gamesList) {
                result.append("Game ID: ").append(game.gameID()).append("\n");
                result.append("Game Name: ").append(game.gameName()).append("\n");
                result.append("White: ").append(game.whiteUsername()).append("\n");
                result.append("Black: ").append(game.blackUsername()).append("\n");
                result.append("\n");
            }
            return result.toString();
        }
        throw new ResponseException(400, "Expected: list");
    }

        public String help () {
            return """
                    - Register
                    - Login
                    - Create
                    - Join
                    - List
                    - Logout
                    - Quit
                    - Help
                    """;
        }

        public int getState () {
            return state;
        }

        public void setState ( int state){
            this.state = state;
        }

        public String getAuth () {
            return auth;
        }

    public AuthData getAuthData() {
        return authData;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }

    public int getGameID () {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}