package com.example.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc

@SpringBootTest
class GameApplicationTests {


	private final MockMvc mockMvc;
	private final ObjectMapper objectMapper;

	GameApplicationTests(MockMvc mockMvc, ObjectMapper objectMapper) {
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
	}

	@Test
	void startGame_returnsInitialState() throws Exception {
		String response = mockMvc.perform(
				post("/games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"initialHeap\":7}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.heap").value(7))
				.andExpect(jsonPath("$.status").value("IN_PROGRESS"))
				.andExpect(jsonPath("$.currentTurn").value("HUMAN"))
				.andExpect(jsonPath("$.gameId").isNotEmpty())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode node = objectMapper.readTree(response);
		assertThat(node.get("gameId").asText()).isNotBlank();
	}

	@Test
	void stateEndpoint_returnsCurrentGameState() throws Exception {
		String start = mockMvc.perform(
				post("/games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"initialHeap\":6}"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		String gameId = objectMapper.readTree(start).get("gameId").asText();

		mockMvc.perform(get("/games/{id}", gameId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.gameId").value(gameId))
				.andExpect(jsonPath("$.heap").value(6))
				.andExpect(jsonPath("$.status").value("IN_PROGRESS"))
				.andExpect(jsonPath("$.currentTurn").value("HUMAN"));
	}

	@Test
	void humanMove_triggersComputerMove_andReturnsUpdatedState() throws Exception {
		String start = mockMvc.perform(
				post("/games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"initialHeap\":7}"))
				.andReturn().getResponse().getContentAsString();
		String gameId = objectMapper.readTree(start).get("gameId").asText();

		// Human takes 2 from 7 -> heap 5; computer (deterministic optimal) takes 1 -> heap 4
		mockMvc.perform(
				post("/games/{id}/moves", gameId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"take\":2}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.gameId").value(gameId))
				.andExpect(jsonPath("$.heap").value(4))
				.andExpect(jsonPath("$.status").value("IN_PROGRESS"))
				.andExpect(jsonPath("$.currentTurn").value("HUMAN"));
	}

	@Test
	void misereRule_humanTakingLastMatch_losesImmediately() throws Exception {
		String start = mockMvc.perform(
				post("/games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"initialHeap\":1}"))
				.andReturn().getResponse().getContentAsString();
		String gameId = objectMapper.readTree(start).get("gameId").asText();

		mockMvc.perform(
				post("/games/{id}/moves", gameId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"take\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.heap").value(0))
				.andExpect(jsonPath("$.status").value("COMPUTER_WINS"))
				.andExpect(jsonPath("$.currentTurn").value(org.hamcrest.Matchers.nullValue()));
	}

	@Test
	void misereRule_computerTakingLastMatch_causesHumanWin() throws Exception {
		String start = mockMvc.perform(
				post("/games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"initialHeap\":2}"))
				.andReturn().getResponse().getContentAsString();
		String gameId = objectMapper.readTree(start).get("gameId").asText();

		// Human takes 1 -> heap 1; computer forced to take 1 -> takes last and loses
		mockMvc.perform(
				post("/games/{id}/moves", gameId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"take\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.heap").value(0))
				.andExpect(jsonPath("$.status").value("HUMAN_WINS"))
				.andExpect(jsonPath("$.currentTurn").value(org.hamcrest.Matchers.nullValue()));
	}

	@Test
	void validation_takeMustBeBetween1And3_returns400() throws Exception {
		String start = mockMvc.perform(
				post("/games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"initialHeap\":7}"))
				.andReturn().getResponse().getContentAsString();
		String gameId = objectMapper.readTree(start).get("gameId").asText();

		mockMvc.perform(
				post("/games/{id}/moves", gameId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"take\":4}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").exists());
	}

	@Test
	void invalidMove_takingMoreThanHeap_returns400() throws Exception {
		String start = mockMvc.perform(
				post("/games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"initialHeap\":2}"))
				.andReturn().getResponse().getContentAsString();
		String gameId = objectMapper.readTree(start).get("gameId").asText();

		mockMvc.perform(
				post("/games/{id}/moves", gameId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"take\":3}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("only 2 left")));
	}

	@Test
	void unknownGame_returns404() throws Exception {
		mockMvc.perform(get("/games/{id}", "missing"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Game not found")));
	}

}
