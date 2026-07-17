package com.refhub.api.domain.reference

import com.refhub.api.common.response.ApiResponse
import com.refhub.api.domain.reference.dto.ReferenceCreateRequest
import com.refhub.api.domain.reference.dto.ReferenceResponse
import com.refhub.api.domain.reference.dto.ReferenceSearchRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "References", description = "레퍼런스 검색/관리 API")
@RestController
@RequestMapping("/api/v1/references")
class ReferenceController(
    private val referenceService: ReferenceService,
) {
    @Operation(summary = "레퍼런스 검색", description = "키워드, 소스, 태그로 레퍼런스를 검색합니다.")
    @GetMapping
    fun search(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) source: ReferenceSource?,
        @RequestParam(required = false) tags: List<String>?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ApiResponse<Page<ReferenceResponse>> {
        val request = ReferenceSearchRequest(keyword, source, tags, page, size)
        return ApiResponse.ok(referenceService.search(request))
    }

    @Operation(summary = "레퍼런스 상세 조회")
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ApiResponse<ReferenceResponse> =
        ApiResponse.ok(referenceService.findById(id))

    @Operation(summary = "레퍼런스 수동 등록")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: ReferenceCreateRequest): ApiResponse<ReferenceResponse> =
        ApiResponse.ok(referenceService.create(request))
}
