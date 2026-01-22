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

    private ChessGame.TeamColor Color;
    private ChessPiece.PieceType Type;
    private Collection<ChessMove> Moves = new ArrayList<>();

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        Color = pieceColor;
        Type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return Color == that.Color && Type == that.Type && Objects.equals(Moves, that.Moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Color, Type, Moves);
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
        return Color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return Type;
    }

    private boolean onBoard(int x_pos, int y_pos){
        if (x_pos < 1 || y_pos < 1){
            return false;
        } else if (x_pos > 8 || y_pos > 8) {
            return false;
        }
        else{
            return true;
        }
    }

    private boolean onPiece(ChessBoard board, ChessPosition position){
        ChessPiece p = board.getPiece(position);
        if (p != null){
            return Color != p.getTeamColor();
        }
        return true;
    }
    private boolean cont(ChessBoard board, ChessPosition position){
        ChessPiece p = board.getPiece(position);
        if (p != null){
            return Color == p.getTeamColor();
        }
        return true;
    }

    private boolean check(ChessBoard board, ChessPosition myPosition, int row, int col, int offset_r, int offset_c, ChessPiece.PieceType promo){
        if (onBoard(row + offset_r, col + offset_c)) {
            if (onPiece(board, new ChessPosition(row + offset_r, col + offset_c))) {
                Moves.add(new ChessMove(myPosition, new ChessPosition(row + offset_r, col + offset_c), promo));
            } else {
                return false;
            }
            if (!cont(board, new ChessPosition(row + offset_r, col + offset_c))) {
                return false;
            }
            return true;
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
                    cont = check(board, myPosition, row, col, sum_r, sum_c, null);
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
                    cont = check(board, myPosition, row, col, sum_r, sum_c, null);
                }
            }
        }
    }

    private void pawnCheck(ChessBoard board, ChessPosition myPosition, int row, int col, int direction, ChessPiece.PieceType promo){
        if (board.getPiece(new ChessPosition(row+direction, col)) == null) {
            check(board, myPosition, row, col, direction, 0, promo);
        }
        for (int i = -1; i< 2; i = i + 2) {
            ChessPosition position = new ChessPosition(row+direction, col+i);
            if (onBoard(row+direction, col+i) && onPiece(board, position) && board.getPiece(position) != null){
                check(board, myPosition, row, col, direction, i, promo);
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
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        switch(Type){
            case KING: {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++){
                        if (i != 0 || j != 0) {
                            if (onBoard(row + i, col + j) && onPiece(board, new ChessPosition(row + i, col + j))) {
                                Moves.add(new ChessMove(myPosition, new ChessPosition(row + i, col + j), null));
                            }
                        }
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
                    check(board, myPosition, row, col, -2, i, null);
                    check(board, myPosition, row, col, 2, i, null);
                    check(board, myPosition, row, col, i, 2, null);
                    check(board, myPosition, row, col, i, -2, null);
                }
            }
                break;
            case PAWN: {
                boolean promotion = false;
                int direction = -1;
                if (Color == ChessGame.TeamColor.WHITE){
                    direction = 1;
                }
                if ((row == 2 && direction == -1) || (row == 7 && direction == 1)){
                    promotion = true;
                } else if ((row == 2 && direction == 1) || (row == 7 && direction == -1)) {
                    if ((board.getPiece(new ChessPosition(row + (direction*2), col)) == null) &&
                            (board.getPiece(new ChessPosition(row + (direction), col)) == null)) {
                        check(board, myPosition, row, col, direction * 2, 0, null);
                    }
                }
                if (promotion){
                    for(PieceType p: PieceType.values()){
                        if (p != PieceType.PAWN && p != PieceType.KING){
                            pawnCheck(board, myPosition, row, col, direction, p);
                        }
                    }
                }else{
                    pawnCheck(board, myPosition, row, col, direction, null);
                }


            }
                break;
        }
        return Moves;
    }
}
