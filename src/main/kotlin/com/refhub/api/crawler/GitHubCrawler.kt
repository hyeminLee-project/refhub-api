package com.refhub.api.crawler

import com.refhub.api.domain.reference.ReferenceSource
import com.refhub.api.event.CrawlEventProducer
import com.refhub.api.event.CrawlResultEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GitHubCrawler(
    private val crawlEventProducer: CrawlEventProducer,
    @Value("\${crawler.github.base-url}") private val baseUrl: String,
    @Value("\${crawler.github.token}") private val token: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val webClient by lazy {
        WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Accept", "application/vnd.github.v3+json")
            .apply {
                if (token.isNotBlank()) {
                    it.defaultHeader("Authorization", "Bearer $token")
                }
            }
            .build()
    }

    fun crawl(query: String? = null) {
        val searchQuery = query ?: "langchain OR llm OR ai-agent"
        log.info("Crawling GitHub: query={}", searchQuery)

        try {
            val response = webClient.get()
                .uri { builder ->
                    builder.path("/search/repositories")
                        .queryParam("q", "$searchQuery language:python language:typescript")
                        .queryParam("sort", "updated")
                        .queryParam("order", "desc")
                        .queryParam("per_page", 30)
                        .build()
                }
                .retrieve()
                .bodyToMono(GitHubSearchResponse::class.java)
                .block() ?: return

            response.items.forEach { repo ->
                crawlEventProducer.sendCrawlResult(
                    CrawlResultEvent(
                        source = ReferenceSource.GITHUB,
                        title = "${repo.fullName}: ${repo.description ?: ""}".take(500),
                        summary = repo.description,
                        url = repo.htmlUrl,
                        sourceId = repo.id.toString(),
                        author = repo.owner.login,
                        tags = listOfNotNull(repo.language?.lowercase(), "github", "repository"),
                    )
                )
            }
            log.info("GitHub crawl completed: {} repos found", response.items.size)
        } catch (e: Exception) {
            log.error("GitHub crawl failed", e)
        }
    }
}

data class GitHubSearchResponse(
    val items: List<GitHubRepo> = emptyList(),
)

data class GitHubRepo(
    val id: Long,
    val fullName: String,
    val description: String?,
    val htmlUrl: String,
    val language: String?,
    val owner: GitHubOwner,
)

data class GitHubOwner(
    val login: String,
)
