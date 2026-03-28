package client;

import exception.ResponseException;
import model.GameData;
import server.ServerFacade;

import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.RESET;

public class GameClient {
    private final int gameID;
    private final String playColor;
    private final String authToken;
    private final ServerFacade server;

    public GameClient(String serverUrl, String playColor , int gameID, String authToken){
        this.gameID = gameID;
        this.playColor = playColor;
        this.authToken = authToken;
        server = new ServerFacade(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to Chess game. Sign in or register to start playing.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.println(getBoard());
            System.out.print("\n" + RESET + ">>> ");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.println(result);
                if (!result.equals(help()) && !result.equals("quit")){
                    String[] tokens = result.split(" ");
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String eval(String Line){
        return "";
    }

    public String printBoard(GameData game){

    }

    public String getBoard() throws ResponseException {
        Collection<GameData> games = server.listGames(authToken);
        GameData currentGame = null;
        for(GameData x: games){
            if (x.gameID() == gameID){
                currentGame = x;
            }
        }
        return printBoard(currentGame);
    }

    public String help(){
        return "";
    }
}
