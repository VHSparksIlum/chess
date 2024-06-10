package ui;

import chess.ChessGame;
import com.google.gson.Gson;
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
    //private int gameID = 0;
    private ChessGame chessGame;
    //private int gameID;

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
//                case "signin" -> signIn(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                //CHANGE FOR CHESS
                case "login" -> logIn(params);
                case "register" -> register(params);
                case "logout" -> logOut(params);
                case "list" -> listGames(params);
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
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
        return "Failed to create game";
    }

//        public String adoptAllPets () throws ResponseException {
//            assertSignedIn();
//            var buffer = new StringBuilder();
//            for (var pet : server.listPets()) {
//                buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
//            }
//
//            server.deleteAllPets();
//            return buffer.toString();
//        }

        public String logOut (String...params) throws ResponseException {
            if (params.length == 0) {
                AuthData info = new AuthData(auth, authData.username());
                server.logout(info);
                state = 0;
                return "Logged out successfully";
            }
            throw new ResponseException(400, "Expected: logout");
        }

//    private Pet getPet(int id) throws ResponseException {
//        for (var pet : server.listPets()) {
//            if (pet.id() == id) {
//                return pet;
//            }
//        }
//        return null;
//    }

        public String help () {
//        if (state == State.SIGNEDOUT) {
//            return """
//                    - signIn <yourname>
//                    - quit
//                    """;
//        }
            return """
                    - Help
                    - Quit
                    - Login
                    - Register
                    - Logout
                    - Create
                    - List
                    - Join
                    """;
            //re-organize
        }

//    private void assertSignedIn() throws ResponseException {
//        if (state == State.SIGNEDOUT) {
//            throw new ResponseException(400, "You must sign in");
//        }
//    }

        public int getState () {
            return state;
        }

        public void setState ( int state){
            this.state = state;
        }

        public String getAuth () {
            return auth;
        }

//    public String getAuthData() {
//        return authData;
//    }
//
//    public void setAuthData(String authData) {
//        this.authData = authData;
//    }
//
//        public int getGameID () {
//            return gameID;
//        }

    }
