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

    public boolean onBoard(int xPos, int yPos){
        if (xPos < 1 || yPos < 1){
            return false;
        } else if (xPos > 8 || yPos > 8) {
            return false;
        }
        else{
            return true;
        }
    }

    public void diagnol(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves, ChessGame.TeamColor color){
        for (int i = -1; i < 2; i = i + 2) {
            for (int j = -1; j < 2; j = j + 2) {
                boolean cont = true;
                int sumR = 0;
                int sumC = 0;
                while (cont) {
                    sumR = sumR + i;
                    sumC = sumC + j;
                    cont = check(board, myPosition, row + sumR, col + sumC, null, moves, color);
                }
            }
        }
    }

    public void orthoginal(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> moves, ChessGame.TeamColor color){
        boolean change = false;
        for (int i = -1; i < 2; i = i + 2) {
            do {
                boolean cont = true;
                int sumR = 0;
                int sumC = 0;
                while (cont) {
                    if (change) {
                        sumR = sumR + i;
                    } else {
                        sumC = sumC + i;
                    }
                    cont = check(board, myPosition, row + sumR, col + sumC,  null, moves, color);
                }
                change = !change;
            } while (change);
        }
    }
}
