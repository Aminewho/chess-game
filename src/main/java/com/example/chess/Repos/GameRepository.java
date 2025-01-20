package com.example.chess.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.chess.Entities.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    // Add custom queries if needed
}
