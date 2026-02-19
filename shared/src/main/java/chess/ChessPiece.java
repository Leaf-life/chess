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
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
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
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        switch(type){
            case KING: {
                return new King().kingMoves(board, myPosition, color);
            }
            case QUEEN: {
                new PossibleMove().orthoginal(board, myPosition, row, col, moves, color);
                new PossibleMove().diagnol(board, myPosition, row, col, moves, color);
            }
                break;
            case ROOK: {
                new PossibleMove().orthoginal(board, myPosition, row, col, moves, color);
            }
                break;
            case BISHOP: {
                new PossibleMove().diagnol(board, myPosition, row, col, moves, color);
            }
                break;
            case KNIGHT: {
                return new Knight().knightMoves(board, myPosition, color);
            }
            case PAWN: {
                return new Pawn().pawnMoves(board, myPosition, color);
            }
        }
        return moves;
    }
}
