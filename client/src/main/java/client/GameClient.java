package client;

import chess.*;
import websocket.*;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import websocket.messages.*;

import javax.management.Notification;
import java.util.*;

import static ui.EscapeSequences.*;

public class GameClient implements ServerMessageHandler {
    private final int gameID;
    private final String playColor;
    private final String authToken;
    private final ServerFacade server;
    private final WebSocketFacade ws;
    public ChessGame game;
    private Collection<ChessMove> moves;

    public GameClient(String serverUrl, String playColor , int gameID, String authToken) throws ResponseException {
        this.gameID = gameID;
        this.playColor = playColor;
        this.authToken = authToken;
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() throws ResponseException {
        //getBoard();
        ws.connect(authToken, gameID);
        System.out.println("Welcome to Chess game: " + gameID);
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            //System.out.println(printBoard());
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

    public int posConverter(String pos){
        int x = -1;
        switch (pos.substring(0, 1).toLowerCase()) {
            case "a" -> x = 8;
            case "b" -> x = 7;
            case "c" -> x = 6;
            case "d" -> x = 5;
            case "e" -> x = 4;
            case "f" -> x = 3;
            case "g" -> x = 2;
            case "h" -> x = 1;
        }
        if (Objects.equals(playColor, ChessGame.TeamColor.BLACK.toString())){
            x = Math.abs(x-9);
        }
        return x;
    }

    public ChessPiece.PieceType getPromotion(String piece){
        switch (piece){
            case "pawn" -> {
                return ChessPiece.PieceType.PAWN;
            }
            case "bishop" -> {
                return ChessPiece.PieceType.BISHOP;
            }
            case "rock" -> {
                return ChessPiece.PieceType.ROOK;
            }
            case "knight" -> {
                return ChessPiece.PieceType.KNIGHT;
            }
            case "queen" -> {
                return ChessPiece.PieceType.QUEEN;
            }
            case "king" -> {
                return ChessPiece.PieceType.KING;
            }
            default -> {
                return null;
            }
        }
    }

    public String makeMove(String... params) throws ResponseException {
        if (params.length >= 3) {
            try {
                //getBoard();
                String startPos = params[0];
                String endPos = params[1];
                String promotion = params[2];
                int startY = Integer.parseInt(startPos.substring(1));
                int startX = posConverter(startPos.substring(0, 1).toLowerCase());
                int endY = Integer.parseInt(endPos.substring(1));
                int endX = posConverter(endPos.substring(0, 1).toLowerCase());
                ChessPosition startChessPos = new ChessPosition(startY, startX);
                ChessPosition endChessPos = new ChessPosition(endY, endX);
                ChessMove move = new ChessMove(startChessPos, endChessPos, getPromotion(promotion.toLowerCase()));
                ws.makeMove(authToken, gameID, move);
                getBoard();
                return "Move made";
            } catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: piece position, end position, and promotion piece (None is doesnt exists)");
    }

    public String showMoves(String... params) throws ResponseException {
        try {
            if (params.length >= 1) {
                String pos = params[0];
                int y = Integer.parseInt(pos.substring(1));
                int x = posConverter(pos.substring(0,1));
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(y, x));
                moves = piece.pieceMoves(game.getBoard(), new ChessPosition(y, x));

                return "Your possible moves:\n" + printBoard();
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, "bad input");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: piece position");
    }

    public String leave(String... params) throws ResponseException {
        ws.leave(authToken, gameID);
        return "quit";
    }

    public String resign(String... params) throws ResponseException {
        boolean resignedCheck = true;
        while (resignedCheck){
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n are you sure (y/n)");
            String line = scanner.nextLine();
            if (line.equals("y")){
                resignedCheck = false;
            } else{
                return "cancled resigned";
            }
        }
        ws.resign(authToken, gameID);
        return "You have resigned";
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
        //getBoard();
        ChessBoard board = game.getBoard();
        StringBuilder result = new StringBuilder();
        if (Objects.equals(playColor, "black")) {
            result.append(" " + " h  " + " g " +
                    " f  " + " e " + "  d " + " c " + "  b " + "  a\n");
            for (int x = 1; x <= 8; x++) {
                result.append(String.valueOf(x));
                for (int y = 1; y <= 8; y++) {
                    boolean move = false;
                    if (moves != null) {
                        for (ChessMove m : moves) {
                            if (m.getEndPosition().equals(new ChessPosition(x, y))) {
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
                            if (m.getEndPosition().equals(new ChessPosition(x, y))) {
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

    @Override
    public void notificationMessage(NotificationMessage notification) {
        System.out.println(notification.getMessage());
    }

    @Override
    public void errorMessage(ErrorMessage notification) {
        System.out.println(notification.getErrorMessage());
    }

    @Override
    public void loadGameMessage(LoadGameMessage notification) throws ResponseException {
        game = notification.getGame();
        System.out.println("\n" + printBoard());
    }
}
