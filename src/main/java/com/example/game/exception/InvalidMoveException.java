package com.example.game.exception;

/**
 * Thrown when a player attempts an illegal move.
 */
public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(String message) {
        super(message);
    }
}
