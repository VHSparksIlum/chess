package server;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;

import exception.ResponseException;
import request.*;
import result.*;
import model.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public LoginResult register(UserData user) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, user, LoginResult.class);
    }

    public LoginResult login(LoginRequest info) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, info, LoginResult.class);
    }

    public void logout(AuthData auth) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, "authorization", auth.authToken(), null, null);
    }

    public CreateGameResult createGame(CreateGameRequest gameName, AuthData auth) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, "authorization", auth.authToken(), gameName, CreateGameResult.class);
    }

    public ListGamesResult listGames(AuthData auth) throws ResponseException
    {
        var path = "/game";
        return this.makeRequest("GET", path, "authorization", auth.authToken(), null, ListGamesResult.class);
    }

    public void joinGame(AuthData auth, JoinGameRequest req) throws ResponseException
    {
        var path = "/game";
        this.makeRequest("PUT", path, "authorization", auth.authToken(), req, null);
    }

    public void clear() throws ResponseException
    {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        return makeRequest(method, path, null, null, request, responseClass);
    } //second method for additional object in call
    private <T> T makeRequest(String method, String path, String headerKey, String headerValue, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            // Set the header if provided
            if (headerKey != null && headerValue != null) {
                http.setRequestProperty(headerKey, headerValue);
            } //new if statement
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}