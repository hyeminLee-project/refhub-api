package com.refhub.api.domain.reference

import com.refhub.api.common.exception.NotFoundException
import com.refhub.api.domain.reference.dto.ReferenceCreateRequest
import com.refhub.api.domain.reference.dto.ReferenceResponse
import com.refhub.api.domain.reference.dto.ReferenceSearchRequest
import com.refhub.api.domain.tag.Tag
import com.refhub.api.domain.tag.TagRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReferenceService(
    private val referenceRepository: ReferenceRepository,
    private val tagRepository: TagRepository,
) {
    @Cacheable(value = ["references"], key = "#request.hashCode()")
    fun search(request: ReferenceSearchRequest): Page<ReferenceResponse> {
        val pageable = PageRequest.of(
            request.page,
            request.size,
            Sort.by(Sort.Direction.DESC, "createdAt"),
        )

        val page = if (!request.tags.isNullOrEmpty()) {
            referenceRepository.findByTagNames(request.tags, pageable)
        } else {
            referenceRepository.search(request.source, request.keyword, pageable)
        }

        return page.map { ReferenceResponse.from(it) }
    }

    fun findById(id: Long): ReferenceResponse {
        val reference = referenceRepository.findById(id)
            .orElseThrow { NotFoundException("레퍼런스", id) }
        return ReferenceResponse.from(reference)
    }

    @Transactional
    @CacheEvict(value = ["references"], allEntries = true)
    fun create(request: ReferenceCreateRequest): ReferenceResponse {
        val tags = getOrCreateTags(request.tags)

        val reference = referenceRepository.save(
            Reference(
                title = request.title,
                summary = request.summary,
                url = request.url,
                source = request.source,
                author = request.author,
            )
        )
        reference.tags.addAll(tags)

        return ReferenceResponse.from(reference)
    }

    @Transactional
    @CacheEvict(value = ["references"], allEntries = true)
    fun saveFromCrawler(
        title: String,
        summary: String?,
        url: String,
        source: ReferenceSource,
        sourceId: String,
        author: String?,
        tagNames: List<String>,
    ): Reference {
        if (referenceRepository.existsBySourceAndSourceId(source, sourceId)) {
            return referenceRepository.findAll()
                .first { it.source == source && it.sourceId == sourceId }
        }

        val tags = getOrCreateTags(tagNames)
        val reference = referenceRepository.save(
            Reference(
                title = title,
                summary = summary,
                url = url,
                source = source,
                sourceId = sourceId,
                author = author,
            )
        )
        reference.tags.addAll(tags)
        return reference
    }

    private fun getOrCreateTags(tagNames: List<String>): List<Tag> {
        if (tagNames.isEmpty()) return emptyList()

        val existing = tagRepository.findByNameIn(tagNames)
        val existingNames = existing.map { it.name }.toSet()

        val newTags = tagNames.filter { it !in existingNames }
            .map { tagRepository.save(Tag(name = it)) }

        return existing + newTags
    }
}
