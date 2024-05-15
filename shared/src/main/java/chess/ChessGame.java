package chess;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private TeamColor oppositeTeamColor;

    public ChessGame(TeamColor teamTurn) {
        this.board = new ChessBoard();
        this.teamTurn = teamTurn;
        this.oppositeTeamColor = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public ChessGame() {
        this.board = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        setTeamTurn(teamTurn);
        board.resetBoard(); //allows board to be set for junit tests that need new board
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
        oppositeTeamColor = (team == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // Check if there is a piece at the specified position and if it's the correct team's turn
        ChessPiece piece = this.board.getPiece(startPosition);
        if (piece == null) {
            return Collections.emptyList(); // Return an empty collection if no valid moves are possible
        }

        // Calculate and return valid moves for the piece
        Collection<ChessMove> validMoves = new ArrayList<>(piece.pieceMoves(this.board, startPosition));

        // Filter out en passant moves if any piece has moved since en passant position was set
        if (board.hasTeamMoved(teamTurn)) {
            validMoves.removeIf(move -> isEnPassantMove(move, piece));
        }
        // Filter out castling moves if king or rook have moved
        validMoves.removeIf(move -> {
            // Check if it's a castling move
            if (isCastlingMove(move, piece)) {
                // Get the relevant rook for this castling move
                ChessPiece rook = getRookForCastling(move, piece, board);

                // Check if the king or rook has moved
                return (piece.getPieceType() == ChessPiece.PieceType.KING && piece.hasMoved()) || (rook != null && rook.hasMoved());
            }
            return false;
        });

        // Filter out moves that would put the king in check
        validMoves.removeIf(move -> {
            // Execute the move temporarily
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece capturedPiece = this.board.getPiece(endPosition);
            this.board.removePiece(startPosition);
            this.board.addPiece(endPosition, piece);

            // Check if the move puts the king in check
            boolean putsKingInCheck = isInCheck(piece.getTeamColor());

            // Rollback the move
            this.board.removePiece(endPosition);
            this.board.addPiece(startPosition, piece);
            if (capturedPiece != null) {
                this.board.addPiece(endPosition, capturedPiece);
            }

            return putsKingInCheck;
        });
        //System.out.println(validMoves);
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Check if there is a piece at the specified start position
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            throw new InvalidMoveException("No piece at the specified start position.");
        }

        // Check if it's the correct team's turn
        if (piece.getTeamColor() == oppositeTeamColor) {
            throw new InvalidMoveException("Not the correct team's turn.");
        }

        if (board.hasEnPassantBeenSet()) {
            board.clearEnPassantPosition();
        }

        // Calculate valid moves for the piece
        Collection<ChessMove> validMoves = new ArrayList<>(piece.pieceMoves(board, startPosition));

        // Check if the specified move is a valid move for the piece
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("The specified move is not a valid move for the piece.");
        }

        // Check if the move is a castling move
        if (isCastlingMove(move, piece)) {
            // Perform castling and update the board
            performCastling(move, piece, getRookForCastling(move, piece, board));
        }

        // Execute the move
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece capturedPiece = board.getPiece(endPosition);

        // Update the board
        board.removePiece(startPosition);
        board.addPiece(endPosition, piece);

            // Check if the move is a one-square move for a pawn
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (endPosition.getRow() == 3 || endPosition.getRow() == 6)) {
                piece.setHasMoved(true);
            }

            // Check if the move is an initial two-square move for a pawn
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (endPosition.getRow() == 4 || endPosition.getRow() == 5)) {
                piece.setHasMoved(true);
            }

            if (piece.getPieceType() == ChessPiece.PieceType.KING || piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                piece.setHasMoved(true);
            }

        // Check if any piece has moved (excluding pawns)
        if (!(piece.getPieceType() == ChessPiece.PieceType.PAWN && Math.abs(startPosition.getRow() - endPosition.getRow()) == 2) && piece.getTeamColor() == TeamColor.WHITE) {
            board.whiteHasMovedPawn = true;
        } else {
            board.blackHasMovedPawn = false;
        }

//            // When an opponent's pawn moves two squares forward, set the en passant position.
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN && Math.abs(startPosition.getRow() - endPosition.getRow()) == 2) {
                ChessPosition enPassantPosition = new ChessPosition((startPosition.getRow() + endPosition.getRow()) / 2, startPosition.getColumn());
                board.setEnPassantPosition(enPassantPosition);
            }

//            // Check if the move is en passant
            if (isEnPassantMove(move, piece)) {
                handleEnPassantCapture(move);
            }

        // Check if the move puts the current player's king in check
            if (isInCheck(teamTurn)) {
                // If the move puts the player's king in check, it's an invalid move
                // Roll back the move
                board.removePiece(endPosition);
                board.addPiece(startPosition, piece);
                if (capturedPiece != null) {
                    board.addPiece(endPosition, capturedPiece);
                }

                throw new InvalidMoveException("The move puts your king in check.");
            }

            // Check if the moved piece is a pawn that has reached the promotion rank
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (endPosition.getRow() == 1 || endPosition.getRow() == 8)) {
                // The chosen piece type should be derived from the Move object
                ChessPiece.PieceType chosenPieceType = move.getPromotionPiece();
                handlePawnPromotion(endPosition, chosenPieceType);
            }

