package ui;

import chess.ChessGame;
import exception.ResponseException;
import websocket.*;
import model.*;
import request.*;
import result.*;
import server.ServerFacade;

import java.util.*;

public class Client {
    private String auth = null;
    private AuthData authData;
    private final ServerFacade server;
    private int state = 0;
    private int gameID = 0;
    private final Map<String, Integer> joinCodeToGameIDMap;
    private final Map<Integer, String> gameIDToJoinCodeMap;


    //need to fix logout using last status if pressing enter
    //improve error messages from failure: 401
    public Client(String serverURL) {
        this.server = new ServerFacade(serverURL);
        this.joinCodeToGameIDMap = new HashMap<>();
        this.gameIDToJoinCodeMap = new HashMap<>();
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
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_MAGENTA);
            System.out.printf("Registered user %s%n", username);
            return logIn(username, password);
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
        if (authData != null) {
            if (params.length == 0) {
                AuthData info = new AuthData(auth, authData.username());
                server.logout(info);
                state = 0;
                return "Logged out successfully";
            }
            throw new ResponseException(400, "Expected: logout");
        }
        else {
            throw new ResponseException(400, "You must be logged in to log out.");
        }
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            if(authData != null) {
                String gameName = params[0];
                CreateGameRequest req = new CreateGameRequest(auth, gameName);
                CreateGameResult res = server.createGame(req, authData);
                int gameID = res.getGameID();
                String joinCode = generateJoinCode();
                saveJoinCode(joinCode, gameID);
                return String.format("Created Game: %s (Join Code: %s)", gameName, joinCode);
            }
            else {
                throw new ResponseException(400, "You must be logged in to create a game.");
            }
        }
        throw new ResponseException(400, "Expected: create <game_name>");
    }

    //prevent join of same user to both teams
    public String joinGame(String... params) throws ResponseException {
        if (authData != null) {
            if (params.length <= 2) {
                AuthData info = new AuthData(auth, authData.username());
                String playerColor = null;
                int gameID = 0;
                if (params.length == 2) {
                    String joinCode = params[0];
                    playerColor = params[1];
                    if (joinCode.matches("[a-zA-Z0-9]+")) {
                        gameID = getGameIDFromJoinCode(joinCode);
                    } else {
                        gameID = Integer.parseInt(joinCode);
                    }
                    //gameID = getGameIDFromJoinCode(joinCode);
                    //gameID = Integer.parseInt(params[1]);

                    ChessGame.TeamColor teamColor = null;
                    if (Objects.equals(playerColor, "black"))
                        teamColor = ChessGame.TeamColor.BLACK;
                    else if (Objects.equals(playerColor, "white")) {
                        teamColor = ChessGame.TeamColor.WHITE;
                    }
                    JoinGameRequest req = new JoinGameRequest(playerColor, gameID);
                    server.joinGame(info, req);

                } else {
                    String joinCode = params[0];
                    if (joinCode.matches("[a-zA-Z0-9]+")) {
                        gameID = getGameIDFromJoinCode(joinCode);
                    } else {
                        gameID = Integer.parseInt(joinCode);
                    }
                }
                this.state = 2;
                this.gameID = gameID;
                if (playerColor == null) {
                    System.out.print("Joined as observer\n");
                    return drawCombined();
                }
                System.out.printf("Joined as team %s%n", playerColor);
                if (Objects.equals(playerColor, "black")) {
                    return drawBoardBlack();
                } else {
                    return drawBoardWhite();
                }
            }
            throw new ResponseException(400, "Expected: join <join_code> <white | black>");
        }
        else {
            throw new ResponseException(400, "Unauthorized");
        }
    }

    //review hashmap initialization
    public String listGames(String... params) throws ResponseException {
        if (authData != null) {
            if (params.length == 0) {
                StringBuilder result = new StringBuilder("\nGAMES LIST:\n");
                AuthData info = new AuthData(auth, authData.username());
                ListGamesResult res = server.listGames(info);
                Collection<GameData> gamesList = res.getGames();
                //System.out.println("Games List: " + gamesList);
                //System.out.println("Number of games in the list: " + gamesList.size()); // Debugging
                for (GameData game : gamesList) {
                    String joinCode = getJoinCodeFromGameID(game.gameID());
                    if (joinCode != null && !joinCode.isEmpty()) {
                        result.append("Game Name: ").append(game.gameName()).append("\n");
                        result.append("Join Code: ").append(joinCode).append("\n");
                        result.append("White: ").append(game.whiteUsername()).append("\n");
                        result.append("Black: ").append(game.blackUsername()).append("\n");
                        result.append("\n");
                    }
                }
//                if (result.length() == 13) {
//                    return "No games available.";
//                }
                return result.toString();
            }

            throw new ResponseException(400, "Expected: list");
        }
        else {
            throw new ResponseException(401, "Unauthorized");
        }
    }


    public String drawBoardWhite() {
        String[][] board = new String[8][8];

        board[0][0] = EscapeSequences.BLACK_ROOK;
        board[0][1] = EscapeSequences.BLACK_KNIGHT;
        board[0][2] = EscapeSequences.BLACK_BISHOP;
        board[0][3] = EscapeSequences.BLACK_QUEEN;
        board[0][4] = EscapeSequences.BLACK_KING;
        board[0][5] = EscapeSequences.BLACK_BISHOP;
        board[0][6] = EscapeSequences.BLACK_KNIGHT;
        board[0][7] = EscapeSequences.BLACK_ROOK;

        for (int i = 1; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EscapeSequences.BLACK_PAWN;
            }
        }

        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EscapeSequences.EMPTY;
            }
        }

        for (int i = 6; i < 7; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EscapeSequences.WHITE_PAWN;
            }
        }

        board[7][0] = EscapeSequences.WHITE_ROOK;
        board[7][1] = EscapeSequences.WHITE_KNIGHT;
        board[7][2] = EscapeSequences.WHITE_BISHOP;
        board[7][3] = EscapeSequences.WHITE_QUEEN;
        board[7][4] = EscapeSequences.WHITE_KING;
        board[7][5] = EscapeSequences.WHITE_BISHOP;
        board[7][6] = EscapeSequences.WHITE_KNIGHT;
        board[7][7] = EscapeSequences.WHITE_ROOK;

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
        String[][] board = new String[8][8];

        board[7][0] = EscapeSequences.BLACK_ROOK;
        board[7][1] = EscapeSequences.BLACK_KNIGHT;
        board[7][2] = EscapeSequences.BLACK_BISHOP;
        board[7][3] = EscapeSequences.BLACK_KING;
        board[7][4] = EscapeSequences.BLACK_QUEEN;
        board[7][5] = EscapeSequences.BLACK_BISHOP;
        board[7][6] = EscapeSequences.BLACK_KNIGHT;
        board[7][7] = EscapeSequences.BLACK_ROOK;

        for (int i = 6; i < 7; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EscapeSequences.BLACK_PAWN;
            }
        }

        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EscapeSequences.EMPTY;
            }
        }

        for (int i = 1; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EscapeSequences.WHITE_PAWN;
            }
        }

        board[0][0] = EscapeSequences.WHITE_ROOK;
        board[0][1] = EscapeSequences.WHITE_KNIGHT;
        board[0][2] = EscapeSequences.WHITE_BISHOP;
        board[0][3] = EscapeSequences.WHITE_KING;
        board[0][4] = EscapeSequences.WHITE_QUEEN;
        board[0][5] = EscapeSequences.WHITE_BISHOP;
        board[0][6] = EscapeSequences.WHITE_KNIGHT;
        board[0][7] = EscapeSequences.WHITE_ROOK;

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

    public String generateJoinCode() {
        String joinCode = UUID.randomUUID().toString().substring(0, 8);
        while (joinCodeToGameIDMap.containsKey(joinCode)) {
            joinCode = UUID.randomUUID().toString().substring(0, 8);
        }
        return joinCode;
    }

    public void saveJoinCode(String joinCode, int gameID) {
        joinCodeToGameIDMap.put(joinCode, gameID);
        gameIDToJoinCodeMap.put(gameID, joinCode);
    }

    public int getGameIDFromJoinCode(String joinCode) {
        if (joinCodeToGameIDMap.containsKey(joinCode)) {
            return joinCodeToGameIDMap.get(joinCode);
        } else {
            throw new IllegalArgumentException("Invalid join code");
        }
    }

    public String getJoinCodeFromGameID(int gameID) {
        return gameIDToJoinCodeMap.getOrDefault(gameID, null);
    }
}