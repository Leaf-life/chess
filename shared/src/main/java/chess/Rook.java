package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rook {

    public Rook(){}

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        new PossibleMove().orthoginal(board, myPosition, row, col, moves, color);
        return moves;
    }
}
