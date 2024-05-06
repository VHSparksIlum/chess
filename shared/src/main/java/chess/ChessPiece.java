package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private final PieceType type;
//    private final ChessGame.TeamColor oppositeTeamColor; //possible code for extra credit moves
//    private boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.type = type;
    }

//    public ChessPiece() {
//    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * @return whether capture is within board boundaries AND the target square contains an opponent piece
     * for pawns
     */
    private boolean isValidPawnCapture(ChessBoard board, ChessPosition position) {
        ChessPiece targetPiece = board.getPiece(position);
        return board.isOnBoard(position.getRow(), position.getColumn()) &&
                targetPiece != null &&
                targetPiece.getTeamColor() != teamColor;
    }

    /**
     * @return whether the move is within board boundaries AND the target square is empty
     * for pawns
     */
    private boolean isValidPawnMove(ChessBoard board, ChessPosition position) {
        return board.isOnBoard(position.getRow(), position.getColumn()) && board.getPiece(position) == null;
    }

    /**
     * @return whether capture is within board boundaries AND the target square contains an opponent piece
     * for queen
     */
    private boolean isValidQueenCapture(ChessBoard board, ChessPosition position) {
        ChessPiece targetPiece = board.getPiece(position);
        return board.isOnBoard(position.getRow(), position.getColumn()) &&
                targetPiece != null &&
                targetPiece.getTeamColor() != teamColor;
    }

    /**
     * @return whether the move is within board boundaries AND the target square is empty
     * for queen
     */
    private boolean isValidQueenMove(ChessBoard board, ChessPosition position) {
        if (!board.isOnBoard(position.getRow(), position.getColumn())) {
            return false;
        }

        // Check if the target square is empty or contains an opponent's piece
        ChessPiece targetPiece = board.getPiece(position);
        return targetPiece == null || targetPiece.getTeamColor() != teamColor;
    }

    /**
     * @return whether the move is within board boundaries AND the target square is empty
     * for rooks
     */
    private boolean isValidRookCapture(ChessBoard board, ChessPosition position) {
        ChessPiece targetPiece = board.getPiece(position);
        return board.isOnBoard(position.getRow(), position.getColumn()) &&
                targetPiece != null &&
                targetPiece.getTeamColor() != teamColor;
    }

    /**
     * @return whether capture is within board boundaries AND the target square contains an opponent piece
     * for rooks
     */
    private boolean isValidRookMove(ChessBoard board, ChessPosition position) {
        if (!board.isOnBoard(position.getRow(), position.getColumn())) {
            return false;
        }

        ChessPiece targetPiece = board.getPiece(position);
        return targetPiece == null || targetPiece.getTeamColor() != teamColor;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();

        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();

        if(getPieceType() == PieceType.BISHOP) {
            // Define the four possible diagonal directions a bishop can move
            int[] rowDirections = { -1, -1, 1, 1 };
            int[] colDirections = { -1, 1, -1, 1 };

            for (int i = 0; i < 4; i++) {
                for (int distance = 1; distance <= 7; distance++) {
                    int newRow = myRow + distance * rowDirections[i];
                    int newCol = myCol + distance * colDirections[i];
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);

                    // Check if the new position is on the chessboard using isOnBoard
                    if (board.isOnBoard(newRow, newCol)) {
                        ChessPiece targetPiece = board.getPiece(newPosition);

                        // If the target square is empty, it's a valid move
                        if (targetPiece == null) {
                            validMoves.add(new ChessMove(myPosition, newPosition));
                        } else {
                            // If the target square has an opponent's piece, it's a valid capture
                            if (targetPiece.getTeamColor() != teamColor) {
                                validMoves.add(new ChessMove(myPosition, newPosition));
                            }
                            break; // Stop searching in this direction if a piece is encountered
                        }
                    } else {
                        break; // Stop searching in this direction if the position is off the board
                    }
                }
            }
        }
        else if(getPieceType() == PieceType.KING) {
            // Define the eight possible directions a king can move
            int[] rowDirections = { -1, -1, -1, 0, 0, 1, 1, 1 };
            int[] colDirections = { -1, 0, 1, -1, 1, -1, 0, 1 };

            for (int i = 0; i < 8; i++) {
                int newRow = myRow + rowDirections[i];
                int newCol = myCol + colDirections[i];
                ChessPosition newPosition = new ChessPosition(newRow, newCol);

                // Check if the new position is on the chessboard
                if (board.isOnBoard(newRow, newCol)) {
                    ChessPiece targetPiece = board.getPiece(newPosition);

                    // If the target square is empty or contains an opponent's piece, it's a valid move
                    if (targetPiece == null || targetPiece.getTeamColor() != teamColor) {
                        validMoves.add(new ChessMove(myPosition, newPosition));
                    }
                }
            }
        }
        else if(getPieceType() == PieceType.KNIGHT) {
            // Define the valid "L" shape moves from the Knight's position
            int[][] knightMoves = {
                    {-2, -1}, {-2, 1},
                    {-1, -2}, {-1, 2},
                    {1, -2}, {1, 2},
                    {2, -1}, {2, 1}
            };

            for (int[] move : knightMoves) {
                int newRow = myRow + move[0];
                int newCol = myCol + move[1];

                // Check if the new position is within the board bounds using isOnBoard
                if (board.isOnBoard(newRow, newCol)) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece targetPiece = board.getPiece(newPosition);

                    // Knights can jump over other pieces, so no need to check for obstructions
                    if (targetPiece == null || targetPiece.getTeamColor() != teamColor) {
                        validMoves.add(new ChessMove(myPosition, newPosition));
                    }
                }
            }
        }
        else if(getPieceType() == PieceType.PAWN) {
            // Determine the direction of pawn movement based on its team color
            int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

            // Move one square forward
            int newRow = myPosition.getRow() + direction;
            int newColumn = myPosition.getColumn();
            if (board.isOnBoard(newRow, newColumn) && board.getPiece(new ChessPosition(newRow, newColumn)) == null) {
                // Check if the pawn reaches the back row of the opposing team
                int backRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
                if (newRow == backRow) {
                    // Add the move four times for promotion (BISHOP, KNIGHT, QUEEN, ROOK)
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newColumn), PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newColumn), PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newColumn), PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newColumn), PieceType.ROOK));
                } else {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newColumn), null));
                }
            }

            // Initial two-square move (only allowed from starting position and not blocked)
            if (((teamColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) ||
                    (teamColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7))) {
                int newRow2 = myPosition.getRow() + 2 * direction;
                int newColumn2 = myPosition.getColumn(); // Updated this line
                ChessPosition twoSquaresForward = new ChessPosition(newRow2, newColumn2);

                // Check if squares in between are empty
                boolean squaresBetweenEmpty = true;
                for (int i = myPosition.getRow() + direction; i != newRow2; i += direction) {
                    ChessPosition positionBetween = new ChessPosition(i, newColumn2);
                    if (board.getPiece(positionBetween) != null) {
                        squaresBetweenEmpty = false;
                        break;
                    }
                }
                if (isValidPawnMove(board, twoSquaresForward) && squaresBetweenEmpty) {
                    validMoves.add(new ChessMove(myPosition, twoSquaresForward, null));
                }
            }

            // Capture diagonally left
            int captureLeftRow = myPosition.getRow() + direction;
            int captureLeftColumn = myPosition.getColumn() - 1;
            if (board.isOnBoard(captureLeftRow, captureLeftColumn)) {
                ChessPosition captureLeft = new ChessPosition(captureLeftRow, captureLeftColumn);
                if (isValidPawnCapture(board, captureLeft)) {
                    // Check if the pawn reaches the back row of the opposing team
                    int backRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
                    if (captureLeftRow == backRow) {
                        // Add the move four times for promotion (BISHOP, KNIGHT, QUEEN, ROOK)
                        validMoves.add(new ChessMove(myPosition, captureLeft, PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, captureLeft, PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, captureLeft, PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, captureLeft, PieceType.ROOK));
                    } else {
                        validMoves.add(new ChessMove(myPosition, captureLeft, null));
                    }
                }
            }

            // Capture diagonally right
            int captureRightRow = myPosition.getRow() + direction;
            int captureRightColumn = myPosition.getColumn() + 1;
            if (board.isOnBoard(captureRightRow, captureRightColumn)) {
                ChessPosition captureRight = new ChessPosition(captureRightRow, captureRightColumn);
                if (isValidPawnCapture(board, captureRight)) {
                    // Check if the pawn reaches the back row of the opposing team
                    int backRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
                    if (captureRightRow == backRow) {
                        // Add the move four times for promotion (BISHOP, KNIGHT, QUEEN, ROOK)
                        validMoves.add(new ChessMove(myPosition, captureRight, PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, captureRight, PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, captureRight, PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, captureRight, PieceType.ROOK));
                    } else {
                        validMoves.add(new ChessMove(myPosition, captureRight, null));
                    }
                }
            }
        }
        else if(getPieceType() == PieceType.QUEEN) {
            // The Queen can move in eight directions: up, down, left, right, and the four diagonals.
            int[][] directions = {
                    { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 }, // Rook-like movements
                    { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } // Bishop-like movements
            };

            for (int[] direction : directions) {
                int rowDelta = direction[0];
                int colDelta = direction[1];

                for (int i = 1; i <= 7; i++) { // Check up to 7 squares in each direction
                    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + i * rowDelta, myPosition.getColumn() + i * colDelta);

                    if (!isValidQueenMove(board, newPosition)) {
                        break;
                    }

                    validMoves.add(new ChessMove(myPosition, newPosition, null));

                    // Check if the move captures an opponent's piece
                    if (isValidQueenCapture(board, newPosition)) {
                        //validMoves.add(new Move(myPosition, newPosition, board.getPiece(newPosition).getPieceType()));
                        break; // Stop if a capture occurs
                    }
                }
            }
        }
        else if(getPieceType() == PieceType.ROOK) {
            // Up, down, left, right
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

            for (int[] direction : directions) {
                int rowDelta = direction[0];
                int colDelta = direction[1];

                for (int i = 1; i <= 7; i++) { // Check up to 7 squares in each direction
                    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + i * rowDelta, myPosition.getColumn() + i * colDelta);

                    if (!isValidRookMove(board, newPosition)) {
                        break;
                    }

                    validMoves.add(new ChessMove(myPosition, newPosition, null));

                    // Check if the move captures an opponent's piece
                    if (isValidRookCapture(board, newPosition)) {
                        //validMoves.add(new Move(myPosition, newPosition, board.getPiece(newPosition).getPieceType()));
                        break; // Stop if a capture occurs
                    }
                }
            }
        }
        else {
            throw new RuntimeException("Unknown PieceType: " + getPieceType());
        }
        return validMoves;
    }
}
