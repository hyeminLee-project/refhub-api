package com.refhub.api.domain.reference

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReferenceRepository : JpaRepository<Reference, Long> {

    fun findBySource(source: ReferenceSource, pageable: Pageable): Page<Reference>

    @Query("""
        SELECT r FROM Reference r
        WHERE (:source IS NULL OR r.source = :source)
        AND (:keyword IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(r.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    fun search(
        source: ReferenceSource?,
        keyword: String?,
        pageable: Pageable,
    ): Page<Reference>

    @Query("""
        SELECT r FROM Reference r JOIN r.tags t
        WHERE t.name IN :tagNames
        GROUP BY r
    """)
    fun findByTagNames(tagNames: List<String>, pageable: Pageable): Page<Reference>

    fun existsBySourceAndSourceId(source: ReferenceSource, sourceId: String): Boolean

    fun findBySourceAndSourceId(source: ReferenceSource, sourceId: String): Reference?
}
