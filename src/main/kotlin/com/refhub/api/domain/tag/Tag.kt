package com.refhub.api.domain.tag

import jakarta.persistence.*

@Entity
@Table(name = "tags")
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 50)
    val name: String,
)
