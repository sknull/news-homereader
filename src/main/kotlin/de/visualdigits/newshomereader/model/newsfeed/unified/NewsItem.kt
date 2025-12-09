package de.visualdigits.newshomereader.model.newsfeed.unified

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.newshomereader.service.cache.ImageProxy
import de.visualdigits.newshomereader.model.cache.NewsItemCacheKey
import de.visualdigits.newshomereader.model.clientdata.ClientData
import de.visualdigits.newshomereader.model.newsfeed.applicationjson.AppJson
import de.visualdigits.newshomereader.model.newsfeed.applicationjson.OffsetDateTimeHeuristicDeserializer
import io.github.cdimascio.essence.Essence
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.net.URI
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Component
@JsonIgnoreProperties("hashCode", "rawHtml", "html", "videoItems", "audioItems", "articleImage")
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
    var imageCaption: String? = null,

    var applicationJson: List<AppJson>? = null,
) : BaseNode<NewsItem>() {

    val newsItemHashCode: UInt = "$feedName$identifier".hashCode().toUInt()

    var rawHtml: String? = null // as fetched from URL
    var html: String? = null // contains only main article markup
    var videoItems: List<MediaItem> = listOf()
    var audioItems: List<MediaItem> = listOf()
    var articleImage: String? = null
    var discussionUrl: String? = null
    var commentCount: Int? = null
    var isFree: Boolean = true

    companion object {
        val jsonMapper: JsonMapper = jacksonMapperBuilder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ISODate
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, JsonInclude.Include.NON_EMPTY))
            .addModule(JavaTimeModule().addDeserializer(OffsetDateTime::class.java, OffsetDateTimeHeuristicDeserializer()))
            .build()
    }

    fun cacheKey(): NewsItemCacheKey = NewsItemCacheKey(newsItemHashCode, updated)

    fun toModel(
        imageProxy: ImageProxy,
        isArticle: Boolean,
        clientData: ClientData,
        path: String? = null
    ): NewsItemRendered {
        if (isArticle) readFullArticle()

        val itemClazz = if (isArticle) "article" else "item"
        val read = clientData.readItems.contains(newsItemHashCode)
        val readClazz = if (read) " read" else ""
        val hideClazz = if (read && clientData.hideRead) " hide" else ""

        return NewsItemRendered(
            itemClass = "news-$itemClazz$readClazz$hideClazz",
            feedName = feedName,
            title = title,
            path = "/news/$path?hashCode=$newsItemHashCode&",
            updated = updated?.format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")),
            isFree = isFree,
            imageTitle = imageTitle,
            imageCaption = imageCaption,
            imageUrl = (if (isArticle) articleImage?:image else image)?.let { img -> imageProxy.getImage(newsItemHashCode, img) },
            audioUrl = audioItems.firstOrNull()?.url,
            videoUrl = videoItems.firstOrNull()?.url,
            discussionUrl = discussionUrl,
            commentCount = commentCount,
            summary = summary,
            html = html
        )
    }

    fun readFullArticle() {
        if (html == null) {
            // read only once from website to acoid traffic
            link?.let { l -> URI(l).toURL().readText() }?.let { rawHtml ->
                this.rawHtml = rawHtml

                // extract main text from raw html using essence's heuristics
                var html = Essence.extract(rawHtml).html

                // if image caption just contains the summary we null it out
                imageCaption?.let { ic ->
                    if(html.contains(ic)) {
                        imageCaption = null
                    }
                }

                // try to avoid repeating the summary (extraction heuristics are not perfect...)
                summary?.let { s ->
                    if(html.contains(s)) {
                        html = html.replace(s, "")
                    }
                }

                this.html = html

                this.applicationJson = Jsoup.parse(rawHtml)
                    .select("script[type=application/ld+json]")
                    .map { script ->
                        val appJson = jsonMapper.readValue(script.data(), AppJson::class.java)
                        appJson.clazz = script.attr("class")
                        appJson
                    }

                val newsArticle = applicationJson
                    ?.find { script -> script.type == "NewsArticle" }
                    ?:applicationJson
                        ?.filter { script -> script.graphs.isNotEmpty() }
                        ?.map { script -> script.graphs.find { g -> g.type == "NewsArticle" } }
                        ?.firstOrNull()

                isFree = newsArticle?.isAccessibleForFree?:true

                articleImage = newsArticle
                    ?.images
                    ?.maxBy { image -> image.width?:0 }
                    ?.url

                var discussionUrl = newsArticle?.discussionUrl
                if (discussionUrl?.startsWith("/") == true) { // make relative url absolute
                    link?.let { l ->
                        val uri = URI(l)
                        discussionUrl = "${uri.scheme}://${uri.host}$discussionUrl"
                    }
                }
                this.discussionUrl = discussionUrl
                this.commentCount = newsArticle?.commentCount?:0

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
        if (updated?.toInstant()?.toEpochMilli() != other.updated?.toInstant()?.toEpochMilli()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = feedName?.hashCode() ?: 0
        result = 31 * result + (identifier?.hashCode() ?: 0)
        result = 31 * result + (updated?.toInstant()?.toEpochMilli()?.hashCode() ?: 0)
        return result
    }
}