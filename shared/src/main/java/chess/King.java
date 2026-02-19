package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class King {

    public King(){}

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++){
                new PossibleMove().check(board, myPosition, row+i, col+j, null, moves, color);
            }
        }
        return moves;
    }
}
