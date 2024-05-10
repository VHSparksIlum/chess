package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    /**
     * Constructor for regular moves (without promotion)
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = null;
    }

    /**
     * Constructor for moves with promotion
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * Constructor for moves with En Passant
     */
    public ChessMove(ChessPosition start, ChessPosition end, ChessPiece.PieceType promotionPiece, boolean isEnPassant) {
        this.startPosition = start;
        this.endPosition = end;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotionPiece;
    }

//    /**
//     * Set the starting position of this chess move
//     *
//     * @param startPosition the ChessPosition to set as the starting position
//     */
//    public void setStartPosition(ChessPosition startPosition) {
//        this.startPosition = startPosition;
//    }
//
//    /**
//     * Set the ending position of this chess move
//     *
//     * @param endPosition the ChessPosition to set as the ending position
//     */
//    public void setEndPosition(ChessPosition endPosition) {
//        this.endPosition = endPosition;
//    }
//
//    /**
//     * Set the promotion piece for this chess move
//     *
//     * @param promotionPiece the ChessPiece.PieceType to set as the promotion piece
//     */
//    public void setPromotionPiece(ChessPiece.PieceType promotionPiece) {
//        this.promotionPiece = promotionPiece;
//    }
//
//    /**
//     * Apply pawn promotion by setting the promotion piece
//     *
//     * @param promotionPiece the ChessPiece.PieceType to set as the promotion piece
//     */
//    public void applyPromotion(ChessPiece.PieceType promotionPiece) {
//        this.promotionPiece = promotionPiece;
//    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Move from ");
        sb.append(startPosition.toString()); // Use the custom toString for Position
        sb.append(" to ");
        sb.append(endPosition.toString()); // Use the custom toString for Position

        if (promotionPiece != null) {
            sb.append(" promoting to ");
            sb.append(promotionPiece); // Use the custom toString for PieceType
        }

        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        if (promotionPiece != chessMove.promotionPiece) return false; // Explicitly compare promotionPiece
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
