package com.example.game.dto;

import com.example.game.constant.GameStatus;
import com.example.game.constant.Player;
import com.example.game.entity.Game;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * Data Transfer Objects used by the REST layer.
 * Kept in one file for brevity; split into separate files in larger projects.
 */
public final class GameDto {

    private GameDto() {}

    // ── Requests ──────────────────────────────────────────────────────────────

    /** Body for POST /games */
    public record StartGameRequest(
            @Min(value = 1, message = "initialHeap must be at least 1")
            int initialHeap
    ) {}

    /** Body for POST /games/{id}/moves */
    public record MoveRequest(
            @Min(value = 1, message = "take must be at least 1")
            @Max(value = 3, message = "take must be at most 3")
            int take
    ) {}

    // ── Responses ─────────────────────────────────────────────────────────────

    /** Returned for every game-related endpoint. */
    public record GameResponse(
            String gameId,
            int heap,
            Player currentTurn,   // null when game is over
            GameStatus status,
            String message        // human-friendly description
    ) {
        /** Build a GameResponse from a domain Game object. */
        public static GameResponse from(Game game) {
            return new GameResponse(
                    game.getId(),
                    game.getHeap(),
                    game.isOver() ? null : game.getCurrentTurn(),
                    game.getStatus(),
                    buildMessage(game)
            );
        }

        private static String buildMessage(Game game) {
            return switch (game.getStatus()) {
                case IN_PROGRESS -> "Game in progress. " + game.getCurrentTurn() + "'s turn. "
                        + game.getHeap() + " match(es) remaining.";
                case HUMAN_WINS  -> "You WIN! The computer took the last match.";
                case COMPUTER_WINS -> "You LOSE. You took the last match.";
            };
        }
    }

    /** Generic error response wrapper. */
    public record ErrorResponse(String error) {}
}