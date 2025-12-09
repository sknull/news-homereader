package de.visualdigits.newshomereader.model.newsfeed.rss

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.newshomereader.HtmlUtil.extractImage
import de.visualdigits.newshomereader.service.cache.NewsItemCache
import de.visualdigits.newshomereader.model.newsfeed.atom.Text
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsItem
import org.apache.commons.text.StringEscapeUtils
import java.io.File
import java.net.URI
import java.time.OffsetDateTime

class Item(
    val guid: Guid? = null,
    val identifier: String? = null,
    val id: String? = null,

    val date: OffsetDateTime? = null, // first publish date time
    val pubDate: OffsetDateTime? = null, // update date time or first publish date time when date is empty

    val about: String? = null,
    val type: String? = null,
    val format: String? = null,
    val source: String? = null,
    val language: String? = null,
    val publisher: String? = null,
    val rights: String? = null,
    val subject: String? = null,
    val audience: String? = null,
    val isFormatOf: String? = null,
    var encoded: Content? = null,
    var content: HtmlContent? = null,
    val topline: String? = null,
    val states: String? = null,

    val title: String? = null,
    val link: String? = null,
    val description: String? = null,
    @field:JacksonXmlElementWrapper(useWrapping = false) @field:JacksonXmlProperty(localName = "category") val categories: List<Text> = listOf(),
    val isPermaLink: Boolean? = null,
    val enclosure: Enclosure? = null,
    val images: List<Image> = listOf(),

    val comments: MutableList<Comment> = mutableListOf(),
) : BaseNode<Item>() {

    fun toNewsItem(newsItemCache: NewsItemCache, feedName: String): NewsItem {
        val content = content?.html
            ?.writeValueAsString(
                indentOutput = false,
                writeXmlDeclaration = false
            )
            ?.replace("Html", "html")
            ?.let { html -> StringEscapeUtils.unescapeHtml4(html).trim() }

        var (image, imageTitle, imageCaption) = content?.let { c -> extractImage(c) }?:Triple(null, null, null)
        if (image == null) {
            image = enclosure?.url
        }

        return newsItemCache.cacheNewsItem(
            NewsItem(
                feedName = feedName,
                identifier = identifier ?: id ?: link?.let { l -> File(URI(l).path).nameWithoutExtension } ?: error("No identifier"),
                published = date ?: pubDate,
                updated = pubDate ?: error("No date"),
                link = link,
                title = title?.trim(),
                summary = description?.trim(),
                keywords = categories.mapNotNull { category -> category.text }.filter { c -> c.isNotEmpty() },
                image = image,
                imageTitle = imageTitle,
                imageCaption = imageCaption
            )
        )
    }

}
