package com.example.game.entity;

import com.example.game.constant.GameStatus;
import com.example.game.constant.Player;
import lombok.Getter;

import java.util.UUID;

/**
 * Core domain object representing a single Nim game session.
 *
 * <p>Rules (Misère variant):
 * <ul>
 *   <li>Players alternate turns taking 1–3 matches from the heap.</li>
 *   <li>The player who takes the LAST match LOSES.</li>
 * </ul>
 *
 * <p>State transitions:
 * <pre>
 *   [IN_PROGRESS, HUMAN turn]
 *       → human takes N matches
 *       → heap == 0  →  COMPUTER_WINS  (human took last match → human loses)
 *       → heap  > 0  →  [IN_PROGRESS, COMPUTER turn]
 *           → computer takes M matches
 *           → heap == 0  →  HUMAN_WINS  (computer took last → computer loses)
 *           → heap  > 0  →  [IN_PROGRESS, HUMAN turn]  (loop)
 * </pre>
 */
@Getter
public class Game {

    // ── Identity ────────────────────────────────────────────────────────────
    private final String id;

    // ── Mutable game state ───────────────────────────────────────────────────
    private int heap;
    private Player currentTurn;
    private GameStatus status;

    // ── Move constraints ─────────────────────────────────────────────────────
    public static final int MIN_TAKE = 1;
    public static final int MAX_TAKE = 3;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a new game session.
     *
     * @param initialHeap number of matches to start with (must be >= 1)
     */
    public Game(int initialHeap) {
        if (initialHeap < 1) {
            throw new IllegalArgumentException("Initial heap must be at least 1");
        }
        this.id          = UUID.randomUUID().toString();
        this.heap        = initialHeap;
        this.currentTurn = Player.HUMAN;       // human always goes first
        this.status      = GameStatus.IN_PROGRESS;
    }

    // ── Domain methods ───────────────────────────────────────────────────────

    /**
     * Returns true if the game has already ended.
     */
    public boolean isOver() {
        return status != GameStatus.IN_PROGRESS;
    }

    /**
     * Validates and applies a move (taking {@code amount} matches).
     * Updates the heap, resolves win/loss if heap reaches zero,
     * and switches the turn to the other player.
     *
     * @param amount   number of matches to take (1–3, must not exceed heap)
     * @param mover    the player making this move
     * @throws IllegalArgumentException if the move is invalid
     * @throws IllegalStateException    if it is not {@code mover}'s turn, or the game is over
     */
    public void applyMove(int amount, Player mover) {
        // ── Guard: game already finished ────────────────────────────────────
        if (isOver()) {
            throw new IllegalStateException("Game is already over");
        }

        // ── Guard: wrong player ──────────────────────────────────────────────
        if (currentTurn != mover) {
            throw new IllegalStateException(
                    "It is " + currentTurn + "'s turn, not " + mover + "'s");
        }

        // ── Guard: invalid amount ────────────────────────────────────────────
        if (amount < MIN_TAKE || amount > MAX_TAKE) {
            throw new IllegalArgumentException(
                    "Must take between " + MIN_TAKE + " and " + MAX_TAKE + " matches");
        }
        if (amount > heap) {
            throw new IllegalArgumentException(
                    "Cannot take " + amount + " matches; only " + heap + " left");
        }

        // ── Apply the move ───────────────────────────────────────────────────
        heap -= amount;

        // ── Resolve outcome ──────────────────────────────────────────────────
        if (heap == 0) {
            // The player who took the last match LOSES (misère rule)
            status = (mover == Player.HUMAN) ? GameStatus.COMPUTER_WINS : GameStatus.HUMAN_WINS;
        } else {
            // Switch turn
            currentTurn = (mover == Player.HUMAN) ? Player.COMPUTER : Player.HUMAN;
        }
    }


}
