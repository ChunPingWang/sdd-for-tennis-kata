package com.tennisscoring.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennisscoring.TennisScoringSystemApplication;
import com.tennisscoring.adapters.primary.dto.request.CreateMatchRequest;
import com.tennisscoring.adapters.primary.dto.request.ScorePointRequest;
import com.tennisscoring.adapters.primary.dto.response.MatchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration tests for complete tennis match workflows.
 * Tests the entire business flow from match creation to completion.
 */
@SpringBootTest(classes = TennisScoringSystemApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EndToEndMatchFlowTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateMatchRequest validCreateRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        validCreateRequest = new CreateMatchRequest();
        validCreateRequest.setPlayer1Name("Rafael Nadal");
        validCreateRequest.setPlayer2Name("Roger Federer");
    }

    @Test
    @DisplayName("Should complete a full tennis match workflow")
    void shouldCompleteFullTennisMatchWorkflow() throws Exception {
        // Step 1: Create a match
        MvcResult createResult = mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.player1.name").value("Rafael Nadal"))
                .andExpect(jsonPath("$.player2.name").value("Roger Federer"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.currentScore").value("0-0 (0-0)"))
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        MatchResponse match = objectMapper.readValue(responseContent, MatchResponse.class);
        String matchId = match.getMatchId();
        String player1Id = match.getPlayer1().getPlayerId();
        String player2Id = match.getPlayer2().getPlayerId();

        // Step 2: Verify match exists in the system
        mockMvc.perform(get("/api/matches/{matchId}", matchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchId").value(matchId))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // Step 3: Score some points for player 1 (15-0)
        ScorePointRequest scoreRequest = new ScorePointRequest(player1Id);
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (15-0)"));

        // Step 4: Score more points for player 1 (30-0)
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (30-0)"));

        // Step 5: Score points for player 2 (30-15)
        ScorePointRequest player2ScoreRequest = new ScorePointRequest(player2Id);
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player2ScoreRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (30-15)"));

        // Step 6: Player 1 scores to 40-15
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (40-15)"));

        // Step 7: Player 1 wins the game (1-0)
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("1-0 (0-0)"));

        // Step 8: Verify match is still in progress
        MvcResult currentMatchResult = mockMvc.perform(get("/api/matches/{matchId}", matchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andReturn();

        String currentMatchContent = currentMatchResult.getResponse().getContentAsString();
        MatchResponse currentMatch = objectMapper.readValue(currentMatchContent, MatchResponse.class);
        
        // Verify the match structure
        assertThat(currentMatch.getSets()).hasSize(1);
        assertThat(currentMatch.getSets().get(0).getGames()).hasSizeGreaterThan(0);

        // Step 9: Verify all matches endpoint includes our match
        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].matchId").value(matchId));
    }

    @Test
    @DisplayName("Should handle deuce scenario correctly")
    void shouldHandleDeuceScenarioCorrectly() throws Exception {
        // Create a match
        MvcResult createResult = mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        MatchResponse match = objectMapper.readValue(responseContent, MatchResponse.class);
        String matchId = match.getMatchId();
        String player1Id = match.getPlayer1().getPlayerId();
        String player2Id = match.getPlayer2().getPlayerId();

        // Score to 40-40 (deuce)
        ScorePointRequest player1Score = new ScorePointRequest(player1Id);
        ScorePointRequest player2Score = new ScorePointRequest(player2Id);

        // Player 1: 15-0, 30-0, 40-0
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(player1Score)))
                    .andExpect(status().isOk());
        }

        // Player 2: 40-15, 40-30, 40-40 (deuce)
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(player2Score)))
                    .andExpect(status().isOk());
        }

        // Verify deuce state
        mockMvc.perform(get("/api/matches/{matchId}", matchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (40-40)"));

        // Player 1 gets advantage
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player1Score)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (AD-40)"));

        // Player 2 equalizes back to deuce
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player2Score)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (平分)"));

        // Player 1 gets advantage and wins the game
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player1Score)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (AD-40)"));

        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player1Score)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("1-0 (0-0)"));
    }

    @Test
    @DisplayName("Should handle match cancellation workflow")
    void shouldHandleMatchCancellationWorkflow() throws Exception {
        // Create a match
        MvcResult createResult = mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        MatchResponse match = objectMapper.readValue(responseContent, MatchResponse.class);
        String matchId = match.getMatchId();
        String player1Id = match.getPlayer1().getPlayerId();

        // Score some points
        ScorePointRequest scoreRequest = new ScorePointRequest(player1Id);
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (15-0)"));

        // Cancel the match
        mockMvc.perform(put("/api/matches/{matchId}/cancel", matchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // Verify cannot score on cancelled match
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());

        // Verify match still exists but is cancelled
        mockMvc.perform(get("/api/matches/{matchId}", matchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("Should handle match deletion workflow")
    void shouldHandleMatchDeletionWorkflow() throws Exception {
        // Create a match
        MvcResult createResult = mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        MatchResponse match = objectMapper.readValue(responseContent, MatchResponse.class);
        String matchId = match.getMatchId();

        // Verify match exists
        mockMvc.perform(get("/api/matches/{matchId}", matchId))
                .andExpect(status().isOk());

        // Delete the match
        mockMvc.perform(delete("/api/matches/{matchId}", matchId))
                .andExpect(status().isNoContent());

        // Verify match no longer exists
        mockMvc.perform(get("/api/matches/{matchId}", matchId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Match Not Found"));

        // Verify all matches endpoint is empty
        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should handle multiple concurrent matches")
    void shouldHandleMultipleConcurrentMatches() throws Exception {
        // Create first match
        CreateMatchRequest match1Request = new CreateMatchRequest();
        match1Request.setPlayer1Name("Novak Djokovic");
        match1Request.setPlayer2Name("Andy Murray");

        MvcResult match1Result = mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(match1Request)))
                .andExpect(status().isCreated())
                .andReturn();

        MatchResponse match1 = objectMapper.readValue(
                match1Result.getResponse().getContentAsString(), MatchResponse.class);

        // Create second match
        MvcResult match2Result = mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        MatchResponse match2 = objectMapper.readValue(
                match2Result.getResponse().getContentAsString(), MatchResponse.class);

        // Score points in both matches
        ScorePointRequest match1Player1Score = new ScorePointRequest(match1.getPlayer1().getPlayerId());
        ScorePointRequest match2Player1Score = new ScorePointRequest(match2.getPlayer1().getPlayerId());

        // Score in match 1
        mockMvc.perform(post("/api/matches/{matchId}/score", match1.getMatchId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(match1Player1Score)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (15-0)"));

        // Score in match 2
        mockMvc.perform(post("/api/matches/{matchId}/score", match2.getMatchId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(match2Player1Score)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentScore").value("0-0 (15-0)"));

        // Verify both matches exist and have different states
        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        // Verify individual match states
        mockMvc.perform(get("/api/matches/{matchId}", match1.getMatchId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.player1.name").value("Novak Djokovic"))
                .andExpect(jsonPath("$.currentScore").value("0-0 (15-0)"));

        mockMvc.perform(get("/api/matches/{matchId}", match2.getMatchId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.player1.name").value("Rafael Nadal"))
                .andExpect(jsonPath("$.currentScore").value("0-0 (15-0)"));
    }

    @Test
    @DisplayName("Should validate business rules throughout match lifecycle")
    void shouldValidateBusinessRulesThroughoutMatchLifecycle() throws Exception {
        // Test duplicate player names
        CreateMatchRequest duplicateRequest = new CreateMatchRequest();
        duplicateRequest.setPlayer1Name("Same Player");
        duplicateRequest.setPlayer2Name("Same Player");

        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate Player"));

        // Test empty player names
        CreateMatchRequest emptyRequest = new CreateMatchRequest();
        emptyRequest.setPlayer1Name("");
        emptyRequest.setPlayer2Name("Valid Player");

        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        // Create valid match for further testing
        MvcResult createResult = mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        MatchResponse match = objectMapper.readValue(responseContent, MatchResponse.class);
        String matchId = match.getMatchId();

        // Test invalid player ID for scoring
        ScorePointRequest invalidPlayerScore = new ScorePointRequest("invalid-uuid");
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPlayerScore)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));

        // Test scoring with non-existent player ID
        ScorePointRequest nonExistentPlayerScore = new ScorePointRequest("123e4567-e89b-12d3-a456-426614174000");
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentPlayerScore)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}