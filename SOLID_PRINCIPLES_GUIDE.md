# SOLID 原則開發者指南

## 目標

本指南幫助開發團隊理解和應用 SOLID 原則，確保程式碼品質和系統架構的一致性。

## 1. 單一職責原則 (SRP) 指南

### 原則定義
> 一個類別應該只有一個改變的理由

### 實作指南

#### ✅ 正確範例
```java
// 好的設計 - 每個服務只負責一項職責
@Service
public class MatchDomainService {
    // 只負責比賽管理邏輯
    public Match createMatch(String player1, String player2) { ... }
    public Match scorePoint(String matchId, String playerId) { ... }
    public void deleteMatch(String matchId) { ... }
}

@Service  
public class MatchStatisticsService {
    // 只負責統計計算
    public MatchStatistics getMatchStatistics(String matchId) { ... }
    public SystemStatistics getSystemStatistics() { ... }
}
```

#### ❌ 錯誤範例
```java
// 壞的設計 - 違反 SRP，混合多種職責
@Service
public class MatchService {
    // 比賽管理
    public Match createMatch(String player1, String player2) { ... }
    
    // 統計計算 - 違反 SRP
    public MatchStatistics getStatistics(String matchId) { ... }
    
    // 事件發布 - 違反 SRP  
    public void publishEvent(String event) { ... }
    
    // 資料驗證 - 違反 SRP
    public boolean validateInput(String input) { ... }
}
```

#### 檢查清單
- [ ] 類別的所有方法都圍繞同一個職責
- [ ] 類別名稱清楚表達其職責
- [ ] 變更需求時只影響一個類別
- [ ] 類別的依賴都與其職責相關

### 重構策略
1. **識別多重職責**: 分析類別中不同類型的方法
2. **提取服務**: 將不同職責分離到專用服務
3. **重新組織依賴**: 調整依賴關係以反映新的職責分離

## 2. 開放封閉原則 (OCP) 指南

### 原則定義
> 軟體實體應該對擴展開放，對修改封閉

### 實作指南

#### ✅ 正確範例 - 工廠模式
```java
// 抽象工廠 - 對擴展開放
public interface MatchFactory {
    Match createMatch(String player1Name, String player2Name);
    boolean supports(String matchType);
}

// 具體實作 - 可以添加新類型而不修改現有程式碼
public class StandardMatchFactory implements MatchFactory {
    @Override
    public boolean supports(String matchType) {
        return "STANDARD".equals(matchType);
    }
    
    @Override
    public Match createMatch(String player1Name, String player2Name) {
        return Match.createStandardMatch(player1Name, player2Name);
    }
}

public class BestOfFiveMatchFactory implements MatchFactory {
    @Override
    public boolean supports(String matchType) {
        return "BEST_OF_5".equals(matchType);
    }
    
    @Override  
    public Match createMatch(String player1Name, String player2Name) {
        return Match.createBestOfFiveMatch(player1Name, player2Name);
    }
}

// 工廠註冊表 - 支援動態擴展
@Component
public class MatchFactoryRegistry {
    private final List<MatchFactory> factories;
    
    public MatchFactoryRegistry(List<MatchFactory> factories) {
        this.factories = factories;
    }
    
    public Match createMatch(String matchType, String player1, String player2) {
        return factories.stream()
            .filter(factory -> factory.supports(matchType))
            .findFirst()
            .orElseThrow(() -> new UnsupportedMatchTypeException(matchType))
            .createMatch(player1, player2);
    }
}
```

#### ❌ 錯誤範例
```java
// 壞的設計 - 違反 OCP，每次添加新類型都要修改
public class MatchFactory {
    public Match createMatch(String matchType, String player1, String player2) {
        switch (matchType) {
            case "STANDARD":
                return Match.createStandardMatch(player1, player2);
            case "BEST_OF_5":
                return Match.createBestOfFiveMatch(player1, player2);
            // 每次添加新類型都要修改這裡 - 違反 OCP
            default:
                throw new UnsupportedMatchTypeException(matchType);
        }
    }
}
```

#### 檢查清單
- [ ] 新功能可以通過添加新類別實現
- [ ] 現有程式碼不需要修改來支援新功能
- [ ] 使用抽象和多型來支援擴展
- [ ] 配置驅動的行為選擇

## 3. 里氏替換原則 (LSP) 指南

### 原則定義
> 子類別必須能夠替換其父類別而不影響程式的正確性

### 實作指南

