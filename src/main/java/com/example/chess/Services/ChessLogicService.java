package com.example.chess.Services;

import com.example.chess.Models.MoveEntity;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.Side;
import org.springframework.stereotype.Service;

import java.util.List;


import java.util.ArrayList;


@Service
public class ChessLogicService {

    private Board board;
    private List<Move> moveHistory;

    public ChessLogicService() {
        this.board = new Board(); // Initialize a new chess board
        this.moveHistory = new ArrayList<>();
    }

    public String undoMove() {
        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.remove(moveHistory.size() - 1);
            board.undoMove();
        }
        return board.getFen();
    }

    public boolean isMoveValid(String moveNotation) {
        List<Move> legalMoves = MoveGenerator.generateLegalMoves(board);
        Move attemptedMove = new Move(Square.valueOf(moveNotation.substring(0, 2)), Square.valueOf(moveNotation.substring(2, 4)));
        return legalMoves.contains(attemptedMove);
    }

    private Piece getPromotionPiece(char promotion, boolean isBlack) {
        switch (Character.toLowerCase(promotion)) {
            case 'q': return isBlack ? Piece.BLACK_QUEEN : Piece.WHITE_QUEEN;
            case 'r': return isBlack ? Piece.BLACK_ROOK : Piece.WHITE_ROOK;
            case 'b': return isBlack ? Piece.BLACK_BISHOP : Piece.WHITE_BISHOP;
            case 'n': return isBlack ? Piece.BLACK_KNIGHT : Piece.WHITE_KNIGHT;
            default: return isBlack ? Piece.BLACK_QUEEN : Piece.WHITE_QUEEN;
        }
    }

    public String applyMove(String moveNotation) {
        try {
            System.out.println("Processing move: " + moveNotation);

            String from = moveNotation.substring(0, 2).toUpperCase();
            String to = moveNotation.substring(2, 4).toUpperCase();

            Move move;
            if (moveNotation.length() == 5) { 
                char promotionPiece = moveNotation.charAt(4);
                boolean isBlack = board.getPiece(Square.valueOf(from)).getPieceSide() == Side.BLACK;
                move = new Move(Square.valueOf(from), Square.valueOf(to), getPromotionPiece(promotionPiece, isBlack));
            } else {
                move = new Move(Square.valueOf(from), Square.valueOf(to));
            }

            if (board.doMove(move)) {
                moveHistory.add(move);
            }

            if (board.isMated()) {
                return "CHECKMATE: " + board.getSideToMove().flip() + " wins!";
            } else if (board.isDraw()) {
                return "DRAW: The game is a draw!";
            }

            return board.getFen();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing move";
        }
    }

    public void resetBoard() {
        board = new Board();
        moveHistory.clear();
    }

    public String getBoardState() {
        return board.getFen();
    }
}
