package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import model.*;
import request.*;
import result.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static server.ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
//        System.out.println(serverUrl);
        facade = new server.ServerFacade("http://localhost:" + port);
        try {
            facade.clear();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterEach
    void clearDB() throws ResponseException {facade.clear();}

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void register()
    {
        try {
            var authData = facade.register(new UserData("user", "password", "student@byu.edu"));
            assertTrue(authData.getAuthToken().length() > 10);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void registerBad()
    {
        try {
            facade.register(new UserData("user", "password", "student@byu.edu"));
            assertThrows(ResponseException.class, () -> facade.register(new UserData("user", "password", "student@byu.edu")));
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void login()
    {
        try {
            facade.register(new UserData("user", "password", "student@byu.edu"));

            facade.login(new LoginRequest("user", "password"));
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void loginBad()
    {
        try {
            facade.login(new LoginRequest("user", "notPassword"));
            assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("user", "notPassword")));
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }

    }


    @Test
    public void logout()
    {
        try {
            LoginResult res = facade.register(new UserData("user", "password", "student@byu.edu"));
            String authToken = res.getAuthToken();
            AuthData auth = new AuthData(authToken, res.getUsername());
            facade.logout(auth);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void logoutBad()
    {
        AuthData auth = new AuthData("bad", null);
        assertThrows(ResponseException.class, () -> facade.logout(auth));

    }

    @Test
    public void createGame()
    {
        try {
            LoginResult res = facade.register(new UserData("gameMaker", "newPlayer", "test@byu.edu"));
            String authToken = res.getAuthToken();
            facade.createGame(new CreateGameRequest(authToken, "first match"), new AuthData(authToken, res.getUsername()));
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void createGameBad()
    {

        assertThrows(ResponseException.class, () ->  facade.createGame(new CreateGameRequest("auth", "first Match"), new AuthData("bad", null)));
    }

    @Test
    public void listGames()
    {
        try {
            LoginResult res = facade.register(new UserData("user", "password", "test@byu.edu"));
            String authToken = res.getAuthToken();
            AuthData auth = new AuthData(authToken, res.getUsername());
            facade.listGames(auth);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void listGamesBad()
    {
        assertThrows(ResponseException.class, () ->  facade.listGames(new AuthData
                ("bad", null)));
    }

    @Test
    public void joinGame()
    {
        try {
            LoginResult res = facade.register(new UserData("bestChessPlayer", "magnus", "magnus@carlson.com"));
            String authToken = res.getAuthToken();
            AuthData auth = new AuthData(authToken, res.getUsername());
            facade.createGame(new CreateGameRequest(authToken, "pro match"), new AuthData(authToken, res.getUsername()));
            facade.joinGame(auth, new JoinGameRequest("WHITE", 1));
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void joinGameBad() throws ResponseException {
        LoginResult res = facade.register(new UserData("bestChessPlayer", "magnus", "magnus@carlson.com"));
        String authToken = res.getAuthToken();
        AuthData auth = new AuthData(authToken, res.getUsername());
        assertThrows(ResponseException.class, () ->  facade.joinGame(auth, new JoinGameRequest("not White", 5)));
    }

    @Test
    public void testClear() throws ResponseException {
        facade.clear();
    }

    @Test
    public void testClear2() throws ResponseException {
        facade.clear();
    }

}
