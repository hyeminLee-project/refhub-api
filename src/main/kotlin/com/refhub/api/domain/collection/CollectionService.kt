package com.refhub.api.domain.collection

import com.refhub.api.common.exception.BusinessException
import com.refhub.api.common.exception.DuplicateException
import com.refhub.api.common.exception.NotFoundException
import com.refhub.api.domain.collection.dto.CollectionCreateRequest
import com.refhub.api.domain.collection.dto.CollectionDetailResponse
import com.refhub.api.domain.collection.dto.CollectionResponse
import com.refhub.api.domain.reference.ReferenceRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CollectionService(
    private val collectionRepository: CollectionRepository,
    private val referenceRepository: ReferenceRepository,
    private val userService: com.refhub.api.domain.user.UserService,
) {
    fun findByUser(userId: Long): List<CollectionResponse> =
        collectionRepository.findByUserId(userId).map { CollectionResponse.from(it) }

    fun findById(id: Long, userId: Long): CollectionDetailResponse {
        val collection = collectionRepository.findById(id)
            .orElseThrow { NotFoundException("컬렉션", id) }

        if (collection.user.id != userId) {
            throw BusinessException("FORBIDDEN", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN)
        }

        return CollectionDetailResponse.from(collection)
    }

    @Transactional
    fun create(userId: Long, request: CollectionCreateRequest): CollectionResponse {
        if (collectionRepository.existsByUserIdAndName(userId, request.name)) {
            throw DuplicateException("컬렉션 이름")
        }

        val user = userService.findById(userId)
        val collection = collectionRepository.save(
            Collection(
                user = user,
                name = request.name,
                description = request.description,
            )
        )
        return CollectionResponse.from(collection)
    }

    @Transactional
    fun addReference(collectionId: Long, referenceId: Long, userId: Long) {
        val collection = collectionRepository.findById(collectionId)
            .orElseThrow { NotFoundException("컬렉션", collectionId) }

        if (collection.user.id != userId) {
            throw BusinessException("FORBIDDEN", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN)
        }

        val reference = referenceRepository.findById(referenceId)
            .orElseThrow { NotFoundException("레퍼런스", referenceId) }

        collection.references.add(reference)
    }
}
