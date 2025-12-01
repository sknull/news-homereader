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
    @field:JsonProperty("@graph") val graphs: List<Graph> = listOf(),
    val about: List<About> = listOf(),
    val additionalType: String? = null,
    val alternativeHeadline: String? = null,
    val alternateName: String? = null,
    val articleBody: String? = null,
    val articleSection: String? = null,
    val author: List<List<Author>> = listOf(),
    val commentCount: String? = null,
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
    val keywords: List<String> = listOf(),
//    val mainEntityOfPage: MainEntityOfPage? = null,
    val mainEntityOfPage: Any? = null,
    val name: String? = null,
    val potentialAction: PotentialAction? = null,
    val provider: String? = null,
    val publisher: Publisher? = null,
    val sourceOrganization: SourceOrganization? = null,
    val thumbnail: List<Thumbnail> = listOf(),
    val thumbnailUrl: List<String> = listOf(),
    val timeRequired: String? = null,
    val transcript: String? = null,
    val uploadDate: OffsetDateTime? = null,
    val url: String? = null,
    val version: String? = null,
    val wordCount: Int? = null
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