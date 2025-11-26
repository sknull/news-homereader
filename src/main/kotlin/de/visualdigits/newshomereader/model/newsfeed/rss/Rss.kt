package de.visualdigits.newshomereader.model.newsfeed.rss

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.hybridxml.model.html.Html.Companion.createHtmlNode
import de.visualdigits.hybridxml.model.html.Html.Companion.parseHtml
import de.visualdigits.hybridxml.model.polymorphic.PolymorphicNode
import de.visualdigits.newshomereader.model.cache.newsitem.NewsItemCache
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsFeed
import org.jsoup.nodes.Element

class Rss(
    @field:JacksonXmlProperty(isAttribute = true) val version: String? = null,
    val channel: Channel? = null,
    val about: String? = null,
    val image: Image? = null,
    @JacksonXmlElementWrapper(useWrapping = false) @JacksonXmlProperty(localName = "item") val items: List<Item>? = null
) : BaseNode<Rss>() {

    override fun postProcessXml() {
        channel?.items?.forEach { item -> postProcessItem(item) }
        items?.forEach { item -> postProcessItem(item) }
    }

    private fun postProcessItem(item: Item) {
        item.encoded?.text?.also { html ->
            val tree = parseHtml(
                html = html,
                rootNodeName = "html"
            ) { label: String?, element: Element?, node: PolymorphicNode<*>?, children: List<PolymorphicNode<*>>, text: String? ->
                createHtmlNode(label, element = element, node = node, children = children.toMutableList(), text = text)
            }
            item.content = tree?.let { html -> HtmlContent(html) }
                ?.also { item.encoded = null } // ensure that we only null out when we could parse successfully
        }
    }

    fun toNewsFeed(newsItemCache: NewsItemCache, feedName: String): NewsFeed {
        val newsFeed = NewsFeed(
            title = channel?.title,
            description = channel?.description,
            link = channel?.link,
            image = channel?.image?.url,
            imageTitle = channel?.image?.title,
            imageCaption = channel?.image?.caption,
            updated = channel?.lastBuildDate,
            rights = channel?.rights,
            language = channel?.language,
            items = items?.map { item -> item.toNewsItem(newsItemCache, feedName) }
                ?: channel?.items?.map { item -> item.toNewsItem(newsItemCache, feedName) }
                ?: listOf()
        )

        return newsFeed
    }
}
