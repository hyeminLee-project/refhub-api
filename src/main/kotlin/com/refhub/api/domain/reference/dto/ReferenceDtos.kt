package com.refhub.api.domain.reference.dto

import com.refhub.api.domain.reference.Reference
import com.refhub.api.domain.reference.ReferenceSource
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class ReferenceCreateRequest(
    @field:NotBlank
    @field:Size(max = 500)
    val title: String,

    val summary: String? = null,

    @field:NotBlank
    val url: String,

    val source: ReferenceSource,

    val author: String? = null,

    val tags: List<String> = emptyList(),
)

data class ReferenceSearchRequest(
    val keyword: String? = null,
    val source: ReferenceSource? = null,
    val tags: List<String>? = null,
    val page: Int = 0,
    val size: Int = 20,
)

data class ReferenceResponse(
    val id: Long,
    val title: String,
    val summary: String?,
    val url: String,
    val source: ReferenceSource,
    val author: String?,
    val tags: List<String>,
    val publishedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(ref: Reference) = ReferenceResponse(
            id = ref.id,
            title = ref.title,
            summary = ref.summary,
            url = ref.url,
            source = ref.source,
            author = ref.author,
            tags = ref.tags.map { it.name },
            publishedAt = ref.publishedAt,
            createdAt = ref.createdAt,
        )
    }
}
