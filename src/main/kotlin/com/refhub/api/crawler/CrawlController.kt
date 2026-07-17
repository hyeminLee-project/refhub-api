package com.refhub.api.crawler

import com.refhub.api.common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Crawl", description = "크롤링 관리 API (Admin)")
@RestController
@RequestMapping("/api/v1/crawl")
class CrawlController(
    private val crawlScheduler: CrawlScheduler,
) {
    @Operation(summary = "수동 크롤 트리거", description = "특정 소스 또는 전체 크롤링을 수동으로 실행합니다.")
    @PostMapping("/trigger")
    fun trigger(
        @RequestParam(required = false) source: String?,
        @RequestParam(required = false) query: String?,
    ): ApiResponse<String> {
        crawlScheduler.triggerManual(source, query)
        return ApiResponse.ok("크롤링이 시작되었습니다.")
    }
}
