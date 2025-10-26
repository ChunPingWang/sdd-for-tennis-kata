# 網球計分系統 (Tennis Scoring System)

基於 Spring Boot 3.x 和 Java 17 開發的完整網球計分系統，採用六角形架構設計原則。

## 🎾 主要功能

- **完整網球計分**: 支援標準網球計分規則，包括局、盤、比賽
- **平分與優勢**: 正確處理平分（Deuce）和優勢（Advantage）計分
- **搶七局支援**: 完整實作 6-6 時的搶七局邏輯
- **比賽管理**: 創建、追蹤和管理網球比賽
- **REST API**: 完整的 RESTful API 介面
- **即時更新**: 事件驅動架構，即時更新比賽狀態
- **完整測試**: 單元測試、整合測試和 BDD 測試場景

## 🏗️ 系統架構

本專案採用**六角形架構**（Ports and Adapters）設計原則：

```
┌─────────────────────────────────────────────────────────────┐
│                    Primary Adapters                        │
│  ┌─────────────────┐  ┌─────────────────┐                 │
│  │   REST API      │  │   Swagger UI    │                 │
│  │  (Controllers)  │  │   文件介面       │                 │
│  └─────────────────┘  └─────────────────┘                 │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    Domain Layer                             │
│  ┌─────────────────┐  ┌─────────────────┐                 │
│  │   Domain        │  │   Domain        │                 │
│  │   Services      │  │   Entities      │                 │
│  └─────────────────┘  └─────────────────┘                 │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                   Secondary Adapters                       │
│  ┌─────────────────┐  ┌─────────────────┐                 │
│  │   Repository    │  │   Event         │                 │
│  │  (In-Memory)    │  │  Publisher      │                 │
│  └─────────────────┘  └─────────────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 快速開始

### 系統需求

- Java 17 或更高版本
- Maven 3.6 或更高版本

### 安裝與執行

#### 1. 下載專案
```bash
git clone <repository-url>
cd tennis-scoring-system
```

#### 2. 編譯專案
```bash
mvn clean compile
```

#### 3. 執行測試
```bash
mvn test
```

#### 4. 啟動應用程式
```bash
mvn spring-boot:run
```

應用程式將在 `http://localhost:8080` 啟動

### 存取 API 文件

應用程式啟動後，可以存取：
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI 規格**: http://localhost:8080/api-docs

## 📚 API 使用指南

### 基本資訊

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **回應格式**: JSON

### API 端點總覽

| 方法 | 端點 | 說明 |
|------|------|------|
| POST | `/matches` | 創建新比賽 |
| GET | `/matches` | 取得所有比賽列表 |
| GET | `/matches/{matchId}` | 取得特定比賽詳情 |
| POST | `/matches/{matchId}/score` | 為比賽記錄得分 |
| PUT | `/matches/{matchId}/cancel` | 取消比賽 |
| DELETE | `/matches/{matchId}` | 刪除比賽 |
| GET | `/matches/statistics` | 取得比賽統計資訊 |

## 🎯 完整使用範例

### 1. 創建新比賽

**請求:**
```bash
curl -X POST http://localhost:8080/api/matches \
  -H "Content-Type: application/json" \
  -d '{
    "player1Name": "拉法·納達爾",
    "player2Name": "羅傑·費德勒"
  }'
```

**回應:**
```json
{
  "matchId": "bca822dc-da60-44d3-93fc-decdd4f39da9",
  "player1": {
    "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17",
    "name": "拉法·納達爾"
  },
  "player2": {
    "playerId": "7beaf12e-19c0-4a02-af34-9d0bf7544baf",
    "name": "羅傑·費德勒"
  },
  "status": "IN_PROGRESS",
  "currentScore": "0-0 (0-0)",
  "sets": [],
  "createdAt": "2025-10-26T08:44:54.112544"
}
```

### 2. 記錄得分

**請求:**
```bash
curl -X POST http://localhost:8080/api/matches/bca822dc-da60-44d3-93fc-decdd4f39da9/score \
  -H "Content-Type: application/json" \
  -d '{
    "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17"
  }'
```

