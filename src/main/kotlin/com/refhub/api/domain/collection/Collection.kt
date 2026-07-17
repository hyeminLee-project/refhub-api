package com.refhub.api.domain.collection

import com.refhub.api.domain.reference.Reference
import com.refhub.api.domain.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "collections",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "name"])]
)
class Collection(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(length = 500)
    var description: String? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "collection_references",
        joinColumns = [JoinColumn(name = "collection_id")],
        inverseJoinColumns = [JoinColumn(name = "reference_id")]
    )
    val references: MutableSet<Reference> = mutableSetOf(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
