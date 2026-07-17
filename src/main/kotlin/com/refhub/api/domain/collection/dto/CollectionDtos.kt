package com.refhub.api.domain.collection.dto

import com.refhub.api.domain.collection.Collection
import com.refhub.api.domain.reference.dto.ReferenceResponse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CollectionCreateRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:Size(max = 500)
    val description: String? = null,
)

data class CollectionResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val referenceCount: Int,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(collection: Collection) = CollectionResponse(
            id = collection.id,
            name = collection.name,
            description = collection.description,
            referenceCount = collection.references.size,
            createdAt = collection.createdAt,
        )
    }
}

data class CollectionDetailResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val references: List<ReferenceResponse>,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(collection: Collection) = CollectionDetailResponse(
            id = collection.id,
            name = collection.name,
            description = collection.description,
            references = collection.references.map { ReferenceResponse.from(it) },
            createdAt = collection.createdAt,
        )
    }
}