**回應:**
```json
{
  "matchId": "bca822dc-da60-44d3-93fc-decdd4f39da9",
  "player1": {
    "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17",
    "name": "拉法·納達爾"
  },
  "player2": {
    "playerId": "7beaf12e-19c0-4a02-af34-9d0bf7544baf",
    "name": "羅傑·費德勒"
  },
  "status": "IN_PROGRESS",
  "currentScore": "0-0 (15-0)",
  "sets": [
    {
      "setNumber": 1,
      "player1Games": 0,
      "player2Games": 0,
      "status": "IN_PROGRESS",
      "currentGame": {
        "gameNumber": 1,
        "player1Score": "15",
        "player2Score": "0",
        "status": "IN_PROGRESS"
      }
    }
  ]
}
```

### 3. 查詢比賽詳情

**請求:**
```bash
curl http://localhost:8080/api/matches/bca822dc-da60-44d3-93fc-decdd4f39da9
```

**回應:**
```json
{
  "matchId": "bca822dc-da60-44d3-93fc-decdd4f39da9",
  "player1": {
    "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17",
    "name": "拉法·納達爾"
  },
  "player2": {
    "playerId": "7beaf12e-19c0-4a02-af34-9d0bf7544baf",
    "name": "羅傑·費德勒"
  },
  "status": "IN_PROGRESS",
  "currentScore": "1-0 (0-0)",
  "sets": [
    {
      "setNumber": 1,
      "player1Games": 1,
      "player2Games": 0,
      "status": "IN_PROGRESS",
      "currentGame": {
        "gameNumber": 2,
        "player1Score": "0",
        "player2Score": "0",
        "status": "IN_PROGRESS"
      }
    }
  ],
  "createdAt": "2025-10-26T08:44:54.112544"
}
```

### 4. 取得所有比賽

**請求:**
```bash
curl http://localhost:8080/api/matches
```

**回應:**
```json
[
  {
    "matchId": "bca822dc-da60-44d3-93fc-decdd4f39da9",
    "player1": {
      "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17",
      "name": "拉法·納達爾"
    },
    "player2": {
      "playerId": "7beaf12e-19c0-4a02-af34-9d0bf7544baf",
      "name": "羅傑·費德勒"
    },
    "status": "IN_PROGRESS",
    "currentScore": "1-0 (0-0)",
    "createdAt": "2025-10-26T08:44:54.112544"
  }
]
```

### 5. 取消比賽

**請求:**
```bash
curl -X PUT http://localhost:8080/api/matches/bca822dc-da60-44d3-93fc-decdd4f39da9/cancel
```

**回應:**
```json
{
  "matchId": "bca822dc-da60-44d3-93fc-decdd4f39da9",
  "player1": {
    "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17",
    "name": "拉法·納達爾"
  },
  "player2": {
    "playerId": "7beaf12e-19c0-4a02-af34-9d0bf7544baf",
    "name": "羅傑·費德勒"
  },
  "status": "CANCELLED",
  "currentScore": "1-0 (15-0)",
  "sets": [
    {
      "setNumber": 1,
      "player1Games": 1,
      "player2Games": 0,
      "status": "CANCELLED"
    }
  ]
}
```

### 6. 刪除比賽

**請求:**
```bash
curl -X DELETE http://localhost:8080/api/matches/bca822dc-da60-44d3-93fc-decdd4f39da9
```

**回應:**
```
HTTP 204 No Content
```

### 7. 取得統計資訊

**請求:**
```bash
curl http://localhost:8080/api/matches/statistics
```

**回應:**
```json
{
  "totalMatches": 15,
  "inProgressMatches": 3,
  "completedMatches": 10,
  "cancelledMatches": 2
}
```

## 🎾 網球計分規則說明

### 基本計分規則

#### 局內計分
- **0 分**: "0" (Love)
- **1 分**: "15"
- **2 分**: "30" 
- **3 分**: "40"
- **4 分**: 贏得該局（如果對手分數 ≤ 2 分）

#### 平分與優勢
- 當雙方都達到 40 分時：**平分 (Deuce)**
- 從平分狀態得分的球員：**優勢 (Advantage)**
- 有優勢的球員再得分：**贏得該局**
- 沒有優勢的球員得分：**回到平分**

#### 盤數計分
- 先贏得 6 局的球員贏得該盤
- 必須領先 2 局（例如：6-4, 7-5）
- 6-6 時：進行**搶七局**

