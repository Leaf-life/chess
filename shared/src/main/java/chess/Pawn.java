package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Pawn {
    PossibleMove pm = new PossibleMove();

    public Pawn(){}

    private void pawnCheck(ChessBoard board, ChessPosition myPosition, int row, int col, int direction, ChessPiece.PieceType promo, Collection<ChessMove> moves, ChessGame.TeamColor color){
        if (board.getPiece(new ChessPosition(row+direction, col)) == null) {
            pm.check(board, myPosition, row + direction, col, promo, moves, color);
        }
        for (int i = -1; i< 2; i = i + 2) {
            ChessPosition square = new ChessPosition(row+direction, col+i);
            if (pm.onBoard(row + direction, col + i)) {
                ChessPiece piece = board.getPiece(square);
                if (piece != null) {
                    pm.check(board, myPosition, row + direction, col + i, promo, moves, color);
                }
            }
        }
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
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
                pm.check(board, myPosition, row + direction * 2, col, null, moves, color);
            }
        }
        if (promotion){
            for(ChessPiece.PieceType p: ChessPiece.PieceType.values()){
                if (p != ChessPiece.PieceType.KING && p != ChessPiece.PieceType.PAWN){
                    pawnCheck(board, myPosition, row, col, direction, p, moves, color);
                }
            }
        }else{
            pawnCheck(board, myPosition, row, col, direction, null, moves, color);
        }
        return moves;
    }
}
