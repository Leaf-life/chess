package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Queen {
    public Queen(){}

    public Collection<ChessMove>queenMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        new PossibleMove().orthoginal(board, myPosition, row, col, moves, color);
        new PossibleMove().diagnol(board, myPosition, row, col, moves, color);
        return moves;
    }
}
