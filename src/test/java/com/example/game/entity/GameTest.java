package com.example.game.entity;

import com.example.game.constant.GameStatus;
import com.example.game.constant.Player;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

	@Test
	void newGame_startsInProgressWithHumanTurn() {
		Game game = new Game(7);
		assertThat(game.getHeap()).isEqualTo(7);
		assertThat(game.getStatus()).isEqualTo(GameStatus.IN_PROGRESS);
		assertThat(game.getCurrentTurn()).isEqualTo(Player.HUMAN);
		assertThat(game.isOver()).isFalse();
	}

	@Test
	void applyMove_rejectsTakingOutOfRange() {
		Game game = new Game(7);
		assertThatThrownBy(() -> game.applyMove(0, Player.HUMAN))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> game.applyMove(4, Player.HUMAN))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void applyMove_rejectsTakingMoreThanHeap() {
		Game game = new Game(2);
		assertThatThrownBy(() -> game.applyMove(3, Player.HUMAN))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("only 2 left");
	}

	@Test
	void misereRule_takingLastMatchLoses() {
		Game game = new Game(1);
		game.applyMove(1, Player.HUMAN);
		assertThat(game.getHeap()).isZero();
		assertThat(game.getStatus()).isEqualTo(GameStatus.COMPUTER_WINS);
		assertThat(game.isOver()).isTrue();
	}

	@Test
	void applyMove_switchesTurnWhenNotOver() {
		Game game = new Game(5);
		game.applyMove(1, Player.HUMAN);
		assertThat(game.getHeap()).isEqualTo(4);
		assertThat(game.getStatus()).isEqualTo(GameStatus.IN_PROGRESS);
		assertThat(game.getCurrentTurn()).isEqualTo(Player.COMPUTER);
	}
}
