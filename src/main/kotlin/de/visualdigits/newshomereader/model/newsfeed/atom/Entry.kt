package de.visualdigits.newshomereader.model.newsfeed.atom


import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.newshomereader.HtmlUtil.extractImage
import de.visualdigits.newshomereader.model.cache.newsitem.NewsItemCache
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsItem
import java.time.OffsetDateTime

class Entry(
    val title: Text? = null,
    val id: String? = null,
    val updated: OffsetDateTime? = null,
    val published: OffsetDateTime? = null,
    val link: Link? = null,
    val author: Author? = null,
    val tags: String? = null,
    val keywords: List<String>? = tags?.split(",")?.map { t -> t.trim() }?.filter { t -> t.isNotEmpty() },
    val summary: Text? = null,
    val content: Text? = null
) : BaseNode<Entry>() {

    fun toNewsItem(newsItemCache: NewsItemCache, feedName: String): NewsItem {
        val content = content?.text ?: ""
        val (image, imageTitle, imageCaption) = extractImage(content)
        return newsItemCache.cacheNewsItem(
            NewsItem(
                feedName = feedName,
                identifier = id ?: error("No identifier"),
                published = published,
                updated = updated ?: error("No date"),
                link = link?.href,
                title = title?.text,
                summary = summary?.text,
                keywords = keywords,
                image = image,
                imageTitle = imageTitle,
                imageCaption = imageCaption
            )
        )
    }
}