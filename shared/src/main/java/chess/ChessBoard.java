package chess;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] Board = new ChessPiece[8][8];

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(Board, that.Board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(Board);
    }

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (piece != null) {
            ChessPiece Piece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
            Board[position.getRow() - 1][position.getColumn() - 1] = Piece;
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return Board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    private void resetAddPiece(ChessGame.TeamColor c, ChessPiece.PieceType t, int x_pos, int y_pos){
        Board[y_pos][x_pos] = new ChessPiece(c, t);
    }

    public void resetBoard() {
        Board = new ChessPiece[8][8];
        for (ChessPiece.PieceType p : ChessPiece.PieceType.values()){
            switch (p){
                case ChessPiece.PieceType.KING: {
                    resetAddPiece(ChessGame.TeamColor.BLACK, p, 4, 7);
                    resetAddPiece(ChessGame.TeamColor.WHITE, p, 4, 0);
                }
                break;
                case ChessPiece.PieceType.QUEEN: {
                    resetAddPiece(ChessGame.TeamColor.BLACK, p, 3, 7);
                    resetAddPiece(ChessGame.TeamColor.WHITE, p, 3, 0);
                }
                break;
                case ChessPiece.PieceType.ROOK: {
                    resetAddPiece(ChessGame.TeamColor.BLACK, p, 0, 7);
                    resetAddPiece(ChessGame.TeamColor.BLACK, p, 7, 7);
                    resetAddPiece(ChessGame.TeamColor.WHITE, p, 0, 0);
                    resetAddPiece(ChessGame.TeamColor.WHITE, p, 7, 0);
                }
                break;
                case ChessPiece.PieceType.BISHOP: {
                    resetAddPiece(ChessGame.TeamColor.BLACK, p, 2, 7);
                    resetAddPiece(ChessGame.TeamColor.BLACK, p, 5, 7);
                    resetAddPiece(ChessGame.TeamColor.WHITE, p, 2, 0);
                    resetAddPiece(ChessGame.TeamColor.WHITE, p, 5, 0);
                }
                break;
                case ChessPiece.PieceType.KNIGHT: {
                    resetAddPiece(ChessGame.TeamColor.BLACK, p, 1, 7);
                    resetAddPiece(ChessGame.TeamColor.BLACK, p, 6, 7);
                    resetAddPiece(ChessGame.TeamColor.WHITE, p, 1, 0);
                    resetAddPiece(ChessGame.TeamColor.WHITE, p, 6, 0);
                }
                break;
                case ChessPiece.PieceType.PAWN: {
                    for (int i = 0; i < 8; i++){
                        resetAddPiece(ChessGame.TeamColor.BLACK, p, i, 6);
                        resetAddPiece(ChessGame.TeamColor.WHITE, p, i, 1);
                    }
                }
                break;
            }
        }
    }
}
