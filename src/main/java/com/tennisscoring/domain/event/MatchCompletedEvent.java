package com.tennisscoring.domain.event;

/**
 * Domain event fired when a tennis match is completed.
 */
public class MatchCompletedEvent extends DomainEvent {
    
    private final String matchId;
    private final String winnerId;
    private final String finalScore;
    private final int totalSets;
    
    public MatchCompletedEvent(String matchId, String winnerId, String finalScore, int totalSets) {
        super();
        this.matchId = matchId;
        this.winnerId = winnerId;
        this.finalScore = finalScore;
        this.totalSets = totalSets;
    }
    
    public String getMatchId() {
        return matchId;
    }
    
    public String getWinnerId() {
        return winnerId;
    }
    
    public String getFinalScore() {
        return finalScore;
    }
    
    public int getTotalSets() {
        return totalSets;
    }
    
    @Override
    public String toString() {
        return "MatchCompletedEvent{" +
                "matchId='" + matchId + '\'' +
                ", winnerId='" + winnerId + '\'' +
                ", finalScore='" + finalScore + '\'' +
                ", totalSets=" + totalSets +
                ", eventId='" + getEventId() + '\'' +
                ", occurredAt=" + getOccurredAt() +
                '}';
    }
}