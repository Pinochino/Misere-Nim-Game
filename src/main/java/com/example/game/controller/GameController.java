package com.example.game.controller;

import com.example.game.dto.GameDto;
import com.example.game.service.GameService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for playing misère Nim (one heap, take 1-3, taking the last loses).
 */
@RestController
@RequestMapping("/games")
@Validated
public class GameController {

	private final GameService gameService;

	public GameController(GameService gameService) {
		this.gameService = gameService;
	}

	/** Start a new game with the given initial heap size. */
	@PostMapping
	public GameDto.GameResponse start(@Valid @RequestBody GameDto.StartGameRequest request) {
		return GameDto.GameResponse.from(gameService.startGame(request.initialHeap()));
	}

	/**
	 * Human makes a move; server immediately makes the computer move (if applicable).
	 */
	@PostMapping("/{gameId}/moves")
	public GameDto.GameResponse move(
			@PathVariable String gameId,
			@Valid @RequestBody GameDto.MoveRequest request
	) {
		return GameDto.GameResponse.from(gameService.humanMove(gameId, request.take()));
	}

	/** Return current game state. */
	@GetMapping("/{gameId}")
	public GameDto.GameResponse state(@PathVariable String gameId) {
		return GameDto.GameResponse.from(gameService.getState(gameId));
	}
}
