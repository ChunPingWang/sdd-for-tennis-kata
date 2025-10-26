package com.tennisscoring.adapters.primary.mapper;

import com.tennisscoring.adapters.primary.dto.response.*;
import com.tennisscoring.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between domain objects and DTOs.
 * 領域物件與 DTO 之間的轉換器
 */
@Component
public class MatchMapper {
    
    /**
     * Convert Match domain object to MatchResponse DTO.
     * 將 Match 領域物件轉換為 MatchResponse DTO
     * 
     * @param match the domain match object
     * @return the match response DTO
     */
    public MatchResponse toResponse(Match match) {
        if (match == null) {
            return null;
        }
        
        MatchResponse response = new MatchResponse();
        response.setMatchId(match.getMatchId());
        response.setPlayer1(toPlayerResponse(match.getPlayer1()));
        response.setPlayer2(toPlayerResponse(match.getPlayer2()));
        response.setStatus(match.getStatus().name());
        response.setCurrentScore(match.getCurrentScore());
        response.setCurrentSetNumber(match.getCurrentSetNumber());
        response.setCurrentGameNumber(match.getCurrentGameNumber());
        response.setCurrentGameTiebreak(match.isCurrentGameTiebreak());
        response.setCreatedAt(match.getCreatedAt());
        response.setCompletedAt(match.getCompletedAt());
        
        // Set winner if match is completed
        if (match.getWinnerPlayer() != null) {
            response.setWinner(toPlayerResponse(match.getWinnerPlayer()));
        }
        
        // Convert sets
        List<SetResponse> setResponses = match.getSets().stream()
                .map(this::toSetResponse)
                .collect(Collectors.toList());
        response.setSets(setResponses);
        
        return response;
    }
    
    /**
     * Convert Player domain object to PlayerResponse DTO.
     * 將 Player 領域物件轉換為 PlayerResponse DTO
     * 
     * @param player the domain player object
     * @return the player response DTO
     */
    public PlayerResponse toPlayerResponse(Player player) {
        if (player == null) {
            return null;
        }
        
        return new PlayerResponse(
                player.getPlayerId().getValue(),
                player.getName(),
                player.getSetsWon(),
                player.getGamesWon(),
                player.getPointsWon()
        );
    }
    
    /**
     * Convert Set domain object to SetResponse DTO.
     * 將 Set 領域物件轉換為 SetResponse DTO
     * 
     * @param set the domain set object
     * @return the set response DTO
     */
    public SetResponse toSetResponse(Set set) {
        if (set == null) {
            return null;
        }
        
        SetResponse response = new SetResponse();
        response.setSetNumber(set.getSetNumber());
        response.setCompleted(set.isCompleted());
        
        // Get game counts for each player
        // This is a simplified approach - in a real implementation,
        // we'd need to track games won by each player in the set
        List<Game> games = set.getGames();
        int player1Games = 0;
        int player2Games = 0;
        
        // Count completed games won by each player
        for (Game game : games) {
            if (game.isCompleted() && game.getWinner() != null) {
                // This is simplified - we'd need proper player ID comparison
                // For now, we'll use a basic counting approach
                if (games.indexOf(game) % 2 == 0) {
                    player1Games++;
                } else {
                    player2Games++;
                }
            }
        }
        
        response.setPlayer1Games(player1Games);
        response.setPlayer2Games(player2Games);
        
        // Set winner if set is completed
        if (set.isCompleted() && set.getWinner() != null) {
            response.setWinnerId(set.getWinner().getValue());
        }
        
        // Check for tiebreak
        boolean hasTiebreak = games.stream().anyMatch(Game::isTiebreak);
        response.setHasTiebreak(hasTiebreak);
        
        // Convert games
        List<GameResponse> gameResponses = games.stream()
                .map(this::toGameResponse)
                .collect(Collectors.toList());
        response.setGames(gameResponses);
        
        // Convert tiebreak if exists
        if (hasTiebreak) {
            Game tiebreakGame = games.stream()
                    .filter(Game::isTiebreak)
                    .findFirst()
                    .orElse(null);
            
            if (tiebreakGame != null) {
                response.setTiebreak(toTiebreakResponse(tiebreakGame));
            }
        }
        
        return response;
    }
    
    /**
     * Convert Game domain object to GameResponse DTO.
     * 將 Game 領域物件轉換為 GameResponse DTO
     * 
     * @param game the domain game object
     * @return the game response DTO
     */
    public GameResponse toGameResponse(Game game) {
        if (game == null) {
            return null;
        }
        
        GameResponse response = new GameResponse();
        response.setGameNumber(game.getGameNumber());
        response.setCompleted(game.isCompleted());
        response.setTiebreak(game.isTiebreak());
        
        // Get player scores - this is simplified
        // In a real implementation, we'd need proper player ID mapping
        List<PlayerId> playerIds = new ArrayList<>(game.getPlayerScores().keySet());
        if (playerIds.size() >= 2) {
            GameScore player1Score = game.getPlayerScore(playerIds.get(0));
            GameScore player2Score = game.getPlayerScore(playerIds.get(1));
            
            response.setPlayer1Score(player1Score.getDisplayValue());
            response.setPlayer2Score(player2Score.getDisplayValue());
        }
        
        // Set winner if game is completed
        if (game.isCompleted() && game.getWinner() != null) {
            response.setWinnerId(game.getWinner().getValue());
        }
        
        // Set game status
        if (game.isCompleted()) {
            response.setStatus("已完成");
        } else if (game.isTiebreak()) {
            response.setStatus("搶七局進行中");
        } else {
            response.setStatus("進行中");
        }
        
        return response;
    }
    
    /**
     * Convert tiebreak game to TiebreakResponse DTO.
     * 將搶七局轉換為 TiebreakResponse DTO
     * 
     * @param tiebreakGame the tiebreak game object
     * @return the tiebreak response DTO
     */
    public TiebreakResponse toTiebreakResponse(Game tiebreakGame) {
        if (tiebreakGame == null || !tiebreakGame.isTiebreak()) {
            return null;
        }
        
        TiebreakResponse response = new TiebreakResponse();
        response.setCompleted(tiebreakGame.isCompleted());
        
        // Get tiebreak scores - this is simplified
        List<PlayerId> playerIds = new ArrayList<>(tiebreakGame.getPlayerScores().keySet());
        if (playerIds.size() >= 2) {
            GameScore player1Score = tiebreakGame.getPlayerScore(playerIds.get(0));
            GameScore player2Score = tiebreakGame.getPlayerScore(playerIds.get(1));
            
            // For tiebreak, we need to parse the numeric values
            try {
                response.setPlayer1Points(Integer.parseInt(player1Score.getDisplayValue()));
                response.setPlayer2Points(Integer.parseInt(player2Score.getDisplayValue()));
            } catch (NumberFormatException e) {
                // Fallback to 0 if parsing fails
                response.setPlayer1Points(0);
                response.setPlayer2Points(0);
            }
        }
        
        // Set winner if tiebreak is completed
        if (tiebreakGame.isCompleted() && tiebreakGame.getWinner() != null) {
            response.setWinnerId(tiebreakGame.getWinner().getValue());
        }
        
        return response;
    }
    
    /**
     * Convert list of matches to list of match responses.
     * 將比賽列表轉換為比賽回應列表
     * 
     * @param matches the list of domain match objects
     * @return the list of match response DTOs
     */
    public List<MatchResponse> toResponseList(List<Match> matches) {
        if (matches == null) {
            return new ArrayList<>();
        }
        
        return matches.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}