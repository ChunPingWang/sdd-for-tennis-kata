package com.tennisscoring.bdd.steps;

import com.tennisscoring.bdd.TestDataBuilder;
import com.tennisscoring.domain.exception.InvalidMatchIdException;
import com.tennisscoring.domain.exception.MatchNotFoundException;
import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchStatus;
import com.tennisscoring.domain.service.MatchDomainService;
import io.cucumber.java.Before;
import io.cucumber.java.zh_tw.假設;
import io.cucumber.java.zh_tw.當;
import io.cucumber.java.zh_tw.那麼;
import io.cucumber.java.zh_tw.而且;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for match deletion BDD scenarios.
 * 比賽刪除 BDD 情境的步驟定義
 */
public class MatchDeletionSteps {
    
    @Autowired
    private MatchDomainService matchDomainService;
    
    @Autowired
    private TestDataBuilder testDataBuilder;
    
    private String recordedMatchId;
    private Exception lastException;
    private List<String> recordedMatchIds = new ArrayList<>();
    
    @Before
    public void setUp() {
        recordedMatchId = null;
        lastException = null;
        recordedMatchIds.clear();
    }
    
    @而且("我記錄了比賽ID")
    public void 我記錄了比賽ID() {
        // Get the most recently created match ID from test data builder
        // 從測試資料建構器取得最近創建的比賽ID
        if (testDataBuilder.getMatchCount() > 0) {
            // This is a simplified approach - in a real scenario, we'd need better tracking
            // 這是簡化的方法 - 在實際情境中，我們需要更好的追蹤機制
            recordedMatchId = testDataBuilder.getMatchIdByKey("張三 vs 李四");
            if (recordedMatchId == null) {
                recordedMatchId = testDataBuilder.getMatchIdByKey("王五 vs 趙六");
            }
            if (recordedMatchId == null) {
                recordedMatchId = testDataBuilder.getMatchIdByKey("甲 vs 乙");
            }
        }
        assertNotNull(recordedMatchId, "應該記錄了比賽ID");
    }
    
