package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
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

    public void run() throws ResponseException {
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
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String eval(String Line){
        return Line;
    }

    public String printBoard(ChessGame game){
        ChessBoard board = game.getBoard();
        StringBuilder result = new StringBuilder();
        for (int x = 1; x <= 8; x++){
            for (int y = 1; y<= 8; y++){
                ChessPiece piece = board.getPiece(new ChessPosition(x, y));
                if (piece != null) {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        switch (piece.getPieceType()) {
                            case KING -> result.append("|K");
                            case QUEEN -> result.append("|Q");
                            case ROOK -> result.append("|R");
                            case BISHOP -> result.append("|B");
                            case KNIGHT -> result.append("|N");
                            case PAWN -> result.append("|P");
                            default -> result.append("| ");
                        }
                    } else{
                        switch (piece.getPieceType()) {
                            case KING -> result.append("|k");
                            case QUEEN -> result.append("|q");
                            case ROOK -> result.append("|r");
                            case BISHOP -> result.append("|b");
                            case KNIGHT -> result.append("|n");
                            case PAWN -> result.append("|p");
                            default -> result.append("| ");
                    }
                }
                result.append("| ");
            }
            result.append("|\n");
        }
        return result.toString();
    }

    public String getBoard() throws ResponseException {
        Collection<GameData> games = server.listGames(authToken);
        GameData currentGame = null;
        for(GameData x: games){
            if (x.gameID() == gameID){
                currentGame = x;
            }
        }
        return printBoard(currentGame.game());
    }

    public String help(){
        return "";
    }
}
