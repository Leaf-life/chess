package client;

import exception.ResponseException;
import server.ServerFacade;
import model.*;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
public class LoginClient {
    private final ServerFacade server;
    private final String serverURL;

    public LoginClient(String serverUrl){
        this.serverURL = serverUrl;
        server = new ServerFacade(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to Chess game. Sign in or register to start playing.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print("\n" + RESET + ">>> ");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.println(result);
                if (!result.equals(help()) && !result.equals("quit")){
                    String[] tokens = result.split(" ");
                    new PostLoginClient(serverURL, tokens[tokens.length - 1]).run();
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
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        return "help \n login \n register \n quit \n";
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            try{
                AuthData results =  server.login(params[0], params[1]);
                return String.format("You signed in as username: %s, authToken: %s", results.username(), results.authToken());
            } catch (ResponseException e) {
                throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: username, and password");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            try{
                AuthData result =  server.registration(new UserData(params[0], params[1], params[2]));
                return String.format("You registered as username: %s, authToken: %s", result.username(), result.authToken());
            } catch (ResponseException e) {
                throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: new username, new password, and email");
    }
}