#### ✅ 正確範例
```java
// 基底類別定義契約
public abstract class BaseMatchRepository implements MatchRepositoryPort {
    
    // 模板方法定義通用行為
    @Override
    public final Match save(Match match) {
        validateMatch(match);
        return doSave(match);
    }
    
    // 子類別實作具體邏輯
    protected abstract Match doSave(Match match);
    
    // 契約方法 - 子類別必須正確實作
    public abstract String getRepositoryType();
    public abstract boolean isThreadSafe();
    
    private void validateMatch(Match match) {
        if (match == null) {
            throw new IllegalArgumentException("Match cannot be null");
        }
    }
}

// 具體實作遵循契約
public class InMemoryMatchRepository extends BaseMatchRepository {
    private final Map<String, Match> matches = new ConcurrentHashMap<>();
    
    @Override
    protected Match doSave(Match match) {
        matches.put(match.getMatchId(), match);
        return match;
    }
    
    @Override
    public String getRepositoryType() {
        return "IN_MEMORY";
    }
    
    @Override
    public boolean isThreadSafe() {
        return true; // ConcurrentHashMap 是執行緒安全的
    }
}

public class DatabaseMatchRepository extends BaseMatchRepository {
    @Override
    protected Match doSave(Match match) {
        // 資料庫儲存邏輯
        return entityManager.merge(match);
    }
    
    @Override
    public String getRepositoryType() {
        return "DATABASE";
    }
    
    @Override
    public boolean isThreadSafe() {
        return true; // JPA EntityManager 在適當配置下是執行緒安全的
    }
}
```

#### ❌ 錯誤範例
```java
// 壞的設計 - 違反 LSP
public class ReadOnlyRepository extends BaseMatchRepository {
    @Override
    protected Match doSave(Match match) {
        // 違反 LSP - 改變了父類別的預期行為
        throw new UnsupportedOperationException("This repository is read-only");
    }
    
    @Override
    public String getRepositoryType() {
        return "READ_ONLY";
    }
    
    @Override
    public boolean isThreadSafe() {
        return true;
    }
}
```

#### 檢查清單
- [ ] 子類別不會拋出父類別未預期的異常
- [ ] 子類別不會改變方法的預期行為
- [ ] 前置條件不能被子類別加強
- [ ] 後置條件不能被子類別削弱

## 4. 介面隔離原則 (ISP) 指南

### 原則定義
> 客戶端不應該被迫依賴它們不使用的介面

### 實作指南

#### ✅ 正確範例 - 介面分離
```java
// 分離的專用介面
public interface MatchCreationPort {
    Match createMatch(String player1Name, String player2Name);
    Match createMatch(String matchType, String player1Name, String player2Name);
}

public interface MatchScoringPort {
    Match scorePoint(String matchId, String playerId);
}

public interface MatchQueryPort {
    Match getMatch(String matchId);
    List<Match> getAllMatches();
    List<Match> getMatchesByStatus(MatchStatus status);
    boolean matchExists(String matchId);
}

public interface MatchDeletionPort {
    void deleteMatch(String matchId);
    Match cancelMatch(String matchId);
}

// 客戶端只依賴需要的介面
@RestController
public class MatchController {
    private final MatchCreationPort matchCreation;
    private final MatchScoringPort matchScoring;
    private final MatchQueryPort matchQuery;
    
    // 只注入需要的介面
    public MatchController(
        MatchCreationPort matchCreation,
        MatchScoringPort matchScoring, 
        MatchQueryPort matchQuery) {
        this.matchCreation = matchCreation;
        this.matchScoring = matchScoring;
        this.matchQuery = matchQuery;
    }
    
    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(@RequestBody CreateMatchRequest request) {
        // 只使用創建介面
        Match match = matchCreation.createMatch(request.getPlayer1Name(), request.getPlayer2Name());
        return ResponseEntity.ok(toResponse(match));
    }
}
```

#### ❌ 錯誤範例
```java
// 壞的設計 - 臃腫的介面
public interface MatchManagementPort {
    // 創建操作
    Match createMatch(String player1Name, String player2Name);
    Match createMatch(String matchType, String player1Name, String player2Name);
    
    // 計分操作
    Match scorePoint(String matchId, String playerId);
    
    // 查詢操作
    Match getMatch(String matchId);
    List<Match> getAllMatches();
    List<Match> getMatchesByStatus(MatchStatus status);
    
    // 刪除操作
    void deleteMatch(String matchId);
    
    // 統計操作 - 不應該在這裡
    MatchStatistics getStatistics(String matchId);
    
    // 事件操作 - 不應該在這裡
    void publishEvent(String eventType, String matchId);
}
```

#### 檢查清單
- [ ] 每個介面專注於單一職責
- [ ] 客戶端只依賴它們使用的方法
- [ ] 介面方法數量適中（通常 < 10 個方法）
- [ ] 介面名稱清楚表達其用途

## 5. 依賴反轉原則 (DIP) 指南

### 原則定義
> 高層模組不應該依賴低層模組，兩者都應該依賴抽象

### 實作指南

