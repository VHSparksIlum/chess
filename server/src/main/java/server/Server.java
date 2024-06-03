package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.*;
import result.*;
import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import service.*;
import spark.*;

import java.util.Collection;

public class Server {
    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;

    public Server(){
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();
        clearService = new ClearService(authDAO, gameDAO, userDAO);
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);
    }
    public Server(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        clearService = new ClearService(authDAO, gameDAO, userDAO);
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::list);
        Spark.post("/game", this::create);
        Spark.put("/game", this::join);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res) {
        clearService.clear();
        return "{}";
    }

    private Object register(Request req, Response res) {
        Gson gson = new Gson();
        RegisterRequest request = (RegisterRequest)gson.fromJson(req.body(), RegisterRequest.class);
        UserData user = new UserData(request.username(), request.password(), request.email());
        AuthData authorization = null;
        RegisterResult response;
        try {
            authorization = UserService.register(user);
            response = new RegisterResult(authorization.username(), authorization.authToken(), "");
            res.status(200);
        } catch (DataAccessException e) {
            response = new RegisterResult(null, null, "Error: already taken");
            res.status(403);
        } catch (IllegalArgumentException e) {
            response = new RegisterResult(null, null, "Error: bad request");
            res.status(400);
        }
        res.type("application/json");
        return gson.toJson(response);
    }

    private Object login(Request req, Response res) {
        Gson gson = new Gson();
        LoginRequest request = (LoginRequest)gson.fromJson(req.body(), LoginRequest.class);
        System.out.println(request.username() + ", " + request.password());
        UserData user = new UserData(request.username(), request.password(), "");
        AuthData authorization = null;
        LoginResult response;
        try {
            authorization = UserService.login(user);
            response = new LoginResult(authorization.username(), authorization.authToken(), "");
            res.status(200);
        } catch (DataAccessException e) {
            response = new LoginResult(null, null, "Error: unauthorized");
            res.status(401);
        }
        res.type("application/json");
        return gson.toJson(response);
    }

    private Object logout(Request req, Response res) {
        Gson gson = new Gson();
        String authToken = req.headers("authorization");
        LogoutRequest request = new LogoutRequest(authToken, null);
        LogoutResult response;
        try {
            UserService.logout(request.authToken());
            response = new LogoutResult("{}");
            res.status(200);
        } catch (DataAccessException e) {
            response = new LogoutResult("Error: unauthorized");
            res.status(401);
        }
        res.type("application/json");
        return gson.toJson(response);
    }

    private Object list(Request req, Response res) {
        Gson gson = new Gson();
        String authToken = req.headers("authorization");
        ListGamesRequest request = new ListGamesRequest();
        ListGamesResult response;
        try {
            Collection<GameData> games = GameService.listGames(authToken);
            GameService.listGames(authToken);
            response = new ListGamesResult("", games);
            res.status(200);
        } catch (DataAccessException e) {
            response = new ListGamesResult("Error: unauthorized", null);
            res.status(401);
        }
        res.type("application/json");
        return gson.toJson(response);
    }

    private Object create(Request req, Response res) {
        Gson gson = new Gson();
        String authToken = req.headers("authorization");
        //CreateGameRequest request = new CreateGameRequest(authToken, );
        CreateGameResult response;
        try {
            var game = new Gson().fromJson(req.body(), GameData.class);
            game = gameService.createGame(req.headers("authorization"),game);
            response = new CreateGameResult("{}", game.gameID());
            res.status(200);
        } catch (DataAccessException e) {
            response = new CreateGameResult("Error: unauthorized", null);
            res.status(401);
        }
        res.type("application/json");
        return gson.toJson(response);
    }

    private Object join(Request req, Response res) {
        Gson gson = new Gson();
        String authToken = req.headers("authorization");
//        JoinGameRequest request = new JoinGameRequest(authToken, );
        var request = new Gson().fromJson(req.body(), JoinGameRequest.class);
        JoinGameResult response;
        try {
            gameService.joinGame(authToken, request.playerColor(), request.gameID());
            response = new JoinGameResult("{}");
            res.status(200);
        } catch (IllegalArgumentException e) {
            response = new JoinGameResult("Error: bad request");
            res.status(400);
        } catch (DataAccessException e) {
            response = new JoinGameResult("Error: unauthorized");
            res.status(401);
        } catch (IllegalAccessError e) {
            response = new JoinGameResult("Error: already taken");
            res.status(403);
        }
        res.type("application/json");
        return gson.toJson(response);
    }

//    private Object register(Request req, Response res) {
//        try {
//            var user = new Gson().fromJson(req.body(), User.class);
//            var auth = userService.registerUser(user);
//            return new Gson().toJson(auth);
//        }catch(DataAccessException dae){
//            return dataAccessException(res, dae);
//        }
//    }
//
//    private Object login(Request req, Response res) {
//        try {
//            var user = new Gson().fromJson(req.body(), User.class);
//            var auth = userService.login(user);
//            return new Gson().toJson(auth);
//        }catch(DataAccessException dae){
//            return dataAccessException(res, dae);
//        }
//    }
//
//    private Object logout(Request req, Response res) {
//        try{
//            userService.logout(req.headers("authorization"));
//            return "{}";
//        }catch(DataAccessException dae){
//            return dataAccessException(res, dae);
//        }
//    }
//    private Object list(Request req, Response res){
//        try{
//            var listGames = gameService.listGames(req.headers("authorization"));
//            return new Gson().toJson(new Games(listGames));
//        }catch(DataAccessException dae){
//            return dataAccessException(res, dae);
//        }
//    }
//    private Object create(Request req, Response res) {
//        try{
//            var game = new Gson().fromJson(req.body(), Game.class);
//            game = gameService.createGame(req.headers("authorization"),game);
//            return new Gson().toJson(game);
//        }catch(DataAccessException dae){
//            return dataAccessException(res, dae);
//        }
//    }
//    private Object join(Request req, Response res) {
//        try{
//            var joinGame = new Gson().fromJson(req.body(), Join.class);
//            gameService.joinGame(req.headers("authorization"),joinGame);
//            return "{}";
//        }catch(DataAccessException dae){
//            return dataAccessException(res, dae);
//        }
//    }
    private String errorCall(Response res, DataAccessException dae){
        switch (dae.getMessage()) {
            case "Bad Request!" -> {
                res.status(400);
                return "{ \"message\": \"Error: Bad Request\" }";
            }
            case "Unauthorized!" -> {
                res.status(401);
                return "{ \"message\": \"Error: Unauthorized\" }";
            }
            case "Already Taken!" -> {
                res.status(403);
                return "{ \"message\": \"Error: Forbidden\" }";
            }
            default -> {
                res.status(500);
                return "{ \"message\": \"Error: description\" }";
            }
        }
    }
}
