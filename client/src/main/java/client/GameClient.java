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

    public String eval(String line){
        return line;
    }



    public String getPieceSymbol(ChessPiece piece){
        if (piece == null){
            return " ";
        }

        String symbol = switch (piece.getPieceType()) {
            case KING -> "k";
            case QUEEN -> "q";
            case ROOK -> "r";
            case BISHOP -> "b";
            case KNIGHT -> "n";
            case PAWN -> "p";
        };

        return piece.getTeamColor() == ChessGame.TeamColor.WHITE
                ? symbol.toUpperCase()
                : symbol;
    }

    public String printBoard(ChessGame game){
        ChessBoard board = game.getBoard();
        StringBuilder result = new StringBuilder();
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ChessPiece piece = board.getPiece(new ChessPosition(x, y));
                result.append("|");
                result.append(getPieceSymbol(piece));
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
