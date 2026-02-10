package chess;

import javax.swing.plaf.ColorUIResource;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class PossibleMove {

    public PossibleMove(){

    }
    public boolean check(ChessBoard board, ChessPosition myPosition, int row, int col, ChessPiece.PieceType promo, Collection<ChessMove> moves, ChessGame.TeamColor color){
        if (onBoard(row, col)) {
            ChessPosition square = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(square);
            if (piece == null) {
                moves.add(new ChessMove(myPosition, square, promo));
                return true;
            } else if ((piece.getTeamColor() != color)) {
                moves.add(new ChessMove(myPosition, square, promo));
            }
        }
        return false;
    }

    private boolean onBoard(int xPos, int yPos){
        if (xPos < 1 || yPos < 1){
            return false;
        } else if (xPos > 8 || yPos > 8) {
            return false;
        }
        else{
            return true;
        }
    }
}
