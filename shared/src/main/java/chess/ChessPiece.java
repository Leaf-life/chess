package chess;

import javax.swing.plaf.ColorUIResource;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor color;
    private ChessPiece.PieceType type;
    private Collection<ChessMove> moves = new ArrayList<>();

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type && Objects.equals(moves, that.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type, moves);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    private boolean onBoard(int xPos, int yPos){
        if (xPos < 1 || yPos < 1){
            return false;
        } else if (xPos > 8 || yPos > 8) {
            return false;
        }
        else{
            return true;
        }
    }

    private boolean check(ChessBoard board, ChessPosition myPosition, int row, int col, ChessPiece.PieceType promo){
        if (onBoard(row, col)) {
            ChessPosition square = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(square);
            if (piece == null) {
                moves.add(new ChessMove(myPosition, square, promo));
                return true;
            } else if ((piece.getTeamColor() != color)) {
                moves.add(new ChessMove(myPosition, square, promo));
            }
        }
        return false;
    }

    private void rook(ChessBoard board, ChessPosition myPosition, int row, int col){
        boolean change = false;
        for (int i = -1; i < 2; i = i + 2) {
            do {
                boolean cont = true;
                int sum_r = 0;
                int sum_c = 0;
                while (cont) {
                    if (change) {
                        sum_r = sum_r + i;
                    } else {
                        sum_c = sum_c + i;
                    }
                    cont = check(board, myPosition, row + sum_r, col + sum_c,  null);
                }
                change = !change;
            } while (change);
        }
    }
    private void bishop(ChessBoard board, ChessPosition myPosition, int row, int col){
        for (int i = -1; i < 2; i = i + 2) {
            for (int j = -1; j < 2; j = j + 2) {
                boolean cont = true;
                int sum_r = 0;
                int sum_c = 0;
                while (cont) {
                    sum_r = sum_r + i;
                    sum_c = sum_c + j;
                    cont = check(board, myPosition, row + sum_r, col + sum_c, null);
                }
            }
        }
    }

    private void pawnCheck(ChessBoard board, ChessPosition myPosition, int row, int col, int direction, ChessPiece.PieceType promo){
        if (board.getPiece(new ChessPosition(row+direction, col)) == null) {
            check(board, myPosition, row + direction, col, promo);
        }
        for (int i = -1; i< 2; i = i + 2) {
            ChessPosition square = new ChessPosition(row+direction, col+i);
            if (onBoard(row + direction, col + i)) {
                ChessPiece piece = board.getPiece(square);
                if (piece != null) {
                    check(board, myPosition, row + direction, col + i, promo);
                }
            }
        }
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        switch(type){
            case KING: {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++){
                        check(board, myPosition, row + i, col + j, null);
                    }
                }
            }
                break;
            case QUEEN: {
                rook(board, myPosition, row, col);
                bishop(board, myPosition, row, col);
            }
                break;
            case ROOK: {
                rook(board, myPosition, row, col);
            }
                break;
            case BISHOP: {
                bishop(board, myPosition, row, col);
            }
                break;
            case KNIGHT: {
                for (int i = -1; i < 2; i = i + 2){
                    for (int j = -2; j < 3; j = j + 4) {
                        check(board, myPosition, row + j, col + i, null);
                        check(board, myPosition, row  +i, col + j, null);
                    }
                }
            }
                break;
            case PAWN: {
                boolean promotion = false;
                int direction = -1;
                if (color == ChessGame.TeamColor.WHITE){
                    direction = 1;
                }
                if ((row == 2 && direction == -1) || (row == 7 && direction == 1)){
                    promotion = true;
                } else if (row == 2 || row == 7) {
                    if ((board.getPiece(new ChessPosition(row + direction * 2, col)) == null) &&
                            (board.getPiece(new ChessPosition(row + direction, col)) == null)) {
                        check(board, myPosition, row + direction * 2, col, null);
                    }
                }
                if (promotion){
                    for(PieceType p: PieceType.values()){
                        if (p != PieceType.KING && p != PieceType.PAWN){
                            pawnCheck(board, myPosition, row, col, direction, p);
                        }
                    }
                }else{
                    pawnCheck(board, myPosition, row, col, direction, null);
                }


            }
                break;
        }
        return moves;
    }
}
