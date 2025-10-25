# 網球計分系統設計文件

## 概述

網球計分系統是一個基於 Java 17 和 Spring Boot 3.x 的後端應用程式，提供 RESTful Web API 來管理單打網球比賽的計分。系統採用測試先行的開發方法，使用 BDD 和 Gherkin 語法描述測試情境，並提供 Swagger API 文件。

### 核心功能
- 創建和管理網球比賽
- 實時計分和狀態更新
- 處理複雜的網球計分規則（平分、優勢、搶七局）
- 提供完整的 RESTful API
- 自動生成 API 文件

## 架構

### 整體架構
系統採用六角形架構（Hexagonal Architecture / Ports and Adapters），將業務邏輯與外部依賴完全分離：

```
                    ┌─────────────────────────────────────┐
                    │           Primary Adapters          │
                    │        (Driving Adapters)           │
                    │                                     │
         ┌──────────┤  REST API Controller                │
         │          │  Swagger Documentation              │
         │          │  Exception Handlers                 │
         │          └─────────────────────────────────────┘
         │                           │
         │                           │ Primary Ports
         │                           │ (Inbound)
         │          ┌─────────────────────────────────────┐
         │          │                                     │
         │          │           Domain Core               │
         │          │        (Business Logic)             │
         │          │                                     │
         │          │  ┌─────────────────────────────┐    │
         │          │  │      Domain Services        │    │
         │          │  │   - MatchService            │    │
         │          │  │   - ScoringService          │    │
         │          │  │   - ValidationService       │    │
         │          │  └─────────────────────────────┘    │
         │          │                                     │
         │          │  ┌─────────────────────────────┐    │
         │          │  │      Domain Models          │    │
         │          │  │   - Match, Set, Game        │    │
         │          │  │   - Player, Score           │    │
         │          │  │   - Value Objects           │    │
         │          │  └─────────────────────────────┘    │
         │          │                                     │
         │          └─────────────────────────────────────┘
         │                           │
         │                           │ Secondary Ports
         │                           │ (Outbound)
         │          ┌─────────────────────────────────────┐
         │          │         Secondary Adapters          │
         │          │        (Driven Adapters)            │
         │          │                                     │
         └──────────┤  In-Memory Repository               │
                    │  Database Repository (Future)       │
                    │  Event Publisher (Future)           │
                    │  Logging Adapter                    │
                    └─────────────────────────────────────┘
```

### 六角形架構層次說明

#### 1. Domain Core (領域核心)
- **Domain Models**: 純粹的業務實體，不依賴任何外部框架
- **Domain Services**: 包含複雜業務邏輯的服務
- **Domain Events**: 領域事件（未來擴展）
- **Value Objects**: 不可變的值物件

#### 2. Primary Ports (主要埠) - Inbound
- **MatchManagementPort**: 比賽管理介面
- **ScoringPort**: 計分操作介面
- **QueryPort**: 查詢操作介面

#### 3. Secondary Ports (次要埠) - Outbound  
- **MatchRepositoryPort**: 資料持久化介面
- **EventPublisherPort**: 事件發布介面
- **LoggingPort**: 日誌記錄介面

#### 4. Primary Adapters (主要適配器) - Driving
- **REST Controllers**: HTTP API 端點
- **Swagger Configuration**: API 文件
- **Exception Handlers**: 錯誤處理

#### 5. Secondary Adapters (次要適配器) - Driven
- **In-Memory Repository**: 記憶體資料儲存
- **Database Repository**: 資料庫持久化（未來）
- **Event Publisher**: 事件發布實作（未來）

### 技術架構
- **框架**: Spring Boot 3.x
- **Java 版本**: Java 17
- **建構工具**: Maven
- **API 文件**: Swagger/OpenAPI 3
- **測試框架**: JUnit 5, Spring Boot Test, Cucumber (BDD)
- **資料儲存**: 記憶體儲存 (HashMap) - 可擴展至資料庫

## 組件和介面

### 1. Primary Ports (主要埠 - Inbound)

#### MatchManagementPort
定義比賽管理的業務介面。

