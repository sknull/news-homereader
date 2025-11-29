package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode
import java.time.OffsetDateTime

class Graph(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val isPartOf: IsPartOf? = null,
    val author: Author? = null,
    val headline: String? = null,
    val datePublished: OffsetDateTime? = null,
    val mainEntityOfPage: MainEntityOfPage? = null,
    val wordCount: Int? = null,
    val commentCount: Int? = null,
    val publisher: Publisher? = null,
    val image: Image? = null,
    val thumbnailUrl: String? = null,
    val articleSection: String? = null,
    val inLanguage: String? = null,
    val potentialAction: List<PotentialAction> = listOf(),
    val copyrightYear: String? = null,
    val copyrightHolder: CopyrightHolder? = null,
    val articleBody: String? = null,
    val isAccessibleForFree: Boolean? = null,
    val about: List<About> = listOf(),
    val description: String? = null,
    val timeRequired: String? = null,
    val url: String? = null,
    val name: String? = null,
    val primaryImageOfPage: PrimaryImageOfPage? = null,
    val relatedLink: List<String> = listOf(),
    val contentUrl: String? = null,
    val width: Int? = null,
    val caption: String? = null,
    val logo: Logo? = null,
    val jobTitle: String? = null
) : BaseNode<Graph>()