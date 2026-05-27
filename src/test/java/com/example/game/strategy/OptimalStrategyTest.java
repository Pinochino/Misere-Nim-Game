package com.example.game.strategy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OptimalStrategyTest {

	private final OptimalStrategy strategy = new OptimalStrategy();

	@Test
	void chooseMove_throwsOnNonPositiveHeap() {
		assertThatThrownBy(() -> strategy.chooseMove(0))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void chooseMove_whenHeapIsWinningPosition_leavesHeapModulo4Equals1() {
		// For misère Nim (take 1-3, last loses), losing positions are heap % 4 == 1.
		int heap = 7; // winning
		int take = strategy.chooseMove(heap);
		assertThat(take).isBetween(1, 3);
		assertThat((heap - take) % 4).isEqualTo(1);
	}

	@Test
	void chooseMove_whenHeapIsLosingPosition_returnsDeterministicValidMove() {
		int heap = 5; // 5 % 4 == 1 => losing position
		int take = strategy.chooseMove(heap);
		assertThat(take).isEqualTo(1);
	}
}