```java
public interface MatchManagementPort {
    Match createMatch(String player1Name, String player2Name);
    void deleteMatch(String matchId);
    Match scorePoint(String matchId, String playerId);
}
```

#### QueryPort
定義查詢操作的業務介面。

```java
public interface QueryPort {
    Match getMatch(String matchId);
    List<Match> getAllMatches();
    List<Match> getMatchesByStatus(MatchStatus status);
}
```

### 2. Secondary Ports (次要埠 - Outbound)

#### MatchRepositoryPort
定義資料持久化的介面。

```java
public interface MatchRepositoryPort {
    Match save(Match match);
    Optional<Match> findById(String matchId);
    List<Match> findAll();
    void deleteById(String matchId);
    boolean existsById(String matchId);
    List<Match> findByStatus(MatchStatus status);
}
```

#### EventPublisherPort
定義事件發布的介面（未來擴展）。

```java
public interface EventPublisherPort {
    void publishMatchCreated(MatchCreatedEvent event);
    void publishPointScored(PointScoredEvent event);
    void publishMatchCompleted(MatchCompletedEvent event);
}
```

### 3. Domain Core (領域核心)

#### Domain Services

**MatchDomainService**
```java
@Component
public class MatchDomainService implements MatchManagementPort, QueryPort {
    
    private final MatchRepositoryPort matchRepository;
    private final ScoringDomainService scoringService;
    private final EventPublisherPort eventPublisher;
    
    @Override
    public Match createMatch(String player1Name, String player2Name) {
        // 業務邏輯：創建比賽
        Match match = Match.create(player1Name, player2Name);
        Match savedMatch = matchRepository.save(match);
        eventPublisher.publishMatchCreated(new MatchCreatedEvent(savedMatch));
        return savedMatch;
    }
    
    @Override
    public Match scorePoint(String matchId, String playerId) {
        Match match = getMatch(matchId);
        scoringService.scorePoint(match, playerId);
        Match updatedMatch = matchRepository.save(match);
        eventPublisher.publishPointScored(new PointScoredEvent(updatedMatch, playerId));
        
        if (match.isCompleted()) {
            eventPublisher.publishMatchCompleted(new MatchCompletedEvent(updatedMatch));
        }
        
        return updatedMatch;
    }
    
    @Override
    public Match getMatch(String matchId) {
        return matchRepository.findById(matchId)
            .orElseThrow(() -> new MatchNotFoundException(matchId));
    }
    
    // 其他方法實作...
}
```

**ScoringDomainService**
```java
@Component
public class ScoringDomainService {
    
    public void scorePoint(Match match, String playerId) {
        validateMatchState(match);
        Player player = match.getPlayer(playerId);
        
        Game currentGame = match.getCurrentGame();
        
        if (currentGame.isTiebreak()) {
            handleTiebreakScoring(currentGame, player);
        } else {
            handleRegularScoring(currentGame, player);
        }
        
        if (currentGame.isCompleted()) {
            handleGameCompletion(match, player);
        }
    }
    
    private void handleRegularScoring(Game game, Player player) {
        // 實作一般計分邏輯
    }
    
    private void handleTiebreakScoring(Game game, Player player) {
        // 實作搶七局計分邏輯
    }
    
    private void handleGameCompletion(Match match, Player player) {
        // 處理局結束後的邏輯
    }
    
    private void validateMatchState(Match match) {
        if (match.isCompleted()) {
            throw new InvalidMatchStateException("Cannot score on completed match");
        }
    }
}
```

### 4. Primary Adapters (主要適配器)

#### MatchController
REST API 控制器，實作 HTTP 端點。

