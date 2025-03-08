package com.example.chess.Controller;

import com.example.chess.Models.MoveEntity;
import com.example.chess.Services.ChessLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ChessController {

    @Autowired
    private ChessLogicService chessLogicService;

    @GetMapping("/board")
    public String getBoardState() {
        return chessLogicService.getBoardState();
    }

    @PostMapping("/move")
    public String makeMove(@RequestBody MoveEntity move) {
        System.out.println("Received move: " + move.getMoveNotation());
        return chessLogicService.applyMove(move.getMoveNotation());
    }
    @PostMapping("/reset")
    public void resetBoard() {
        chessLogicService.resetBoard();
    }
}
