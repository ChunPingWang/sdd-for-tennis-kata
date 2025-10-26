# 程式碼審查檢查清單 - SOLID 原則

## 使用說明

本檢查清單用於程式碼審查時驗證 SOLID 原則的遵循情況。審查者應該逐項檢查，確保程式碼符合高品質標準。

## 🔍 審查流程

### 1. 準備階段
- [ ] 了解變更的業務需求和技術背景
- [ ] 檢查相關的設計文件和架構圖
- [ ] 確認測試覆蓋率和測試品質

### 2. SOLID 原則檢查
按照以下檢查清單逐項驗證每個原則的遵循情況。

### 3. 總體評估
- [ ] 程式碼整體架構合理
- [ ] 符合專案的編碼標準
- [ ] 沒有明顯的效能問題

---

## 📋 SOLID 原則檢查清單

### 🎯 單一職責原則 (SRP) 檢查

#### 類別層級檢查
- [ ] **職責單一性**: 每個類別只負責一項明確的職責
- [ ] **變更理由唯一**: 類別只有一個改變的理由
- [ ] **命名清晰**: 類別名稱清楚表達其職責
- [ ] **方法一致性**: 所有方法都圍繞同一個職責

#### 方法層級檢查
- [ ] **方法專注**: 每個方法只做一件事
- [ ] **參數合理**: 方法參數數量適中（建議 ≤ 3 個）
- [ ] **回傳值明確**: 方法回傳值類型和意義清楚

#### 常見違規模式檢查
- [ ] **避免 God Class**: 類別不超過 200 行程式碼
- [ ] **避免混合職責**: 沒有在同一類別中混合業務邏輯、資料存取、UI 邏輯
- [ ] **避免工具類別濫用**: 工具類別方法相關且專注

```java
// ✅ 好的範例
@Service
public class MatchStatisticsService {
    // 只負責統計相關的邏輯
    public MatchStatistics getMatchStatistics(String matchId) { ... }
    public SystemStatistics getSystemStatistics() { ... }
}

// ❌ 壞的範例  
@Service
public class MatchService {
    public Match createMatch(...) { ... }        // 比賽管理
    public Statistics getStats(...) { ... }      // 統計 - 違反 SRP
    public void sendEmail(...) { ... }           // 通知 - 違反 SRP
}
```

---

### 🔓 開放封閉原則 (OCP) 檢查

#### 擴展性檢查
- [ ] **抽象設計**: 使用介面和抽象類別支援擴展
- [ ] **策略模式**: 使用策略模式處理變化的演算法
- [ ] **工廠模式**: 使用工廠模式支援新類型的創建
- [ ] **配置驅動**: 行為可以通過配置改變而不修改程式碼

#### 修改封閉性檢查
- [ ] **避免修改現有程式碼**: 新功能通過添加新類別實現
- [ ] **避免大型 switch**: 沒有大型的 switch/if-else 語句
- [ ] **多型使用**: 適當使用多型來避免條件邏輯

```java
// ✅ 好的範例 - 工廠模式
public interface MatchFactory {
    Match createMatch(String player1, String player2);
    boolean supports(String matchType);
}

// 添加新類型不需要修改現有程式碼
public class TournamentMatchFactory implements MatchFactory { ... }

// ❌ 壞的範例
public class MatchCreator {
    public Match createMatch(String type, String p1, String p2) {
        switch (type) {
            case "STANDARD": return new StandardMatch(p1, p2);
            case "TOURNAMENT": return new TournamentMatch(p1, p2);
            // 每次添加新類型都要修改這裡
        }
    }
}
```

---

### 🔄 里氏替換原則 (LSP) 檢查

#### 替換性檢查
- [ ] **行為一致性**: 子類別不改變父類別的預期行為
- [ ] **異常處理**: 子類別不拋出父類別未預期的異常
- [ ] **前置條件**: 子類別不加強前置條件
- [ ] **後置條件**: 子類別不削弱後置條件

#### 契約遵循檢查
- [ ] **介面契約**: 所有實作都遵循介面定義的契約
- [ ] **文件一致**: 實作行為與文件描述一致
- [ ] **測試通過**: 所有實作都能通過相同的測試

```java
// ✅ 好的範例
public abstract class BaseRepository {
    public final Entity save(Entity entity) {
        validate(entity);
        return doSave(entity);
    }
    protected abstract Entity doSave(Entity entity);
}

public class DatabaseRepository extends BaseRepository {
    @Override
    protected Entity doSave(Entity entity) {
        return entityManager.persist(entity); // 遵循契約
    }
}

// ❌ 壞的範例
public class ReadOnlyRepository extends BaseRepository {
    @Override
    protected Entity doSave(Entity entity) {
        throw new UnsupportedOperationException(); // 違反 LSP
    }
}
```

---

### 🔗 介面隔離原則 (ISP) 檢查

#### 介面設計檢查
- [ ] **介面專注**: 每個介面只包含相關的方法
- [ ] **客戶端需求**: 客戶端只依賴它們使用的方法
- [ ] **介面大小**: 介面方法數量適中（建議 ≤ 7 個方法）
- [ ] **職責單一**: 介面職責明確且單一

#### 依賴檢查
- [ ] **最小依賴**: 類別只實作需要的介面
- [ ] **介面分離**: 大介面已分解為多個小介面
- [ ] **客戶端隔離**: 不同客戶端使用不同的介面

