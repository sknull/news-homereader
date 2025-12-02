package de.visualdigits.newshomereader.service

import de.visualdigits.newshomereader.HtmlUtil.getRequestUri
import de.visualdigits.newshomereader.model.cache.images.ImageProxy
import de.visualdigits.newshomereader.model.cache.newsitem.NewsItemCache
import de.visualdigits.newshomereader.model.configuration.NewsHomeReader
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsFeed
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsItem
import de.visualdigits.newshomereader.model.page.Page
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import java.net.URI
import java.time.format.DateTimeFormatter.ofPattern

@Service
class PageService(
    private val newsHomeReader: NewsHomeReader,
    private val newsItemCache: NewsItemCache,
    private val imageProxy: ImageProxy
) {

    fun renderPage(
        hashCode: UInt? = null,
        hideRead: Boolean = false,
        readItems: MutableSet<UInt> = mutableSetOf(),
        requestUri: String,
        response: HttpServletResponse,
        model: Model
    ): String {
        val currentPage = newsHomeReader.newsFeedsConfiguration?.naviMain?.getPage(requestUri)
        renderPageModel(currentPage, hideRead, readItems, hashCode, response, model)

        return "page"
    }

    fun formHideRead(
        readItems: MutableSet<UInt> = mutableSetOf(),
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        val currentPage = newsHomeReader.newsFeedsConfiguration?.naviMain?.getPage(
            request
                .getRequestUri()
                .replace("/formHideRead", "").let { ru -> if (ru.startsWith("/")) ru.drop(1) else ru })
        val hideRead = request.parameterMap["hideRead"] == null

        addPersistentCookie("hideRead", hideRead.toString(), response)

        renderPageModel(currentPage, hideRead, readItems, response = response, model = model)

        return "page"
    }

    fun formMarkAllRead(
        readItems: MutableSet<UInt> = mutableSetOf(),
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        val currentPage = newsHomeReader.newsFeedsConfiguration?.naviMain?.getPage(
            request
                .getRequestUri()
                .replace("/formMarkAllRead", "").let { ru -> if (ru.startsWith("/")) ru.drop(1) else ru })
        val hideRead = request.parameterMap["hideRead"]?.firstOrNull() == "true"
        val markAllRead = request.parameterMap["markAllRead"] != null
        val markAllUnread = request.parameterMap["markAllUnread"] != null
        if (currentPage != null) {
            val (feed, _) = determineFeed(currentPage)
            if (markAllRead) {
                readItems.addAll(feed?.items?.map { item -> item.newsItemHashCode }?:listOf())
            } else if (markAllUnread) {
                readItems.removeAll((feed?.items?.map { item -> item.newsItemHashCode }?:listOf()).toSet())
            }
            addPersistentCookie("readItems", readItems.joinToString("/"), response)
        }
        renderPageModel(currentPage, hideRead, readItems, response = response, model = model)

        return "page"
    }

    private fun renderPageModel(
        currentPage: Page?,
        hideRead: Boolean = false,
        readItems: MutableSet<UInt> = mutableSetOf(),
        hashCode: UInt? = null,
        response: HttpServletResponse,
        model: Model
    ) {
        if (currentPage != null) {
            val path = currentPage.path()
            model.addAttribute("path", path)
            model.addAttribute("pathText", path.replace("/", " / "))
            model.addAttribute("theme", newsHomeReader.theme)
            model.addAttribute("title", newsHomeReader.siteTitle)
            model.addAttribute("naviMain", newsHomeReader.newsFeedsConfiguration?.toHtml(theme = newsHomeReader.theme, currentPage = currentPage, hideRead = hideRead))
            model.addAttribute("hideRead", hideRead)

            val (feed, isMultiFeed) = determineFeed(currentPage)
            val isArticle = hashCode != null
            model.addAttribute("isArticle", isArticle)
            model.addAttribute("isMultiFeed", isMultiFeed)
            if (isArticle) {
                readItems.add(hashCode)
                addPersistentCookie("readItems", readItems.joinToString("/"), response)
                newsItemCache
                    .getNewsItem(hashCode)
                    ?.also { item ->
                        renderArticleModel(item, hideRead, readItems, model)
                    }
            } else {
                feed?.also { feed ->
                    renderFeedModel(path, hideRead, readItems, isMultiFeed, feed, model)
                }
            }

            cleanupReadItems(readItems, response)
        }
    }

    private fun renderArticleModel(
        item: NewsItem,
        hideRead: Boolean,
        readItems: MutableSet<UInt>,
        model: Model
    ) {
        model.addAttribute("itemLink", item.link)
        model.addAttribute("itemTitle", item.title)
        model.addAttribute("feedtitleDate", item.updated?.format(ofPattern("dd.MM.YYYY HH:mm")))
        model.addAttribute(
            "newsItem", item.toModel(
                imageProxy = imageProxy,
                isArticle = true,
                hideRead = hideRead,
                readItems = readItems
            )
        )
    }

    private fun renderFeedModel(
        path: String,
        hideRead: Boolean,
        readItems: Set<UInt>,
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
                    item.toModel(imageProxy, false, hideRead, readItems, path)
                }
        )
    }

    private fun cleanupReadItems(readItems: MutableSet<UInt>, response: HttpServletResponse) {
        val unknownHashes = readItems.toMutableSet()
        unknownHashes.removeAll(newsItemCache.getNewsItemHashCodes())
        readItems.removeAll(unknownHashes)
        addPersistentCookie("readItems", readItems.joinToString("/"), response)
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

    private fun addPersistentCookie(name: String, value: String, response: HttpServletResponse) {
        val cookie = Cookie(name, value)
        cookie.maxAge = Int.MAX_VALUE
        cookie.secure = false
        cookie.isHttpOnly = true
        cookie.path = "/"
        response.addCookie(cookie)
    }
}
