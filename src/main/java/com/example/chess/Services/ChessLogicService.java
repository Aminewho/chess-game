package com.example.chess.Services;
import com.github.bhlangonijr.chesslib.Board;
import java.util.List;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.Square;
import org.springframework.stereotype.Service;
import com.example.chess.Models.MoveEntity;

@Service
public class ChessLogicService {
    
    private Board board;
    
    public ChessLogicService() {
        this.board = new Board(); // Initialize a new chess board
    }
    
    public boolean isMoveValid(String from, String to) {
        List<Move> legalMoves = MoveGenerator.generateLegalMoves(board);
        Move attemptedMove = new Move(Square.valueOf(from), Square.valueOf(to));
        return legalMoves.contains(attemptedMove);
    }
    
    public void applyMove(String from, String to) {
        Move move = new Move(Square.valueOf(from), Square.valueOf(to));
        if (isMoveValid(from, to)) {
            board.doMove(move); // Apply move if it's valid
        } else {
            throw new IllegalArgumentException("Invalid move: " + from + " to " + to);
        }
    }
    
    public void resetBoard() {
        board = new Board(); // Reset the board to the initial state
    }
    
    public String getBoardState() {
        return board.getFen(); // Get the FEN representation of the current board state
    }
}
