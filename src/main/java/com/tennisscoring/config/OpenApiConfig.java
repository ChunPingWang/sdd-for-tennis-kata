package com.tennisscoring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * API 文件的 OpenAPI/Swagger 配置
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    
    /**
     * Configure OpenAPI documentation.
     * 配置 OpenAPI 文件
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + contextPath)
                                .description("本地開發伺服器"),
                        new Server()
                                .url("https://api.tennisscoring.com" + contextPath)
                                .description("生產環境伺服器")
                ));
    }
    
    /**
     * Create API information.
     * 建立 API 資訊
     * 
     * @return API information
     */
    private Info apiInfo() {
        return new Info()
                .title("網球計分系統 API")
                .description("""
                        網球計分系統提供完整的單打網球比賽管理功能，包括：
                        
                        ## 主要功能
                        - 🎾 比賽創建與管理
                        - 📊 即時計分記錄
                        - 🏆 比賽結果追蹤
                        - 📈 比賽統計資訊
                        
                        ## 計分規則
                        - 支援標準網球計分規則（0, 15, 30, 40, 平分, 優勢）
                        - 自動處理搶七局（6-6 時觸發）
                        - 三盤兩勝制比賽
                        
                        ## 技術特色
                        - 基於六角形架構設計
                        - RESTful API 設計
                        - 完整的錯誤處理
                        - 即時比賽狀態更新
                        
                        ## 使用方式
                        1. 使用 POST /api/matches 創建新比賽
                        2. 使用 POST /api/matches/{matchId}/score 記錄得分
                        3. 使用 GET /api/matches/{matchId} 查詢比賽狀態
                        4. 使用 GET /api/matches 查看所有比賽
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("網球計分系統開發團隊")
                        .email("support@tennisscoring.com")
                        .url("https://github.com/tennisscoring/tennis-scoring-system"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }
}