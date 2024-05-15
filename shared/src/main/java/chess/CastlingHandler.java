package chess;

import java.util.Collection;

public class CastlingHandler {
    public boolean princessIsInAnotherCastle(ChessBoard board, ChessMove move, ChessPiece piece) {
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

    public void secretService(ChessBoard board, ChessMove move, ChessPiece king, ChessPiece rook, boolean check) throws InvalidMoveException {
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
        if (check) {
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

    public ChessPiece targetRook(ChessMove move, ChessPiece king, ChessBoard board) {
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

    public static boolean castleConditions(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor) {
        if ((myPosition.getRow() != 8 && myPosition.getRow() != 1) || myPosition.getColumn() != 5 ||
                isSquareAttacked(myPosition.getRow(), myPosition.getColumn(), board, teamColor))
        {
            return false;
        }

        // Check if the squares between the king and queen-side rook are empty
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        for (int col = myCol - 1; col > myCol - 4; col--) {
            if (col >= 1 && board.getPiece(new ChessPosition(myRow, col)) != null) {
                return false;
            }
        }

        return true;
    }

    private static boolean isSquareAttacked(int row, int col, ChessBoard board, ChessGame.TeamColor teamColor) {

        // Ensure that the row and col are within the bounds of the chessboard
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }

        // Check if any opponent piece (of the attacking team's color) can attack the specified square
        for (int r = 1; r < 8; r++) {
            for (int c = 1; c < 8; c++) {
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                if (piece != null && piece.getTeamColor() != teamColor) {
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
    private static boolean isKingAttackingSquare(ChessBoard board, ChessPosition kingPosition, ChessPosition squarePosition) {
        // Get the row and column of the king
        int kingRow = kingPosition.getRow();
        int kingCol = kingPosition.getColumn();

        // Get the row and column of the square to be checked
//        int targetRow = squarePosition.getRow();
//        int targetCol = squarePosition.getColumn();

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
}
