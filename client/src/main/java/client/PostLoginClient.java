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
                System.out.println(result);
                String[] lineTokens = line.toLowerCase().split(" ");
                String[] tokens = result.split(" ");
                if (lineTokens[0].equals("joingame") && tokens[0].equals("Success")){
                    new GameClient(serverURL, lineTokens[1] , Integer.parseInt(lineTokens[2]), authToken).run();
                }
                else if (lineTokens[0].equals("observegame") && tokens[0].equals("Success")){
                    new GameClient(serverURL, "white" , Integer.parseInt(lineTokens[1]), authToken).run();
                }
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
                case "creategame" -> createGame(params);
                case "listgames" -> listGames(params);
                case "joingame" -> joinGame(params);
                case "observegame" -> observeGame(params);
                case "logout" -> logout(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        return "help \n logout \n createGame \n listGames \n joinGame \n observeGame \n";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            try{
                GameData result = server.createGame(authToken, params[0]);
                return String.format("You created game %s (ID: %s)", result.gameName(), result.gameID());
            } catch (ResponseException e) {
                throw new ResponseException(e.code(), e.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: gameName");
    }

    public String listGames(String... params) throws ResponseException {
        try {
            Collection<GameData> result = server.listGames(authToken);
            StringBuilder response = new StringBuilder("The games are:\n");
            for (GameData x : result) {
                response.append("Name: ")
                        .append(x.gameName())
                        .append(" ID: ")
                        .append(x.gameID().toString())
                        .append(" White Player: ")
                        .append(x.whiteUsername())
                        .append(" Black Player: ")
                        .append(x.blackUsername())
                        .append("\n");
            }
            return response.toString();
        } catch (ResponseException e) {
            throw new ResponseException(e.code(), e.getMessage());
        }
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            try{
                server.joinGame(authToken, params[0].toUpperCase(), Integer.parseInt(params[1]));
                return "Success You have joined the game";
            } catch (ResponseException e) {
                throw new ResponseException(e.code(), "id wrong or player color already taken");
            } catch (NumberFormatException e){
                throw new ResponseException(ResponseException.Code.ClientError, "need to give a game ID");
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: teamColor and gameId");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            try{
                server.observeGame(authToken, Integer.parseInt(params[0]));
                return "Success You are observing the game";
            } catch (ResponseException e) {
                throw new ResponseException(e.code(), "Id not found");
            } catch (NumberFormatException e){
                throw new ResponseException(ResponseException.Code.ClientError, "need to give a game ID");
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: gameId");
    }

    public String logout(String... params) throws ResponseException{
        try {
            server.logout(authToken);
            return "logout";
        } catch (ResponseException e) {
            throw new ResponseException(e.code(), e.getMessage());
        }
    }
}
