package com.refhub.api.domain.reference

import com.refhub.api.domain.tag.Tag
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "references",
    indexes = [
        Index(name = "idx_reference_source", columnList = "source"),
        Index(name = "idx_reference_created_at", columnList = "createdAt"),
    ]
)
class Reference(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 500)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var summary: String? = null,

    @Column(nullable = false, length = 1000)
    val url: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val source: ReferenceSource,

    @Column(length = 200)
    var author: String? = null,

    @Column(length = 100)
    var sourceId: String? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "reference_tags",
        joinColumns = [JoinColumn(name = "reference_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: MutableSet<Tag> = mutableSetOf(),

    val publishedAt: LocalDateTime? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now(),
)

enum class ReferenceSource {
    ARXIV, GITHUB, YOUTUBE, BLOG
}
