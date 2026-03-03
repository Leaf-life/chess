package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Pawn {

    ChessBoard board;
    ChessGame.TeamColor color;

    public Pawn(ChessBoard board, ChessGame.TeamColor color){
        this.board = board;
        this.color = color;
    }

    private void pawnCheck(ChessPosition myPosition, int row, int col, int direction, ChessPiece.PieceType promo, Collection<ChessMove> moves){
        PossibleMove pm = new PossibleMove(board, color);
        if (board.getPiece(new ChessPosition(row+direction, col)) == null) {
            pm.check(myPosition, row + direction, col, promo, moves);
        }
        for (int i = -1; i< 2; i = i + 2) {
            ChessPosition square = new ChessPosition(row+direction, col+i);
            if (pm.onBoard(row + direction, col + i)) {
                ChessPiece piece = board.getPiece(square);
                if (piece != null) {
                    pm.check(myPosition, row + direction, col + i, promo, moves);
                }
            }
        }
    }

    public Collection<ChessMove> pawnMoves(ChessPosition myPosition){
        PossibleMove pm = new PossibleMove(board, color);
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
                pm.check(myPosition, row + direction * 2, col, null, moves);
            }
        }
        if (promotion){
            for(ChessPiece.PieceType p: ChessPiece.PieceType.values()){
                if (p != ChessPiece.PieceType.KING && p != ChessPiece.PieceType.PAWN){
                    pawnCheck(myPosition, row, col, direction, p, moves);
                }
            }
        }else{
            pawnCheck(myPosition, row, col, direction, null, moves);
        }
        return moves;
    }
}
