package com.tennisscoring.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennisscoring.adapters.primary.dto.request.CreateMatchRequest;
import com.tennisscoring.adapters.primary.dto.request.ScorePointRequest;
import com.tennisscoring.adapters.primary.dto.response.MatchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Match Controller Integration Tests")
class MatchControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Should create match successfully")
    void shouldCreateMatchSuccessfully() throws Exception {
        // Given
        CreateMatchRequest request = new CreateMatchRequest();
        request.setPlayer1Name("John Doe");
        request.setPlayer2Name("Jane Smith");

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.player1.name").value("John Doe"))
                .andExpect(jsonPath("$.player2.name").value("Jane Smith"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.matchId").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        MatchResponse matchResponse = objectMapper.readValue(responseBody, MatchResponse.class);
        
        assertThat(matchResponse.getMatchId()).isNotNull();
        assertThat(matchResponse.getPlayer1().getName()).isEqualTo("John Doe");
        assertThat(matchResponse.getPlayer2().getName()).isEqualTo("Jane Smith");
        assertThat(matchResponse.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("Should return 400 for invalid create match request")
    void shouldReturn400ForInvalidCreateMatchRequest() throws Exception {
        // Given
        CreateMatchRequest request = new CreateMatchRequest();
        request.setPlayer1Name(""); // Invalid empty name
        request.setPlayer2Name("Jane Smith");

        // When & Then
        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return 409 for duplicate player names")
    void shouldReturn409ForDuplicatePlayerNames() throws Exception {
        // Given
        CreateMatchRequest request = new CreateMatchRequest();
        request.setPlayer1Name("John Doe");
        request.setPlayer2Name("john doe"); // Same name, different case

        // When & Then
        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should get match successfully")
    void shouldGetMatchSuccessfully() throws Exception {
        // Given - Create a match first
        String matchId = createTestMatch("John Doe", "Jane Smith");

        // When & Then
        mockMvc.perform(get("/api/matches/{matchId}", matchId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.matchId").value(matchId))
                .andExpect(jsonPath("$.player1.name").value("John Doe"))
                .andExpect(jsonPath("$.player2.name").value("Jane Smith"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Should return 404 for non-existent match")
    void shouldReturn404ForNonExistentMatch() throws Exception {
        // Given
        String nonExistentMatchId = "123e4567-e89b-12d3-a456-426614174000";

        // When & Then
        mockMvc.perform(get("/api/matches/{matchId}", nonExistentMatchId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should get all matches successfully")
    void shouldGetAllMatchesSuccessfully() throws Exception {
        // Given - Create multiple matches
        createTestMatch("John Doe", "Jane Smith");
        createTestMatch("Alice", "Bob");

        // When & Then
        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].matchId").exists())
                .andExpect(jsonPath("$[1].matchId").exists());
    }

    @Test
    @DisplayName("Should score point successfully")
    void shouldScorePointSuccessfully() throws Exception {
        // Given - Create a match first
        String matchId = createTestMatch("John Doe", "Jane Smith");
        MatchResponse match = getMatch(matchId);
        String player1Id = match.getPlayer1().getPlayerId();

        ScorePointRequest request = new ScorePointRequest();
        request.setPlayerId(player1Id);

        // When & Then
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.matchId").value(matchId))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Should return 400 for invalid score point request")
    void shouldReturn400ForInvalidScorePointRequest() throws Exception {
        // Given
        String matchId = createTestMatch("John Doe", "Jane Smith");
        
        ScorePointRequest request = new ScorePointRequest();
        request.setPlayerId("invalid-player-id"); // Invalid UUID format

        // When & Then
        mockMvc.perform(post("/api/matches/{matchId}/score", matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should return 404 when scoring on non-existent match")
    void shouldReturn404WhenScoringOnNonExistentMatch() throws Exception {
        // Given
        String nonExistentMatchId = "123e4567-e89b-12d3-a456-426614174000";
        
        ScorePointRequest request = new ScorePointRequest();
        request.setPlayerId("123e4567-e89b-12d3-a456-426614174001");

        // When & Then
        mockMvc.perform(post("/api/matches/{matchId}/score", nonExistentMatchId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should cancel match successfully")
    void shouldCancelMatchSuccessfully() throws Exception {
        // Given
        String matchId = createTestMatch("John Doe", "Jane Smith");

        // When & Then
        mockMvc.perform(put("/api/matches/{matchId}/cancel", matchId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.matchId").value(matchId))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("Should return 404 when cancelling non-existent match")
    void shouldReturn404WhenCancellingNonExistentMatch() throws Exception {
        // Given
        String nonExistentMatchId = "123e4567-e89b-12d3-a456-426614174000";

        // When & Then
        mockMvc.perform(put("/api/matches/{matchId}/cancel", nonExistentMatchId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should delete match successfully")
    void shouldDeleteMatchSuccessfully() throws Exception {
        // Given
        String matchId = createTestMatch("John Doe", "Jane Smith");

        // When & Then
        mockMvc.perform(delete("/api/matches/{matchId}", matchId))
                .andExpect(status().isNoContent());

        // Verify match is deleted
        mockMvc.perform(get("/api/matches/{matchId}", matchId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent match")
    void shouldReturn404WhenDeletingNonExistentMatch() throws Exception {
        // Given
        String nonExistentMatchId = "123e4567-e89b-12d3-a456-426614174000";

        // When & Then
        mockMvc.perform(delete("/api/matches/{matchId}", nonExistentMatchId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should get match statistics successfully")
    void shouldGetMatchStatisticsSuccessfully() throws Exception {
        // Given - Create matches with different statuses
        String matchId1 = createTestMatch("John Doe", "Jane Smith");
        String matchId2 = createTestMatch("Alice", "Bob");
        
        // Cancel one match
        mockMvc.perform(put("/api/matches/{matchId}/cancel", matchId2));

        // When & Then
        mockMvc.perform(get("/api/matches/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalMatches").value(2))
                .andExpect(jsonPath("$.inProgressMatches").value(1))
                .andExpect(jsonPath("$.completedMatches").value(0))
                .andExpect(jsonPath("$.cancelledMatches").value(1));
    }

    @Test
    @DisplayName("Should handle malformed JSON request")
    void shouldHandleMalformedJsonRequest() throws Exception {
        // Given
        String malformedJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle missing request body")
    void shouldHandleMissingRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle unsupported media type")
    void shouldHandleUnsupportedMediaType() throws Exception {
        // Given
        CreateMatchRequest request = new CreateMatchRequest();
        request.setPlayer1Name("John Doe");
        request.setPlayer2Name("Jane Smith");

        // When & Then
        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType());
    }

    // Helper methods

    private String createTestMatch(String player1Name, String player2Name) throws Exception {
        CreateMatchRequest request = new CreateMatchRequest();
        request.setPlayer1Name(player1Name);
        request.setPlayer2Name(player2Name);

        MvcResult result = mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        MatchResponse matchResponse = objectMapper.readValue(responseBody, MatchResponse.class);
        return matchResponse.getMatchId();
    }

    private MatchResponse getMatch(String matchId) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/matches/{matchId}", matchId))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseBody, MatchResponse.class);
    }
}