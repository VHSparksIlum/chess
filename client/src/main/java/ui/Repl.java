package ui;

import java.util.Scanner;

public class Repl {
    private final Client client;

    private int state = 0;

    public Repl(String serverUrl) {
        client = new Client(serverUrl);
    }

    public void run() {
        System.out.println(EscapeSequences.SET_TEXT_BOLD +EscapeSequences.SET_BG_COLOR_LIGHT_GREY +
                EscapeSequences.SET_TEXT_COLOR_BLACK +" ♕ Welcome to 240 Chess Game "+EscapeSequences.BLACK_QUEEN);
        System.out.println(EscapeSequences.RESET_BG_COLOR +EscapeSequences.SET_TEXT_COLOR_MAGENTA +EscapeSequences.RESET_TEXT_BOLD_FAINT);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            //=while (state == 0 && !result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            //}

            try {
                result = client.eval(line);
                state = client.getState();
                if (state == 1) {
                    client.setAuthData(client.getAuthData());
                    client.setState(0);
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + client.eval("help"));
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
                    client.setState(1);
                    client.setGameID(client.getGameID());
                    client.setAuthData(client.getAuthData());
                    //System.out.println(EscapeSequences.SET_BG_COLOR_WHITE + EscapeSequences.SET_TEXT_COLOR_BLACK + client.eval("draw"));
                }
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        else if (state == 2) {
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
}
