package com.refhub.api.event

import com.refhub.api.config.KafkaConfig
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CrawlEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun sendCrawlRequest(event: CrawlRequestEvent) {
        log.info("Sending crawl request: source={}, query={}", event.source, event.query)
        kafkaTemplate.send(KafkaConfig.TOPIC_CRAWL_REQUEST, event.source.name, event)
    }

    fun sendCrawlResult(event: CrawlResultEvent) {
        log.debug("Sending crawl result: source={}, title={}", event.source, event.title)
        kafkaTemplate.send(KafkaConfig.TOPIC_CRAWL_RESULT, event.source.name, event)
    }
}
