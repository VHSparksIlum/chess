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
    private int state = 0;
    private int gameID = 0;

    public Client(String serverURL) {
        this.server = new ServerFacade(serverURL);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params); // find a way to make register turn into login after success
                case "login" -> logIn(params);
                case "logout" -> logOut(params);
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "list" -> listGames(params);
                case "draw" -> drawCombined();
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
        if (params.length > 1) {
            String username = params[0];
            String password = params[1];
            LoginRequest info = new LoginRequest(username, password);
            LoginResult res = server.login(info);
            this.auth = res.getAuthToken();
            this.authData = new AuthData(auth, res.getUsername());
            state = 1;
            return String.format("You signed in as %s.", username);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String logOut(String... params) throws ResponseException {
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
            if(authData != null) {
                String gameName = params[0];
                CreateGameRequest req = new CreateGameRequest(auth, gameName);
                CreateGameResult res = server.createGame(req, authData);
                int gameID = res.getGameID();
                return String.format("Created Game: %s (id: %s)", gameName, gameID);
            }
            else {
                throw new ResponseException(400, "You must be logged in to create a game.");
            }
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
            this.gameID = gameID;
            if (playerColor == null) {
                return "Joined as observer";
            }
            return String.format("Joined as team %s", playerColor);
        }
        throw new ResponseException(400, "Expected: join <game_id> <white | black>");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length == 0) {
            StringBuilder result = new StringBuilder("\nGAMES LIST:\n");
            AuthData info = new AuthData(auth, authData.username());
            ListGamesResult res = server.listGames(info);
            Collection<GameData> gamesList = res.getGames();
            this.state = 1;
            for (GameData game : gamesList) {
                //result.append("Game ID: ").append(game.gameID()).append("\n");

                result.append("Game Name: ").append(game.gameName()).append("\n");
                result.append("White: ").append(game.whiteUsername()).append("\n");
                result.append("Black: ").append(game.blackUsername()).append("\n");
                result.append("\n");
            }
            return result.toString();
        }
        throw new ResponseException(400, "Expected: list");
    }

    public String drawBoardWhite() {
        String[][] board = {
                {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK},
                {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN},
                {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK}
        };
        StringBuilder result = new StringBuilder();
        result.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        result.append(" \u2003\u2003a \u2003b \u2003c \u2003d \u2003e \u2003f \u2003g \u2003h\u2003\u2003 ").append(EscapeSequences.SET_BG_COLOR_BLACK).append("\n");
        for (int i = 0; i < 8; i++) {
            result.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            result.append(" ").append(8 - i).append(" ");
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    result.append(EscapeSequences.SET_BG_COLOR_WHITE);
                } else {
                    result.append(EscapeSequences.SET_BG_COLOR_BLUE);
                }
                result.append(board[i][j]);
            }
            result.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            result.append(" ").append(8 - i).append(" ").append(EscapeSequences.SET_BG_COLOR_BLACK).append("\n");
        }
        result.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        result.append(" \u2003\u2003a \u2003b \u2003c \u2003d \u2003e \u2003f \u2003g \u2003h\u2003\u2003 ").append(EscapeSequences.SET_BG_COLOR_BLACK).append("\n");
        result.append(EscapeSequences.RESET_TEXT_COLOR);

        return result.toString();
        }

    //FOR PHASE 5 DRAW
    public String drawBoardBlack() {
        String[][] board = {
                {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK},
                {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN},
                {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK}
        };
        StringBuilder flipped = new StringBuilder();
        flipped.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        flipped.append(" \u2003\u2003h \u2003g \u2003f \u2003e \u2003d \u2003c \u2003b \u2003a\u2003\u2003 ").append(EscapeSequences.SET_BG_COLOR_BLACK).append("\n");
        for (int i = 0; i < 8; i++) {
            flipped.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            flipped.append(" ").append(i + 1).append(" ");
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    flipped.append(EscapeSequences.SET_BG_COLOR_WHITE);
                } else {
                    flipped.append(EscapeSequences.SET_BG_COLOR_BLUE);
                }
                flipped.append(board[i][j]);
            }
            flipped.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            flipped.append(" ").append(i + 1).append(" ").append(EscapeSequences.SET_BG_COLOR_BLACK).append("\n");
        }
        flipped.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        flipped.append(" \u2003\u2003h \u2003g \u2003f \u2003e \u2003d \u2003c \u2003b \u2003a\u2003\u2003 ").append(EscapeSequences.SET_BG_COLOR_BLACK).append("\n");
        flipped.append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_TEXT_COLOR);

        return flipped.toString();
    }

    public String drawCombined() {
        return drawBoardWhite() + "\n" + drawBoardBlack();
    }


    public String help() {
        return """
                - Register <username> <password> <email>
                - Login <username> <password>
                - Create <game name>
                - Join <ID> <white|black>
                - List
                - Logout
                - Quit
                - Help
                """;
    }
    public int getState () { return state; }

    public void setState ( int state){
        this.state = state;
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