package com.example.chess.Models;

import jakarta.persistence.*;

@Entity
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fromPosition; // E.g., "e2"

    @Column(nullable = false)
    private String toPosition; // E.g., "e4"

    @Column(nullable = false)
    private String piece; // E.g., "PAWN", "KNIGHT", etc.

    @Column(nullable = true)
    private String capturedPiece; // E.g., "PAWN", "BISHOP", or null if no capture.

    @Column(nullable = false)
    private String player; // "WHITE" or "BLACK"

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game; // Links the move to a specific game.

    // Constructors
    public Move() {
    }

    public Move(String fromPosition, String toPosition, String piece, String player) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.piece = piece;
        this.player = player;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromPosition() {
        return fromPosition;
    }

    public void setFromPosition(String fromPosition) {
        this.fromPosition = fromPosition;
    }

    public String getToPosition() {
        return toPosition;
    }

    public void setToPosition(String toPosition) {
        this.toPosition = toPosition;
    }

    public String getPiece() {
        return piece;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }

    public String getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(String capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
