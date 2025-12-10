package de.visualdigits.newshomereader.service

import de.visualdigits.newshomereader.HtmlUtil.addPersistentCookie
import de.visualdigits.newshomereader.HtmlUtil.getRequestUri
import de.visualdigits.newshomereader.model.configuration.NewsFeedsConfiguration
import de.visualdigits.newshomereader.model.configuration.NewsHomeReader
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsFeed
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsItem
import de.visualdigits.newshomereader.model.page.Page
import de.visualdigits.newshomereader.service.cache.ImageProxy
import de.visualdigits.newshomereader.service.cache.NewsItemCache
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import java.net.URI
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*

@Service
class PageService(
    private val newsHomeReader: NewsHomeReader,
    private val newsItemCache: NewsItemCache,
    private val imageProxy: ImageProxy,
    private val clientDataCacheService: ClientDataCacheService
) {

    fun renderPage(
        hashCode: UInt? = null,
        url: String? = null,
        clientCode: UUID? = null,
        requestUri: String,
        response: HttpServletResponse,
        model: Model
    ): String? {
        val cc = if (clientCode == null) {
            val c = UUID.randomUUID()
            response.addPersistentCookie("clientCode", c.toString())
            c
        } else {
            clientCode
        }

        val newsFeedsConfiguration = newsHomeReader.newsFeedsConfiguration()
        val currentPage = newsFeedsConfiguration?.naviMain?.getPage(requestUri)
        val template = renderPageModel(newsFeedsConfiguration, currentPage, hashCode, cc, model)
        return if (template != null) {
            template
        } else {
            response.sendRedirect(url)
            null
        }
    }

    fun formHideRead(
        clientCode: UUID,
        request: HttpServletRequest,
        model: Model
    ): String? {
        val newsFeedsConfiguration = newsHomeReader.newsFeedsConfiguration()
        val requestUri = request.getRequestUri().removePrefix("/formHideRead/")
        val currentPage = newsFeedsConfiguration?.naviMain?.getPage(requestUri)
        val hideRead = request.parameterMap["hideRead"] == null
        clientDataCacheService.setHideRead(clientCode, hideRead)
        return renderPageModel(newsFeedsConfiguration, currentPage, clientCode = clientCode, model = model)
    }

    fun formMarkAllRead(
        clientCode: UUID,
        request: HttpServletRequest,
        model: Model
    ): String? {
        val newsFeedsConfiguration = newsHomeReader.newsFeedsConfiguration()
        val requestUri = request.getRequestUri().removePrefix("/formMarkAllRead/")
        val currentPage = newsFeedsConfiguration?.naviMain?.getPage(requestUri)
        val markAllOlder = request.parameterMap["markAllOlder"]?.firstOrNull()?.toLong()
        if (currentPage != null) {
            val (feed, _) = determineFeed(currentPage)
            if (markAllOlder != null) {
                val thresholdDateTime = OffsetDateTime.now().minusDays(markAllOlder)
                clientDataCacheService.addReadItems(clientCode, feed?.items
                    ?.filter { item -> item.updated?.isBefore(thresholdDateTime) == true }
                    ?.map { item -> item.newsItemHashCode }
                )
            } else {
                val markAllRead = request.parameterMap["markAllRead"] != null
                val markAllUnread = request.parameterMap["markAllUnread"] != null
                if (markAllRead) {
                    clientDataCacheService.addReadItems(clientCode, feed?.items?.map { item -> item.newsItemHashCode })
                } else if (markAllUnread) {
                    clientDataCacheService.removeReadItems(clientCode, feed?.items?.map { item -> item.newsItemHashCode })
                }
            }
        }
        return renderPageModel(newsFeedsConfiguration, currentPage, clientCode = clientCode, model = model)
        
    }

    private fun renderPageModel(
        newsFeedsConfiguration: NewsFeedsConfiguration?,
        currentPage: Page?,
        hashCode: UInt? = null,
        clientCode: UUID,
        model: Model
    ): String? {
        return if (currentPage != null) {
            val hideRead = clientDataCacheService.isHideRead(clientCode)
            val path = currentPage.path()
            model.addAttribute("path", path)
            model.addAttribute("pathText", path.replace("/", " / "))
            model.addAttribute("theme", newsHomeReader.theme)
            model.addAttribute("title", newsHomeReader.siteTitle)
            model.addAttribute("naviMain", newsFeedsConfiguration?.toHtml(theme = newsHomeReader.theme, currentPage = currentPage, hideRead = hideRead))
            model.addAttribute("hideRead", hideRead)

            val (feed, isMultiFeed) = determineFeed(currentPage)
            val isArticle = hashCode != null
            model.addAttribute("isArticle", isArticle)
            model.addAttribute("isMultiFeed", isMultiFeed)
            if (isArticle) {
                clientDataCacheService.addReadItem(clientCode, hashCode)
                val newsItem = newsItemCache.getNewsItem(hashCode)
                if (newsItem != null) {
                    renderArticleModel(path, newsItem, clientCode, model)
                    "page"
                } else {
                    null
                }
            } else {
                feed?.also { feed ->
                    renderFeedModel(path, clientCode, isMultiFeed, feed, model)
                }
                "page"
            }
        } else {
            null
        }
    }

    private fun renderArticleModel(
        path: String,
        item: NewsItem,
        clientCode: UUID,
        model: Model
    ) {
        model.addAttribute("itemLink", item.link)
        model.addAttribute("itemTitle", item.title)
        model.addAttribute("feedtitleDate", item.updated?.format(ofPattern("dd.MM.YYYY HH:mm")))
        model.addAttribute(
            "newsItem", item.toModel(
                imageProxy = imageProxy,
                isArticle = true,
                clientData = clientDataCacheService.getClientData(clientCode),
                path = path
            )
        )
    }

    private fun renderFeedModel(
        path: String,
        clientCode: UUID,
        isMultiFeed: Boolean,
        feed: NewsFeed,
        model: Model
    ) {
        model.addAttribute("isFeedTitleLink", !isMultiFeed)
        model.addAttribute("feedLink", feed.link)
        model.addAttribute("feedTitle", feed.title)
        model.addAttribute("feedtitleTitle", feed.description ?: "")
        model.addAttribute("feedtitleDate", "")
        model.addAttribute(
            "newsItems", feed.items
                .sortedBy { item -> item.updated }
                .reversed()
                .map { item ->
                    item.toModel(
                        imageProxy = imageProxy,
                        isArticle = false,
                        clientData = clientDataCacheService.getClientData(clientCode),
                        path = path
                    )
                }
        )
    }

    private fun determineFeed(currentPage: Page): Pair<NewsFeed?, Boolean> = if (currentPage.isLeaf()) {
        currentPage.url?.let { u ->
            Pair(NewsFeed.readValue(newsItemCache, currentPage.name, URI(u)).applyPageFilters(currentPage), false) } ?: Pair(null, false)
    } else {
        val children = currentPage.allChildren()
        val feeds = children.mapNotNull { page -> page.url?.let { u -> NewsFeed.readValue(newsItemCache, page.name, URI(u)).applyPageFilters(page) } }
        val f = NewsFeed(
            title = feeds.joinToString(" ") { c -> c.title ?: "" },
            updated = feeds.map { feed -> feed.updated }.maxBy { u -> u?.toInstant()?.toEpochMilli() ?: 0 },
            keywords = feeds.flatMap { feed -> feed.keywords ?: listOf() },
            items = feeds.flatMap { feed -> feed.items }
        )
        Pair(f, true)
    }
}
