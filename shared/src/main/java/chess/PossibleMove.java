package chess;

import javax.swing.plaf.ColorUIResource;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class PossibleMove {

    ChessBoard board;
    ChessGame.TeamColor color;

    public PossibleMove(ChessBoard board, ChessGame.TeamColor color){
        this.board = board;
        this.color = color;
    }

    public boolean check(ChessPosition myPosition, int row, int col, ChessPiece.PieceType promo, Collection<ChessMove> moves){
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

    public void diagnol(ChessPosition myPosition, int row, int col, Collection<ChessMove> moves){
        for (int i = -1; i < 2; i = i + 2) {
            for (int j = -1; j < 2; j = j + 2) {
                boolean cont = true;
                int sumR = 0;
                int sumC = 0;
                while (cont) {
                    sumR = sumR + i;
                    sumC = sumC + j;
                    cont = check(myPosition, row + sumR, col + sumC, null, moves);
                }
            }
        }
    }

    public void orthoginal(ChessPosition myPosition, int row, int col, Collection<ChessMove> moves){
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
                    cont = check(myPosition, row + sumR, col + sumC,  null, moves);
                }
                change = !change;
            } while (change);
        }
    }
}