```java
@RestController
@RequestMapping("/api/matches")
@Tag(name = "Match Management", description = "網球比賽管理 API")
public class MatchController {
    
    private final MatchManagementPort matchManagement;
    private final QueryPort queryPort;
    private final MatchResponseMapper mapper;
    
    @PostMapping
    @Operation(summary = "創建新比賽")
    public ResponseEntity<MatchResponse> createMatch(
            @Valid @RequestBody CreateMatchRequest request) {
        
        Match match = matchManagement.createMatch(
            request.getPlayer1Name(), 
            request.getPlayer2Name()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toResponse(match));
    }
    
    @GetMapping("/{matchId}")
    @Operation(summary = "查詢比賽資訊")
    public ResponseEntity<MatchResponse> getMatch(
            @PathVariable @Valid @UUID String matchId) {
        
        Match match = queryPort.getMatch(matchId);
        return ResponseEntity.ok(mapper.toResponse(match));
    }
    
    @PutMapping("/{matchId}/score")
    @Operation(summary = "記錄得分")
    public ResponseEntity<MatchResponse> scorePoint(
            @PathVariable @Valid @UUID String matchId,
            @Valid @RequestBody ScorePointRequest request) {
        
        Match match = matchManagement.scorePoint(matchId, request.getPlayerId());
        return ResponseEntity.ok(mapper.toResponse(match));
    }
    
    @DeleteMapping("/{matchId}")
    @Operation(summary = "刪除比賽")
    public ResponseEntity<Void> deleteMatch(
            @PathVariable @Valid @UUID String matchId) {
        
        matchManagement.deleteMatch(matchId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @Operation(summary = "查詢所有比賽")
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        List<Match> matches = queryPort.getAllMatches();
        return ResponseEntity.ok(
            matches.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList())
        );
    }
}
```

### 5. Secondary Adapters (次要適配器)

#### InMemoryMatchRepository
記憶體儲存的實作。

```java
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
    
    @Override
    public List<Match> findAll() {
        return new ArrayList<>(matches.values());
    }
    
    @Override
    public void deleteById(String matchId) {
        matches.remove(matchId);
    }
    
    @Override
    public boolean existsById(String matchId) {
        return matches.containsKey(matchId);
    }
    
    @Override
    public List<Match> findByStatus(MatchStatus status) {
        return matches.values().stream()
            .filter(match -> match.getStatus() == status)
            .collect(Collectors.toList());
    }
}
```

#### NoOpEventPublisher
事件發布的空實作（未來可替換為真實實作）。

```java
@Component
public class NoOpEventPublisher implements EventPublisherPort {
    
    private static final Logger logger = LoggerFactory.getLogger(NoOpEventPublisher.class);
    
    @Override
    public void publishMatchCreated(MatchCreatedEvent event) {
        logger.info("Match created: {}", event.getMatchId());
    }
    
    @Override
    public void publishPointScored(PointScoredEvent event) {
        logger.info("Point scored in match: {}", event.getMatchId());
    }
    
    @Override
    public void publishMatchCompleted(MatchCompletedEvent event) {
        logger.info("Match completed: {}", event.getMatchId());
    }
}

### 6. Domain Models (領域模型)

#### 核心領域物件

**Match (比賽) - Aggregate Root**
```java
public class Match {
    private final MatchId matchId;
    private final Player player1;
    private final Player player2;
    private final List<Set> sets;
    private MatchStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Player winner;
    
    // 私有建構子，強制使用工廠方法
    private Match(MatchId matchId, Player player1, Player player2) {
        this.matchId = matchId;
        this.player1 = player1;
        this.player2 = player2;
        this.sets = new ArrayList<>();
        this.status = MatchStatus.IN_PROGRESS;
        this.createdAt = LocalDateTime.now();
        
        // 初始化第一盤
        this.sets.add(new Set(1));
    }
    
    // 工廠方法
    public static Match create(String player1Name, String player2Name) {
        return new Match(
            MatchId.generate(),
            new Player(PlayerId.generate(), player1Name),
            new Player(PlayerId.generate(), player2Name)
        );
    }
    
    // 業務方法
    public Game getCurrentGame() {
        return getCurrentSet().getCurrentGame();
    }
    
    public Set getCurrentSet() {
        return sets.stream()
            .filter(set -> !set.isCompleted())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No active set found"));
    }
    
    public Player getPlayer(String playerId) {
        if (player1.getPlayerId().getValue().equals(playerId)) {
            return player1;
        } else if (player2.getPlayerId().getValue().equals(playerId)) {
            return player2;
        }
        throw new IllegalArgumentException("Player not found: " + playerId);
    }
    
    public boolean isCompleted() {
        return status == MatchStatus.COMPLETED;
    }
    