#### ✅ 正確範例
```java
// 抽象介面
public interface MatchService {
    Match createMatch(String player1Name, String player2Name);
    Match scorePoint(String matchId, String playerId);
    Match getMatch(String matchId);
}

public interface MatchRepositoryPort {
    Match save(Match match);
    Optional<Match> findById(String matchId);
    void deleteById(String matchId);
}

// 高層模組依賴抽象
@Service
public class MatchDomainService implements MatchService {
    private final MatchRepositoryPort matchRepository; // 依賴抽象
    private final ScoringService scoringService;       // 依賴抽象
    private final EventService eventService;           // 依賴抽象
    
    public MatchDomainService(
        MatchRepositoryPort matchRepository,
        ScoringService scoringService,
        EventService eventService) {
        this.matchRepository = matchRepository;
        this.scoringService = scoringService;
        this.eventService = eventService;
    }
    
    @Override
    public Match createMatch(String player1Name, String player2Name) {
        Match match = Match.create(player1Name, player2Name);
        Match savedMatch = matchRepository.save(match); // 使用抽象
        eventService.publishMatchCreated(savedMatch);   // 使用抽象
        return savedMatch;
    }
}

// 低層模組實作抽象
@Repository
public class InMemoryMatchRepository implements MatchRepositoryPort {
    private final Map<String, Match> matches = new ConcurrentHashMap<>();
    
    @Override
    public Match save(Match match) {
        matches.put(match.getMatchId(), match);
        return match;
    }
    
    @Override
    public Optional<Match> findById(String matchId) {
        return Optional.ofNullable(matches.get(matchId));
    }
}

// 配置類別管理依賴注入
@Configuration
public class ServiceConfiguration {
    
    @Bean
    public MatchService matchService(
        MatchRepositoryPort matchRepository,
        ScoringService scoringService,
        EventService eventService) {
        return new MatchDomainService(matchRepository, scoringService, eventService);
    }
}
```

#### ❌ 錯誤範例
```java
// 壞的設計 - 直接依賴具體實作
@Service
public class MatchDomainService {
    private final InMemoryMatchRepository matchRepository; // 直接依賴具體類別
    private final ScoringDomainService scoringService;     // 直接依賴具體類別
    
    public MatchDomainService() {
        // 在建構子中創建依賴 - 違反 DIP
        this.matchRepository = new InMemoryMatchRepository();
        this.scoringService = new ScoringDomainService();
    }
}
```

#### 檢查清單
- [ ] 服務類別依賴介面而非具體實作
- [ ] 使用依賴注入而非直接實例化
- [ ] 抽象不依賴細節，細節依賴抽象
- [ ] 配置類別集中管理依賴關係

## 程式碼審查檢查清單

### SRP 檢查項目
- [ ] 每個類別只有一個改變的理由
- [ ] 類別名稱清楚表達其職責
- [ ] 方法都與類別的主要職責相關
- [ ] 沒有混合不同領域的邏輯

### OCP 檢查項目  
- [ ] 新功能可以通過擴展實現
- [ ] 現有程式碼不需要修改
- [ ] 使用抽象和多型
- [ ] 避免大型 switch/if-else 語句

### LSP 檢查項目
- [ ] 子類別可以完全替換父類別
- [ ] 不會改變預期行為
- [ ] 不會拋出意外異常
- [ ] 遵循契約和約定

### ISP 檢查項目
- [ ] 介面專注且精簡
- [ ] 客戶端不依賴未使用的方法
- [ ] 介面職責單一
- [ ] 避免臃腫的介面

### DIP 檢查項目
- [ ] 依賴抽象而非具體實作
- [ ] 使用依賴注入
- [ ] 高層模組不依賴低層模組
- [ ] 配置集中管理

## 重構指導

### 識別違規模式
1. **God Class**: 過大的類別，承擔多種職責
2. **Feature Envy**: 類別過度使用其他類別的資料
3. **Shotgun Surgery**: 一個變更需要修改多個類別
4. **Large Interface**: 介面包含太多方法

### 重構步驟
1. **分析現狀**: 識別違反 SOLID 原則的程式碼
2. **制定計劃**: 確定重構的優先順序和範圍
3. **逐步重構**: 小步驟、頻繁測試
4. **驗證結果**: 確保重構後仍符合 SOLID 原則

### 重構工具
- **Extract Class**: 分離職責到新類別
- **Extract Interface**: 創建抽象介面
- **Move Method**: 將方法移到合適的類別
- **Replace Conditional with Polymorphism**: 使用多型替換條件邏輯

## 最佳實務

### 設計時考慮
1. **從介面開始**: 先定義抽象，再實作具體
2. **小而專注**: 保持類別和介面的精簡
3. **組合優於繼承**: 使用組合來實現功能擴展
4. **測試驅動**: 通過測試驗證 SOLID 原則的遵循

### 團隊協作
1. **程式碼審查**: 在審查中檢查 SOLID 原則
2. **知識分享**: 定期分享 SOLID 原則的應用經驗
3. **文件維護**: 保持架構文件的更新
4. **持續改進**: 定期重構和優化

## 結論

遵循 SOLID 原則能夠：
- 提高程式碼的可維護性和可讀性
- 降低系統的複雜度和耦合度
- 增強系統的可擴展性和靈活性
- 改善測試的可行性和覆蓋率

記住，SOLID 原則是指導原則，不是絕對規則。在實際應用中，需要根據具體情況靈活運用，平衡各種因素，追求最適合的設計方案。