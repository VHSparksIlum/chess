package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.board = new ChessPiece[8][8];
        //initializeDefaultBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }

//    private void initializeDefaultBoard() {
//        // Pawns
//        for (int col = 1; col <= 8; col++) {
//            addPiece(new Position(2, col), new Pawn(ChessGame.TeamColor.WHITE));
//            addPiece(new Position(7, col), new Pawn(ChessGame.TeamColor.BLACK));
//        }
//
//        // Rooks
//        addPiece(new Position(1, 'a' - 'a' + 1), whiteQueenSideRook);
//        addPiece(new Position(1, 'h' - 'a' + 1), whiteKingSideRook);
//        addPiece(new Position(8, 'a' - 'a' + 1), blackQueenSideRook);
//        addPiece(new Position(8, 'h' - 'a' + 1), blackKingSideRook);
//
//        // Knights
//        addPiece(new Position(1, 'b' - 'a' + 1), new Knight(ChessGame.TeamColor.WHITE));
//        addPiece(new Position(1, 'g' - 'a' + 1), new Knight(ChessGame.TeamColor.WHITE));
//        addPiece(new Position(8, 'b' - 'a' + 1), new Knight(ChessGame.TeamColor.BLACK));
//        addPiece(new Position(8, 'g' - 'a' + 1), new Knight(ChessGame.TeamColor.BLACK));
//
//        // Bishops
//        addPiece(new Position(1, 'c' - 'a' + 1), new Bishop(ChessGame.TeamColor.WHITE));
//        addPiece(new Position(1, 'f' - 'a' + 1), new Bishop(ChessGame.TeamColor.WHITE));
//        addPiece(new Position(8, 'c' - 'a' + 1), new Bishop(ChessGame.TeamColor.BLACK));
//        addPiece(new Position(8, 'f' - 'a' + 1), new Bishop(ChessGame.TeamColor.BLACK));
//
//        // Queens
//        addPiece(new Position(1, 'd' - 'a' + 1), new Queen(ChessGame.TeamColor.WHITE));
//        addPiece(new Position(8, 'd' - 'a' + 1), new Queen(ChessGame.TeamColor.BLACK));
//
//        // Kings
//        addPiece(new Position(1, 'e' - 'a' + 1), new King(ChessGame.TeamColor.WHITE));
//        addPiece(new Position(8, 'e' - 'a' + 1), new King(ChessGame.TeamColor.BLACK));
//
//    }
}
