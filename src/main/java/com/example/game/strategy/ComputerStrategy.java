package com.example.game.strategy;

/**
 * Strategy interface for the computer player.
 *
 * <p>Implementations decide how many matches the computer takes
 * given the current heap size.  The returned value must always be
 * in the range [1, min(3, heap)].
 */
public interface ComputerStrategy {

    /**
     * Choose how many matches to take.
     *
     * @param heap current number of matches remaining (>= 1)
     * @return number of matches to take (1 – 3, must not exceed heap)
     */
    int chooseMove(int heap);

    /**
     * Human-readable name of this strategy (used for logging / API responses).
     */
    String name();
}
