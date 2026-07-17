package com.refhub.api.event

import com.refhub.api.domain.reference.ReferenceSource

data class CrawlRequestEvent(
    val source: ReferenceSource,
    val query: String? = null,
)

data class CrawlResultEvent(
    val source: ReferenceSource,
    val title: String,
    val summary: String?,
    val url: String,
    val sourceId: String,
    val author: String?,
    val tags: List<String> = emptyList(),
)