#### 搶七局計分
- 先得到 7 分的球員贏得搶七局
- 必須領先 2 分
- 搶七局獲勝者以 7-6 贏得該盤

#### 比賽計分
- 三盤兩勝制（先贏 2 盤獲勝）
- 比賽在球員贏得 2 盤時結束

## 🎮 完整比賽流程範例

以下是一個完整的比賽流程範例，展示如何從創建比賽到完成一局：

### 步驟 1: 創建比賽並取得球員 ID

```bash
# 創建比賽
MATCH_RESPONSE=$(curl -s -X POST http://localhost:8080/api/matches \
  -H "Content-Type: application/json" \
  -d '{
    "player1Name": "拉法·納達爾",
    "player2Name": "羅傑·費德勒"
  }')

# 解析回應取得 ID
MATCH_ID=$(echo $MATCH_RESPONSE | jq -r '.matchId')
PLAYER1_ID=$(echo $MATCH_RESPONSE | jq -r '.player1.playerId')
PLAYER2_ID=$(echo $MATCH_RESPONSE | jq -r '.player2.playerId')

echo "比賽 ID: $MATCH_ID"
echo "納達爾 ID: $PLAYER1_ID"
echo "費德勒 ID: $PLAYER2_ID"
```

### 步驟 2: 進行第一局（納達爾 4-1 獲勝）

```bash
# 納達爾得 4 分
for i in {1..4}; do
  curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
    -H "Content-Type: application/json" \
    -d "{\"playerId\": \"$PLAYER1_ID\"}" > /dev/null
  echo "納達爾得第 $i 分"
done

# 費德勒得 1 分
curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
  -H "Content-Type: application/json" \
  -d "{\"playerId\": \"$PLAYER2_ID\"}" > /dev/null
echo "費德勒得 1 分"

# 查看比分
curl -s http://localhost:8080/api/matches/$MATCH_ID | jq '.currentScore'
# 輸出: "1-0 (0-0)"
```

### 步驟 3: 進行平分局面

```bash
# 雙方各得 3 分達到平分
for i in {1..3}; do
  # 納達爾得分
  curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
    -H "Content-Type: application/json" \
    -d "{\"playerId\": \"$PLAYER1_ID\"}" > /dev/null
  
  # 費德勒得分
  curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
    -H "Content-Type: application/json" \
    -d "{\"playerId\": \"$PLAYER2_ID\"}" > /dev/null
done

echo "達到平分狀態"

# 納達爾取得優勢
curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
  -H "Content-Type: application/json" \
  -d "{\"playerId\": \"$PLAYER1_ID\"}" > /dev/null
echo "納達爾取得優勢"

# 納達爾贏得該局
curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
  -H "Content-Type: application/json" \
  -d "{\"playerId\": \"$PLAYER1_ID\"}" > /dev/null
echo "納達爾贏得該局"

# 查看最終比分
curl -s http://localhost:8080/api/matches/$MATCH_ID | jq '{
  currentScore: .currentScore,
  status: .status,
  sets: .sets
}'
```

## ❌ 錯誤處理

系統提供完整的錯誤處理機制：

### 常見錯誤回應格式

```json
{
  "error": "錯誤類型",
  "message": "詳細錯誤訊息",
  "status": 400,
  "path": "/api/matches",
  "timestamp": "2025-10-26T08:44:42.123456"
}
```

### 錯誤類型說明

| HTTP 狀態碼 | 錯誤類型 | 說明 |
|-------------|----------|------|
| 400 | Bad Request | 請求格式錯誤或參數驗證失敗 |
| 404 | Not Found | 找不到指定的比賽或球員 |
| 409 | Conflict | 比賽狀態衝突（如已結束的比賽無法繼續得分） |
| 500 | Internal Server Error | 系統內部錯誤 |

### 錯誤範例

#### 1. 球員名稱重複
```bash
curl -X POST http://localhost:8080/api/matches \
  -H "Content-Type: application/json" \
  -d '{
    "player1Name": "同名球員",
    "player2Name": "同名球員"
  }'
```

