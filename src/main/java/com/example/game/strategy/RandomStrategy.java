package com.example.game.strategy;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Random strategy: the computer picks a uniformly random number of matches
 * from [1, min(3, heap)].  Useful for testing and for less-intimidating games.
 */
@Component("RANDOM")
public class RandomStrategy implements ComputerStrategy {

    private final Random random = new Random();

    @Override
    public int chooseMove(int heap) {
        if (heap <= 0) {
            throw new IllegalArgumentException("Heap must be >= 1");
        }
        int maxTake = Math.min(3, heap);
        return random.nextInt(maxTake) + 1;    // uniform in [1, maxTake]
    }

    @Override
    public String name() {
        return "RANDOM";
    }
}

