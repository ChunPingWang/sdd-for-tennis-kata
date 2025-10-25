# 網球計分系統需求文件

## 簡介

開發一個單打網球計分系統的後台程式，提供 Web API 給外部使用。系統需要根據標準網球計分規則來追蹤和管理比賽進度，包括分數、局數、盤數的計算和狀態管理。

## 術語表

- **Tennis_Scoring_System**: 網球計分系統，負責管理整個網球比賽的計分邏輯
- **Match**: 比賽，由多個盤組成的完整網球對戰
- **Set**: 盤，由多個局組成，通常先贏6局且領先2局者獲勝
- **Game**: 局，由多個分組成，使用15-30-40-勝利的計分方式
- **Point**: 分，網球比賽中的最小計分單位
- **Player**: 球員，參與比賽的選手
- **Deuce**: 平分，當雙方都達到40分時的狀態
- **Advantage**: 優勢，在平分後領先一分的狀態
- **Tiebreak**: 搶七局，當盤數達到6-6時進行的特殊局
- **Web_API**: 網頁應用程式介面，提供HTTP端點供外部系統調用
- **Spring_Boot_Application**: 基於Spring Boot框架的Java應用程式
- **BDD_Scenarios**: 使用Gherkin語法描述的行為驅動開發情境
- **Swagger_Documentation**: API文件和測試介面
- **Spring_Boot_Test**: Spring Boot測試框架，用於API端點測試

## 需求

### 需求 1

**使用者故事:** 作為一個網球比賽管理者，我想要創建新的比賽，以便開始追蹤兩名球員之間的對戰。

#### 驗收標準

1. WHEN 管理者提供兩名球員資訊，THE Tennis_Scoring_System SHALL 創建新的比賽記錄
2. THE Tennis_Scoring_System SHALL 初始化比賽狀態為0-0分、0-0局、0-0盤
3. THE Tennis_Scoring_System SHALL 分配唯一的比賽識別碼
4. THE Tennis_Scoring_System SHALL 記錄比賽創建時間
5. THE Tennis_Scoring_System SHALL 設定比賽狀態為進行中

### 需求 2

**使用者故事:** 作為一個網球比賽管理者，我想要記錄球員得分，以便正確更新比賽狀態。

#### 驗收標準

1. WHEN 球員贏得一分，THE Tennis_Scoring_System SHALL 根據當前分數更新該球員的分數
2. WHILE 比賽狀態為進行中，THE Tennis_Scoring_System SHALL 接受得分輸入
3. THE Tennis_Scoring_System SHALL 將分數從0更新為15，從15更新為30，從30更新為40
4. WHEN 球員從40分再得一分且對手分數少於40分，THE Tennis_Scoring_System SHALL 宣告該球員贏得該局
5. WHEN 雙方都達到40分，THE Tennis_Scoring_System SHALL 進入平分狀態

### 需求 3

**使用者故事:** 作為一個網球比賽管理者，我想要處理平分和優勢狀態，以便正確管理局的勝負。

#### 驗收標準

1. WHILE 比賽處於平分狀態，THE Tennis_Scoring_System SHALL 在球員得分時給予優勢狀態
2. WHEN 擁有優勢的球員再得一分，THE Tennis_Scoring_System SHALL 宣告該球員贏得該局
3. WHEN 處於劣勢的球員得分，THE Tennis_Scoring_System SHALL 將狀態重置為平分
4. THE Tennis_Scoring_System SHALL 在平分和優勢狀態之間正確轉換

### 需求 4

**使用者故事:** 作為一個網球比賽管理者，我想要追蹤局數和盤數，以便確定比賽的整體進度。

#### 驗收標準

1. WHEN 球員贏得一局，THE Tennis_Scoring_System SHALL 增加該球員的局數計數
2. WHEN 球員達到6局且領先對手至少2局，THE Tennis_Scoring_System SHALL 宣告該球員贏得該盤
3. WHEN 盤數達到6-6，THE Tennis_Scoring_System SHALL 啟動搶七局模式
4. WHEN 球員贏得一盤，THE Tennis_Scoring_System SHALL 重置局數計數並開始新盤
5. THE Tennis_Scoring_System SHALL 追蹤每盤的局數歷史記錄

### 需求 5

**使用者故事:** 作為一個網球比賽管理者，我想要處理搶七局，以便在6-6平手時決定盤的勝負。

#### 驗收標準

1. WHILE 處於搶七局模式，THE Tennis_Scoring_System SHALL 使用1-2-3-4...的計分方式
2. WHEN 球員在搶七局中達到7分且領先對手至少2分，THE Tennis_Scoring_System SHALL 宣告該球員贏得該盤
3. WHILE 搶七局進行中，THE Tennis_Scoring_System SHALL 在每奇數分後交換發球權
4. THE Tennis_Scoring_System SHALL 在搶七局結束後重置為正常局的計分模式

