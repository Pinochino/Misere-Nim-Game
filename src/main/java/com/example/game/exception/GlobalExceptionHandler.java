package com.example.game.exception;


import com.example.game.dto.GameDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Translates domain exceptions into clean JSON HTTP error responses.
 *
 * <pre>
 * GameNotFoundException  → 404 Not Found
 * InvalidMoveException   → 400 Bad Request
 * Validation errors      → 400 Bad Request  (bean validation)
 * Other exceptions       → 500 Internal Server Error
 * </pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<GameDto.ErrorResponse> handleNotFound(GameNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new GameDto.ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidMoveException.class)
    public ResponseEntity<GameDto.ErrorResponse> handleInvalidMove(InvalidMoveException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GameDto.ErrorResponse(ex.getMessage()));
    }

    /** Handles @Valid / @Validated failures on request bodies. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GameDto.ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GameDto.ErrorResponse(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GameDto.ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GameDto.ErrorResponse("Unexpected error: " + ex.getMessage()));
    }
}
