package de.visualdigits.newshomereader.model.newsfeed.unified

import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.hybridxml.model.html.Html
import de.visualdigits.hybridxml.model.polymorphic.PolymorphicNode
import de.visualdigits.newshomereader.model.cache.images.ImageProxy
import de.visualdigits.newshomereader.model.cache.newsitem.NewsItemCacheKey
import io.github.cdimascio.essence.Essence
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.net.URI
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Component
@Cacheable("newsItem")
class NewsItem(
    val feedName: String? = null,

    val identifier: String? = null,

    val published: OffsetDateTime? = null,
    val updated: OffsetDateTime? = null,

    val link: String? = null,

    val title: String? = null,
    val summary: String? = null,
    val keywords: List<String>? = null,
    val image: String? = null,
    val imageTitle: String? = null,
    val imageCaption: String? = null,

    var html: String? = null,

    var read: Boolean = false
) : BaseNode<NewsItem>() {

    fun cacheKey(): NewsItemCacheKey = NewsItemCacheKey(feedName?:error("No feed name"), identifier?:error("No identifier"), updated)

    fun toHtml(
        imageProxy: ImageProxy,
        fullArticle: Boolean,
        isMultiFeed: Boolean,
        hideRead: Boolean,
        path: String? = null
    ): String {
        val sb = StringBuilder()
        val itemClazz = if (fullArticle) "article" else "item"
        val readClazz = if (read) " read" else ""
        val hideClazz = if (read && hideRead) " hide" else ""

        sb.append("<div class=\"news-$itemClazz$readClazz$hideClazz\">\n")

        if (!fullArticle) {
            sb.append("<div class=\"news-title\">")
            if (isMultiFeed) feedName?.also { fn -> sb.append("<div class=\"feedName\">$fn</div>") }
            sb.append("<div class=\"date\">${updated?.format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm"))}</div>")
            sb.append("<div class=\"title\">")
            sb.append("<a class=\"title\" href=\"/news/$path?feedName=$feedName&identifier=$identifier&hideRead=$hideRead&\" alt=\"Artikel text abrufen.\" title=\"Artikel text abrufen.\">$title</a>\n")
            sb.append("</div>\n")
            sb.append("</div>\n")
        }

        image?.also { img ->
            imageProxy.getImage(img)?.also { imgUrl ->
                sb.append("<div class=\"news-image\">")
                sb.append("<div class=\"image\">")
                sb.append("<img src=\"$imgUrl\" alt=\"$imageTitle\" title=\"$imageTitle\"/>")
                sb.append("<div class=\"glasspane\"></div>")
                sb.append("</div>")
                if (fullArticle) sb.append("<div class=\"caption\">$imageCaption</div>")
                sb.append("</div>")
            }
        }

        sb.append("<div class=\"news-summary\">$summary</div>\n")
        if (fullArticle) html?.also { h -> sb.append("<div class=\"news-html\">$h</div>\n") }

        sb.append("</div>\n")

        return sb.toString()
    }

    fun readFullArticle() {
        if (html == null) {
            // read only once from website to acoid traffic
            link?.let { l -> URI(l).toURL().readText() }?.let { rawHtml ->
                // try to avoid repeating the summary (extraction heuristics are not perfect...)
                var html = Essence.extract(rawHtml).html
                summary?.let { s ->
                    if(html.contains(s)) {
                        html = html.replace(s, "")
                    }
                }
                this.html = html
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsItem

        if (feedName != other.feedName) return false
        if (identifier != other.identifier) return false
        if (updated != other.updated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = feedName?.hashCode() ?: 0
        result = 31 * result + (identifier?.hashCode() ?: 0)
        result = 31 * result + (updated?.hashCode() ?: 0)
        return result
    }
}