### 需求 6

**使用者故事:** 作為一個網球比賽管理者，我想要確定比賽的最終勝負，以便宣告獲勝者。

#### 驗收標準

1. WHEN 球員贏得2盤，THE Tennis_Scoring_System SHALL 宣告該球員為比賽獲勝者
2. WHEN 比賽結束，THE Tennis_Scoring_System SHALL 更新比賽狀態為已完成
3. THE Tennis_Scoring_System SHALL 記錄比賽結束時間
4. THE Tennis_Scoring_System SHALL 保存完整的比賽結果記錄

### 需求 7

**使用者故事:** 作為一個外部系統開發者，我想要透過Web API查詢比賽狀態，以便在我的應用程式中顯示即時比分。

#### 驗收標準

1. THE Web_API SHALL 提供GET端點來查詢特定比賽的當前狀態
2. THE Web_API SHALL 返回包含分數、局數、盤數的完整比賽資訊
3. THE Web_API SHALL 提供比賽歷史記錄的查詢功能
4. THE Web_API SHALL 使用JSON格式返回資料
5. THE Web_API SHALL 在比賽不存在時返回適當的錯誤訊息

### 需求 8

**使用者故事:** 作為一個外部系統開發者，我想要透過Web API操作比賽，以便整合到我的比賽管理系統中。

#### 驗收標準

1. THE Web_API SHALL 提供POST端點來創建新比賽
2. THE Web_API SHALL 提供PUT端點來記錄球員得分
3. THE Web_API SHALL 提供DELETE端點來取消比賽
4. THE Web_API SHALL 驗證輸入資料的有效性
5. THE Web_API SHALL 在操作成功時返回更新後的比賽狀態

### 需求 9

**使用者故事:** 作為一個系統管理員，我想要系統能夠處理錯誤情況，以便確保系統的穩定性和可靠性。

#### 驗收標準

1. WHEN 接收到無效的比賽ID，THE Tennis_Scoring_System SHALL 返回404錯誤
2. WHEN 嘗試對已結束的比賽記分，THE Tennis_Scoring_System SHALL 返回400錯誤
3. THE Tennis_Scoring_System SHALL 記錄所有錯誤和異常情況
4. THE Tennis_Scoring_System SHALL 在系統錯誤時返回500錯誤並保持資料完整性
5. THE Tennis_Scoring_System SHALL 驗證所有輸入參數的格式和範圍

### 需求 10

**使用者故事:** 作為一個開發團隊成員，我想要使用Java 17和Spring Boot框架開發系統，以便利用現代Java特性和成熟的企業級框架。

#### 驗收標準

1. THE Spring_Boot_Application SHALL 使用Java 17作為運行環境
2. THE Spring_Boot_Application SHALL 使用Spring Boot 3.x版本
3. THE Spring_Boot_Application SHALL 提供RESTful Web API端點
4. THE Spring_Boot_Application SHALL 使用Spring Boot的依賴注入和自動配置功能
5. THE Spring_Boot_Application SHALL 包含適當的Spring Boot starter依賴

### 需求 11

**使用者故事:** 作為一個開發團隊成員，我想要使用BDD方法和Gherkin語法描述測試情境，以便確保系統行為符合業務需求。

#### 驗收標準

1. THE BDD_Scenarios SHALL 使用Gherkin語法描述所有主要功能情境
2. THE BDD_Scenarios SHALL 包含Given-When-Then格式的測試步驟
3. THE BDD_Scenarios SHALL 涵蓋所有計分規則和API操作
4. THE BDD_Scenarios SHALL 作為自動化測試的基礎
5. THE BDD_Scenarios SHALL 與需求文件保持一致

### 需求 12

**使用者故事:** 作為一個開發團隊成員，我想要採用測試先行的開發方式，以便確保代碼品質和功能正確性。

#### 驗收標準

1. THE Spring_Boot_Test SHALL 在實現功能之前先編寫測試
2. THE Spring_Boot_Test SHALL 使用@SpringBootTest註解進行整合測試
3. THE Spring_Boot_Test SHALL 測試所有Web API端點
4. THE Spring_Boot_Test SHALL 驗證HTTP狀態碼和回應內容
5. THE Spring_Boot_Test SHALL 包含正面和負面測試案例

### 需求 13

**使用者故事:** 作為一個API使用者，我想要有完整的API文件和測試介面，以便快速了解和測試API功能。

#### 驗收標準

1. THE Swagger_Documentation SHALL 自動生成API文件
2. THE Swagger_Documentation SHALL 提供互動式API測試介面
3. THE Swagger_Documentation SHALL 描述所有端點的參數和回應格式
4. THE Swagger_Documentation SHALL 包含API使用範例
5. THE Swagger_Documentation SHALL 可透過/swagger-ui.html路徑存取