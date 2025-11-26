package de.visualdigits.newshomereader.model.newsfeed.atom

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.newshomereader.model.cache.newsitem.NewsItemCache
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsFeed
import java.time.OffsetDateTime


class Feed(
    @JacksonXmlProperty(isAttribute = true) val xmlns: String = "http://www.w3.org/2005/Atom",
    val title: Text? = null,
    val subtitle: Text? = null,
    val updated: OffsetDateTime? = null,
    val id: String? = null,
    val author: Author? = null,
    @field:JacksonXmlElementWrapper(useWrapping = false) @JacksonXmlProperty(localName = "link") val links: List<Link>? = null,
    val rights: String? = null,
    val tags: String? = null,
    val keywords: List<String>? = tags?.split(",")?.map { t -> t.trim() }?.filter { t -> t.isNotEmpty() },
    @field:JacksonXmlElementWrapper(useWrapping = false) @JacksonXmlProperty(localName = "entry") val entries: List<Entry>? = null
) : BaseNode<Feed>() {

    fun toNewsFeed(newsItemCache: NewsItemCache, feedName: String): NewsFeed {
        return NewsFeed(
            title = title?.text,
            description = subtitle?.text,
            link = links?.firstOrNull()?.href,
            updated = updated,
            rights = rights,
            keywords = keywords,
            items = entries?.map { entry -> entry.toNewsItem(newsItemCache, feedName) } ?: listOf()
        )
    }
}