```java
// ✅ 好的範例 - 分離的介面
public interface MatchCreationPort {
    Match createMatch(String player1, String player2);
}

public interface MatchScoringPort {
    Match scorePoint(String matchId, String playerId);
}

// 客戶端只依賴需要的介面
public class MatchController {
    private final MatchCreationPort matchCreation;
    private final MatchScoringPort matchScoring;
}

// ❌ 壞的範例 - 臃腫的介面
public interface MatchManagementPort {
    Match createMatch(...);
    Match scorePoint(...);
    Match getMatch(...);
    List<Match> getAllMatches();
    void deleteMatch(...);
    Statistics getStatistics(...); // 不相關的方法
}
```

---

### ⬆️ 依賴反轉原則 (DIP) 檢查

#### 依賴方向檢查
- [ ] **依賴抽象**: 高層模組依賴介面而非具體實作
- [ ] **依賴注入**: 使用依賴注入而非直接實例化
- [ ] **配置管理**: 依賴關係在配置類別中管理
- [ ] **測試友好**: 依賴可以輕鬆 Mock 和替換

#### 抽象設計檢查
- [ ] **介面定義**: 為主要服務定義介面
- [ ] **實作分離**: 介面和實作在不同的套件
- [ ] **循環依賴**: 沒有循環依賴問題

```java
// ✅ 好的範例
@Service
public class MatchDomainService implements MatchService {
    private final MatchRepositoryPort repository;    // 依賴抽象
    private final ScoringService scoringService;     // 依賴抽象
    
    public MatchDomainService(MatchRepositoryPort repository, 
                             ScoringService scoringService) {
        this.repository = repository;
        this.scoringService = scoringService;
    }
}

// ❌ 壞的範例
@Service  
public class MatchDomainService {
    private final InMemoryRepository repository;     // 依賴具體實作
    
    public MatchDomainService() {
        this.repository = new InMemoryRepository();  // 直接實例化
    }
}
```

---

## 🎯 整體架構檢查

### 六角形架構檢查
- [ ] **領域核心**: 業務邏輯不依賴外部框架
- [ ] **Port 定義**: Primary 和 Secondary Port 定義清楚
- [ ] **Adapter 實作**: Adapter 正確實作 Port 介面
- [ ] **依賴方向**: 依賴方向符合六角形架構原則

### 測試檢查
- [ ] **單元測試**: 每個類別都有對應的單元測試
- [ ] **Mock 使用**: 適當使用 Mock 隔離依賴
- [ ] **測試覆蓋**: 測試覆蓋率達到要求（建議 > 80%）
- [ ] **BDD 測試**: 重要業務流程有 BDD 測試

---

## 📊 評分標準

### 評分等級
- **優秀 (90-100%)**: 完全符合 SOLID 原則，程式碼品質極高
- **良好 (80-89%)**: 基本符合 SOLID 原則，有少量改進空間
- **及格 (70-79%)**: 部分符合 SOLID 原則，需要重構改進
- **不及格 (<70%)**: 嚴重違反 SOLID 原則，需要重新設計

### 必須修正的問題
- [ ] 嚴重違反 SRP（God Class）
- [ ] 硬編碼的依賴關係
- [ ] 臃腫的介面設計
- [ ] 無法擴展的設計

### 建議改進的問題
- [ ] 方法參數過多
- [ ] 類別職責不夠明確
- [ ] 缺少適當的抽象
- [ ] 測試覆蓋不足

---

## 📝 審查報告模板

```markdown
## 程式碼審查報告

### 基本資訊
- **審查者**: [姓名]
- **審查日期**: [日期]
- **變更範圍**: [描述]
- **相關 Issue**: [連結]

### SOLID 原則評估
- **SRP**: ✅/⚠️/❌ [評語]
- **OCP**: ✅/⚠️/❌ [評語]  
- **LSP**: ✅/⚠️/❌ [評語]
- **ISP**: ✅/⚠️/❌ [評語]
- **DIP**: ✅/⚠️/❌ [評語]

### 主要發現
#### 優點
- [列出程式碼的優點]

#### 需要改進
- [列出需要改進的地方]

#### 必須修正
- [列出必須修正的問題]

### 總體評分
- **評分**: [優秀/良好/及格/不及格]
- **建議**: [審查建議]

### 後續行動
- [ ] [具體的改進行動項目]
```

---

## 🔧 常用重構技巧

### SRP 重構
- **Extract Class**: 將多重職責分離到不同類別
- **Extract Method**: 將複雜方法分解為多個小方法
- **Move Method**: 將方法移到更合適的類別

### OCP 重構  
- **Replace Conditional with Polymorphism**: 用多型替換條件邏輯
- **Extract Interface**: 為變化點創建抽象介面
- **Strategy Pattern**: 封裝變化的演算法

### ISP 重構
- **Extract Interface**: 從大介面中提取小介面
- **Segregate Interface**: 將介面按客戶端需求分離

### DIP 重構
- **Extract Interface**: 為具體類別創建抽象介面
- **Introduce Dependency Injection**: 使用依賴注入替換直接實例化

---

## 📚 參考資源

- [SOLID Principles Guide](./SOLID_PRINCIPLES_GUIDE.md)
- [SOLID Principles Compliance Report](./SOLID_PRINCIPLES_COMPLIANCE.md)
- [Architecture Documentation](./ARCHITECTURE.md)
- [Testing Guidelines](./TESTING_GUIDELINES.md)

記住：程式碼審查不僅是找出問題，更是團隊學習和知識分享的機會。保持建設性的態度，專注於程式碼品質的提升。