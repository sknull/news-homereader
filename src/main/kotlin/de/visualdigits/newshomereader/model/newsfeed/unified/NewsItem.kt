package de.visualdigits.newshomereader.model.newsfeed.unified

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.newshomereader.model.cache.images.ImageProxy
import de.visualdigits.newshomereader.model.cache.newsitem.NewsItemCacheKey
import de.visualdigits.newshomereader.model.newsfeed.applicationjson.AppJson
import de.visualdigits.newshomereader.model.newsfeed.applicationjson.DateOnlyDeserializer
import io.github.cdimascio.essence.Essence
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.net.URI
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Component
@JsonIgnoreProperties("hashCode")
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

    var rawHtml: String? = null, // as fetched from URL
    var html: String? = null, // contains only main article markup
    var applicationJson: List<AppJson>? = null,
    var videoItems: List<MediaItem> = listOf(),
    var audioItems: List<MediaItem> = listOf(),
) : BaseNode<NewsItem>() {

    val newsItemHashCode: UInt = "$feedName$identifier".hashCode().toUInt()

    companion object {
        val jsonMapper = jacksonMapperBuilder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ISODate
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, JsonInclude.Include.NON_EMPTY))
            .addModule(JavaTimeModule().addDeserializer(OffsetDateTime::class.java, DateOnlyDeserializer()))
            .build()
    }

    fun cacheKey(): NewsItemCacheKey = NewsItemCacheKey(newsItemHashCode, updated)

    fun toModel(
        imageProxy: ImageProxy,
        isArticle: Boolean,
        hideRead: Boolean,
        readItems: MutableSet<UInt> = mutableSetOf(),
        path: String? = null
    ): NewsItemRendered {
        if (isArticle) readFullArticle()

        val itemClazz = if (isArticle) "article" else "item"
        val read = readItems.contains(newsItemHashCode)
        val readClazz = if (read) " read" else ""
        val hideClazz = if (read && hideRead) " hide" else ""

        return NewsItemRendered(
            hasImage = image != null,
            hasAudio = audioItems.isNotEmpty(),
            hasVideo = videoItems.isNotEmpty(),

            itemClass = "news-$itemClazz$readClazz$hideClazz",
            feedName = feedName,
            title = title,
            path = "/news/$path?hashCode=$newsItemHashCode&hideRead=$hideRead&",
            updated = updated?.format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")),
            imageTitle = imageTitle,
            imageCaption = imageCaption,
            imageUrl = image?.let { img -> imageProxy.getImage(newsItemHashCode, img) },
            audioUrl = audioItems.firstOrNull()?.let { ai -> ai.url },
            videoUrl = videoItems.firstOrNull()?.let { vi -> vi.url },
            summary = summary,
            html = html
        )
    }

    fun readFullArticle() {
        if (html == null) {
            // read only once from website to acoid traffic
            link?.let { l -> URI(l).toURL().readText() }?.let { rawHtml ->
                this.rawHtml = rawHtml
                // try to avoid repeating the summary (extraction heuristics are not perfect...)
                var html = Essence.extract(rawHtml).html
                summary?.let { s ->
                    if(html.contains(s)) {
                        html = html.replace(s, "")
                    }
                }
                this.html = html

                this.applicationJson = Jsoup.parse(rawHtml)
                    .select("script[type=application/ld+json]")
                    .map { script -> jsonMapper.readValue(script.data(), AppJson::class.java) }
                audioItems = applicationJson
                    ?.filter { script -> script.type == "AudioObject" }
                    ?.map { ao -> ao.toMediaItem() }
                    ?:listOf()
                videoItems = applicationJson
                    ?.filter { script -> script.type == "VideoObject" }
                    ?.map { vo -> vo.toMediaItem() }
                    ?:listOf()
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