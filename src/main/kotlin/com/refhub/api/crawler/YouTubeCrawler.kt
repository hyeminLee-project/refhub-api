package com.refhub.api.crawler

import com.refhub.api.domain.reference.ReferenceSource
import com.refhub.api.event.CrawlEventProducer
import com.refhub.api.event.CrawlResultEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class YouTubeCrawler(
    private val crawlEventProducer: CrawlEventProducer,
    @Value("\${crawler.youtube.base-url}") private val baseUrl: String,
    @Value("\${crawler.youtube.api-key}") private val apiKey: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val webClient = WebClient.builder().baseUrl(baseUrl).build()

    fun crawl(query: String? = null) {
        if (apiKey.isBlank()) {
            log.warn("YouTube API key not configured, skipping crawl")
            return
        }

        val searchQuery = query ?: "langchain tutorial OR AI agent development"
        log.info("Crawling YouTube: query={}", searchQuery)

        try {
            val response = webClient.get()
                .uri { builder ->
                    builder.path("/search")
                        .queryParam("part", "snippet")
                        .queryParam("q", searchQuery)
                        .queryParam("type", "video")
                        .queryParam("order", "date")
                        .queryParam("maxResults", 25)
                        .queryParam("key", apiKey)
                        .build()
                }
                .retrieve()
                .bodyToMono(YouTubeSearchResponse::class.java)
                .block() ?: return

            response.items.forEach { item ->
                val videoId = item.id.videoId ?: return@forEach
                crawlEventProducer.sendCrawlResult(
                    CrawlResultEvent(
                        source = ReferenceSource.YOUTUBE,
                        title = item.snippet.title,
                        summary = item.snippet.description,
                        url = "https://www.youtube.com/watch?v=$videoId",
                        sourceId = videoId,
                        author = item.snippet.channelTitle,
                        tags = listOf("youtube", "video", "tutorial"),
                    )
                )
            }
            log.info("YouTube crawl completed: {} videos found", response.items.size)
        } catch (e: Exception) {
            log.error("YouTube crawl failed", e)
        }
    }
}

data class YouTubeSearchResponse(
    val items: List<YouTubeItem> = emptyList(),
)

data class YouTubeItem(
    val id: YouTubeVideoId,
    val snippet: YouTubeSnippet,
)

data class YouTubeVideoId(
    val videoId: String?,
)

data class YouTubeSnippet(
    val title: String,
    val description: String?,
    val channelTitle: String,
)