    public void complete(Player winner) {
        this.status = MatchStatus.COMPLETED;
        this.winner = winner;
        this.completedAt = LocalDateTime.now();
    }
    
    // 不可變的 getter 方法
    public String getMatchId() { return matchId.getValue(); }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public List<Set> getSets() { return Collections.unmodifiableList(sets); }
    public MatchStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Player getWinner() { return winner; }
}
```

**Set (盤) - Entity**
```java
public class Set {
    private final int setNumber;
    private final List<Game> games;
    private final Map<PlayerId, Integer> gamesWon;
    private boolean isCompleted;
    private Player winner;
    
    public Set(int setNumber) {
        this.setNumber = setNumber;
        this.games = new ArrayList<>();
        this.gamesWon = new HashMap<>();
        this.isCompleted = false;
        
        // 初始化第一局
        this.games.add(new Game(1, false));
    }
    
    public Game getCurrentGame() {
        return games.stream()
            .filter(game -> !game.isCompleted())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No active game found"));
    }
    
    public void addGame(boolean isTiebreak) {
        int gameNumber = games.size() + 1;
        games.add(new Game(gameNumber, isTiebreak));
    }
    
    public int getGamesWon(PlayerId playerId) {
        return gamesWon.getOrDefault(playerId, 0);
    }
    
    public void incrementGamesWon(PlayerId playerId) {
        gamesWon.put(playerId, getGamesWon(playerId) + 1);
    }
    
    public boolean shouldStartTiebreak() {
        return gamesWon.values().stream().allMatch(games -> games == 6);
    }
    
    // Getters
    public int getSetNumber() { return setNumber; }
    public List<Game> getGames() { return Collections.unmodifiableList(games); }
    public boolean isCompleted() { return isCompleted; }
    public Player getWinner() { return winner; }
}
```

**Game (局) - Entity**
```java
public class Game {
    private final int gameNumber;
    private final Map<PlayerId, GameScore> scores;
    private final boolean isTiebreak;
    private GameStatus status;
    private Player winner;
    
    public Game(int gameNumber, boolean isTiebreak) {
        this.gameNumber = gameNumber;
        this.isTiebreak = isTiebreak;
        this.scores = new HashMap<>();
        this.status = GameStatus.IN_PROGRESS;
        
        // 初始化分數
        if (isTiebreak) {
            // 搶七局使用數字計分
            scores.put(PlayerId.PLAYER1, GameScore.LOVE);
            scores.put(PlayerId.PLAYER2, GameScore.LOVE);
        } else {
            // 一般局使用網球計分
            scores.put(PlayerId.PLAYER1, GameScore.LOVE);
            scores.put(PlayerId.PLAYER2, GameScore.LOVE);
        }
    }
    
    public void scorePoint(PlayerId playerId) {
        if (isCompleted()) {
            throw new IllegalStateException("Cannot score on completed game");
        }
        
        if (isTiebreak) {
            scoreTiebreakPoint(playerId);
        } else {
            scoreRegularPoint(playerId);
        }
    }
    
    private void scoreRegularPoint(PlayerId playerId) {
        GameScore currentScore = scores.get(playerId);
        GameScore opponentScore = scores.get(getOpponent(playerId));
        
        // 實作網球計分邏輯
        switch (currentScore) {
            case LOVE -> scores.put(playerId, GameScore.FIFTEEN);
            case FIFTEEN -> scores.put(playerId, GameScore.THIRTY);
            case THIRTY -> scores.put(playerId, GameScore.FORTY);
            case FORTY -> handleFortyScoring(playerId, opponentScore);
            case ADVANTAGE -> winGame(playerId);
        }
    }
    
    private void handleFortyScoring(PlayerId playerId, GameScore opponentScore) {
        switch (opponentScore) {
            case LOVE, FIFTEEN, THIRTY -> winGame(playerId);
            case FORTY -> {
                status = GameStatus.DEUCE;
                scores.put(playerId, GameScore.ADVANTAGE);
            }
            case ADVANTAGE -> {
                status = GameStatus.DEUCE;
                scores.put(getOpponent(playerId), GameScore.FORTY);
                scores.put(playerId, GameScore.FORTY);
            }
        }
    }
    
