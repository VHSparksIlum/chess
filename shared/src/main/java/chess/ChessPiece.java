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
    private boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.teamColor = pieceColor;
        this.type = type;
        this.hasMoved = false;
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

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", type=" + type +
                ", hasMoved=" + hasMoved +
                '}';
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

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * @return whether the move is within board boundaries AND the target square is empty
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
     * @return whether capture is within board boundaries AND the target square contains an opponent piece
     */
    private boolean isValidCapture(ChessBoard board, ChessPosition position) {
        ChessPiece checkPiece = board.getPiece(position);
        return board.isOnBoard(position.getRow(), position.getColumn()) && checkPiece != null && checkPiece.getTeamColor() != teamColor;
    }

//    public boolean canCastle(ChessBoard board, ChessPosition myPosition) {
//        if ((myPosition.getRow() != 8 && myPosition.getRow() != 1) || myPosition.getColumn() != 5 ||
//                board.isSquareAttacked(myPosition.getRow(), myPosition.getColumn(), oppositeTeamColor, board)
//        ) {
//            return false;
//        }
//
//        // Check if the squares between the king and queen-side rook are empty
//        int myRow = myPosition.getRow();
//        int myCol = myPosition.getColumn();
//        for (int col = myCol - 1; col > myCol - 4; col--) {
//            if (col >= 1 && board.getPiece(new ChessPosition(myRow, col)) != null) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//        for (int col = myCol + 1; col <= myCol + 2; col++) {
//            if (col <= 8 && board.getPiece(new ChessPosition(myRow, col)) != null) {
//                return false;
//            }
//        }

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
            checkSingleSquareMove(myPosition, direction, backRow, validMoves, board);

            // Initial two-square move
            checkTwoSquareMove(myPosition, direction, initialMove, validMoves, board);

            // Capture diagonally
            checkDiagonalCapture(myPosition, direction, backRow, validMoves, board);
        }
        else if (getPieceType() != null) {
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
//            // Check if castling to the left is a valid move for the king
//            if (canCastle(board, myPosition)) {
//                validMoves.add(new ChessMove(myPosition, new ChessPosition(myRow, myCol - 2), null, true)); // Mark as a castling move
//            }
//
//            // Check if castling to the right is a valid move for the king
//            if (canCastle(board, myPosition)) {
//                validMoves.add(new ChessMove(myPosition, new ChessPosition(myRow, myCol + 2), null, true)); // Mark as a castling move
//            }
        }

        return validMoves;
    }

    //PAWN MOVE SET
    private void checkSingleSquareMove(ChessPosition myPosition, int direction, int backRow, List<ChessMove> validMoves, ChessBoard board) {
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
    }

    private void checkTwoSquareMove(ChessPosition myPosition, int direction, boolean initialMove, List<ChessMove> validMoves, ChessBoard board) {
        if (initialMove) {
            int newRow2 = myPosition.getRow() + 2 * direction;
            //Find destination square
            ChessPosition twoSquaresForward = new ChessPosition(newRow2, myPosition.getColumn());
            boolean notBlocked = isBlocked(myPosition, newRow2, direction, board);
            if (isValidMove(board, twoSquaresForward) && notBlocked) {
                validMoves.add(new ChessMove(myPosition, twoSquaresForward, null));
                // Set the En Passant target square for the opponent's pawn
                int enPassantTargetRow = (teamColor == ChessGame.TeamColor.WHITE) ? 3 : 6;
                board.setEnPassantPosition(new ChessPosition(enPassantTargetRow, myPosition.getColumn()));
            } else {
                // Clear the En Passant target square for the opponent
                board.setEnPassantPosition(null);
            }
        }
    }

    private void checkDiagonalCapture(ChessPosition myPosition, int direction, int backRow, List<ChessMove> validMoves, ChessBoard board) {
        int captureRow = myPosition.getRow() + direction;
        int[] captureColumns = {myPosition.getColumn() - 1, myPosition.getColumn() + 1};

        for (int col : captureColumns) {
            ChessPosition capturePos = new ChessPosition(captureRow, col);
            if (board.isOnBoard(captureRow, col) && isValidCapture(board, capturePos)) {
                if (captureRow == backRow) {
                    addPromotionMoves(validMoves, myPosition, capturePos);
                } else {
                    validMoves.add(new ChessMove(myPosition, capturePos, null));
                }
            } else {
                // Check for En Passant capture on the left side
                ChessPosition enPassantPosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
                if (board.getEnPassantPosition() != null && enPassantPosition.equals(board.getEnPassantPosition())) {
                    // Check if there's an opponent's pawn in the En Passant target square
                    int enPassantTargetRow = (teamColor == ChessGame.TeamColor.WHITE) ? 5 : 4;
                    ChessPosition enPassantTarget = new ChessPosition(enPassantTargetRow, myPosition.getColumn());
                    ChessPiece enPassantTargetPiece = board.getPiece(enPassantTarget);
                    if (enPassantTargetPiece != null && enPassantTargetPiece.getTeamColor() != teamColor &&
                            enPassantTargetPiece.getPieceType() == PieceType.PAWN && enPassantPosition.equals(board.getEnPassantPosition())) {
                        validMoves.add(new ChessMove(myPosition, capturePos, null, true));
                    }
                }
            }
        }
    }

    private boolean isBlocked(ChessPosition startPos, int endRow, int direction, ChessBoard board) {
        for (int i = startPos.getRow() + direction; i != endRow; i += direction) {
            if (board.getPiece(new ChessPosition(i, startPos.getColumn())) != null) {
                return false;
            }
        }
        return true;
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


}
