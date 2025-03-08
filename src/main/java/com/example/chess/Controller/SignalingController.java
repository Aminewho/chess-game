package com.example.chess.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SignalingController {

    @MessageMapping("/signal")
    @SendTo("/topic/signal")
    public String handleSignal(String message) {
        // Process signaling messages (e.g., SDP offers, answers, ICE candidates)
        return message;
    }
}