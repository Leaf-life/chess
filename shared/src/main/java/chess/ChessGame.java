package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessGame.TeamColor team;
    private ChessBoard board = new ChessBoard();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return team == chessGame.team && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, board);
    }

    public ChessGame() {
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public void Move(ChessBoard board, ChessPiece piece, ChessMove move){
        ChessPiece.PieceType promo = move.getPromotionPiece();
        if (promo != null && piece.getPieceType() == ChessPiece.PieceType.PAWN){
            piece = new ChessPiece(piece.getTeamColor(), promo);
        }
        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);
    }

    public ChessBoard pretendMove(ChessMove move, ChessPiece piece) {
        ChessPiece[][] tBoard = new ChessPiece[board.Board.length][];
        for (int i = 0; i < board.Board.length; i++) {
            tBoard[i] = Arrays.copyOf(board.Board[i], board.Board.length);
        }
        ;
        ChessBoard testBoard = new ChessBoard();
        testBoard.setBoard(tBoard);
        Move(testBoard, piece, move);
        return testBoard;
    }
    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return new ArrayList<ChessMove>();
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Iterator<ChessMove> m = possibleMoves.iterator();
        ChessGame checkGame = new ChessGame();
        while (m.hasNext()) {
            ChessBoard testBoard = pretendMove(m.next(), piece);
            checkGame.setBoard(testBoard);
            if (checkGame.isInCheck(piece.getTeamColor())) {
                m.remove();
            }
        }
        return possibleMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null || piece.getTeamColor() != team){
            throw new InvalidMoveException();
        }
        Collection<ChessMove> vMoves = validMoves(startPosition);
        for (ChessMove m: vMoves){
            if (endPosition.equals(m.getEndPosition())){
                Move(board, piece, move);
                if (team == TeamColor.WHITE){
                    setTeamTurn(TeamColor.BLACK);
                }else{
                    setTeamTurn(TeamColor.WHITE);
                }
                return;
            }
        }
        throw new InvalidMoveException();
    }

    private ArrayList<ChessPosition> findPiece (ChessPiece.PieceType type, TeamColor color){
        ArrayList<ChessPosition> pieces = new ArrayList<>();
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition checkPosition = new ChessPosition(i, j);
                ChessPiece checkPiece = board.getPiece(checkPosition);
                if (checkPiece != null) {
                    if (checkPiece.getPieceType().equals(type) && checkPiece.getTeamColor().equals(color)) {
                        pieces.add(checkPosition);
                    }
                }
            }
        }
        return pieces;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findPiece(ChessPiece.PieceType.KING, teamColor).getFirst();
        for (ChessPiece.PieceType x: ChessPiece.PieceType.values()){
            chess.ChessGame.TeamColor checkColor = TeamColor.WHITE;
            if (teamColor == TeamColor.WHITE) {
                checkColor = TeamColor.BLACK;
            }
            ArrayList<ChessPosition> checkMoves = findPiece(x, checkColor);
            for (ChessPosition p : checkMoves) {
                ChessPiece checkPiece = board.getPiece(p);
                Collection<ChessMove> moves = checkPiece.pieceMoves(board, p);
                for (ChessMove m : moves) {
                    if (kingPos.equals(m.getEndPosition())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && isInStalemate(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPos = findPiece(ChessPiece.PieceType.KING, teamColor).getFirst();
        ChessPiece king = board.getPiece(kingPos);
        Collection<ChessMove> possibleMoves = validMoves(kingPos);
        return possibleMoves.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
