package de.visualdigits.newshomereader.model.newsfeed.unified

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.newshomereader.model.cache.images.ImageProxy
import de.visualdigits.newshomereader.model.cache.newsitem.NewsItemCache
import de.visualdigits.newshomereader.model.newsfeed.atom.Feed
import de.visualdigits.newshomereader.model.newsfeed.rss.Rss
import de.visualdigits.newshomereader.model.page.Page
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ui.Model
import java.io.File
import java.net.URI
import java.time.OffsetDateTime
import kotlin.String

@JsonIgnoreProperties("pageName")
class NewsFeed(
    val title: String? = null,
    val description: String? = null,
    val link: String? = null,
    val image: String? = null,
    val imageTitle: String? = null,
    val imageCaption: String? = null,
    val updated: OffsetDateTime? = null,
    val rights: String? = null,
    val language: String? = null,
    val keywords: List<String>? = null,

    @JacksonXmlElementWrapper(useWrapping = false) @JacksonXmlProperty(localName = "item") val items: List<NewsItem> = listOf()
) : BaseNode<NewsFeed>() {

    companion object {

        private val log: Logger = LoggerFactory.getLogger(javaClass)

        fun readValue(newsItemCache: NewsItemCache, feedName: String, uri: URI): NewsFeed {
            return readValue(newsItemCache, feedName, uri.toURL().readText())
        }

        fun readValue(newsItemCache: NewsItemCache, feedName: String, file: File): NewsFeed {
            return readValue(newsItemCache, feedName, file.readText())
        }

        private fun readValue(newsItemCache: NewsItemCache, feedName: String, xml: String): NewsFeed {
            val feedType = Jsoup
                .parse(xml, "", Parser.xmlParser())
                .root()
                .select("> *")
                .firstOrNull()
                ?.tagName()
                ?.split(":")
                ?.firstOrNull()
            return when (feedType) {
                "rss", "rdf" -> {
                    val rss = readValue<Rss>(xml)
                    rss.toNewsFeed(newsItemCache, feedName)
                }
                "feed" -> {
                    val feed = readValue<Feed>(xml)
                    feed.toNewsFeed(newsItemCache, feedName)
                }
                else -> error("Unsupported feed type '$feedType'")
            }
        }
    }

    /**
     * Returns a copy of this news feed with the item filters of the given page applied.
     */
    fun applyPageFilters(currentPage: Page): NewsFeed {
        val stopWords = currentPage.filters["title"]?.stopWords?:listOf()
        val filteredItems = items
            .filter { item ->
                val words = item.title?.split(" ")?:listOf()
                stopWords.none { sw -> words.contains(sw) }
            }
        val filteredFeed = copy(filteredItems)
        return filteredFeed
    }

    private fun copy(items: List<NewsItem>? = null): NewsFeed {
        return NewsFeed(
            title,
            description,
            link,
            image,
            imageTitle,
            imageCaption,
            updated,
            rights,
            language,
            keywords,
            items ?: this.items
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsFeed

        if (title != other.title) return false
        if (updated != other.updated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (updated?.hashCode() ?: 0)
        return result
    }
}

