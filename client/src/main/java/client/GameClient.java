package client;

import chess.*;
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
    private Collection<ChessMove> moves;

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
            System.out.println(printBoard());
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
                case "redraw" -> printBoard();
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

    public String makeMove(String... params) throws ResponseException {
        getBoard();
        return null;
    }

    public String showMoves(String... params) throws ResponseException {
        if (params.length >= 2) {
            try{
                String pos = params[0];
                int y = Integer.parseInt(pos.substring(1));
                int x = 0;
                switch (pos.substring(0, 1).toLowerCase()){
                    case "a" -> x=1;
                    case "b" -> x=2;
                    case "c" -> x=3;
                    case "d" -> x=4;
                    case "e" -> x=5;
                    case "f" -> x=6;
                    case "g" -> x=7;
                    case "h" -> x=8;
                }
                System.out.println(x);
                System.out.println(y);
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(y, x));
                moves = piece.pieceMoves(game.getBoard(), new ChessPosition(y, x));

                return String.format("Your possible moves:\n" + printBoard());
            } catch (ResponseException e) {
                throw new ResponseException(e.code(), e.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: piece position");
    }

    public String leave(String... params){
        return null;
    }

    public String resign(String... params){
        return null;
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

    public String printBoard() throws ResponseException {
        getBoard();
        ChessBoard board = game.getBoard();
        StringBuilder result = new StringBuilder();
        if (Objects.equals(playColor, "black")) {
            result.append("   " + " h  " + " g " +
                    " f  " + " e " + "  d " + " c " + "  b " + "  a\n");
            for (int x = 1; x <= 8; x++) {
                result.append(String.valueOf(x));
                for (int y = 1; y <= 8; y++) {
                    boolean move = false;
                    if (moves != null) {
                        for (ChessMove m : moves) {
                            if (m.getEndPosition().equals(new ChessPosition(Math.abs(x-9), y))) {
                                result.append(SET_BG_COLOR_YELLOW);
                                move = true;
                            }
                        }
                    }
                    if (!move) {
                        if (y % 2 == 0 && x % 2 == 0) {
                            result.append(SET_BG_COLOR_LIGHT_GREY);
                        } else if (y % 2 == 1 && x % 2 == 1) {
                            result.append(SET_BG_COLOR_LIGHT_GREY);
                        } else if (y % 2 == 0 && x % 2 == 1) {
                            result.append(SET_BG_COLOR_DARK_GREY);
                        } else {
                            result.append(SET_BG_COLOR_DARK_GREY);
                        }
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
                    boolean move = false;
                    if (moves != null) {
                        for (ChessMove m : moves) {
                            if (m.getEndPosition().equals(new ChessPosition(Math.abs(x-9), Math.abs(y)))) {
                                result.append(SET_BG_COLOR_YELLOW);
                                move = true;
                            }
                        }
                    }
                    if (!move) {
                        if (y % 2 == 0 && x % 2 == 0) {
                            result.append(SET_BG_COLOR_LIGHT_GREY);
                        } else if (y % 2 == 1 && x % 2 == 1) {
                            result.append(SET_BG_COLOR_LIGHT_GREY);
                        } else if (y % 2 == 0 && x % 2 == 1) {
                            result.append(SET_BG_COLOR_DARK_GREY);
                        } else {
                            result.append(SET_BG_COLOR_DARK_GREY);
                        }
                    }
                    ChessPiece piece = board.getPiece(new ChessPosition(x, y));
                    result.append(getPieceSymbol(piece));
                }
                result.append(RESET_BG_COLOR);
                result.append("\n");
            }
        }
        result.append(" ");
        moves = null;
        return result.toString();
    }

    public void getBoard() throws ResponseException {
        Collection<GameData> games = server.listGames(authToken);
        GameData currentGame = null;
        for(GameData x: games){
            if (x.gameID() == gameID){
                currentGame = x;
            }
        }
        game = currentGame.game();
    }
}
