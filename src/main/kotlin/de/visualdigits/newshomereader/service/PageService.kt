package de.visualdigits.newshomereader.service

import de.visualdigits.newshomereader.HtmlUtil.addPersistentCookie
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
        clientCode: UUID? = null,
        requestUri: String,
        response: HttpServletResponse,
        model: Model
    ): String {
        val cc = if (clientCode == null) {
            val c = UUID.randomUUID()
            response.addPersistentCookie("clientCode", c.toString())
            c
        } else {
            clientCode
        }

        val newsFeedsConfiguration = newsHomeReader.newsFeedsConfiguration()
        val currentPage = newsFeedsConfiguration?.naviMain?.getPage(requestUri)
        renderPageModel(newsFeedsConfiguration, currentPage, hashCode, cc, model)

        return "page"
    }

    fun formHideRead(
        clientCode: UUID,
        requestUri: String,
        request: HttpServletRequest,
        model: Model
    ): String {
        val newsFeedsConfiguration = newsHomeReader.newsFeedsConfiguration()
        val currentPage = newsFeedsConfiguration?.naviMain?.getPage(requestUri)
        val hideRead = request.parameterMap["hideRead"] == null
        clientDataCacheService.setHideRead(clientCode, hideRead)
        renderPageModel(newsFeedsConfiguration, currentPage, clientCode = clientCode, model = model)

        return "page"
    }

    fun formMarkAllRead(
        clientCode: UUID,
        requestUri: String,
        request: HttpServletRequest,
        model: Model
    ): String {
        val newsFeedsConfiguration = newsHomeReader.newsFeedsConfiguration()
        val currentPage = newsFeedsConfiguration?.naviMain?.getPage(requestUri)
        val markAllRead = request.parameterMap["markAllRead"] != null
        val markAllUnread = request.parameterMap["markAllUnread"] != null
        if (currentPage != null) {
            val (feed, _) = determineFeed(currentPage)
            if (markAllRead) {
                clientDataCacheService.addReadItems(clientCode, feed?.items?.map { item -> item.newsItemHashCode })
            } else if (markAllUnread) {
                clientDataCacheService.removeReadItems(clientCode, feed?.items?.map { item -> item.newsItemHashCode })
            }
        }
        renderPageModel(newsFeedsConfiguration, currentPage, clientCode = clientCode, model = model)

        return "page"
    }

    private fun renderPageModel(
        newsFeedsConfiguration: NewsFeedsConfiguration?,
        currentPage: Page?,
        hashCode: UInt? = null,
        clientCode: UUID,
        model: Model
    ) {
        if (currentPage != null) {
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
                newsItemCache
                    .getNewsItem(hashCode)
                    ?.also { item ->
                        renderArticleModel(item, clientCode, model)
                    }
            } else {
                feed?.also { feed ->
                    renderFeedModel(path, clientCode, isMultiFeed, feed, model)
                }
            }
            clientDataCacheService.cleanupOrphanedReadItems(clientCode)
        }
    }

    private fun renderArticleModel(
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
                clientData = clientDataCacheService.getClientData(clientCode)
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
