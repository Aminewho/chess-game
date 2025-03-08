package com.example.chess.Models;
import jakarta.persistence.*;
import com.github.bhlangonijr.chesslib.move.Move;
import java.time.LocalDateTime;

@Entity
public class MoveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false, length = 10)
    private String moveNotation; // Store move in standard chess notation (e.g., "e2e4")

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Constructors
    public MoveEntity() {}

    public MoveEntity(Game game, Move move) {
        this.game = game;
        this.moveNotation = move.toString(); // Convert Move to String
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getMoveNotation() {
        return moveNotation;
    }
  
    public void setMoveNotation(String moveNotation) {
        this.moveNotation = moveNotation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
