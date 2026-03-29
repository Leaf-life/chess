package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;

import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

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
            return EMPTY;
        }
        String symbol = "";
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            symbol = switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case ROOK -> WHITE_ROOK;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case PAWN -> WHITE_PAWN;
            };
        }else {
             symbol = switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case ROOK -> BLACK_ROOK;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case PAWN -> BLACK_PAWN;
            };
        }

        return symbol;
    }

    public String printBoard(ChessGame game){
        ChessBoard board = game.getBoard();
        StringBuilder result = new StringBuilder();
        if (Objects.equals(playColor, "black")) {
            for (int x = 1; x <= 8; x++) {
                result.append(" ");
                for (int i = 1; i <= 36; i++){
                    result.append(LONG_DASH);
                }
                result.append("\n");
                for (int y = 1; y <= 8; y++) {
                    ChessPiece piece = board.getPiece(new ChessPosition(x, y));
                    result.append("|");
                    result.append(getPieceSymbol(piece));
                }
                result.append("|\n");
            }
        } else {
            for (int x = 8; x >= 1; x--) {
                result.append(" ");
                for (int i = 1; i <= 36; i++){
                    result.append(LONG_DASH);
                }
                result.append("\n");
                for (int y = 8; y >= 1; y--) {
                    ChessPiece piece = board.getPiece(new ChessPosition(x, y));
                    result.append("|");
                    result.append(getPieceSymbol(piece));
                }
                result.append("|\n");
            }
        }
        result.append(" ");
        for (int i = 1; i <= 36; i++){
            result.append(LONG_DASH);
        }
        result.append("\n");
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
