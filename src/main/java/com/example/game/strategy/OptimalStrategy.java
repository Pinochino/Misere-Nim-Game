package com.example.game.strategy;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("OPTIMAL")
@Primary
public class OptimalStrategy implements ComputerStrategy {

    @Override
    public int chooseMove(int heap) {
        if (heap <= 0) {
            throw new IllegalArgumentException("Heap must be >= 1");
        }

        int optimal = (heap - 1) % 4;

        if (optimal >= 1) {
            // We are in a winning position; take exactly `optimal` matches.
            return optimal;
        }

        // We are in a losing position (heap % 4 == 1).
        // No move can guarantee a win → return a deterministic valid move.
        return 1;
    }

    @Override
    public String name() {
        return "OPTIMAL";
    }
}