package client;

import exception.ResponseException;

import server.ServerFacade;
import model.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginClient {
    private final ServerFacade server;
    private final String serverURL;
    private final String authToken;

    public PostLoginClient(String serverUrl, String authToken){
        this.authToken = authToken;
        this.serverURL = serverUrl;
        server = new ServerFacade(serverUrl);
    }

    public void run(){
        System.out.println("You are signed in you may now start playing chess.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            System.out.print("\n" + RESET + ">>> ");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "createGame" -> createGame(params);
                case "listGames" -> listGames(params);
                case "joinGame" -> joinGame(params);
                case "observeGame" -> observeGame(params);
                case "logout" -> logout(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        return "help \n logout \n createGame \n listGame \n playGame \n observeGame \n";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            try{
                GameData result = server.createGame(params[0], params[1]);
                return String.format("You created game %s (ID: %s)", result.gameName(), result.gameID());
            } catch (ResponseException e) {
                throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: gameData");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length >= 1) {
            try{
                Collection<GameData> result = server.listGames(params[0]);
                StringBuilder response = new StringBuilder("The games are:\n");
                for (GameData x: result){
                    response.append("Name: ")
                            .append(x.gameName())
                            .append(" ID: ")
                            .append(x.gameID().toString())
                            .append("\n");
                }
                return response.toString();
            } catch (ResponseException e) {
                throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: gameData");
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length >= 3) {
            try{
                server.joinGame(params[0], params[1], Integer.parseInt(params[2]));
                return "You joined the game";
            } catch (ResponseException e) {
                throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: gameData");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            try{
                server.observeGame(params[0], Integer.parseInt(params[1]));
                return "You are observing the game";
            } catch (ResponseException e) {
                throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: gameData");
    }

    public String logout(String... params) throws ResponseException{
        try {
            server.logout(authToken);
            return "logout";
        } catch (ResponseException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }
}
