package chess;

public class EnPassantHandler {

    public boolean enPassantMove(ChessBoard board, ChessMove move, ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int startRow = move.getStartPosition().getRow();
            int endRow = move.getEndPosition().getRow();
            int startCol = move.getStartPosition().getColumn();
            int endCol = move.getEndPosition().getColumn();
//            Position endPosition = new Position(endRow, endCol);

            // Check if the move is one square forward and left or right (en passant)
            int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
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

    public boolean enPassantCapture(ChessBoard board, ChessMove move, ChessGame.TeamColor teamTurn) {
        ChessPosition enPassantPosition = move.getEndPosition();

        // Check if the move is a two-square diagonal move by a pawn
        if (Math.abs(move.getStartPosition().getColumn() - enPassantPosition.getColumn()) == 1
                && Math.abs(move.getStartPosition().getRow() - enPassantPosition.getRow()) == 1) {

            // Check destination square
            if (board.getPiece(enPassantPosition) != null || board.getPiece(enPassantPosition).getPieceType() == ChessPiece.PieceType.PAWN || board.getPiece(enPassantPosition).getTeamColor() == teamTurn) {
                int direction = (teamTurn == ChessGame.TeamColor.WHITE) ? 1 : -1; // Adjust the direction

                // Determine the square where the captured pawn should be
                ChessPosition capturedPawnPosition = new ChessPosition(enPassantPosition.getRow() - direction, enPassantPosition.getColumn());

                ChessPiece capturedPawn = board.getPiece(capturedPawnPosition);

                // Check if the piece at the capturedPawnPosition is a pawn and has the 'hasMoved' flag set to true
                return capturedPawn.getPieceType() == ChessPiece.PieceType.PAWN && capturedPawn.hasMoved();
            }
        }
        return false;
    }

    public void backstabber(ChessBoard board, ChessMove move, ChessGame.TeamColor teamTurn) {
        // Check if the move is a valid en passant capture
        if (enPassantCapture(board, move, teamTurn)) {
            // Use your concrete implementation of ChessPosition (e.g., ConcretePosition)
            ChessPosition enPassantPosition = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn());

            int direction = (teamTurn == ChessGame.TeamColor.WHITE) ? 1 : -1; // Adjust the direction

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
}
