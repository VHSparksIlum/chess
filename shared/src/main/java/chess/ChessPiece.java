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
//    private final ChessGame.TeamColor oppositeTeamColor; //possible code for king castling
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
        else {
            throw new RuntimeException("Not BISHOP");
        }
        return validMoves;
    }
}