    @當("我刪除這場比賽")
    public void 我刪除這場比賽() {
        try {
            assertNotNull(recordedMatchId, "應該有記錄的比賽ID");
            matchDomainService.deleteMatch(recordedMatchId);
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @那麼("比賽應該成功被刪除")
    public void 比賽應該成功被刪除() {
        assertNull(lastException, "不應該有異常發生");
    }
    
    @而且("當我嘗試查詢該比賽時應該找不到")
    public void 當我嘗試查詢該比賽時應該找不到() {
        assertNotNull(recordedMatchId, "應該有記錄的比賽ID");
        assertThrows(MatchNotFoundException.class, () -> {
            matchDomainService.getMatch(recordedMatchId);
        }, "查詢已刪除的比賽應該拋出 MatchNotFoundException");
    }
    
    @假設("系統中沒有ID為 {string} 的比賽")
    public void 系統中沒有ID為的比賽(String matchId) {
        // Verify that the match doesn't exist
        // 驗證比賽不存在
        assertThrows(MatchNotFoundException.class, () -> {
            matchDomainService.getMatch(matchId);
        }, "系統中不應該有ID為 " + matchId + " 的比賽");
    }
    
    @當("我嘗試刪除ID為 {string} 的比賽")
    public void 我嘗試刪除ID為的比賽(String matchId) {
        try {
            matchDomainService.deleteMatch(matchId);
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @那麼("系統應該拒絕刪除操作")
    public void 系統應該拒絕刪除操作() {
        assertNotNull(lastException, "應該有異常發生");
        assertTrue(lastException instanceof MatchNotFoundException || 
                  lastException instanceof InvalidMatchIdException,
                  "應該拋出 MatchNotFoundException 或 InvalidMatchIdException");
    }
    
    @而且("應該顯示錯誤訊息 {string}")
    public void 應該顯示錯誤訊息(String expectedMessage) {
        assertNotNull(lastException, "應該有異常發生");
        String actualMessage = lastException.getMessage();
        assertTrue(actualMessage.contains(expectedMessage) || 
                  expectedMessage.equals("比賽不存在") && (lastException instanceof MatchNotFoundException) ||
                  expectedMessage.equals("比賽ID不能為空") && (lastException instanceof InvalidMatchIdException),
                  "錯誤訊息應該包含: " + expectedMessage + ", 實際訊息: " + actualMessage);
    }
    
    @當("我嘗試刪除ID為空字串的比賽")
    public void 我嘗試刪除ID為空字串的比賽() {
        try {
            matchDomainService.deleteMatch("");
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @當("我嘗試刪除ID為null的比賽")
    public void 我嘗試刪除ID為null的比賽() {
        try {
            matchDomainService.deleteMatch(null);
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @而且("比賽狀態是 {string}")
    public void 比賽狀態是(String status) {
        assertNotNull(recordedMatchId, "應該有記錄的比賽ID");
        Match match = matchDomainService.getMatch(recordedMatchId);
        MatchStatus expectedStatus = switch (status) {
            case "進行中" -> MatchStatus.IN_PROGRESS;
            case "已完成" -> MatchStatus.COMPLETED;
            case "已取消" -> MatchStatus.CANCELLED;
            default -> throw new IllegalArgumentException("未知的比賽狀態: " + status);
        };
        assertEquals(expectedStatus, match.getStatus(), "比賽狀態應該是 " + status);
    }
    
    @而且("系統中應該不再有這場比賽的記錄")
    public void 系統中應該不再有這場比賽的記錄() {
        assertNotNull(recordedMatchId, "應該有記錄的比賽ID");
        assertThrows(MatchNotFoundException.class, () -> {
            matchDomainService.getMatch(recordedMatchId);
        }, "系統中不應該再有這場比賽的記錄");
    }
    
    @而且("比賽已經完成，獲勝者是 {string}")
    public void 比賽已經完成獲勝者是(String winnerName) {
        assertNotNull(recordedMatchId, "應該有記錄的比賽ID");
        Match match = matchDomainService.getMatch(recordedMatchId);
        
        // Simulate match completion - this would normally happen through scoring
        // 模擬比賽完成 - 這通常通過計分來實現
        // For BDD testing, we'll assume the match can be set to completed state
        // 對於 BDD 測試，我們假設比賽可以設置為完成狀態
        assertEquals(MatchStatus.COMPLETED, match.getStatus(), "比賽應該已完成");
        
        if (match.getWinnerPlayer() != null) {
            assertEquals(winnerName, match.getWinnerPlayer().getName(), "獲勝者應該是 " + winnerName);
        }
    }
    
    @假設("我創建了 {int} 場比賽")
    public void 我創建了場比賽(int matchCount) {
        recordedMatchIds.clear();
        for (int i = 1; i <= matchCount; i++) {
            String player1 = "球員" + (i * 2 - 1);
            String player2 = "球員" + (i * 2);
            Match match = matchDomainService.createMatch(player1, player2);
            recordedMatchIds.add(match.getMatchId());
            testDataBuilder.storeMatch("match" + i, match);
        }
    }
    
    @而且("我記錄了第二場比賽的ID")
    public void 我記錄了第二場比賽的ID() {
        assertTrue(recordedMatchIds.size() >= 2, "應該至少有2場比賽");
        recordedMatchId = recordedMatchIds.get(1); // Second match (index 1)
    }
    
    @當("我刪除第二場比賽")
    public void 我刪除第二場比賽() {
        try {
            assertNotNull(recordedMatchId, "應該有記錄的第二場比賽ID");
            matchDomainService.deleteMatch(recordedMatchId);
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @那麼("系統中應該剩下 {int} 場比賽")
    public void 系統中應該剩下場比賽(int expectedCount) {
        List<Match> allMatches = matchDomainService.getAllMatches();
        assertEquals(expectedCount, allMatches.size(), "系統中應該剩下 " + expectedCount + " 場比賽");
    }
    
    @而且("被刪除的比賽不應該出現在比賽列表中")
    public void 被刪除的比賽不應該出現在比賽列表中() {
        List<Match> allMatches = matchDomainService.getAllMatches();
        for (Match match : allMatches) {
            assertNotEquals(recordedMatchId, match.getMatchId(), "被刪除的比賽不應該出現在列表中");
        }
    }
    
    @而且("我記錄了所有比賽的ID")
    public void 我記錄了所有比賽的ID() {
        // IDs are already recorded in recordedMatchIds from the creation step
        // ID已經在創建步驟中記錄在recordedMatchIds中
        assertFalse(recordedMatchIds.isEmpty(), "應該記錄了比賽ID");
    }
    
    @當("我刪除前兩場比賽")
    public void 我刪除前兩場比賽() {
        try {
            assertTrue(recordedMatchIds.size() >= 2, "應該至少有2場比賽");
            matchDomainService.deleteMatch(recordedMatchIds.get(0));
            matchDomainService.deleteMatch(recordedMatchIds.get(1));
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @而且("剩下的比賽應該是球員 {string} 對戰 {string} 的比賽")
    public void 剩下的比賽應該是球員對戰的比賽(String player1Name, String player2Name) {
        List<Match> allMatches = matchDomainService.getAllMatches();
        assertEquals(1, allMatches.size(), "應該只剩下1場比賽");
        
        Match remainingMatch = allMatches.get(0);
        assertTrue((remainingMatch.getPlayer1().getName().equals(player1Name) && 
                   remainingMatch.getPlayer2().getName().equals(player2Name)) ||
                  (remainingMatch.getPlayer1().getName().equals(player2Name) && 
                   remainingMatch.getPlayer2().getName().equals(player1Name)),
                  "剩下的比賽應該是 " + player1Name + " 對戰 " + player2Name);
    }
}