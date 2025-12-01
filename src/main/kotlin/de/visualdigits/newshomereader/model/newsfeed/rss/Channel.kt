package de.visualdigits.newshomereader.model.newsfeed.rss

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import de.visualdigits.hybridxml.model.BaseNode
import java.time.OffsetDateTime

class Channel(
    val version: String? = null,

    val title: String? = null,
    val link: String? = null,
    val description: String? = null,

    val source: String? = null,
    val publisher: String? = null,
    val rights: String? = null,
    val date: OffsetDateTime? = null,
    val updatePeriod: String? = null,
    val updateFrequency: String? = null,
    val updateBase: OffsetDateTime? = null,
    val broadcasting: String? = null,

    val image: Image? = null,
    val language: String? = null,
    val copyright: String? = null,
    val lastBuildDate: OffsetDateTime? = null,
    val pubDate: OffsetDateTime? = null,
    val docs: String? = null,
    val ttl: Int? = null,
    val itemRefs: List<String> = listOf(),
    @field:JacksonXmlElementWrapper(useWrapping = false) @field:JsonProperty("item") val items: List<Item>? = null
) : BaseNode<Channel>()
