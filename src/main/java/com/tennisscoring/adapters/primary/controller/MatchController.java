package com.tennisscoring.adapters.primary.controller;

import com.tennisscoring.adapters.primary.dto.request.CreateMatchRequest;
import com.tennisscoring.adapters.primary.dto.request.ScorePointRequest;
import com.tennisscoring.adapters.primary.dto.response.MatchResponse;
import com.tennisscoring.adapters.primary.mapper.MatchMapper;
import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.service.MatchService;
import com.tennisscoring.domain.service.StatisticsService;
import com.tennisscoring.domain.service.MatchStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API controller for tennis match management.
 * 網球比賽管理的 REST API 控制器
 */
@RestController
@RequestMapping("/api/matches")
@Tag(name = "Match Management", description = "網球比賽管理 API")
public class MatchController {
    
    private final MatchService matchService;
    private final StatisticsService statisticsService;
    private final MatchMapper matchMapper;
    
    @Autowired
    public MatchController(MatchService matchService, 
                          StatisticsService statisticsService,
                          MatchMapper matchMapper) {
        this.matchService = matchService;
        this.statisticsService = statisticsService;
        this.matchMapper = matchMapper;
    }
    
    @Operation(
        summary = "創建新比賽",
        description = "創建一場新的網球比賽，需要提供兩位球員的名稱"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "比賽創建成功",
            content = @Content(schema = @Schema(implementation = MatchResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "請求參數無效"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "球員名稱重複"
        )
    })
    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(
            @Valid @RequestBody CreateMatchRequest request) {
        
        Match match = matchService.createMatch(
            request.getPlayer1Name(), 
            request.getPlayer2Name()
        );
        
        MatchResponse response = matchMapper.toResponse(match);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(
        summary = "取得比賽資訊",
        description = "根據比賽ID取得比賽的詳細資訊"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得比賽資訊",
            content = @Content(schema = @Schema(implementation = MatchResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "比賽不存在"
        )
    })
    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponse> getMatch(
            @Parameter(description = "比賽ID", required = true)
            @PathVariable String matchId) {
        
        Match match = matchService.getMatch(matchId);
        MatchResponse response = matchMapper.toResponse(match);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "取得所有比賽",
        description = "取得系統中所有比賽的列表"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得比賽列表",
            content = @Content(schema = @Schema(implementation = MatchResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        List<Match> matches = matchService.getAllMatches();
        List<MatchResponse> responses = matches.stream()
                .map(matchMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @Operation(
        summary = "記錄得分",
        description = "為指定球員記錄一分，系統會自動更新比賽狀態"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "得分記錄成功",
            content = @Content(schema = @Schema(implementation = MatchResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "請求參數無效"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "比賽或球員不存在"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "比賽已結束，無法繼續得分"
        )
    })
    @PostMapping("/{matchId}/score")
    public ResponseEntity<MatchResponse> scorePoint(
            @Parameter(description = "比賽ID", required = true)
            @PathVariable String matchId,
            @Valid @RequestBody ScorePointRequest request) {
        
        Match match = matchService.scorePoint(matchId, request.getPlayerId());
        MatchResponse response = matchMapper.toResponse(match);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "取消比賽",
        description = "取消正在進行中的比賽"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "比賽取消成功",
            content = @Content(schema = @Schema(implementation = MatchResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "比賽不存在"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "比賽已結束，無法取消"
        )
    })
    @PutMapping("/{matchId}/cancel")
    public ResponseEntity<MatchResponse> cancelMatch(
            @Parameter(description = "比賽ID", required = true)
            @PathVariable String matchId) {
        
        Match match = matchService.cancelMatch(matchId);
        
        MatchResponse response = matchMapper.toResponse(match);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "刪除比賽",
        description = "從系統中刪除比賽記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "比賽刪除成功"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "比賽不存在"
        )
    })
    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(
            @Parameter(description = "比賽ID", required = true)
            @PathVariable String matchId) {
        
        matchService.deleteMatch(matchId);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "取得比賽狀態統計",
        description = "取得系統中各種狀態比賽的統計資訊"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得統計資訊"
        )
    })
    @GetMapping("/statistics")
    public ResponseEntity<MatchStatisticsResponse> getMatchStatistics() {
        MatchStatisticsService.SystemStatistics systemStats = statisticsService.getSystemStatistics();
        
        MatchStatisticsResponse statistics = new MatchStatisticsResponse(
                systemStats.getTotalMatches(),
                systemStats.getInProgressMatches(),
                systemStats.getCompletedMatches(),
                systemStats.getCancelledMatches()
        );
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Response model for match statistics.
     * 比賽統計資訊的回應模型
     */
    public static class MatchStatisticsResponse {
        private int totalMatches;
        private int inProgressMatches;
        private int completedMatches;
        private int cancelledMatches;
        
        public MatchStatisticsResponse() {}
        
        public MatchStatisticsResponse(int totalMatches, int inProgressMatches, 
                                     int completedMatches, int cancelledMatches) {
            this.totalMatches = totalMatches;
            this.inProgressMatches = inProgressMatches;
            this.completedMatches = completedMatches;
            this.cancelledMatches = cancelledMatches;
        }
        
        public int getTotalMatches() { return totalMatches; }
        public void setTotalMatches(int totalMatches) { this.totalMatches = totalMatches; }
        
        public int getInProgressMatches() { return inProgressMatches; }
        public void setInProgressMatches(int inProgressMatches) { this.inProgressMatches = inProgressMatches; }
        
        public int getCompletedMatches() { return completedMatches; }
        public void setCompletedMatches(int completedMatches) { this.completedMatches = completedMatches; }
        
        public int getCancelledMatches() { return cancelledMatches; }
        public void setCancelledMatches(int cancelledMatches) { this.cancelledMatches = cancelledMatches; }
    }
}