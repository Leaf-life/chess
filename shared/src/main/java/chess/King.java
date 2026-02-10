package chess;

import java.util.Collection;
import java.util.Objects;

public class King {

    public King(){}

    public void kingMoves(ChessBoard board, ChessPosition myPosition, int row, int col,Collection<ChessMove> moves, ChessGame.TeamColor color){
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++){
                new PossibleMove().check(board, myPosition, row+i, col+j, null, moves, color);
            }
        }
    }
}