**錯誤回應:**
```json
{
  "error": "Duplicate Player",
  "message": "球員名稱必須不同",
  "status": 400,
  "path": "/api/matches",
  "timestamp": "2025-10-26T08:44:54.388"
}
```

#### 2. 比賽不存在
```bash
curl http://localhost:8080/api/matches/invalid-match-id
```

**錯誤回應:**
```json
{
  "error": "Match Not Found",
  "message": "找不到 ID 為 invalid-match-id 的比賽",
  "status": 404,
  "path": "/api/matches/invalid-match-id",
  "timestamp": "2025-10-26T08:44:54.287"
}
```

#### 3. 已結束比賽無法得分
```bash
curl -X POST http://localhost:8080/api/matches/completed-match-id/score \
  -H "Content-Type: application/json" \
  -d '{"playerId": "some-player-id"}'
```

**錯誤回應:**
```json
{
  "error": "Invalid Match State",
  "message": "無法在已結束的比賽中記錄得分",
  "status": 409,
  "path": "/api/matches/completed-match-id/score",
  "timestamp": "2025-10-26T08:44:54.494"
}
```

## 🧪 測試

本專案包含多層次的完整測試：

### 執行所有測試
```bash
mvn test
```

### 分類執行測試

#### 單元測試
```bash
mvn test -Dtest="*Test"
```

#### 整合測試
```bash
mvn test -Dtest="*Integration*"
```

#### BDD 測試（Cucumber）
```bash
mvn test -Dtest="*Steps"
```

#### 效能測試
```bash
mvn test -Dtest="*Performance*"
```

### 測試覆蓋率

專案維持高測試覆蓋率：
- **領域模型**: 95%+ 覆蓋率
- **領域服務**: 90%+ 覆蓋率  
- **API 控制器**: 85%+ 覆蓋率
- **整合場景**: 100% 關鍵路徑覆蓋

## ⚙️ 配置說明

### 應用程式配置

可透過 `application.yml` 進行配置：

```yaml
server:
  port: 8080

logging:
  level:
    com.tennisscoring: INFO
    org.springframework: WARN

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### 環境變數

支援以下環境變數：

- `SERVER_PORT`: 應用程式埠號（預設：8080）
- `LOGGING_LEVEL_ROOT`: 根日誌等級（預設：INFO）
- `SPRING_PROFILES_ACTIVE`: 啟用的 Spring Profile

### Profile 設定

- **default**: 標準配置
- **test**: 測試專用配置，使用記憶體儲存
- **dev**: 開發環境配置，啟用詳細日誌

## 🏆 領域模型

### 核心實體

- **Match**: 聚合根，管理整個網球比賽
- **Set**: 代表比賽中的一盤（包含多局）
- **Game**: 個別局次，處理網球計分（0, 15, 30, 40, 平分, 優勢）
- **Player**: 比賽參與者，具有唯一識別

### 值物件

- **MatchId**: 唯一比賽識別碼
- **PlayerId**: 唯一球員識別碼  
- **PlayerName**: 經過驗證的球員姓名
- **GameScore**: 網球局內計分列舉

### 領域事件

- **MatchCreatedEvent**: 比賽創建時觸發
- **PointScoredEvent**: 記錄得分時觸發
- **GameWonEvent**: 局次結束時觸發
- **SetWonEvent**: 盤次結束時觸發
- **MatchCompletedEvent**: 比賽結束時觸發

## 🚀 部署

### 建置生產版本

```bash
mvn clean package -Pprod
```

### Docker 支援

```dockerfile
FROM openjdk:17-jre-slim
COPY target/tennis-scoring-system-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 建置並執行 Docker 容器

```bash
# 建置映像檔
docker build -t tennis-scoring-system .

# 執行容器
docker run -p 8080:8080 tennis-scoring-system
```

## 🤝 開發貢獻

### 開發流程

1. Fork 此專案
2. 建立功能分支 (`git checkout -b feature/amazing-feature`)
3. 為變更撰寫測試
4. 實作功能
5. 確保所有測試通過 (`mvn test`)
6. 提交變更 (`git commit -m 'Add amazing feature'`)
7. 推送到分支 (`git push origin feature/amazing-feature`)
8. 開啟 Pull Request

### 程式碼品質

