package com.example.game.config;

import com.example.game.strategy.ComputerStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Locale;
import java.util.Map;

@Configuration
public class ComputerStrategyConfig {

	@Bean
	@Primary
	public ComputerStrategy computerStrategy(
			@Value("${game.strategy:OPTIMAL}") String strategyName,
			Map<String, ComputerStrategy> strategies
	) {
		String key = strategyName == null ? "OPTIMAL" : strategyName.trim().toUpperCase(Locale.ROOT);
		ComputerStrategy selected = strategies.get(key);
		if (selected == null) {
			throw new IllegalStateException(
					"Unknown game.strategy='" + strategyName + "'. Available: " + strategies.keySet());
		}
		return selected;
	}
}
