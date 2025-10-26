# 實作計劃

- [x] 1. 建立專案結構和核心介面
  - 建立 Maven 專案結構和依賴配置
  - 定義六角形架構的目錄結構
  - 建立 Primary Ports 和 Secondary Ports 介面
  - _需求: 10.1, 10.2, 10.3_

- [x] 1.1 建立 Maven 專案配置
  - 建立 pom.xml 包含 Spring Boot 3.x, Java 17, Swagger, 測試依賴
  - 配置 Maven 編譯器和測試外掛
  - _需求: 10.1, 10.2, 12.2_

- [x] 1.2 建立六角形架構目錄結構
  - 建立 domain, ports, adapters, config 套件結構
  - 建立測試目錄結構包含 features 資料夾
  - _需求: 10.4_

- [x] 1.3 定義 Primary Ports 介面
  - 建立 MatchManagementPort 介面定義比賽管理操作
  - 建立 QueryPort 介面定義查詢操作
  - _需求: 1.1, 2.1, 7.1_

- [x] 1.4 定義 Secondary Ports 介面
  - 建立 MatchRepositoryPort 介面定義資料持久化操作
  - 建立 EventPublisherPort 介面定義事件發布操作
  - _需求: 1.3, 6.3_

- [x] 2. 實作領域模型和值物件
  - 建立核心領域實體 (Match, Set, Game, Player)
  - 實作值物件 (MatchId, PlayerId, PlayerName)
  - 定義列舉類型和領域事件
  - _需求: 1.2, 2.3, 3.4, 4.1_

- [x] 2.1 建立值物件
  - 實作 MatchId, PlayerId, PlayerName 使用 Java Record
  - 加入驗證邏輯和工廠方法
  - _需求: 1.2, 9.5_

- [x] 2.2 實作 Player 實體
  - 建立 Player 類別包含 ID, 名稱和統計資料
  - 實作球員相關的業務方法
  - _需求: 1.1, 1.2_

- [x] 2.3 實作 Game 實體
  - 建立 Game 類別處理局內計分邏輯
  - 實作一般計分和搶七局計分方法
  - 處理平分和優勢狀態轉換
  - _需求: 2.1, 2.3, 3.1, 3.2, 3.3, 5.1, 5.2_

- [x] 2.4 實作 Set 實體
  - 建立 Set 類別管理局數和盤的勝負
  - 實作局數統計和搶七局觸發邏輯
  - _需求: 4.1, 4.2, 4.3, 5.1_

- [x] 2.5 實作 Match 聚合根
  - 建立 Match 類別作為聚合根
  - 實作比賽創建、狀態管理和勝負判定
  - 加入工廠方法和業務邏輯方法
  - _需求: 1.1, 1.2, 1.3, 6.1, 6.2_

- [x] 2.6 定義列舉類型和領域事件
  - 建立 GameScore, MatchStatus, GameStatus 列舉
  - 實作 DomainEvent 基底類別和具體事件類別
  - _需求: 1.4, 6.3_

- [x] 3. 實作領域服務
  - 建立 MatchDomainService 實作業務邏輯
  - 建立 ScoringDomainService 處理複雜計分規則
  - 實作輸入驗證和錯誤處理
  - _需求: 2.1, 2.2, 3.1, 4.4, 5.3, 9.1, 9.2_

- [x] 3.1 實作 ScoringDomainService
  - 實作網球計分邏輯包含平分和優勢處理
  - 實作搶七局計分邏輯
  - 加入計分狀態驗證
  - _需求: 2.1, 2.3, 3.1, 3.2, 3.3, 5.1, 5.2, 5.3_

- [x] 3.2 實作 MatchDomainService
  - 實作 MatchManagementPort 和 QueryPort 介面
  - 整合 ScoringDomainService 處理得分邏輯
  - 實作比賽狀態管理和事件發布
  - _需求: 1.1, 2.2, 4.4, 6.1, 6.2, 6.3_

- [x] 3.3 實作輸入驗證和錯誤處理
  - 建立自定義異常類別
  - 實作業務規則驗證邏輯
  - _需求: 9.1, 9.2, 9.5_

- [x] 4. 實作 Secondary Adapters
  - 建立 InMemoryMatchRepository 實作資料儲存
  - 建立 NoOpEventPublisher 實作事件發布
  - 確保執行緒安全和資料一致性
  - _需求: 1.3, 6.3, 9.4_

- [x] 4.1 實作 InMemoryMatchRepository
  - 使用 ConcurrentHashMap 實作執行緒安全的記憶體儲存
  - 實作所有 MatchRepositoryPort 方法
  - _需求: 1.3, 9.4_

- [x] 4.2 實作 NoOpEventPublisher
  - 建立簡單的日誌記錄事件發布器
  - 實作所有 EventPublisherPort 方法
  - _需求: 6.3_

- [x] 5. 建立 BDD 測試情境
  - 撰寫 Gherkin 功能檔案描述所有主要業務情境
  - 實作 Cucumber 步驟定義
  - 建立測試資料和輔助方法
  - _需求: 11.1, 11.2, 11.3, 11.4_

- [x] 5.1 撰寫比賽管理 BDD 情境
  - 建立 match-creation.feature 描述比賽創建情境
  - 建立 match-deletion.feature 描述比賽刪除情境
  - _需求: 1.1, 1.2, 1.3, 11.1, 11.2_

- [x] 5.2 撰寫計分邏輯 BDD 情境
  - 建立 scoring.feature 描述基本計分情境
  - 建立 deuce-advantage.feature 描述平分和優勢情境
  - 建立 tiebreak.feature 描述搶七局情境
  - _需求: 2.1, 2.3, 3.1, 3.2, 5.1, 5.2, 11.1, 11.2_

