package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode
import java.time.OffsetDateTime

class Thumbnail(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val url: String? = null,
    val author: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val datePublished: OffsetDateTime? = null,
    val description: String? = null
) : BaseNode<Thumbnail>()