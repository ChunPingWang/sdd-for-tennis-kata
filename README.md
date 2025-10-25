# 網球計分系統 - Spec-Driven Development 記錄

## 專案概述

本專案採用 Spec-Driven Development (SDD) 方法開發一個單打網球計分系統的後台程式，提供 Web API 給外部使用。

## SDD 開發流程記錄

### 步驟 1: 需求收集 (Requirements Gathering)

#### 使用者輸入
```
根據這份網球計分規則，開發一個單打的網球計分程式的後台程式，提供 Web API 給外部使用使用 SDD 的開發方式
```

**參考資料:** [網球計分系統 - 維基百科](https://zh.wikipedia.org/zh-tw/%E7%BD%91%E7%90%83%E8%AE%A1%E5%88%86%E7%B3%BB%E7%BB%9F)

#### 補充需求
```
使用 Java 17
Spring Boot Web API
並採用 BDD 的 Gherkins 描述情境，並以測試先行
Web API 的測試需用 Spring Boot Test，並提供 Swagger
```

#### 產出文件
- **文件位置:** `.kiro/specs/tennis-scoring-system/requirements.md`
- **內容包含:**
  - 完整的術語表定義
  - 13 個主要需求，涵蓋：
    - 比賽創建和管理
    - 網球計分邏輯（0-15-30-40）
    - 平分和優勢處理
    - 搶七局邏輯
    - Web API 功能
    - Java 17 + Spring Boot 技術要求
    - BDD + Gherkin 測試要求
    - Swagger API 文件要求
  - 每個需求都使用 EARS 格式的驗收標準

### 步驟 2: 系統設計 (System Design)

#### 使用者輸入
```
開始設計階段
```

#### 架構調整需求
```
加入，採用六角形架構
```

#### 產出文件
- **文件位置:** `.kiro/specs/tennis-scoring-system/design.md`
- **架構選擇:** 六角形架構 (Hexagonal Architecture / Ports and Adapters)
- **技術棧:**
  - Java 17
  - Spring Boot 3.x
  - Maven 建構工具
  - Swagger/OpenAPI 3
  - JUnit 5 + Spring Boot Test + Cucumber (BDD)

#### 設計重點

**1. 六角形架構層次:**
- **Domain Core (領域核心):** 純粹的業務邏輯，不依賴外部框架
- **Primary Ports (主要埠):** 定義業務介面 (MatchManagementPort, QueryPort)
- **Secondary Ports (次要埠):** 定義外部依賴介面 (MatchRepositoryPort, EventPublisherPort)
- **Primary Adapters (主要適配器):** REST Controllers, Swagger 配置
- **Secondary Adapters (次要適配器):** 記憶體儲存, 事件發布實作

**2. 領域模型設計:**
- **Aggregate Root:** Match (比賽) 作為聚合根
- **Entities:** Set (盤), Game (局), Player (球員)
- **Value Objects:** MatchId, PlayerId, PlayerName (使用 Java Record)
- **Domain Events:** MatchCreatedEvent, PointScoredEvent, MatchCompletedEvent
- **Enums:** GameScore, MatchStatus, GameStatus

**3. API 設計:**
- RESTful 端點設計
- 完整的請求/回應模型
- 全域異常處理
- Bean Validation 輸入驗證

**4. 測試策略:**
- **BDD 測試:** 使用 Cucumber + Gherkin 語法
- **單元測試:** 針對 Domain Services 和業務邏輯
- **整合測試:** 使用 @SpringBootTest 測試 API 端點
- **測試覆蓋率目標:** 單元測試 >90%, 整合測試 >80%

**5. 品質保證:**
- 錯誤處理策略
- 並發安全設計
- 效能和安全性考量
- 監控和日誌策略

### 步驟 3: 實作計劃 (Implementation Plan)

#### 使用者輸入
```
將 SDD 的每個步驟，我輸入的內容，寫到 README.md，包含前面兩個步驟 requirements 與 design
```

**狀態:** 準備進入實作計劃階段

## 專案結構

```
tennis-scoring-system/
├── .kiro/
│   └── specs/
│       └── tennis-scoring-system/
│           ├── requirements.md    # 需求文件
│           ├── design.md         # 設計文件
│           └── tasks.md          # 實作計劃 (待建立)
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/tennisscoring/
│   │           ├── domain/        # 領域核心
│   │           ├── ports/         # 埠介面
│   │           ├── adapters/      # 適配器實作
│   │           └── config/        # 配置
│   └── test/
│       ├── java/                 # 單元測試和整合測試
│       └── resources/
│           └── features/         # BDD 功能檔案
├── pom.xml                       # Maven 配置
└── README.md                     # 本文件
```

## 核心業務規則

### 網球計分規則
1. **基本計分:** 0 → 15 → 30 → 40 → 勝利
2. **平分處理:** 雙方 40-40 時進入平分，需連續領先 2 分才能獲勝
3. **局數計算:** 先贏 6 局且領先 2 局者獲勝該盤
4. **搶七局:** 6-6 時進行搶七局，先得 7 分且領先 2 分者獲勝
5. **比賽勝負:** 先贏 2 盤者獲勝整場比賽

### API 端點設計
- `POST /api/matches` - 創建新比賽
- `GET /api/matches/{matchId}` - 查詢比賽資訊
- `PUT /api/matches/{matchId}/score` - 記錄得分
- `DELETE /api/matches/{matchId}` - 刪除比賽
- `GET /api/matches` - 查詢所有比賽

## 開發方法論

### Spec-Driven Development (SDD)
1. **需求收集:** 使用 EARS 格式和 INCOSE 品質規則
2. **系統設計:** 採用六角形架構確保可測試性和可維護性
3. **實作計劃:** 將設計轉換為可執行的開發任務
4. **測試先行:** BDD + TDD 確保品質

### 測試策略
- **BDD 測試:** 使用 Gherkin 語法描述業務情境
- **測試先行:** 先寫測試再實作功能
- **多層測試:** 單元測試 + 整合測試 + BDD 測試

## 技術決策記錄

| 決策 | 理由 | 替代方案 |
|------|------|----------|
| Java 17 | 現代 Java 特性，長期支援版本 | Java 11, Java 21 |
| Spring Boot 3.x | 成熟的企業級框架，豐富的生態系統 | Quarkus, Micronaut |
| 六角形架構 | 業務邏輯與框架分離，高可測試性 | 分層架構, 清潔架構 |
| 記憶體儲存 | 簡化初期開發，易於測試 | 資料庫持久化 |
| Cucumber BDD | 業務可讀的測試規格 | 純 JUnit 測試 |

## 下一步

準備進入實作計劃階段，將設計轉換為具體的開發任務清單。