- [x] 5.3 撰寫比賽進度 BDD 情境
  - 建立 set-game-progression.feature 描述局數和盤數進度
  - 建立 match-completion.feature 描述比賽結束情境
  - _需求: 4.1, 4.2, 4.3, 6.1, 6.2, 11.1, 11.2_

- [x] 5.4 實作 Cucumber 步驟定義
  - 建立步驟定義類別實作 Given-When-Then 步驟
  - 建立測試資料建構器和輔助方法
  - _需求: 11.3, 11.4_

- [x] 6. 實作 Primary Adapters
  - 建立 MatchController REST API 控制器
  - 實作請求/回應模型和資料轉換
  - 建立全域異常處理器
  - _需求: 7.1, 7.2, 7.4, 8.1, 8.2, 8.4, 9.1, 9.2_

- [x] 6.1 建立 API 請求/回應模型
  - 實作 CreateMatchRequest, ScorePointRequest 等請求模型
  - 實作 MatchResponse, ErrorResponse 等回應模型
  - 加入 Bean Validation 註解
  - _需求: 7.4, 8.4, 9.5_

- [x] 6.2 實作 MatchController
  - 建立所有 REST API 端點
  - 實作請求處理和回應轉換邏輯
  - 加入 Swagger 註解
  - _需求: 7.1, 7.2, 8.1, 8.2, 13.3_

- [x] 6.3 建立全域異常處理器
  - 實作 @ControllerAdvice 處理所有異常
  - 建立統一的錯誤回應格式
  - _需求: 9.1, 9.2, 9.3, 9.4_

- [x] 6.4 實作資料轉換器
  - 建立 Mapper 類別轉換領域物件到 DTO
  - 實作雙向轉換邏輯
  - _需求: 7.2, 7.4_

- [x] 7. 建立 Spring Boot 配置
  - 建立主應用程式類別和配置
  - 配置 Swagger/OpenAPI 文件
  - 建立應用程式屬性檔案
  - _需求: 10.2, 10.3, 13.1, 13.2, 13.5_

- [x] 7.1 建立 Spring Boot 主應用程式
  - 建立 @SpringBootApplication 主類別
  - 配置 Bean 定義和依賴注入
  - _需求: 10.2, 10.4_

- [x] 7.2 配置 Swagger/OpenAPI
  - 建立 OpenApiConfig 配置類別
  - 設定 API 文件資訊和描述
  - _需求: 13.1, 13.2, 13.3, 13.4, 13.5_

- [x] 7.3 建立應用程式配置檔案
  - 建立 application.yml 配置檔案
  - 設定伺服器埠、日誌等級和 Swagger 路徑
  - _需求: 10.3, 13.5_

- [x] 8. 撰寫單元測試
  - 為所有領域服務撰寫單元測試
  - 測試業務邏輯和錯誤處理
  - 確保高測試覆蓋率
  - _需求: 12.1, 12.5_

- [x] 8.1 撰寫領域模型單元測試
  - 測試 Match, Set, Game, Player 的業務邏輯
  - 測試值物件的驗證邏輯
  - _需求: 12.1, 12.5_

- [x] 8.2 撰寫領域服務單元測試
  - 測試 MatchDomainService 和 ScoringDomainService
  - 使用 Mock 物件隔離外部依賴
  - _需求: 12.1, 12.5_

- [x] 8.3 撰寫 Repository 單元測試
  - 測試 InMemoryMatchRepository 的所有操作
  - 測試並發安全性
  - _需求: 12.1, 12.5_

- [x] 9. 撰寫整合測試
  - 建立 Spring Boot 整合測試
  - 測試完整的 API 端點功能
  - 驗證 HTTP 狀態碼和回應內容
  - _需求: 12.2, 12.3, 12.4, 12.5_

- [x] 9.1 建立 API 整合測試
  - 使用 @SpringBootTest 測試所有 REST 端點
  - 測試正面和負面情境
  - 驗證請求/回應格式
  - _需求: 12.2, 12.3, 12.4, 12.5_

- [x] 9.2 建立端到端測試情境
  - 測試完整的比賽流程從創建到結束
  - 驗證複雜的業務情境
  - _需求: 12.2, 12.4, 12.5_

- [x] 10. 執行所有測試並修正問題
  - 執行 BDD 測試、單元測試和整合測試
  - 修正任何失敗的測試
  - 確保所有功能正常運作
  - _需求: 11.4, 12.1, 12.2, 12.5_

- [x] 10.1 執行並修正 BDD 測試
  - 執行所有 Cucumber 功能測試
  - 修正步驟定義和業務邏輯問題
  - _需求: 11.4_

- [x] 10.2 執行並修正單元測試
  - 執行所有單元測試並確保通過
  - 修正業務邏輯錯誤
  - _需求: 12.1, 12.5_

- [x] 10.3 執行並修正整合測試
  - 執行所有整合測試並確保通過
  - 修正 API 端點和配置問題
  - _需求: 12.2, 12.4, 12.5_

- [x] 11. 建立專案文件和部署準備
  - 更新 README.md 包含執行指南
  - 建立 API 使用範例
  - 準備部署配置
  - _需求: 13.4_

- [x] 11.1 更新專案文件
  - 更新 README.md 包含建置和執行指南
  - 建立 API 使用範例和說明
  - _需求: 13.4_

- [x] 11.2 驗證 Swagger 文件
  - 確認 Swagger UI 可正常存取
  - 驗證所有 API 端點文件完整性
  - 測試 API 互動功能
  - _需求: 13.1, 13.2, 13.3, 13.4, 13.5_