package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;
    private ChessPosition enPassantPosition;
    public boolean hasEnPassantBeenSet = false;

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
        initializeDefaultBoard(); //ISSUE IS BOARD IS NOT LOADING SCENARIOS
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
//         System.out.println("Adding " + piece.getPieceType() + " to position " + position);

        this.board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Removes a chess piece from the chessboard
     *
     * @param position where to add the piece to
     */
    public void removePiece(ChessPosition position) {
        //System.out.println("Removing piece at position " + position);

        this.board[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        // Debug statement to log information about the piece
//        ChessPiece piece = this.board[position.getRow() - 1][position.getColumn() - 1];
//        if (piece != null) {
//            System.out.println("Getting piece at position " + position + ": " + piece.getTeamColor() + " " + piece.getPieceType());
//        } else {
//            System.out.println("Getting piece at position " + position + ": null");
//        }

        return this.board[position.getRow() - 1][position.getColumn() - 1];
    }

    // Set the en passant position when a pawn moves two squares
    public void setEnPassantPosition(ChessPosition position) {
        enPassantPosition = position;
        hasEnPassantBeenSet = true;
    }

    public ChessPosition getEnPassantPosition() {
        return enPassantPosition;
    }

    public void clearEnPassantPosition() {
        enPassantPosition = null;
        hasEnPassantBeenSet = false;
    }

    public boolean hasEnPassantBeenSet() {
        return false;
    }

    /**
     * Promote a pawn to the specified piece type.
     *
     * @param position        The position of the pawn to be promoted.
     * @param promotionPiece  The piece type to promote the pawn to.
     * @throws IllegalArgumentException If the specified position does not contain a pawn or the promotion piece is not a valid promotion type.
     */
    public void promotePawn(ChessPosition position, ChessPiece.PieceType promotionPiece) {
        ChessPiece piece = getPiece(position);
        ChessPiece promotion = selectPromotion(promotionPiece);
        if (piece != null) {
            // Remove the pawn
            removePiece(position);

            // Add the promoted piece to the same position
            addPiece(position, promotion); // May need to modify based on how player will decide piece to promote to
        } else {
            throw new IllegalArgumentException("Invalid promotion: The specified position does not contain a pawn.");
        }
    }

    private ChessPiece selectPromotion(ChessPiece.PieceType promotionPiece) {
        if (promotionPiece == ChessPiece.PieceType.QUEEN) {
            throw new RuntimeException("QUEEN");
        } else if (promotionPiece == ChessPiece.PieceType.BISHOP) {
            throw new RuntimeException("BISHOP");
        } else if (promotionPiece == ChessPiece.PieceType.KNIGHT) {
            throw new RuntimeException("KNIGHT");
        } else if (promotionPiece == ChessPiece.PieceType.ROOK) {
            throw new RuntimeException("ROOK");
        } else {
            throw new RuntimeException("No promotion piece selected");
        }
    }

    public boolean isSquareAttacked(int row, int col, ChessGame.TeamColor attackingTeam, ChessBoard board) {

        // Ensure that the row and col are within the bounds of the chessboard
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }

        // Check if any opponent piece (of the attacking team's color) can attack the specified square
        for (int r = 1; r < 8; r++) {
            for (int c = 1; c < 8; c++) {
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                if (piece != null && piece.getTeamColor() == attackingTeam) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        if (isKingAttackingSquare(board, new ChessPosition(r, c), new ChessPosition(row, col))) {
                            return true;
                        }
                    } else {
                        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, new ChessPosition(r, c));
                        for (ChessMove move : pieceMoves) {
                            if (move.getEndPosition().getRow() == row && move.getEndPosition().getColumn()-1 == col) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // Helper function for checking is square is under attack
    private boolean isKingAttackingSquare(ChessBoard board, ChessPosition kingPosition, ChessPosition squarePosition) {
        // Get the row and column of the king
        int kingRow = kingPosition.getRow();
        int kingCol = kingPosition.getColumn();

        // Get the row and column of the square to be checked
        int targetRow = squarePosition.getRow();
        int targetCol = squarePosition.getColumn();

        // Define the eight possible directions a king can move
        int[] rowDirections = { -1, -1, -1, 0, 0, 1, 1, 1 };
        int[] colDirections = { -1, 0, 1, -1, 1, -1, 0, 1 };

        for (int i = 0; i < 8; i++) {
            int newRow = kingRow + rowDirections[i];
            int newCol = kingCol + colDirections[i];

            ChessPosition newPosition = new ChessPosition(newRow, newCol);

            // Check if the new position is on the chessboard
            if (board.isOnBoard(newRow, newCol) && newPosition.equals(squarePosition)) {
                // If the target square matches the specified square, the king can attack it
                return true;
            }
        }

        return false;
    }


//    Rook whiteKingSideRook = new Rook(ChessGame.TeamColor.WHITE);
//    boolean hasKingSideRookMovedWhite = false;
//
//    Rook whiteQueenSideRook = new Rook(ChessGame.TeamColor.WHITE);
//    boolean hasQueenSideRookMovedWhite = false;
//
//    Rook blackKingSideRook = new Rook(ChessGame.TeamColor.BLACK);
//    boolean hasKingSideRookMovedBlack = false;
//
//    Rook blackQueenSideRook = new Rook(ChessGame.TeamColor.BLACK);
//    boolean hasQueenSideRookMovedBlack = false;

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


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return hasEnPassantBeenSet == that.hasEnPassantBeenSet && Objects.deepEquals(board, that.board) && Objects.equals(enPassantPosition, that.enPassantPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(board), enPassantPosition, hasEnPassantBeenSet);
    }

    public String toString(ChessPosition position) {
        ChessPiece piece = getPiece(position);
        if (piece != null) {
            return "Piece at position " + position + ": " + piece.getTeamColor() + " " + piece.getPieceType();
        } else {
            return "No piece at position " + position;
        }
    }
}
