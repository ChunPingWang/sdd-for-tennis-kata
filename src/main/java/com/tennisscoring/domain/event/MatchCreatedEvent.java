package com.tennisscoring.domain.event;

/**
 * Domain event fired when a new tennis match is created.
 */
public class MatchCreatedEvent extends DomainEvent {
    
    private final String matchId;
    private final String player1Name;
    private final String player2Name;
    
    public MatchCreatedEvent(String matchId, String player1Name, String player2Name) {
        super();
        this.matchId = matchId;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }
    
    public String getMatchId() {
        return matchId;
    }
    
    public String getPlayer1Name() {
        return player1Name;
    }
    
    public String getPlayer2Name() {
        return player2Name;
    }
    
    @Override
    public String toString() {
        return "MatchCreatedEvent{" +
                "matchId='" + matchId + '\'' +
                ", player1Name='" + player1Name + '\'' +
                ", player2Name='" + player2Name + '\'' +
                ", eventId='" + getEventId() + '\'' +
                ", occurredAt=" + getOccurredAt() +
                '}';
    }
}