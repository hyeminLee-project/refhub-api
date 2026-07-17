package com.refhub.api.event

import com.refhub.api.config.KafkaConfig
import com.refhub.api.domain.reference.ReferenceService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class CrawlEventConsumer(
    private val referenceService: ReferenceService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = [KafkaConfig.TOPIC_CRAWL_RESULT],
        groupId = "refhub-group",
    )
    fun handleCrawlResult(event: CrawlResultEvent) {
        log.info("Received crawl result: source={}, title={}", event.source, event.title)
        try {
            referenceService.saveFromCrawler(
                title = event.title,
                summary = event.summary,
                url = event.url,
                source = event.source,
                sourceId = event.sourceId,
                author = event.author,
                tagNames = event.tags,
            )
        } catch (e: Exception) {
            log.error("Failed to save crawl result: {}", event.title, e)
        }
    }
}