    private void scoreTiebreakPoint(PlayerId playerId) {
        // 搶七局計分邏輯
        int currentPoints = getTiebreakScore(playerId);
        int opponentPoints = getTiebreakScore(getOpponent(playerId));
        
        currentPoints++;
        
        if (currentPoints >= 7 && currentPoints - opponentPoints >= 2) {
            winGame(playerId);
        }
        
        // 更新分數（在搶七局中使用特殊處理）
        setTiebreakScore(playerId, currentPoints);
    }
    
    private void winGame(PlayerId playerId) {
        this.status = GameStatus.COMPLETED;
        // 這裡需要從 PlayerId 轉換為 Player 物件
        // 實際實作中需要透過 Match 來取得 Player 參考
    }
    
    // Getters 和輔助方法
    public int getGameNumber() { return gameNumber; }
    public boolean isTiebreak() { return isTiebreak; }
    public GameStatus getStatus() { return status; }
    public boolean isCompleted() { return status == GameStatus.COMPLETED; }
    public Player getWinner() { return winner; }
    
    public GameScore getScore(PlayerId playerId) {
        return scores.get(playerId);
    }
    
    private PlayerId getOpponent(PlayerId playerId) {
        // 簡化實作，實際需要更複雜的邏輯
        return playerId == PlayerId.PLAYER1 ? PlayerId.PLAYER2 : PlayerId.PLAYER1;
    }
}
```

**Player (球員) - Entity**
```java
public class Player {
    private final PlayerId playerId;
    private final PlayerName name;
    private int setsWon;
    private int gamesWon;
    private int pointsWon;
    
    public Player(PlayerId playerId, String name) {
        this.playerId = playerId;
        this.name = new PlayerName(name);
        this.setsWon = 0;
        this.gamesWon = 0;
        this.pointsWon = 0;
    }
    
    public void incrementSetsWon() { this.setsWon++; }
    public void incrementGamesWon() { this.gamesWon++; }
    public void incrementPointsWon() { this.pointsWon++; }
    
    // Getters
    public PlayerId getPlayerId() { return playerId; }
    public String getName() { return name.getValue(); }
    public int getSetsWon() { return setsWon; }
    public int getGamesWon() { return gamesWon; }
    public int getPointsWon() { return pointsWon; }
}
```

**Value Objects (值物件)**
```java
public record MatchId(String value) {
    public MatchId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or empty");
        }
    }
    
    public static MatchId generate() {
        return new MatchId(UUID.randomUUID().toString());
    }
    
    public String getValue() { return value; }
}

public record PlayerId(String value) {
    public PlayerId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty");
        }
    }
    
    public static PlayerId generate() {
        return new PlayerId(UUID.randomUUID().toString());
    }
    
    public String getValue() { return value; }
}

public record PlayerName(String value) {
    public PlayerName {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Player name cannot exceed 50 characters");
        }
    }
    
    public String getValue() { return value; }
}
```

**列舉類型**
```java
public enum GameScore {
    LOVE(0, "0"),
    FIFTEEN(15, "15"), 
    THIRTY(30, "30"), 
    FORTY(40, "40"), 
    ADVANTAGE(50, "AD");
    
    private final int numericValue;
    private final String displayValue;
    
    GameScore(int numericValue, String displayValue) {
        this.numericValue = numericValue;
        this.displayValue = displayValue;
    }
    
    public int getNumericValue() { return numericValue; }
    public String getDisplayValue() { return displayValue; }
}

public enum MatchStatus {
    IN_PROGRESS("進行中"),
    COMPLETED("已完成"),
    CANCELLED("已取消");
    
    private final String displayName;
    
    MatchStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() { return displayName; }
}

public enum GameStatus {
    IN_PROGRESS("進行中"),
    DEUCE("平分"),
    ADVANTAGE_PLAYER1("球員1優勢"),
    ADVANTAGE_PLAYER2("球員2優勢"),
    COMPLETED("已完成");
    
    private final String displayName;
    
    GameStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() { return displayName; }
}
```

**Domain Events (領域事件)**
```java
public abstract class DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredAt;
    
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
    }
    
    public String getEventId() { return eventId; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}

public class MatchCreatedEvent extends DomainEvent {
    private final String matchId;
    private final String player1Name;
    private final String player2Name;
    
    public MatchCreatedEvent(Match match) {
        super();
        this.matchId = match.getMatchId();
        this.player1Name = match.getPlayer1().getName();
        this.player2Name = match.getPlayer2().getName();
    }
    
    // Getters
    public String getMatchId() { return matchId; }
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
}

public class PointScoredEvent extends DomainEvent {
    private final String matchId;
    private final String playerId;
    private final String currentScore;
    
    public PointScoredEvent(Match match, String playerId) {
        super();
        this.matchId = match.getMatchId();
        this.playerId = playerId;
        this.currentScore = buildCurrentScore(match);
    }
    
    private String buildCurrentScore(Match match) {
        // 建構當前分數字串
        return "實作分數格式化邏輯";
    }
    
    // Getters
    public String getMatchId() { return matchId; }
    public String getPlayerId() { return playerId; }
    public String getCurrentScore() { return currentScore; }
}

public class MatchCompletedEvent extends DomainEvent {
    private final String matchId;
    private final String winnerId;
    private final String finalScore;
    
    public MatchCompletedEvent(Match match) {
        super();
        this.matchId = match.getMatchId();
        this.winnerId = match.getWinner().getPlayerId().getValue();
        this.finalScore = buildFinalScore(match);
    }
    
    private String buildFinalScore(Match match) {
        // 建構最終比分字串
        return "實作最終比分格式化邏輯";
    }
    
    // Getters
    public String getMatchId() { return matchId; }
    public String getWinnerId() { return winnerId; }
    public String getFinalScore() { return finalScore; }
}
```

## 資料模型

### API 請求/回應模型

#### CreateMatchRequest
```java
public class CreateMatchRequest {
    @NotBlank
    private String player1Name;
    
    @NotBlank
    private String player2Name;
}
```

#### ScorePointRequest
```java
public class ScorePointRequest {
    @NotBlank
    private String playerId;
}
```

#### MatchResponse
```java
public class MatchResponse {
    private String matchId;
    private PlayerResponse player1;
    private PlayerResponse player2;
    private List<SetResponse> sets;
    private String status;
    private String createdAt;
    private String completedAt;
    private String winner;
    private CurrentScoreResponse currentScore;
}
```

#### CurrentScoreResponse
```java
public class CurrentScoreResponse {
    private String player1Score;
    private String player2Score;
    private int player1Games;
    private int player2Games;
    private int player1Sets;
    private int player2Sets;
    private int currentSet;
    private int currentGame;
    private boolean isTiebreak;
    private String gameStatus;
}
```

## 錯誤處理

### 全域異常處理器
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMatchNotFound(MatchNotFoundException ex);
    
    @ExceptionHandler(InvalidMatchStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMatchState(InvalidMatchStateException ex);
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex);
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex);
}
```

### 自定義異常
```java
public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(String matchId) {
        super("Match not found with ID: " + matchId);
    }
}

public class InvalidMatchStateException extends RuntimeException {
    public InvalidMatchStateException(String message) {
        super(message);
    }
}
```

### 錯誤回應模型
```java
public class ErrorResponse {
    private String error;
    private String message;
    private int status;
    private LocalDateTime timestamp;
}
```

## 測試策略

### 1. BDD 測試情境 (Cucumber + Gherkin)

#### 功能檔案範例 (match-creation.feature)
```gherkin
Feature: 網球比賽創建
  作為比賽管理者
  我想要創建新的網球比賽
  以便開始追蹤兩名球員之間的對戰

  Scenario: 成功創建新比賽
    Given 我有兩名球員 "張三" 和 "李四"
    When 我創建一場新比賽
    Then 比賽應該被成功創建
    And 比賽狀態應該是 "進行中"
    And 初始分數應該是 0-0

  Scenario: 創建比賽時缺少球員資訊
    Given 我只提供一名球員 "張三"
    When 我嘗試創建比賽
    Then 應該返回 400 錯誤
    And 錯誤訊息應該包含 "球員資訊不完整"
```

