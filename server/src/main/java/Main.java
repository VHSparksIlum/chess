import chess.*;
import dataaccess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        try {
            new Server(new SqlDataAccess()).run(8080);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        //new Server(new MemoryAuthDAO(), new MemoryGameDAO(), new MemoryUserDAO()).run(8080);
    }
}