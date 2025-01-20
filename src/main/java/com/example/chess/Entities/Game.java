package com.example.chess.Entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String whitePlayer;
    private String blackPlayer;
    private String currentTurn; // "white" or "black"
    
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Move> moves;

    // Getters and setters
}

