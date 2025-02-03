package com.example.chess.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.chess.Repos.MoveRepository;
import com.example.chess.Repos.GameRepository;
import com.example.chess.Models.Game;
import com.example.chess.Models.MoveEntity;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MoveRepository moveRepository;

    @Autowired
    private ChessLogicService chessLogicService; // Handles move validation and game rules.

    /**
     * Make a move in the game, updating the game state and saving the move.
     *
     * @param gameId      ID of the game.
     * @param fromPosition Starting position of the piece (e.g., "e2").
     * @param toPosition   Target position of the piece (e.g., "e4").
     * @param pieceMoved   The piece being moved (e.g., "PAWN").
     * @param player       The player making the move ("WHITE" or "BLACK").
     * @return Updated Game object after the move.
     */
    public Game makeMove(Long gameId, String fromPosition, String toPosition, String pieceMoved, String player) {
        // Retrieve the game or throw an exception if not found.
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Check if it's the correct player's turn.
        if (!game.getCurrentPlayer().equalsIgnoreCase(player)) {
            throw new IllegalArgumentException("It's not " + player + "'s turn!");
        }

        // Get all previous moves for validation purposes.
        List<MoveEntity> previousMoves = game.getMoves();

        // Validate the move using ChessLogicService.
        if (!chessLogicService.isMoveValid(pieceMoved, fromPosition, toPosition, previousMoves, game.getGameState())) {
            throw new IllegalArgumentException("Invalid move!");
        }

        // Create and save the new move.
        MoveEntity move = new MoveEntity();
        move.setFromPosition(fromPosition);
        move.setToPosition(toPosition);
        move.setPiece(pieceMoved);
        move.setPlayer(player);
        move.setTurnNumber(previousMoves.size() + 1);
        move.setTimestamp(LocalDateTime.now());
        move.setGame(game);

        // Update game state.
        String updatedGameState = chessLogicService.updateGameState(game.getGameState(), fromPosition, toPosition, pieceMoved);
        game.setGameState(updatedGameState);

        // Check if the game is over after this move (e.g., checkmate, stalemate).
        if (chessLogicService.isGameOver(updatedGameState, player)) {
            game.setGameOver(true);
            game.setWinner(player.equalsIgnoreCase("WHITE") ? "WHITE" : "BLACK");
        } else {
            // Switch to the next player's turn.
            game.setCurrentPlayer(player.equalsIgnoreCase("WHITE") ? "BLACK" : "WHITE");
        }

        // Save the move and the updated game.
        moveRepository.save(move);
        return gameRepository.save(game);
    }

    /**
     * Get all moves for a given game.
     *
     * @param gameId ID of the game.
     * @return List of moves in the game.
     */
    public List<MoveEntity> getAllMoves(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        return game.getMoves();
    }

    /**
     * Create a new game.
     *
     * @return A new Game object with the initial state.
     */
    public Game createNewGame() {
        Game newGame = new Game("initialFEN", "WHITE"); // Initialize with FEN or default state.
        return gameRepository.save(newGame);
    }

    /**
     * Forfeit the game for a specific player.
     *
     * @param gameId ID of the game.
     * @param player The player forfeiting the game ("WHITE" or "BLACK").
     * @return Updated Game object with the game over state.
     */
    public Game forfeitGame(Long gameId, String player) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (game.isGameOver()) {
            throw new IllegalStateException("The game is already over.");
        }

        game.setGameOver(true);
        game.setWinner(player.equalsIgnoreCase("WHITE") ? "BLACK" : "WHITE"); // Opponent wins by forfeit.
        return gameRepository.save(game);
    }
}
