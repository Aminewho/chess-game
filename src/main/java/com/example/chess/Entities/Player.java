package com.example.chess.Entities;

import javax.persistence.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String color; // "white" or "black"

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    // Getters and setters
}
