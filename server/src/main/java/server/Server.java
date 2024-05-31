package server;

import com.google.gson.Gson;
import request.*;
import result.*;
import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import service.*;
import spark.*;

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
        RegisterResult response = new RegisterResult("success", "User registered successfully.");
        res.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(response);
    }

    private Object login(Request req, Response res) {
        LoginResult response = new LoginResult("success", "User logged in successfully.");
        res.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(response);
    }

    private Object logout(Request req, Response res) {
        LogoutResult response = new LogoutResult("success", "User logged out successfully.");
        res.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(response);
    }

    private Object list(Request req, Response res) {
        ListGamesResult response = new ListGamesResult("success", "Listed games successfully.");
        res.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(response);
    }

    private Object create(Request req, Response res) {
        CreateGameResult response = new CreateGameResult("success", "Game created successfully.");
        res.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(response);
    }

    private Object join(Request req, Response res) {
        JoinGameResult response = new JoinGameResult("success", "Joined game successfully.");
        res.type("application/json");
        Gson gson = new Gson();
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
