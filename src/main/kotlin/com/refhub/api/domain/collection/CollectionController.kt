package com.refhub.api.domain.collection

import com.refhub.api.common.response.ApiResponse
import com.refhub.api.domain.collection.dto.CollectionCreateRequest
import com.refhub.api.domain.collection.dto.CollectionDetailResponse
import com.refhub.api.domain.collection.dto.CollectionResponse
import com.refhub.api.security.UserPrincipal
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Collections", description = "컬렉션(북마크) 관리 API")
@RestController
@RequestMapping("/api/v1/collections")
class CollectionController(
    private val collectionService: CollectionService,
) {
    @Operation(summary = "내 컬렉션 목록")
    @GetMapping
    fun findMyCollections(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ApiResponse<List<CollectionResponse>> =
        ApiResponse.ok(collectionService.findByUser(principal.userId))

    @Operation(summary = "컬렉션 상세 조회")
    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ApiResponse<CollectionDetailResponse> =
        ApiResponse.ok(collectionService.findById(id, principal.userId))

    @Operation(summary = "컬렉션 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CollectionCreateRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ApiResponse<CollectionResponse> =
        ApiResponse.ok(collectionService.create(principal.userId, request))

    @Operation(summary = "컬렉션에 레퍼런스 추가")
    @PostMapping("/{collectionId}/references/{referenceId}")
    fun addReference(
        @PathVariable collectionId: Long,
        @PathVariable referenceId: Long,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ApiResponse<String> {
        collectionService.addReference(collectionId, referenceId, principal.userId)
        return ApiResponse.ok("레퍼런스가 컬렉션에 추가되었습니다.")
    }
}
