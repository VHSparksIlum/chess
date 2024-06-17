package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlDataAccess implements AuthDAO, GameDAO, UserDAO {

    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
           """
           CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(512) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(authToken)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
           """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `game` varchar(2048),
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256),
              PRIMARY KEY (`username`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    @Override
    public AuthData createAuth(String authToken, String username) {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try {
            AuthData auth = new AuthData(authToken, username);
            executeUpdate(statement, authToken, username);
            return auth;
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        return new AuthData(authToken, username);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("authExists");
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM auth WHERE authToken=?";
        try {
            executeUpdate(statement, authToken);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<GameData> listGames() {
        List<GameData> gamesList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String json = rs.getString("json");
                        GameData game = new Gson().fromJson(json, GameData.class);
                        game = new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
                        gamesList.add(game);
                    }
                    return gamesList;
                }
            }
        } catch (Exception e) {
            System.out.println("getUser");
            System.out.println(e.getMessage());
        }
        return null;
    }


    @Override
    public GameData getGame(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("json");
                        return new Gson().fromJson(json, GameData.class);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("getUser");
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public GameData createGame(GameData game) {
        var statement = "INSERT INTO games (name, game, json) VALUES (?, ?, ?)";
        try {
            int id = executeUpdate(statement, game.gameName(), new Gson().toJson(game.game()), new Gson().toJson(game));
            return new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    @Override
    public void joinGame(int gameID, String playerColor, AuthData auth) {
        try {
            GameData game = getGame(gameID);
            GameData updatedGame = null;
            String username = getUsername(auth);
            System.out.println("Updating");
            var statement = "SELECT username FROM auth WHERE authToken=?";
            System.out.println("here");
            System.out.println(username);
            var statement2 = "";
            if (Objects.equals(playerColor, "WHITE"))
            {
                updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                statement2 = "UPDATE games SET whiteUsername = ?, json = ? WHERE id = ?";
            }
            else if (Objects.equals(playerColor, "BLACK"))
            {
                updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                statement2 = "UPDATE games SET blackUsername = ?, json = ? WHERE id = ?";
            }
            try
            {
                executeUpdate(statement2, username, new Gson().toJson(updatedGame), gameID);
            }
            catch (DataAccessException e)
            {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void makeMove(int gameID, ChessGame game) {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            GameData gameData = getGame(gameID);
            GameData newData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            var statement = "UPDATE games SET game=?, json=? WHERE id=?";
            executeUpdate(statement, gson.toJson(game), gson.toJson(newData), gameID);
            System.out.println("updated");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");

                        return new UserData(name, password, email);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("getUser");
            System.out.println(e.getMessage());
        }
        return null;
    }


    @Override
    public boolean foundUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        //for (UserData user : USERS) {
                            //if (Objects.equals(user.username(), username)) {
                                return true;
                            //}
                        //}
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("foundUser");
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public void createUser(UserData user) {
        try {
            if (foundUser(user.username())) {
                throw new DataAccessException("User already exists");
            }

            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

            String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            executeUpdate(statement, user.username(), hashedPassword, user.email());
        } catch (DataAccessException e) {
            System.out.println("createUser");
            System.out.println(e.getMessage());
        }
    }


    public static String getUsername(AuthData authData) throws SQLException {
        String username = "";
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.authToken());
                try (var rs = ps.executeQuery()) {
                    if (rs.next())
                    {
                        username = rs.getString("username");
                    }
                }
            }
        }
        catch (DataAccessException e)
        {
            System.out.println(e.getMessage());
        }
        return username;
    }

    public void clear()
    {
        var statements = new String[] { "TRUNCATE auth;", "TRUNCATE users;", "TRUNCATE games;" };
        try {
            for (String statement : statements)
            {
                executeUpdate(statement);
            }
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String readHashedPasswordFromDatabase(String username) {
        String hashedPassword = null;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    hashedPassword = resultSet.getString("password");
                }
            }
        } catch (SQLException e) {
            System.out.println("readPassword");
            System.out.println(e.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return hashedPassword;
    }

    public static boolean verifyUser(String username, String providedClearTextPassword) {
        String hashedPassword = readHashedPasswordFromDatabase(username);
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }
}
