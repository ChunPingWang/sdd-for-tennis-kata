package com.tennisscoring.bdd;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.Player;
import com.tennisscoring.domain.model.PlayerId;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Test data builder for creating test objects in BDD scenarios.
 * 測試資料建構器，用於在 BDD 情境中建立測試物件
 */
@Component
public class TestDataBuilder {
    
    private final Map<String, Match> matches = new HashMap<>();
    private final Map<String, String> matchIds = new HashMap<>();
    private Match lastCreatedMatch;
    
    /**
     * Create a new match with the given player names.
     * 使用指定的球員名稱建立新比賽
     * 
     * @param player1Name first player name
     * @param player2Name second player name
     * @return the created match
     */
    public Match createMatch(String player1Name, String player2Name) {
        Match match = Match.create(player1Name, player2Name);
        String key = player1Name + " vs " + player2Name;
        matches.put(key, match);
        matchIds.put(key, match.getMatchId());
        lastCreatedMatch = match;
        return match;
    }
    
    /**
     * Get a match by player names.
     * 根據球員名稱取得比賽
     * 
     * @param player1Name first player name
     * @param player2Name second player name
     * @return the match or null if not found
     */
    public Match getMatch(String player1Name, String player2Name) {
        String key = player1Name + " vs " + player2Name;
        return matches.get(key);
    }
    
    /**
     * Get match ID by player names.
     * 根據球員名稱取得比賽ID
     * 
     * @param player1Name first player name
     * @param player2Name second player name
     * @return the match ID or null if not found
     */
    public String getMatchId(String player1Name, String player2Name) {
        String key = player1Name + " vs " + player2Name;
        return matchIds.get(key);
    }
    
    /**
     * Store a match with a custom key.
     * 使用自訂鍵值儲存比賽
     * 
     * @param key the key to store the match under
     * @param match the match to store
     */
    public void storeMatch(String key, Match match) {
        matches.put(key, match);
        matchIds.put(key, match.getMatchId());
        lastCreatedMatch = match;
    }
    
    /**
     * Get a match by key.
     * 根據鍵值取得比賽
     * 
     * @param key the key
     * @return the match or null if not found
     */
    public Match getMatchByKey(String key) {
        return matches.get(key);
    }
    
    /**
     * Get match ID by key.
     * 根據鍵值取得比賽ID
     * 
     * @param key the key
     * @return the match ID or null if not found
     */
    public String getMatchIdByKey(String key) {
        return matchIds.get(key);
    }
    
    /**
     * Get player by name from a match.
     * 從比賽中根據名稱取得球員
     * 
     * @param match the match
     * @param playerName the player name
     * @return the player or null if not found
     */
    public Player getPlayerFromMatch(Match match, String playerName) {
        if (match.getPlayer1().getName().equals(playerName)) {
            return match.getPlayer1();
        } else if (match.getPlayer2().getName().equals(playerName)) {
            return match.getPlayer2();
        }
        return null;
    }
    
    /**
     * Get player ID by name from a match.
     * 從比賽中根據名稱取得球員ID
     * 
     * @param match the match
     * @param playerName the player name
     * @return the player ID or null if not found
     */
    public PlayerId getPlayerIdFromMatch(Match match, String playerName) {
        Player player = getPlayerFromMatch(match, playerName);
        return player != null ? player.getPlayerId() : null;
    }
    
    /**
     * Clear all stored test data.
     * 清除所有儲存的測試資料
     */
    public void clear() {
        matches.clear();
        matchIds.clear();
        lastCreatedMatch = null;
    }
    
    /**
     * Get the number of stored matches.
     * 取得儲存的比賽數量
     * 
     * @return the number of matches
     */
    public int getMatchCount() {
        return matches.size();
    }
    
    /**
     * Get the last created match.
     * 取得最後創建的比賽
     * 
     * @return the last created match or null if none
     */
    public Match getLastCreatedMatch() {
        return lastCreatedMatch;
    }
}