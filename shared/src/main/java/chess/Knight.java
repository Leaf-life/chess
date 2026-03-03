package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Knight {
    public Knight(){}

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i < 2; i = i + 2){
            for (int j = -2; j < 3; j = j + 4) {
                new PossibleMove(board, color).check(myPosition, row + j, col + i, null, moves);
                new PossibleMove(board, color).check(myPosition, row  +i, col + j, null, moves);
            }
        }
        return moves;
    }
}
