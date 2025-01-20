package com.example.chess.Entities;

import javax.persistence.*;

@Entity
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String moveNotation;  // Example: "e2 to e4"

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    // Getters and setters
}

