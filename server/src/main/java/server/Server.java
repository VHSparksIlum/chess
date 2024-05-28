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
    //    private final GameService gameService;
    private final UserService userService;

    public Server(){
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();
        clearService = new ClearService(authDAO, gameDAO, userDAO);
//        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);
    }
    public Server(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        clearService = new ClearService(authDAO, gameDAO, userDAO);
//        gameService = new GameService(authDAO, gameDAO);
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
}
