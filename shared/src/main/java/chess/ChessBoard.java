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
    private ChessPiece[][] board;

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    //SIMPLIFY
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //board[position.getRow()][position.getColumn()] = piece;
        int row = position.getRow(); // Use 1-based indexing
        int col = position.getColumn(); // Use 1-based indexing

         //Print a message to track the addition of a piece
//         System.out.println("Adding " + piece.getPieceType() + " to position " + position);

        this.board[row - 1][col - 1] = piece; // Add the piece to the board
    }

    //SIMPLIFY
    public void removePiece(ChessPosition position) {
        int row = position.getRow(); // Use 1-based indexing
        int col = position.getColumn(); // Use 1-based indexing
        this.board[row - 1][col - 1] = null; // Clear the position on the board

        //System.out.println("Removing piece at position " + position);

    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    //SIMPLIFY
    public ChessPiece getPiece(ChessPosition position) {
        //return board[position.getRow()][position.getColumn()];
        int row = position.getRow(); // Use 1-based indexing
        int col = position.getColumn(); // Use 1-based indexing
//        ChessPiece piece = this.board[row - 1][col - 1];

        // Debug statement to log information about the piece
//        if (piece != null) {
//            System.out.println("Getting piece at position " + position + ": " + piece.getTeamColor() + " " + piece.getPieceType());
//        } else {
//            System.out.println("Getting piece at position " + position + ": null");
//        }

        return this.board[row - 1][col - 1]; // Retrieve the piece from the board
    }

    /**
     * Checks whether the attempted piece move is on the board parameters
     * (Accounting for difference in array indices range)
     */
    public boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.board = new ChessPiece[8][8];
        initializeDefaultBoard();
    }

    private void initializeDefaultBoard() {
        // Pawns
        for (int col = 1; col <= 8; col++) { // Use 1-based indexing for columns
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        // Rooks
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK)); //whiteQueenSideRook);
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK)); //whiteKingSideRook);
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)); //blackQueenSideRook);
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)); //blackKingSideRook);

        // Knights
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));

        // Bishops
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));

        // Queens
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));

        // Kings
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));

//        System.out.println("Board state after initialization:");
//        printBoardState();
    }

    /**
     * Debug to verify board and piece initialization
     * (Layout of default chess board)
     */
    private void printBoardState() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = this.board[row][col];
                if (piece != null) {
                    System.out.println("Row " + (row + 1) + ", Col " + (col + 1) + ": " + piece.getTeamColor() + " " + piece.getPieceType());
                }
            }
        }
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


//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("ChessBoard{\n");
//        for (ChessPiece[] row : board) {
//            sb.append("  ");
//            for (ChessPiece piece : row) {
//                if (piece != null) {
//                    sb.append(piece.getPieceType()).append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "W" : "B").append(" ");
//                } else {
//                    sb.append("| ");
//                }
//            }
//            sb.append("\n");
//        }
//        sb.append("}");
//        return sb.toString();
//    }
}
