package ui;

import java.util.Scanner;

import com.google.gson.Gson;
import websocket.messages.*;
import websocket.messages.ServerMessageHandler;

public class Repl implements ServerMessageHandler  {
    private final Client client;

    private int state;

    public Repl(String serverUrl) {
        client = new Client(serverUrl, this);
    }

    public void run() {
        System.out.println(EscapeSequences.SET_TEXT_BOLD +EscapeSequences.SET_BG_COLOR_LIGHT_GREY +
                EscapeSequences.SET_TEXT_COLOR_BLACK +" ♕ Welcome to 240 Chess Game "+EscapeSequences.BLACK_QUEEN);
        System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_MAGENTA + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            while (state == 0 && !result.equals("quit")) {
                printPrompt();
                String line = scanner.nextLine();

                try {
                    result = client.eval(line);
                    state = client.getState();
                    if (state == 1) {
                        client.setAuthData(client.getAuthData());
                        //client.setState(0);
                        //System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + client.eval("help"));
                    }
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_MAGENTA + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
            if (state == 1) {
                printPrompt();
                String line = scanner.nextLine();

                try {
                    result = client.eval(line);
                    state = client.getState();
                    if (state == 0) {
                        client.setState(1);
                        System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA + client.eval("help"));
                    }
                    if (state == 2) {
                        //client.setState(1);
                        client.setGameID(client.getGameID());
                        client.setAuthData(client.getAuthData());
                        client.eval("draw");
                    }
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_MAGENTA + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            } else if (state == 2) {
                printPrompt();
                String line = scanner.nextLine();
                System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_BLUE);

                try {
                    result = client.eval(line);
                    state = client.getState();
                    if (state == 0 || state == 1) {
                        client.setState(2);
                    }
                    System.out.print(result);
                    System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_BLUE);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_GREEN + "\n[" + this.identifyState(this.state) + "] " +
                EscapeSequences.SET_TEXT_COLOR_WHITE + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

    public String identifyState(int state)
    {
        if (state == 0)
        {
            return "SIGNED OUT";
        }
        else if (state == 1)
        {
            return "LOGGED IN";
        }
        else if (state == 2)
        {
            return "IN GAME";
        }
        return "ERROR";
    }

    @Override
    public void handle(String message) {
        ServerMessage sm = new Gson().fromJson(message, ServerMessage.class);
        switch (sm.getServerMessageType()) {
            case ERROR:
            {
                ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                System.out.println(errorMessage.getErrorMessage());
                break;
            }
            case NOTIFICATION: {
                Notification notification = new Gson().fromJson(message, Notification.class);
                System.out.println("\n" + EscapeSequences.SET_TEXT_COLOR_BLUE + notification.getMessage());
                printPrompt();
                break;
            }
            case LOAD_GAME: {
                LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                System.out.println(EscapeSequences.SET_BG_COLOR_WHITE + EscapeSequences.SET_TEXT_COLOR_BLACK + client.drawBoard(loadGameMessage.getGame()));
                System.out.println(EscapeSequences.SET_BG_COLOR_BLACK);
                printPrompt();
                break;
            }
        }
    }
}
