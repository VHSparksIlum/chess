package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.teamColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }

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
     */

    private boolean isValidMove(ChessBoard board, ChessPosition position) {
        if (getPieceType() != PieceType.PAWN) {

            if(!board.isOnBoard(position.getRow(), position.getColumn())) {
                return false;
            }
            ChessPiece checkPiece = board.getPiece(position);

            return checkPiece == null || checkPiece.getTeamColor() != teamColor;
        }
        else {
            return board.isOnBoard(position.getRow(), position.getColumn()) && board.getPiece(position) == null;
        }
    }

    /**
     * @return whether the move is within board boundaries AND the target square is empty
     */
    private boolean isValidCapture(ChessBoard board, ChessPosition position) {
        ChessPiece checkPiece = board.getPiece(position);
        return board.isOnBoard(position.getRow(), position.getColumn()) && checkPiece != null && checkPiece.getTeamColor() != teamColor;
    }

    /**
     * adds promotion moves condensed
     */
    private void addPromotionMoves (List<ChessMove> validMoves, ChessPosition position, ChessPosition newPosition) {
        validMoves.add(new ChessMove(position, newPosition, PieceType.BISHOP));
        validMoves.add(new ChessMove(position, newPosition, PieceType.KNIGHT));
        validMoves.add(new ChessMove(position, newPosition, PieceType.QUEEN));
        validMoves.add(new ChessMove(position, newPosition, PieceType.ROOK));
    }

    /**
     * @return valid moves for sliding pieces
     */
    private int[][] getMoves() {
        int[][] queenMoves = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0}, // Rook-like movements
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Bishop-like movements
        };
        int[][] rookMoves = Arrays.copyOfRange(queenMoves, 0, 4);
        int[][] bishopMoves = Arrays.copyOfRange(queenMoves, 4, 8);
        int[][] kingMoves = {
                {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}
        };
        int[][] knightMoves = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
        };

        int[][] moves;
        PieceType pieceType = getPieceType();
        if (pieceType == PieceType.BISHOP) {
            moves = bishopMoves;
        } else if (pieceType == PieceType.KING) {
            moves = kingMoves;
        } else if (pieceType == PieceType.KNIGHT) {
            moves = knightMoves;
        } else if (pieceType == PieceType.QUEEN) {
            moves = queenMoves;
        } else if (pieceType == PieceType.ROOK) {
            moves = rookMoves;
        } else {
            throw new RuntimeException("Unknown PieceType: " + pieceType);
        }
        return moves;
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

        if (getPieceType() == PieceType.PAWN) {
            // Determine the direction of pawn movement based on its team color
            int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
            int backRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
            boolean initialMove = (teamColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) || (teamColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7);

            // Move one square forward
            int newRow = myPosition.getRow() + direction;
            int newColumn = myPosition.getColumn();
            if (board.isOnBoard(newRow, newColumn) && board.getPiece(new ChessPosition(newRow, newColumn)) == null) {
                // Check if the pawn reaches the back row of the opposing team
                if (newRow == backRow) {
                    // Add the move four times for promotion (BISHOP, KNIGHT, QUEEN, ROOK)
                    addPromotionMoves(validMoves, myPosition, new ChessPosition(newRow, newColumn));
                } else {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newColumn), null));
                }
            }

            // Initial two-square move (only allowed from starting position and not blocked)
            if (initialMove) {
                int newRow2 = myPosition.getRow() + 2 * direction;
                int newColumn2 = myPosition.getColumn();
                ChessPosition twoSquaresForward = new ChessPosition(newRow2, newColumn2);

                // Check if squares in between are empty
                boolean notBlocked = true;
                for (int i = myPosition.getRow() + direction; i != newRow2; i += direction) {
                    if (board.getPiece(new ChessPosition(i, newColumn2)) != null) {
                        notBlocked = false;
                        break;
                    }
                }
                if (isValidMove(board, twoSquaresForward) && notBlocked) {
                    validMoves.add(new ChessMove(myPosition, twoSquaresForward, null));
                }
            }

            // Capture diagonally
            int captureRow = myPosition.getRow() + direction;
            int captureLeftCol = myPosition.getColumn() - 1;
            int captureRightCol = myPosition.getColumn() + 1;
            ChessPosition captureLeft = new ChessPosition(captureRow, captureLeftCol);
            ChessPosition captureRight = new ChessPosition(captureRow, captureRightCol);
            if ((board.isOnBoard(captureRow, captureLeftCol) && isValidCapture(board, captureLeft))) {
                if (captureRow == backRow) {
                    addPromotionMoves(validMoves, myPosition, captureLeft);
                } else {
                    validMoves.add(new ChessMove(myPosition, captureLeft, null));
                }
            }
            if ((board.isOnBoard(captureRow, captureRightCol) && isValidCapture(board, captureRight))) {
                if (captureRow == backRow) {
                    addPromotionMoves(validMoves, myPosition, captureRight);
                } else {
                    validMoves.add(new ChessMove(myPosition, captureRight, null));
                }
            }
        } else if (getPieceType() != null) {
            int[][] moves = getMoves();
            for (int[] direction : moves) {
                int rowDelta = direction[0];
                int colDelta = direction[1];

                int maxIterations = (getPieceType() == PieceType.KING || getPieceType() == PieceType.KNIGHT) ? 1 : 7;

                //Check up to 7 squares in the given direction, according to piece type
                for (int i = 1; i <= maxIterations; i++) {
                    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + i * rowDelta, myPosition.getColumn() + i * colDelta);

                    if (!isValidMove(board, newPosition)) {
                        break;
                    }

                    validMoves.add(new ChessMove(myPosition, newPosition, null));

                    // Check if the move captures an opponent's piece
                    if (isValidCapture(board, newPosition)) {
                        //validMoves.add(new Move(myPosition, newPosition, board.getPiece(newPosition).getPieceType()));
                        break; // Stop if a capture occurs
                    }
                }
            }
        }

        return validMoves;
    }
}
