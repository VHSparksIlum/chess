package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;

public class ConnectionManager {
    //public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, ArrayList<Connection>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String authToken, Session session) {
        var connection = new Connection(authToken, session);
        ArrayList<Connection> tmp = connections.get(gameID);
        if (tmp == null)
        {
            ArrayList<Connection> tmp2 = new ArrayList<>();
            tmp2.add(connection);
            connections.put(gameID, tmp2);
        }
        else
        {
            tmp.add(connection);
            connections.put(gameID, tmp);
        }
    }

    public void removeSessionFromGame(int gameID, String authToken) {
        ArrayList<Connection> tmp = connections.get(gameID);
        tmp.removeIf(conn -> Objects.equals(conn.authToken, authToken));
    }

    public void removeSession(Session session)
    {
        for (int key : connections.keySet())
        {
            ArrayList<Connection> tmp = connections.get(key);
            tmp.removeIf(conn -> conn.session == session);
            connections.put(key, tmp);
        }
    }

    public void broadcast(int gameID, String excludeUser, Notification notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID)) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeUser)) {
                    String msg = new Gson().toJson(notification, Notification.class);
                    c.send(msg);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            ArrayList<Connection> tmp = connections.get(gameID);
            tmp.remove(c);
            connections.put(gameID, tmp);
        }
    }

    public void sendLoadCommand(int gameID, LoadGameMessage loadGameMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID)) {
            if (c.session.isOpen()) {
                String msg = new Gson().toJson(loadGameMessage, LoadGameMessage.class);
                c.send(msg);
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            ArrayList<Connection> tmp = connections.get(gameID);
            tmp.remove(c);
            connections.put(gameID, tmp);
        }
    }

    public void sendOneLoadCommand(int gameID, String authToken, LoadGameMessage loadGameMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID)) {
            if (c.session.isOpen()) {
                if (c.authToken.equals(authToken)) {
                    String msg = new Gson().toJson(loadGameMessage, LoadGameMessage.class);
                    c.send(msg);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            ArrayList<Connection> tmp = connections.get(gameID);
            tmp.remove(c);
            connections.put(gameID, tmp);
        }
    }

    public void sendError(String authToken, ErrorMessage errorMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (int gameID : connections.keySet()) {
            for (var c : connections.get(gameID)) {
                if (c.session.isOpen()) {
                    if (c.authToken.equals(authToken)) {
                        String msg = new Gson().toJson(errorMessage, ErrorMessage.class);
                        c.send(msg);
                    }
                } else {
                    removeList.add(c);
                }
            }

            // Clean up any connections that were left open.
            for (var c : removeList) {
                ArrayList<Connection> tmp = connections.get(gameID);
                tmp.remove(c);
                connections.put(gameID, tmp);
            }
        }
    }
}