package handlers;

import request.JoinGameRequest;
import result.JoinGameResult;
import dataaccess.*;
import service.GameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.Map;

public class JoinHandler {
    private final GameService gameService;

    public JoinHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handleJoinGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            var body = turnToJava(req, Map.class);
            String playerColor = (String) body.get("playerColor");
            int gameID = ((Double) body.get("gameID")).intValue();

            gameService.joinGame(authToken, playerColor, gameID);

            // Successful join, return an empty JSON object
            res.type("application/json");
            res.status(200);
            return "{}";
        } catch (DataAccessException dae) {
            // Error occurred, handle and return appropriate response
            return turnToJson(res, dae);
        }
    }

    private Object turnToJson(Response res, DataAccessException dae) {
        res.type("application/json");
        int status;
        String message;

        switch (dae.getMessage()) {
            case "unauthorized":
                status = 401;
                message = "Unauthorized";
                break;
            case "bad request":
                status = 400;
                message = "Bad Request";
                break;
            case "team taken":
                status = 403;
                message = "Team already taken";
                break;
            default:
                status = 500;
                message = "Internal Server Error";
        }

        res.status(status);
        var body = new Gson().toJson(Map.of("message", "Error: " + message));
        res.body(body);
        return body;
    }

    public static <T> T turnToJava(Request req, Class<T> clazz) {
        var body = new Gson().fromJson(req.body(), clazz);
        if (body == null) {
            throw new RuntimeException("Missing required body");
        }
        return body;
    }
}
