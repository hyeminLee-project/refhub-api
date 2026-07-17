package com.refhub.api.crawler

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CrawlScheduler(
    private val arxivCrawler: ArxivCrawler,
    private val gitHubCrawler: GitHubCrawler,
    private val youTubeCrawler: YouTubeCrawler,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 6 * * *")
    fun scheduledCrawl() {
        log.info("Starting scheduled crawl job")
        arxivCrawler.crawl()
        gitHubCrawler.crawl()
        youTubeCrawler.crawl()
        log.info("Scheduled crawl job completed")
    }

    fun triggerManual(source: String?, query: String?) {
        log.info("Manual crawl triggered: source={}, query={}", source, query)
        when (source?.uppercase()) {
            "ARXIV" -> arxivCrawler.crawl(query)
            "GITHUB" -> gitHubCrawler.crawl(query)
            "YOUTUBE" -> youTubeCrawler.crawl(query)
            else -> {
                arxivCrawler.crawl(query)
                gitHubCrawler.crawl(query)
                youTubeCrawler.crawl(query)
            }
        }
    }
}
