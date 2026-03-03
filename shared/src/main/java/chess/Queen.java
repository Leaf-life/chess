package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Queen {
    public Queen(){}

    public Collection<ChessMove>queenMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        new PossibleMove(board, color).orthoginal(myPosition, row, col, moves);
        new PossibleMove(board, color).diagnol(myPosition, row, col, moves);
        return moves;
    }
}
