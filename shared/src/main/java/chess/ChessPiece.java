package chess;

import javax.swing.plaf.ColorUIResource;
import java.security.KeyStore;
import java.util.Collection;
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
    private Collection<ChessMove> Moves;

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
        if (x_pos <= 0 || y_pos <= 0){
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
        if (onBoard(row + offset_r, col + offset_c) && onPiece(board, new ChessPosition(row + offset_r, col + offset_c))) {
            Moves.add(new ChessMove(myPosition, new ChessPosition(row + offset_r, col + offset_c), promo));
        } else {
            return false;
        }
        if (!cont(board, new ChessPosition(row + offset_r, col + offset_c))) {
            return false;
        }
        return true;
    }

    private void rook(ChessBoard board, ChessPosition myPosition, int row, int col){
        boolean change = false;
        for (int i = -1; i < 2; i = i + 2) {
            for (int j = -1; j < 2; j = j + 2) {
                boolean cont = true;
                int sum_r = 0;
                int sum_c = 0;
                while (cont) {
                    if (change){
                        sum_r = sum_r + i;
                    }else {
                        sum_c = sum_c + j;
                    }
                    cont = check(board, myPosition, row, col, sum_r, sum_c, null);
                }
                change = !change;
            }
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
                        if (i != 0 && j != 0 && onBoard(row + i, col + j) && onPiece(board, new ChessPosition(row + i, col + j))) {
                            Moves.add(new ChessMove(myPosition, new ChessPosition(row + i, col + j), null));
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
                    check(board, myPosition, row, col, -3, i, null);
                    check(board, myPosition, row, col, i, 3, null);
                }
            }
                break;
            case PAWN: {
                int direction = -1;
                if (Color == ChessGame.TeamColor.WHITE){
                    direction = 1;
                }
                boolean promotion = false;
                ChessPiece.PieceType promo = null;

                check(board, myPosition, row, col, direction, 0, promo);
                for (int i = -1; i< 2; i = i + 2) {
                    if (onPiece(board, new ChessPosition(row+1, col+i))){
                        check(board, myPosition, row, col, direction, i, promo);
                    }
                }
            }
                break;
        }
        return Moves;
    }
}
