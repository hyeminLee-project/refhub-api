package com.refhub.api.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {

    companion object {
        const val TOPIC_CRAWL_REQUEST = "refhub.crawl.request"
        const val TOPIC_CRAWL_RESULT = "refhub.crawl.result"
    }

    @Bean
    fun crawlRequestTopic(): NewTopic =
        TopicBuilder.name(TOPIC_CRAWL_REQUEST)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun crawlResultTopic(): NewTopic =
        TopicBuilder.name(TOPIC_CRAWL_RESULT)
            .partitions(3)
            .replicas(1)
            .build()
}