#### 計分邏輯測試 (scoring.feature)
```gherkin
Feature: 網球計分邏輯
  作為比賽管理者
  我想要正確記錄球員得分
  以便準確追蹤比賽進度

  Scenario: 基本計分流程
    Given 存在一場進行中的比賽
    And 當前分數是 0-0
    When 球員1得分
    Then 球員1的分數應該是 15
    When 球員1再得分
    Then 球員1的分數應該是 30
    When 球員1再得分
    Then 球員1的分數應該是 40
    When 球員1再得分
    Then 球員1應該贏得該局

  Scenario: 平分處理
    Given 存在一場進行中的比賽
    And 雙方分數都是 40
    When 球員1得分
    Then 球員1應該獲得優勢
    When 球員2得分
    Then 比賽應該回到平分狀態
```

### 2. 單元測試

#### Service 層測試
```java
@ExtendWith(MockitoExtension.class)
class MatchServiceTest {
    
    @Mock
    private MatchRepository matchRepository;
    
    @Mock
    private ScoringService scoringService;
    
    @InjectMocks
    private MatchService matchService;
    
    @Test
    void shouldCreateMatchSuccessfully() {
        // 測試比賽創建邏輯
    }
    
    @Test
    void shouldThrowExceptionWhenMatchNotFound() {
        // 測試異常處理
    }
}
```

### 3. 整合測試

#### API 端點測試
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class MatchControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCreateMatchViaAPI() {
        CreateMatchRequest request = new CreateMatchRequest("張三", "李四");
        
        ResponseEntity<MatchResponse> response = restTemplate.postForEntity(
            "/api/matches", request, MatchResponse.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getPlayer1().getName()).isEqualTo("張三");
    }
}
```

### 4. 測試覆蓋率目標
- 單元測試覆蓋率: > 90%
- 整合測試覆蓋率: > 80%
- BDD 情境覆蓋: 所有主要業務流程

## 配置和部署

### 1. Maven 依賴 (pom.xml)
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Swagger/OpenAPI -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-spring</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2. 應用程式配置 (application.yml)
```yaml
server:
  port: 8080

spring:
  application:
    name: tennis-scoring-system

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

logging:
  level:
    com.tennisscoring: DEBUG
    org.springframework.web: INFO
```

### 3. Swagger 配置
```java
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "網球計分系統 API",
        version = "1.0",
        description = "提供網球比賽計分管理的 RESTful API"
    )
)
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components())
            .info(new Info()
                .title("Tennis Scoring System API")
                .version("1.0")
                .description("RESTful API for managing tennis match scoring"));
    }
}
```

## 效能考量

### 1. 記憶體管理
- 使用 ConcurrentHashMap 確保執行緒安全
- 實作比賽清理機制，避免記憶體洩漏
- 考慮實作 LRU 快取策略

### 2. 並發處理
- 使用 @Transactional 確保資料一致性
- 實作樂觀鎖定避免競態條件
- 考慮使用 CompletableFuture 處理非同步操作

### 3. 擴展性
- 設計支援資料庫持久化的介面
- 考慮實作快取層 (Redis)
- 支援水平擴展的無狀態設計

## 安全性考量

### 1. 輸入驗證
- 使用 Bean Validation 驗證所有輸入
- 實作自定義驗證器
- 防止 SQL 注入和 XSS 攻擊

### 2. API 安全
- 實作 CORS 配置
- 考慮加入 API 金鑰驗證
- 實作請求限流機制

### 3. 錯誤處理
- 避免洩漏敏感資訊
- 統一錯誤回應格式
- 實作詳細的日誌記錄

## 監控和日誌

### 1. 應用程式監控
- 整合 Spring Boot Actuator
- 實作健康檢查端點
- 監控 API 回應時間和錯誤率

### 2. 日誌策略
- 使用結構化日誌格式
- 實作不同層級的日誌記錄
- 考慮整合 ELK Stack

這個設計提供了一個完整、可擴展且符合現代 Java 開發最佳實務的網球計分系統架構。