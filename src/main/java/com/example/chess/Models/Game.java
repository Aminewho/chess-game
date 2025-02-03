package com.example.chess.Models;

import jakarta.persistence.*;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.GameResult;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String gameState; // FEN notation

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameResult result = GameResult.ONGOING; // Game status (WHITE_WIN, BLACK_WIN, DRAW, etc.)

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MoveEntity> moves = new ArrayList<>();

    // Constructors
    public Game() {
        this.gameState = new Board().getFen(); // Initialize board with default FEN
    }

    public Game(String gameState) {
        this.gameState = gameState;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }

    public List<MoveEntity> getMoves() {
        return moves;
    }

    public void setMoves(List<MoveEntity> moves) {
        this.moves = moves;
    }

    public void addMove(MoveEntity move) {
        this.moves.add(move);
        move.setGame(this);
    }
}
