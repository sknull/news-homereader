package de.visualdigits.newshomereader.model.newsfeed.applicationjson

import com.fasterxml.jackson.annotation.JsonProperty

class HasPart(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val name: String? = null,
    val startOffset: Double? = null,
    val endOffset: Double? = null,
    val url: String? = null
)