package com.tennisscoring.domain.event;

/**
 * Domain event fired when a player scores a point in a match.
 */
public class PointScoredEvent extends DomainEvent {
    
    private final String matchId;
    private final String playerId;
    private final String currentScore;
    private final int currentSet;
    private final int currentGame;
    
    public PointScoredEvent(String matchId, String playerId, String currentScore, int currentSet, int currentGame) {
        super();
        this.matchId = matchId;
        this.playerId = playerId;
        this.currentScore = currentScore;
        this.currentSet = currentSet;
        this.currentGame = currentGame;
    }
    
    public String getMatchId() {
        return matchId;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public String getCurrentScore() {
        return currentScore;
    }
    
    public int getCurrentSet() {
        return currentSet;
    }
    
    public int getCurrentGame() {
        return currentGame;
    }
    
    @Override
    public String toString() {
        return "PointScoredEvent{" +
                "matchId='" + matchId + '\'' +
                ", playerId='" + playerId + '\'' +
                ", currentScore='" + currentScore + '\'' +
                ", currentSet=" + currentSet +
                ", currentGame=" + currentGame +
                ", eventId='" + getEventId() + '\'' +
                ", occurredAt=" + getOccurredAt() +
                '}';
    }
}