package chess;

import javax.swing.plaf.ColorUIResource;
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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch(Type){
            case KING: {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++){
                        if (i != 0 && j != 0 && myPosition.getRow() + i >= 0 && myPosition.getRow() + i < 8
                        && myPosition.getColumn() + 1 >= 0 && myPosition.getColumn() + 1 < 8) {
                            Moves.add(new ChessMove(myPosition,
                                    new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + 1),
                                    null));
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
                for (int i = 9; i > 0; i--) {
                    Moves.add(new ChessMove(myPosition, myPosition, null));
                }
            }
                break;
            case BISHOP: {
                for (int i = 9; i > 0; i--) {
                    Moves.add(new ChessMove(myPosition, myPosition, null));
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
