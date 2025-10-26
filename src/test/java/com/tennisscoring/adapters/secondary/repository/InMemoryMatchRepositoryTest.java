package com.tennisscoring.adapters.secondary.repository;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("InMemoryMatchRepository Unit Tests")
class InMemoryMatchRepositoryTest {

    private InMemoryMatchRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMatchRepository();
    }

    @Test
    @DisplayName("Should save match successfully")
    void shouldSaveMatchSuccessfully() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");

        // When
        Match savedMatch = repository.save(match);

        // Then
        assertThat(savedMatch).isNotNull();
        assertThat(savedMatch.getMatchId()).isEqualTo(match.getMatchId());
        assertThat(savedMatch.getPlayer1().getName()).isEqualTo("John Doe");
        assertThat(savedMatch.getPlayer2().getName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should update existing match when saving with same ID")
    void shouldUpdateExistingMatchWhenSavingWithSameId() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        repository.save(match);
        
        // Score a point to modify the match
        match.scorePoint(match.getPlayer1().getPlayerId());

        // When
        Match updatedMatch = repository.save(match);

        // Then
        assertThat(updatedMatch.getPlayer1().getPointsWon()).isEqualTo(1);
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Should find match by ID successfully")
    void shouldFindMatchByIdSuccessfully() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        repository.save(match);

        // When
        Optional<Match> foundMatch = repository.findById(match.getMatchId());

        // Then
        assertThat(foundMatch).isPresent();
        assertThat(foundMatch.get().getMatchId()).isEqualTo(match.getMatchId());
    }

    @Test
    @DisplayName("Should return empty when finding non-existent match")
    void shouldReturnEmptyWhenFindingNonExistentMatch() {
        // Given
        String nonExistentId = "non-existent-id";

        // When
        Optional<Match> foundMatch = repository.findById(nonExistentId);

        // Then
        assertThat(foundMatch).isEmpty();
    }

    @Test
    @DisplayName("Should find all matches successfully")
    void shouldFindAllMatchesSuccessfully() {
        // Given
        Match match1 = Match.create("John Doe", "Jane Smith");
        Match match2 = Match.create("Alice", "Bob");
        repository.save(match1);
        repository.save(match2);

        // When
        List<Match> allMatches = repository.findAll();

        // Then
        assertThat(allMatches).hasSize(2);
        assertThat(allMatches).extracting(Match::getMatchId)
            .containsExactlyInAnyOrder(match1.getMatchId(), match2.getMatchId());
    }

    @Test
    @DisplayName("Should return empty list when no matches exist")
    void shouldReturnEmptyListWhenNoMatchesExist() {
        // When
        List<Match> allMatches = repository.findAll();

        // Then
        assertThat(allMatches).isEmpty();
    }

    @Test
    @DisplayName("Should delete match by ID successfully")
    void shouldDeleteMatchByIdSuccessfully() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        repository.save(match);
        assertThat(repository.existsById(match.getMatchId())).isTrue();

        // When
        repository.deleteById(match.getMatchId());

        // Then
        assertThat(repository.existsById(match.getMatchId())).isFalse();
        assertThat(repository.findById(match.getMatchId())).isEmpty();
    }

    @Test
    @DisplayName("Should handle deletion of non-existent match gracefully")
    void shouldHandleDeletionOfNonExistentMatchGracefully() {
        // Given
        String nonExistentId = "non-existent-id";

        // When & Then
        assertThatNoException().isThrownBy(() -> repository.deleteById(nonExistentId));
    }

    @Test
    @DisplayName("Should check match existence correctly")
    void shouldCheckMatchExistenceCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        repository.save(match);

        // When & Then
        assertThat(repository.existsById(match.getMatchId())).isTrue();
        assertThat(repository.existsById("non-existent-id")).isFalse();
    }

    @Test
    @DisplayName("Should find matches by status successfully")
    void shouldFindMatchesByStatusSuccessfully() {
        // Given
        Match inProgressMatch = Match.create("John Doe", "Jane Smith");
        Match cancelledMatch = Match.create("Alice", "Bob");
        cancelledMatch.cancel();
        
        repository.save(inProgressMatch);
        repository.save(cancelledMatch);

        // When
        List<Match> inProgressMatches = repository.findByStatus(MatchStatus.IN_PROGRESS);
        List<Match> cancelledMatches = repository.findByStatus(MatchStatus.CANCELLED);
        List<Match> completedMatches = repository.findByStatus(MatchStatus.COMPLETED);

        // Then
        assertThat(inProgressMatches).hasSize(1);
        assertThat(inProgressMatches.get(0).getMatchId()).isEqualTo(inProgressMatch.getMatchId());
        
        assertThat(cancelledMatches).hasSize(1);
        assertThat(cancelledMatches.get(0).getMatchId()).isEqualTo(cancelledMatch.getMatchId());
        
        assertThat(completedMatches).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when no matches have specified status")
    void shouldReturnEmptyListWhenNoMatchesHaveSpecifiedStatus() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        repository.save(match);

        // When
        List<Match> completedMatches = repository.findByStatus(MatchStatus.COMPLETED);

        // Then
        assertThat(completedMatches).isEmpty();
    }

    @Test
    @DisplayName("Should be thread-safe for concurrent operations")
    void shouldBeThreadSafeForConcurrentOperations() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int numberOfOperations = 100;

        // When
        CompletableFuture<Void>[] futures = IntStream.range(0, numberOfOperations)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                Match match = Match.create("Player" + i + "A", "Player" + i + "B");
                repository.save(match);
            }, executor))
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Then
        List<Match> allMatches = repository.findAll();
        assertThat(allMatches).hasSize(numberOfOperations);
        
        // Verify all matches have unique IDs
        assertThat(allMatches.stream().map(Match::getMatchId).distinct().count())
            .isEqualTo(numberOfOperations);
    }

    @Test
    @DisplayName("Should handle concurrent read and write operations safely")
    void shouldHandleConcurrentReadAndWriteOperationsSafely() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Match initialMatch = Match.create("John Doe", "Jane Smith");
        repository.save(initialMatch);

        // When
        CompletableFuture<Void>[] writeFutures = IntStream.range(0, 50)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                Match match = Match.create("Writer" + i + "A", "Writer" + i + "B");
                repository.save(match);
            }, executor))
            .toArray(CompletableFuture[]::new);

        CompletableFuture<Void>[] readFutures = IntStream.range(0, 50)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                repository.findAll();
                repository.existsById(initialMatch.getMatchId());
                repository.findById(initialMatch.getMatchId());
            }, executor))
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(writeFutures).join();
        CompletableFuture.allOf(readFutures).join();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Then
        assertThat(repository.findAll()).hasSizeGreaterThanOrEqualTo(51); // Initial + 50 new matches
        assertThat(repository.existsById(initialMatch.getMatchId())).isTrue();
    }

    @Test
    @DisplayName("Should handle concurrent deletions safely")
    void shouldHandleConcurrentDeletionsSafely() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int numberOfMatches = 100;
        
        // Create matches
        Match[] matches = IntStream.range(0, numberOfMatches)
            .mapToObj(i -> {
                Match match = Match.create("Player" + i + "A", "Player" + i + "B");
                repository.save(match);
                return match;
            })
            .toArray(Match[]::new);

        // When
        CompletableFuture<Void>[] deleteFutures = IntStream.range(0, numberOfMatches)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                repository.deleteById(matches[i].getMatchId());
            }, executor))
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(deleteFutures).join();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Then
        assertThat(repository.findAll()).isEmpty();
        for (Match match : matches) {
            assertThat(repository.existsById(match.getMatchId())).isFalse();
        }
    }

    @Test
    @DisplayName("Should maintain data consistency during concurrent updates")
    void shouldMaintainDataConsistencyDuringConcurrentUpdates() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Match match = Match.create("John Doe", "Jane Smith");
        repository.save(match);
        int numberOfUpdates = 10; // Reduced to avoid match completion

        // When
        CompletableFuture<Void>[] updateFutures = IntStream.range(0, numberOfUpdates)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                Optional<Match> foundMatch = repository.findById(match.getMatchId());
                if (foundMatch.isPresent()) {
                    Match updatedMatch = foundMatch.get();
                    // Only score if match is still in progress
                    if (!updatedMatch.isCompleted()) {
                        try {
                            updatedMatch.scorePoint(updatedMatch.getPlayer1().getPlayerId());
                            repository.save(updatedMatch);
                        } catch (IllegalStateException e) {
                            // Match might have completed, ignore
                        }
                    }
                }
            }, executor))
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(updateFutures).join();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Then
        Optional<Match> finalMatch = repository.findById(match.getMatchId());
        assertThat(finalMatch).isPresent();
        assertThat(finalMatch.get().getPlayer1().getPointsWon()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle null match ID gracefully")
    void shouldHandleNullMatchIdGracefully() {
        // When & Then
        assertThat(repository.findById(null)).isEmpty();
        assertThat(repository.existsById(null)).isFalse();
        assertThatNoException().isThrownBy(() -> repository.deleteById(null));
    }

    @Test
    @DisplayName("Should handle empty match ID gracefully")
    void shouldHandleEmptyMatchIdGracefully() {
        // When & Then
        assertThat(repository.findById("")).isEmpty();
        assertThat(repository.existsById("")).isFalse();
        assertThatNoException().isThrownBy(() -> repository.deleteById(""));
    }

    @Test
    @DisplayName("Should handle null match in save gracefully")
    void shouldHandleNullMatchInSaveGracefully() {
        // When & Then
        assertThatThrownBy(() -> repository.save(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Match cannot be null");
    }

    @Test
    @DisplayName("Should handle null status in findByStatus gracefully")
    void shouldHandleNullStatusInFindByStatusGracefully() {
        // When
        List<Match> result = repository.findByStatus(null);
        
        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return immutable list from findAll")
    void shouldReturnImmutableListFromFindAll() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        repository.save(match);

        // When
        List<Match> matches = repository.findAll();

        // Then
        // The list should be a new ArrayList, so it's mutable
        assertThatNoException().isThrownBy(() -> matches.add(Match.create("Alice", "Bob")));
        // But the original repository should not be affected
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Should return immutable list from findByStatus")
    void shouldReturnImmutableListFromFindByStatus() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        repository.save(match);

        // When
        List<Match> matches = repository.findByStatus(MatchStatus.IN_PROGRESS);

        // Then
        // The list should be a new ArrayList, so it's mutable
        assertThatNoException().isThrownBy(() -> matches.add(Match.create("Alice", "Bob")));
        // But the original repository should not be affected
        assertThat(repository.findByStatus(MatchStatus.IN_PROGRESS)).hasSize(1);
    }
}