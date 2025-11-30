package de.visualdigits.newshomereader.service

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
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLDecoder
import java.time.format.DateTimeFormatter

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
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        val currentPage = determineCurrentPage(request, "/news")
        renderMarkup(currentPage, model, hideRead, readItems, hashCode, response)

        return "page"
    }

    fun formHideRead(
        readItems: MutableSet<UInt> = mutableSetOf(),
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        val currentPage = determineCurrentPage(request, "/formHideRead")
        val hideRead = request.parameterMap["hideRead"] == null

        addPersistentCookie("hideRead", hideRead.toString(), response)

        renderMarkup(currentPage, model, hideRead, readItems, response = response)

        return "page"
    }

    fun formMarkAllRead(
        readItems: MutableSet<UInt> = mutableSetOf(),
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        val currentPage = determineCurrentPage(request, "/formMarkAllRead")
        val hideRead = request.parameterMap["hideRead"]?.firstOrNull() == "true"
        val markAllRead = request.parameterMap["markAllRead"] != null
        val markAllUnread = request.parameterMap["markAllUnread"] != null
        if (currentPage != null) {
            val (feed, _) = determineFeed(currentPage)
            if (markAllRead) {
                readItems.addAll(feed?.items?.map { item -> item.newsItemHashCode }?:listOf())
            } else if (markAllUnread) {
                readItems.removeAll(feed?.items?.map { item -> item.newsItemHashCode }?:listOf())
            }
            addPersistentCookie("readItems", readItems.joinToString("/"), response)
        }
        renderMarkup(currentPage, model, hideRead, readItems, response = response)

        return "page"
    }

    private fun renderMarkup(
        currentPage: Page?,
        model: Model,
        hideRead: Boolean = false,
        readItems: MutableSet<UInt> = mutableSetOf(),
        hashCode: UInt? = null,
        response: HttpServletResponse
    ) {
        if (currentPage != null) {
            val path = currentPage.path()

            model.addAttribute("theme", newsHomeReader.theme)
            model.addAttribute("title", newsHomeReader.siteTitle)
            model.addAttribute("naviMain", newsHomeReader.newsFeedsConfiguration?.toHtml(theme = newsHomeReader.theme, currentPage = currentPage, hideRead = hideRead))

            val (feed, isMultiFeed) = determineFeed(currentPage)
            if (hashCode != null) {
                readItems.add(hashCode)
                addPersistentCookie("readItems", readItems.joinToString("/"), response)
                val newsItem = newsItemCache.getNewsItem(hashCode)
                newsItem?.also { item ->
                    item.readFullArticle()
                    renderArticleTitle(path, hideRead, item, model)
                    model.addAttribute(
                        "content", item.toHtml(
                            imageProxy = imageProxy,
                            fullArticle = true,
                            isMultiFeed = isMultiFeed,
                            hideRead = hideRead,
                            readItems = readItems
                        )
                    )
                }
            } else {
                feed?.also { feed ->
                    renderForms(hideRead, path, model)
                    renderFeedTitle(isMultiFeed, path, feed, model)

                    model.addAttribute("content", feed.toHtml(
                        imageProxy = imageProxy,
                        multiFeed = isMultiFeed,
                        hideRead = hideRead,
                        readItems = readItems,
                        path = path
                    ))
                }
            }

            cleanupReadItems(readItems, response)
        }
    }

    private fun cleanupReadItems(readItems: MutableSet<UInt>, response: HttpServletResponse) {
        val unknownHashes = readItems.toMutableSet()
        unknownHashes.removeAll(newsItemCache.getNewsItemHashCodes())
        readItems.removeAll(unknownHashes)
        addPersistentCookie("readItems", readItems.joinToString("/"), response)
    }

    private fun renderFeedTitle(isMultiFeed: Boolean, path: String, feed: NewsFeed, model: Model) {
        val sb = StringBuilder()
        if (isMultiFeed) {
            sb.append("<div id=\"feedtitle-path\">${path.replace("/", " / ")}</div>")
        } else {
            sb.append("<div id=\"feedtitle-path\"><a class=\"title\" href=\"${feed.link}\" alt=\"Webseite besuchen\" title=\"Webseite besuchen\" target=\"_blank\">${feed.title}</a></div>")
        }
        sb.append("<div id=\"feedtitle-title\">${feed.description ?: ""}</div>")
        sb.append("<div id=\"feedtitle-date\"></div>")
        model.addAttribute("feedtitle", sb.toString())
    }

    private fun renderArticleTitle(path: String, hideRead: Boolean, item: NewsItem, model: Model) {
        val sb = StringBuilder()
        sb.append("<div id=\"feedtitle-path\"><a class=\"title\" href=\"/news/$path\" alt=\"Zurück zum Feed\" title=\"Zurück zum Feed\">${path.replace("/", " / ")}</a><span class=\"feedName\">${item.feedName}</span></div>")
        sb.append("<div id=\"feedtitle-title\"><a class=\"title\" href=\"${item.link}\" alt=\"Original Artikel aufrufen.\" title=\"Original Artikel aufrufen.\" target=\"_blank\">${item.title}</a></div>")
        sb.append("<div id=\"feedtitle-date\">${item.updated?.format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm"))}</div>")
        model.addAttribute("feedtitle", sb.toString())
    }

    private fun renderForms(
        hideRead: Boolean,
        path: String,
        model: Model
    ) {
        val buttons = StringBuilder()
        val checked = if (hideRead) " checked" else ""
        buttons.append("<div id=\"feedtitle-buttons\">")

        buttons.append("<form id=\"formHideRead\" action=\"/formHideRead/$path\" method=\"POST\">")
        buttons.append("<div class=\"container\">")
        buttons.append("<span class=\"label\">Gelesene verstecken</span>")
        buttons.append("<label class=\"switch\">")
        buttons.append("<input type=\"checkbox\" id=\"hideRead\" name=\"hideRead\" value=\"true\"$checked>")
        buttons.append("<span class=\"slider\" onclick=\"javascript:formHideRead.submit();\"></span>")
        buttons.append("</label>")
        buttons.append("</div>")
        buttons.append("</form>")

        buttons.append("<form id=\"formMarkAllRead\" action=\"/formMarkAllRead/$path\" method=\"POST\">")
        buttons.append("<button type=\"submit\" id=\"markAllRead\" name=\"markAllRead\" value=\"true\">Alle gelesen</button>")
        buttons.append("<button type=\"submit\" id=\"markAllUnread\" name=\"markAllUnread\" value=\"true\">Alle ungelesen</button>")
        buttons.append("<input type=\"hidden\" id=\"hideRead\" name=\"hideRead\" value=\"$hideRead\">")
        buttons.append("<input type=\"hidden\" id=\"hideRead\" name=\"hideRead\" value=\"$hideRead\">")
        buttons.append("</form>")

        buttons.append("</div>")
        model.addAttribute("buttons", buttons.toString())
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

    private fun determineCurrentPage(request: HttpServletRequest, rootPage: String): Page? {
        var requestUri = getRequestUri(request).replace(rootPage, "").let { ru -> if (ru.startsWith("/")) ru.drop(1) else ru }
        val currentPage = newsHomeReader.newsFeedsConfiguration?.naviMain?.getPage(requestUri)

        return currentPage
    }

    private fun getRequestUri(request: HttpServletRequest): String {
        var uri = ""
        try {
            uri = URLDecoder.decode(request.requestURI, request.characterEncoding)
        } catch (_: UnsupportedEncodingException) {
            // ignore
        }
        return uri
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
