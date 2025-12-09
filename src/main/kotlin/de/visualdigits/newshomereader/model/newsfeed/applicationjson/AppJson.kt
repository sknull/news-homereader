package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.newshomereader.model.newsfeed.unified.ThumbnailItem
import de.visualdigits.newshomereader.model.newsfeed.unified.MediaItem
import java.time.OffsetDateTime

class AppJson(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    @field:JsonProperty("@context") val context: String? = null,
    @field:JsonProperty("@graph") val graphs: List<AppJson> = listOf(),
    @field:JsonProperty("class") var clazz: String? = null,
    val about: List<About> = listOf(),
    val additionalType: String? = null,
    val alternateName: String? = null,
    val alternativeHeadline: String? = null,
    val articleBody: String? = null,
    val articleSection: String? = null,
    val author: List<List<Author>> = listOf(),
    val caption: String? = null,
    val commentCount: Int? = null,
    val contentUrl: String? = null,
    val copyrightHolder: CopyrightHolder? = null,
    val copyrightYear: String? = null,
    val dateModified: OffsetDateTime? = null,
    val datePublished: OffsetDateTime? = null,
    val description: String? = null,
    val discussionUrl: String? = null,
    val duration: String? = null,
    val expires: OffsetDateTime? = null,
    val hasPart: List<HasPart> = listOf(),
    val headline: String? = null,
    val identifier: String? = null,
    @field:JsonProperty("image") val images: List<Image> = listOf(),
    val inLanguage: String? = null,
    val isAccessibleForFree: Boolean? = null,
    val isFamilyFriendly: Boolean? = null,
    val isPartOf: IsPartOf? = null,
    val itemListElement: List<ItemElement> = listOf(),
    val jobTitle: String? = null,
    val keywords: List<String> = listOf(),
    val logo: Logo? = null,
    val mainEntityOfPage: Any? = null,
    val name: String? = null,
    @field:JsonProperty("potentialAction") val potentialActions: List<PotentialAction> = listOf(),
    val primaryImageOfPage: Image? = null,
    val provider: String? = null,
    val publisher: Publisher? = null,
    val relatedLink: List<String> = listOf(),
    val sourceOrganization: SourceOrganization? = null,
    val thumbnail: List<Thumbnail> = listOf(),
    val thumbnailUrl: List<String> = listOf(),
    val timeRequired: String? = null,
    val transcript: String? = null,
    val uploadDate: OffsetDateTime? = null,
    val url: String? = null,
    val version: String? = null,
    val width: Int? = null,
    val wordCount: Int? = null,
) : BaseNode<AppJson>() {
    
    fun toMediaItem(): MediaItem {
        return MediaItem(
            url = contentUrl?:url,
            headline = headline,
            description = description,
            datePublished = datePublished,
            dateModified = dateModified,
            uploadDate = uploadDate,
            expires = expires,
            keywords = keywords,
            thumbnails = images.map { io ->
                ThumbnailItem(
                    url = io.contentUrl?:io.url,
                    description = io.description,
                    author = io.author,
                    datePublished = io.datePublished,
                    width = io.width,
                    height = io.height
                )
            }
        )
    }
}