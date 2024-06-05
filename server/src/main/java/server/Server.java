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
    private ClearService clearService;
    private GameService gameService;
    private UserService userService;

    //Constructor for StandardAPITests
    public Server(){
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();
        clearService = new ClearService(authDAO, gameDAO, userDAO);
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);
    }

    //Constructor for Memory Server
    public Server(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        clearService = new ClearService(authDAO, gameDAO, userDAO);
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);
    }

    //Constructor for SQL Server
    public Server(SqlDataAccess sqlDataAccess) throws DataAccessException {
        SqlDataAccess sql = new SqlDataAccess();
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
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
        UserData user = new UserData(request.username(), request.password(), request.email());
        AuthData authorization;
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
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
        System.out.println(request.username() + ", " + request.password());
        UserData user = new UserData(request.username(), request.password(), "");
        AuthData authorization;
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
        //ListGamesRequest request = new ListGamesRequest();
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
            game = gameService.createGame(authToken, game);
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
}
