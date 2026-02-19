package chess;

import java.util.Collection;

public class Knight {
    public Knight(){}

    public void knightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, ChessGame.TeamColor color){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i < 2; i = i + 2){
            for (int j = -2; j < 3; j = j + 4) {
                new PossibleMove().check(board, myPosition, row + j, col + i, null, moves, color);
                new PossibleMove().check(board, myPosition, row  +i, col + j, null, moves, color);
            }
        }
    }
}