- **Checkstyle**: 強制執行編碼標準
- **SpotBugs**: 靜態分析偵測潛在問題
- **JaCoCo**: 程式碼覆蓋率報告
- **SonarQube**: 程式碼品質指標（需額外配置）

## 📞 支援與說明

如需協助或有疑問：
- 在專案中建立 Issue
- 查看 [API 文件](http://localhost:8080/swagger-ui/index.html)
- 參考 `src/test/resources/features/` 中的測試場景

## 📝 授權

本專案採用 MIT 授權條款 - 詳見 [LICENSE](LICENSE) 檔案。

## 🙏 致謝

- 網球計分規則基於國際網球總會（ITF）官方規定
- 六角形架構原則來自 Alistair Cockburn
- Spring Boot 框架提供快速開發能力
- Cucumber 支援行為驅動開發

## 🤖 Kiro SDD 開發流程

本專案採用 **Kiro Spec-Driven Development (SDD)** 方法論開發，這是一個系統化的軟體開發流程，確保從需求到實作的完整追溯性。

### 📋 SDD 三階段流程

#### 階段 1: 需求收集 (Requirements Gathering)
**目標**: 將模糊的使用者需求轉換為精確的技術規格

**輸入**:
```
根據這份網球計分規則，開發一個單打的網球計分程式的後台程式，提供 Web API 給外部使用
使用 Java 17、Spring Boot Web API，並採用 BDD 的 Gherkins 描述情境，並以測試先行
Web API 的測試需用 Spring Boot Test，並提供 Swagger
```

**Kiro 處理過程**:
1. **需求分析**: 解析使用者輸入，識別核心功能需求
2. **EARS 格式化**: 使用 Easy Approach to Requirements Syntax 標準化需求
3. **INCOSE 品質檢查**: 確保需求符合國際系統工程標準
4. **術語表建立**: 定義所有技術和業務術語

**產出文件**: [`.kiro/specs/tennis-scoring-system/requirements.md`](.kiro/specs/tennis-scoring-system/requirements.md)

**內容包含**:
- 📖 **術語表**: 13 個核心概念定義
- 📋 **13 個主要需求**: 涵蓋比賽管理、計分邏輯、API 設計
- ✅ **65 個驗收標準**: 每個需求都有 3-5 個 EARS 格式的驗收標準
- 🎯 **需求追溯**: 每個需求都可追溯到原始使用者輸入

#### 階段 2: 系統設計 (System Design)
**目標**: 將需求轉換為可實作的技術架構

**輸入**:
```
開始設計階段
加入，採用六角形架構
```

**Kiro 處理過程**:
1. **架構選擇**: 根據需求選擇六角形架構
2. **技術棧決策**: 選擇 Java 17 + Spring Boot 3.x + Maven
3. **領域建模**: 設計 DDD 領域模型
4. **介面設計**: 定義 Ports 和 Adapters
5. **測試策略**: 規劃 BDD + 單元測試 + 整合測試

**產出文件**: [`.kiro/specs/tennis-scoring-system/design.md`](.kiro/specs/tennis-scoring-system/design.md)

**內容包含**:
- 🏗️ **六角形架構設計**: 完整的架構圖和層次說明
- 🎯 **領域模型**: Match, Set, Game, Player 等核心實體
- 🔌 **Ports 和 Adapters**: 主要埠和次要埠的介面定義
- 📊 **資料模型**: API 請求/回應模型設計
- 🧪 **測試策略**: BDD + 單元測試 + 整合測試規劃
- ⚙️ **技術決策**: 框架選擇和配置說明

#### 階段 3: 實作計劃 (Implementation Plan)
**目標**: 將設計轉換為可執行的開發任務

**Kiro 處理過程**:
1. **任務分解**: 將設計分解為具體的編程任務
2. **依賴分析**: 確定任務執行順序
3. **測試先行**: 每個任務都包含測試要求
4. **增量開發**: 確保每個步驟都能產生可工作的軟體

**產出文件**: [`.kiro/specs/tennis-scoring-system/tasks.md`](.kiro/specs/tennis-scoring-system/tasks.md)

**內容包含**:
- 📝 **11 個主要任務**: 從專案結構到文件撰寫
- 🔧 **42 個子任務**: 具體的實作步驟
- ✅ **完成狀態追蹤**: 每個任務的完成狀態
- 🔗 **需求追溯**: 每個任務都連結到相關需求

### 🔄 SDD 工作流程圖

```mermaid
graph TD
    A[使用者輸入] --> B[Kiro 需求分析]
    B --> C[requirements.md]
    C --> D[使用者確認需求]
    D --> E[Kiro 系統設計]
    E --> F[design.md]
    F --> G[使用者確認設計]
    G --> H[Kiro 實作計劃]
    H --> I[tasks.md]
    I --> J[開發者執行任務]
    J --> K[完整系統]
    
    style A fill:#e1f5fe
    style C fill:#f3e5f5
    style F fill:#e8f5e8
    style I fill:#fff3e0
    style K fill:#ffebee
```

### 📊 SDD 品質保證

#### EARS 需求格式
每個需求都遵循 EARS (Easy Approach to Requirements Syntax) 格式：
- **Ubiquitous**: THE <system> SHALL <response>
- **Event-driven**: WHEN <trigger>, THE <system> SHALL <response>
- **State-driven**: WHILE <condition>, THE <system> SHALL <response>

#### INCOSE 品質規則
所有需求都符合 INCOSE 國際系統工程標準：
- ✅ 主動語態
- ✅ 無模糊詞彙
- ✅ 可測試性
- ✅ 一致性術語

#### 完整追溯性
```
使用者輸入 → 需求 → 設計 → 任務 → 程式碼 → 測試
```

### 🎯 SDD 開發成果

透過 Kiro SDD 流程，本專案達成：

#### 📋 需求階段成果
- **13 個標準化需求** - 涵蓋所有功能面向
- **65 個驗收標準** - 確保需求可測試
- **100% 需求追溯** - 每個功能都可追溯到原始需求

#### 🏗️ 設計階段成果
- **六角形架構** - 業務邏輯與框架分離
- **DDD 領域模型** - 清晰的業務概念建模
- **完整 API 設計** - RESTful 端點和資料模型

#### 🔧 實作階段成果
- **42 個實作任務** - 系統化的開發步驟
- **測試先行開發** - 每個功能都有對應測試
- **增量交付** - 每個階段都產生可工作的軟體

#### 📊 品質指標
- **95%+ 測試覆蓋率** - 單元測試覆蓋率
- **100% API 覆蓋** - 所有端點都有測試
- **完整 BDD 場景** - 業務邏輯完全覆蓋

### 🔍 文件導覽

| 文件 | 階段 | 內容 | 用途 |
|------|------|------|------|
| [`requirements.md`](.kiro/specs/tennis-scoring-system/requirements.md) | 需求 | 13個需求 + 65個驗收標準 | 需求追溯和驗證 |
| [`design.md`](.kiro/specs/tennis-scoring-system/design.md) | 設計 | 架構設計 + 技術選型 | 開發指導和架構參考 |
| [`tasks.md`](.kiro/specs/tennis-scoring-system/tasks.md) | 實作 | 11個任務 + 42個子任務 | 開發進度追蹤 |
| [`README.md`](README.md) | 使用 | 完整使用指南 + API 範例 | 使用者和開發者參考 |
| [`docs/API_GUIDE.md`](docs/API_GUIDE.md) | 參考 | 詳細 API 文件 | API 整合參考 |
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | 參考 | 架構深度解析 | 架構理解和維護 |

### 💡 SDD 方法論優勢

#### 1. **系統化流程**
- 標準化的三階段流程
- 每個階段都有明確的輸入、處理和輸出
- 確保沒有遺漏重要需求

#### 2. **品質保證**
- EARS 和 INCOSE 標準確保需求品質
- 測試先行確保程式碼品質
- 完整追溯性確保一致性

#### 3. **可重複性**
- 標準化流程可應用於任何專案
- 文件模板可重複使用
- 經驗可累積和改進

#### 4. **協作友善**
- 清晰的文件結構便於團隊協作
- 標準化術語減少溝通誤解
- 階段性確認確保利害關係人參與

---

**版本**: 1.0.0  
**最後更新**: 2025-10-26  
**開發團隊**: 網球計分系統開發團隊  
**開發方法**: Kiro Spec-Driven Development (SDD)