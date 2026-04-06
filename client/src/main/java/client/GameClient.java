package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameClient {
    private final int gameID;
    private final String playColor;
    private final String authToken;
    private final ServerFacade server;
    private ChessGame game;

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

    public String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> getBoard();
                case "makemove" -> makeMove(params);
                case "showmoves" -> showMoves(params);
                case "leave" -> leave(params);
                case "resign" -> resign(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        return "makeMove \n showMoves \n leave \n resign \n";
    }

    public String makeMove(String... params){

    }

    public String showMoves(String... params){

    }

    public String leave(String... params){

    }

    public String resign(String... params){

    }

    public String getPieceSymbol(ChessPiece piece){
        if (piece == null){
            return EMPTY;
        }
        String symbol = "";
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
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

    public String printBoard(){
        ChessBoard board = game.getBoard();
        StringBuilder result = new StringBuilder();
        if (Objects.equals(playColor, "black")) {
            result.append("   " + " h  " + " g " +
                    " f  " + " e " + "  d " + " c " + "  b " + "  a\n");
            for (int x = 1; x <= 8; x++) {
                result.append(String.valueOf(x));
                for (int y = 1; y <= 8; y++) {
                    if (y%2 == 0 && x%2 == 0) {
                        result.append(SET_BG_COLOR_LIGHT_GREY);
                    } else if (y%2 == 1 && x%2 == 1) {
                        result.append(SET_BG_COLOR_LIGHT_GREY);
                    } else if (y%2 == 0 && x%2 == 1) {
                        result.append(SET_BG_COLOR_DARK_GREY);
                    } else {
                        result.append(SET_BG_COLOR_DARK_GREY);
                    }
                    ChessPiece piece = board.getPiece(new ChessPosition(x, y));
                    result.append(getPieceSymbol(piece));
                }
                result.append(RESET_BG_COLOR);
                result.append("\n");
            }
        } else {
            result.append("   " + " a  " + " b " +
                    " c  " + " d " + "  e " + " f " + "  g " + "  h\n");
            for (int x = 8; x >= 1; x--) {
                result.append(" ")
                        .append(String.valueOf(x))
                        .append(" ");
                for (int y = 8; y >= 1; y--) {
                    if (y%2 == 0 && x%2 == 0) {
                        result.append(SET_BG_COLOR_LIGHT_GREY);
                    } else if (y%2 == 1 && x%2 == 1) {
                        result.append(SET_BG_COLOR_LIGHT_GREY);
                    } else if (y%2 == 0 && x%2 == 1) {
                        result.append(SET_BG_COLOR_DARK_GREY);
                    } else {
                        result.append(SET_BG_COLOR_DARK_GREY);
                    }
                    ChessPiece piece = board.getPiece(new ChessPosition(x, y));
                    result.append(getPieceSymbol(piece));
                }
                result.append(RESET_BG_COLOR);
                result.append("\n");
            }
        }
        result.append(" ");
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
        game = currentGame.game();
        return printBoard();
    }
}
