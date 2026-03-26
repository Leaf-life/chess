package client;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginClient {
    public PostLoginClient(String serverUrl){

    }

    public void run(){
        System.out.println("You are signed in you may now start playing chess.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            System.out.print("\n" + RESET + ">>> " + GREEN);
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(BLUE + result);
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
                case "playGame" -> playGame(params);
                case "observeGame" -> observeGame(params);
                case "logout" -> "logout";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        return "help \n logout \n createGame \n listGame \n playGame \n observeGame \n";
    }

    public String createGame(String... params){

    }

    public String listGames(String... params){

    }

    public String playGame(String... params){

    }

    public String observeGame(String... params){

    }
}
