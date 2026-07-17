package com.refhub.api.domain.collection

import org.springframework.data.jpa.repository.JpaRepository

interface CollectionRepository : JpaRepository<Collection, Long> {
    fun findByUserId(userId: Long): List<Collection>
    fun existsByUserIdAndName(userId: Long, name: String): Boolean
}
