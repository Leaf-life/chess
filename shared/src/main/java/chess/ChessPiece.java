package chess;

import javax.swing.plaf.ColorUIResource;
import java.security.KeyStore;
import java.util.Collection;

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
                    Moves.add(new ChessMove(myPosition, myPosition, null));
            }
                break;
            case ROOK: {
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
                            if (onBoard(row + sum_r, col + sum_c) && onPiece(board, new ChessPosition(row + sum_r, col + sum_c))) {
                                Moves.add(new ChessMove(myPosition, new ChessPosition(row + sum_r, col + sum_c), null));
                            } else {
                                cont = false;
                            }
                            if (!cont(board, new ChessPosition(row + sum_r, col + sum_c))) {
                                cont = false;
                            }
                        }
                        change = !change;
                    }
                }
            }
                break;
            case BISHOP: {
                for (int i = -1; i < 2; i = i + 2) {
                    for (int j = -1; j < 2; j = j + 2) {
                        boolean cont = true;
                        int sum_r = 0;
                        int sum_c = 0;
                        while (cont) {
                            sum_r = sum_r + i;
                            sum_c = sum_c + j;
                            if (onBoard(row + sum_r, col + sum_c) && onPiece(board, new ChessPosition(row + sum_r, col + sum_c))) {
                                Moves.add(new ChessMove(myPosition, new ChessPosition(row + sum_r, col + sum_c), null));
                            } else {
                                cont = false;
                            }
                            if (!cont(board, new ChessPosition(row + sum_r, col + sum_c))) {
                                cont = false;
                            }
                        }
                }
                break;
            case KNIGHT: {
                for (int i = 9; i > 0; i--) {
                    Moves.add(new ChessMove(myPosition, myPosition, null));
                }
            }
                break;
            case PAWN: {
                for (int i = 9; i > 0; i--) {
                    Moves.add(new ChessMove(myPosition, myPosition, null));
                }
            }
                break;
        }
        return Moves;
    }
}