//        System.out.println("Moving " + piece.getPieceType() + " to position: " + move);
        // Move is valid; update the team turn
        setTeamTurn(teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Find the position of the king of the specified team
        ChessPosition kingPosition = findKingPosition(teamColor);

//        if (kingPosition == null) {
//            // King not found, something is wrong with the board setup
//            throw new IllegalStateException("King not found on the board.");
//        }

        // Check if any opponent's piece can attack the king's position
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, currentPosition);
                    for (ChessMove move: moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
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
        if (!isInCheck(teamColor)) {
            return false;
        }

        // Iterate through all pieces of the specified team and check if any of their moves can remove the check
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, currentPosition);
                    for (ChessMove move : moves) {
                        // Try making each move and check if the king is still in check
                        ChessPosition originalPosition = move.getStartPosition();
                        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());

                        // Execute the move
                        board.removePiece(originalPosition);
                        board.addPiece(move.getEndPosition(), piece);

                        // Check if the move removes the check
                        boolean stillInCheck = isInCheck(teamColor);

                        // Rollback the move
                        board.removePiece(move.getEndPosition());
                        board.addPiece(originalPosition, piece);
                        if (capturedPiece != null) {
                            board.addPiece(move.getEndPosition(), capturedPiece);
                        }

                        // If the move removes the check, it's not checkmate
                        if (!stillInCheck) {
                            return false;
                        }
                    }
                }
            }
        }

        // If no move can remove the check, it's checkmate
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        // Iterate through all pieces of the specified team
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, currentPosition);

                    // Check if any move is legal (doesn't put the king in check)
                    for (ChessMove move : moves) {
                        ChessPosition originalPosition = move.getStartPosition();
                        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());

                        // Execute the move
                        board.removePiece(originalPosition);
                        board.addPiece(move.getEndPosition(), piece);

                        // Check if the move is legal
                        boolean legalMove = !isInCheck(teamColor);

                        // Rollback the move
                        board.removePiece(move.getEndPosition());
                        board.addPiece(originalPosition, piece);
                        if (capturedPiece != null) {
                            board.addPiece(move.getEndPosition(), capturedPiece);
                        }

                        // If at least one legal move exists, it's not a stalemate
                        if (legalMove) {
                            return false;
                        }
                    }
                }
            }
        }

        // If no legal moves exist, it's a stalemate
        return true;
    }

    // Helper method to find the position of the king of a specified team
    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    //System.out.println(currentPosition);
                    return currentPosition;
                }
            }
        }
        return null; // King not found
    }

    private void handlePawnPromotion(ChessPosition position, ChessPiece.PieceType chosenPieceType) {
        board.promotePawn(position, chosenPieceType);
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

//  EXTRA CREDIT MOVES
    // Function to check if a move is a castling move
    private boolean isCastlingMove(ChessMove move, ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            // King-side castling
            if (Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) == 2 || Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) == 3) {
                int direction = (move.getEndPosition().getColumn() > move.getStartPosition().getColumn()) ? 1 : -1;
                int row = move.getEndPosition().getRow();
                int startCol = move.getStartPosition().getColumn();
                int targetCol = move.getEndPosition().getColumn();

                if (direction == 1) {
                    // Check if squares between king and rook are empty (king-side)
                    for (int col = startCol + 1; col < targetCol; col++) {
                        if (board.getPiece(new ChessPosition(row, col)) != null) {
                            return false; // Invalid castling
                        }
                    }
                } else {
                    // Check if squares between king and rook are empty (queen-side)
                    for (int col = startCol - 1; col > targetCol; col--) {
                        if (board.getPiece(new ChessPosition(row, col)) != null) {
                            return false; // Invalid castling
                        }
                    }
                }

                //System.out.println(move + " move is a valid castling move: ");
                return true; // Valid castling move
            }
        }
        //System.out.println(move + " move is NOT a valid castling move: ");
        return false; // Not a castling move
    }

    // Perform the castling move
    private void performCastling(ChessMove move, ChessPiece king, ChessPiece rook) throws InvalidMoveException {
//        if (Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) == 2) {
//            // King-side castling
        int direction = (move.getEndPosition().getColumn() > move.getStartPosition().getColumn()) ? 1 : -1;
        int row = move.getEndPosition().getRow();
        ChessPosition kingStartPosition = new ChessPosition(row, 5);
        ChessPosition kingEndPosition;
        board.removePiece(kingStartPosition);

        if (direction == 1) {
            // Move king (king-side castling)
            kingEndPosition = new ChessPosition(row, 7);
            board.addPiece(kingEndPosition, king);
            // Move rook (king-side castling)
            ChessPosition rookStartPosition = new ChessPosition(row, 8);
            ChessPosition rookEndPosition = new ChessPosition(row, 6);
            board.removePiece(rookStartPosition);
            board.addPiece(rookEndPosition, rook);
        } else {
            // Move king (queen-side castling)
            kingEndPosition = new ChessPosition(row, 3);
            board.addPiece(kingEndPosition, king);
            // Move rook (queen-side castling)
            ChessPosition rookStartPosition = new ChessPosition(row, 1);
            ChessPosition rookEndPosition = new ChessPosition(row, 4);
            board.removePiece(rookStartPosition);
            board.addPiece(rookEndPosition, rook);
        }

        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        if (isInCheck(teamTurn)) {
            // If the move puts the player's king in check, it's an invalid move
            // Roll back the move
            board.removePiece(move.getEndPosition());
            board.addPiece(move.getStartPosition(), king);
            if (capturedPiece != null) {
                board.addPiece(move.getEndPosition(), capturedPiece);
            }

            throw new InvalidMoveException("The move puts your king in check.");
        }
    }

    private ChessPiece getRookForCastling(ChessMove move, ChessPiece king, ChessBoard board) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        int direction = Integer.compare(end.getColumn(), start.getColumn());
        int row = start.getRow();

        // Determine the column of the rook based on the direction of the castling
        int rookColumn = (direction > 0) ? 8 : 1;

        // Check if there's a piece (rook) at the potential rook position
        ChessPiece rook = board.getPiece(new ChessPosition(row, rookColumn));

        // Validate that the piece at the potential rook position is a rook and belongs to the same team
        if (rook != null && rook.getPieceType() == ChessPiece.PieceType.ROOK && rook.getTeamColor() == king.getTeamColor()) {
            return rook;
        }

        return null; // No valid rook found
    }

    private boolean isEnPassantMove(ChessMove move, ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int startRow = move.getStartPosition().getRow();
            int endRow = move.getEndPosition().getRow();
            int startCol = move.getStartPosition().getColumn();
            int endCol = move.getEndPosition().getColumn();
//            Position endPosition = new Position(endRow, endCol);

            // Check if the move is one square forward and left or right (en passant)
            int direction = (piece.getTeamColor() == TeamColor.WHITE) ? 1 : -1;
//            board.setEnPassantPosition(endPosition);

            // Get the en passant position from your board
            ChessPosition enPassantPosition = board.getEnPassantPosition();

            if (//enPassantPosition != null &&
                    endRow - startRow == direction &&
                    Math.abs(endCol - startCol) == 1 &&
                    move.getEndPosition().equals(enPassantPosition)) {

                // Check if there's a pawn in the adjacent column at the same row
                ChessPosition adjacentPosition = new ChessPosition(endRow - direction, endCol);
                ChessPiece adjacentPawn = board.getPiece(adjacentPosition);

                // Check if the adjacent piece is a pawn of the opposing color and it moved two squares in the last turn
                return adjacentPawn.getPieceType() == ChessPiece.PieceType.PAWN && adjacentPawn.getTeamColor() != piece.getTeamColor() && adjacentPawn.hasMoved(); // Valid en passant move
            }
        }
        return false; // Not an en passant move
    }

    public boolean isValidEnPassantCapture(ChessMove move) {
        ChessPosition enPassantPosition = move.getEndPosition();

        // Check if the move is a two-square diagonal move by a pawn
        if (Math.abs(move.getStartPosition().getColumn() - enPassantPosition.getColumn()) == 1
                && Math.abs(move.getStartPosition().getRow() - enPassantPosition.getRow()) == 1) {

            // Check destination square
            if (board.getPiece(enPassantPosition) != null || board.getPiece(enPassantPosition).getPieceType() == ChessPiece.PieceType.PAWN || board.getPiece(enPassantPosition).getTeamColor() == teamTurn) {
                int direction = (teamTurn == TeamColor.WHITE) ? 1 : -1; // Adjust the direction

                // Determine the square where the captured pawn should be
                ChessPosition capturedPawnPosition = new ChessPosition(enPassantPosition.getRow() - direction, enPassantPosition.getColumn());

                ChessPiece capturedPawn = board.getPiece(capturedPawnPosition);

                // Check if the piece at the capturedPawnPosition is a pawn and has the 'hasMoved' flag set to true
                return capturedPawn.getPieceType() == ChessPiece.PieceType.PAWN && capturedPawn.hasMoved();
            }
        }
        return false;
    }

    public void handleEnPassantCapture(ChessMove move) {
        // Check if the move is a valid en passant capture
        if (isValidEnPassantCapture(move)) {
            // Use your concrete implementation of ChessPosition (e.g., ConcretePosition)
            ChessPosition enPassantPosition = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());

            int direction = (teamTurn == TeamColor.WHITE) ? 1 : -1; // Adjust the direction

            // Identify the square where the captured pawn is located
            ChessPosition capturedPawnPosition = new ChessPosition(
                    enPassantPosition.getRow() - direction,
                    enPassantPosition.getColumn()
            );

            // Remove the captured pawn from the board
            board.removePiece(capturedPawnPosition);
            board.setEnPassantPosition(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn && oppositeTeamColor == chessGame.oppositeTeamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, oppositeTeamColor);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", teamTurn=" + teamTurn +
                ", oppositeTeamColor=" + oppositeTeamColor +
                '}';
    }
}
