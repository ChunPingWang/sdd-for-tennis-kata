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

---

**版本**: 1.0.0  
**最後更新**: 2025-10-26  
**開發團隊**: 網球計分系統開發團隊