package com.example.game.service;

import com.example.game.constant.Player;
import com.example.game.entity.Game;
import com.example.game.exception.GameNotFoundException;
import com.example.game.exception.InvalidMoveException;
import com.example.game.strategy.ComputerStrategy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    // In-memory store; keyed by game UUID
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    private final ComputerStrategy strategy;

    public GameService(ComputerStrategy strategy) {
        this.strategy = strategy;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Creates a new game with the given initial heap size.
     *
     * @param initialHeap must be >= 1
     */
    public Game startGame(int initialHeap) {
        if (initialHeap < 1) {
            throw new InvalidMoveException("Initial heap must be at least 1");
        }
        Game game = new Game(initialHeap);
        games.put(game.getId(), game);
        return game;
    }

    /**
     * Applies the human player's move, then immediately triggers the computer's
     * response move (if the game is still in progress after the human moves).
     *
     * @param gameId game session identifier
     * @param take   number of matches the human wants to take (1–3)
     * @return updated game state
     */
    public Game humanMove(String gameId, int take) {
        Game game = findGame(gameId);

        // ── Guard: game already over ─────────────────────────────────────────
        if (game.isOver()) {
            throw new InvalidMoveException("Game is already over. Status: " + game.getStatus());
        }

        // ── Guard: not human's turn ──────────────────────────────────────────
        if (game.getCurrentTurn() != Player.HUMAN) {
            throw new InvalidMoveException("It is not your turn");
        }

        // ── Apply human move (Game validates amount & heap) ──────────────────
        try {
            game.applyMove(take, Player.HUMAN);
        } catch (IllegalArgumentException e) {
            throw new InvalidMoveException(e.getMessage());
        }

        // ── Computer responds immediately (if game is still going) ───────────
        if (!game.isOver()) {
            performComputerMove(game);
        }

        return game;
    }

    /**
     * Returns the current state of a game.
     */
    public Game getState(String gameId) {
        return findGame(gameId);
    }

    /**
     * Returns the name of the active computer strategy.
     */
    public String getStrategyName() {
        return strategy.name();
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private void performComputerMove(Game game) {
        int amount = strategy.chooseMove(game.getHeap());
        // strategy.chooseMove guarantees a valid value; applyMove is the safety net
        game.applyMove(amount, Player.COMPUTER);
    }

    private Game findGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException(gameId);
        }
        return game;
    }
}
