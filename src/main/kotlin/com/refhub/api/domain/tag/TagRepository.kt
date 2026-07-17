package com.refhub.api.domain.tag

import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {
    fun findByName(name: String): Tag?
    fun findByNameIn(names: List<String>): List<Tag>
}
