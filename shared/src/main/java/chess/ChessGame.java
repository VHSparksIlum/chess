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
    private final EndGameConditions endGameConditions;
    private final EnPassantHandler enPassantHandler;
    private final CastlingHandler castlingHandler;

    public ChessGame() {
        this.board = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        this.endGameConditions = new EndGameConditions();
        this.enPassantHandler = new EnPassantHandler();
        this.castlingHandler = new CastlingHandler();
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
        validMoves.removeIf(move -> { //validMoves.removeIf(move -> moveCalculator.isMoveInvalid(move, piece, board));
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
            performCastling(move, piece, getRookForCastling(move, piece, board), board, teamTurn);
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
    public boolean isInCheck(ChessGame.TeamColor teamColor) {
        return endGameConditions.checkPlease(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(ChessGame.TeamColor teamColor) {
        return endGameConditions.checkmate(board, teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(ChessGame.TeamColor teamColor) {
        return endGameConditions.stalemate(board, teamColor);
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

    private boolean isCastlingMove(ChessMove move, ChessPiece piece) {
        return castlingHandler.princessIsInAnotherCastle(board, move, piece);
    }

    private void performCastling(ChessMove move, ChessPiece king, ChessPiece rook, ChessBoard board, ChessGame.TeamColor team) throws InvalidMoveException {
        boolean checkChecker = isInCheck(team);
        castlingHandler.secretService(board, move, king, rook, checkChecker);
    }

    private ChessPiece getRookForCastling(ChessMove move, ChessPiece king, ChessBoard board) {
        return castlingHandler.targetRook(move, king, board);
    }

    private boolean isEnPassantMove(ChessMove move, ChessPiece piece) {
        return enPassantHandler.enPassantMove(board, move, piece);
    }

    public void handleEnPassantCapture(ChessMove move) {
        enPassantHandler.backstabber(board, move, teamTurn);
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
