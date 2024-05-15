package chess;

import java.util.Collection;

public class EndGameConditions {

    public boolean checkPlease(ChessBoard board, ChessGame.TeamColor teamColor) {
        // Find the position of the king of the specified team
        ChessPosition kingPosition = findKingPosition(board, teamColor);

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

    public boolean checkmate(ChessBoard board, ChessGame.TeamColor teamColor) {
        if (!checkPlease(board, teamColor)) {
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
                        boolean stillInCheck = checkPlease(board, teamColor);

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

    public boolean stalemate(ChessBoard board, ChessGame.TeamColor teamColor) {
        if (checkPlease(board, teamColor)) {
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
                        boolean legalMove = !checkPlease(board, teamColor);

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
    private ChessPosition findKingPosition(ChessBoard board, ChessGame.TeamColor teamColor) {
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
}
