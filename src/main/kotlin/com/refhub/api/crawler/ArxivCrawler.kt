package com.refhub.api.crawler

import com.refhub.api.domain.reference.ReferenceSource
import com.refhub.api.event.CrawlEventProducer
import com.refhub.api.event.CrawlResultEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory

@Component
class ArxivCrawler(
    private val crawlEventProducer: CrawlEventProducer,
    @Value("\${crawler.arxiv.base-url}") private val baseUrl: String,
    @Value("\${crawler.arxiv.default-query}") private val defaultQuery: String,
    @Value("\${crawler.arxiv.max-results}") private val maxResults: Int,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val webClient = WebClient.builder().baseUrl(baseUrl).build()

    fun crawl(query: String? = null) {
        val searchQuery = query ?: defaultQuery
        log.info("Crawling arXiv: query={}", searchQuery)

        try {
            val xml = webClient.get()
                .uri { builder ->
                    builder
                        .queryParam("search_query", searchQuery)
                        .queryParam("max_results", maxResults)
                        .queryParam("sortBy", "submittedDate")
                        .queryParam("sortOrder", "descending")
                        .build()
                }
                .retrieve()
                .bodyToMono(String::class.java)
                .block() ?: return

            parseAndPublish(xml)
        } catch (e: Exception) {
            log.error("arXiv crawl failed", e)
        }
    }

    private fun parseAndPublish(xml: String) {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(ByteArrayInputStream(xml.toByteArray()))

        val entries = document.getElementsByTagName("entry")
        var count = 0

        for (i in 0 until entries.length) {
            val entry = entries.item(i) as Element
            val title = entry.getElementsByTagName("title").item(0)?.textContent?.trim() ?: continue
            val summary = entry.getElementsByTagName("summary").item(0)?.textContent?.trim()
            val id = entry.getElementsByTagName("id").item(0)?.textContent?.trim() ?: continue
            val author = entry.getElementsByTagName("name").item(0)?.textContent?.trim()

            val categories = mutableListOf<String>()
            val categoryNodes = entry.getElementsByTagName("category")
            for (j in 0 until categoryNodes.length) {
                (categoryNodes.item(j) as? Element)?.getAttribute("term")?.let { categories.add(it) }
            }

            crawlEventProducer.sendCrawlResult(
                CrawlResultEvent(
                    source = ReferenceSource.ARXIV,
                    title = title,
                    summary = summary,
                    url = id,
                    sourceId = id.substringAfterLast("/"),
                    author = author,
                    tags = categories.take(3) + listOf("arxiv", "paper"),
                )
            )
            count++
        }
        log.info("arXiv crawl completed: {} papers found", count)
    }
}
