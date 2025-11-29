package de.visualdigits.newshomereader.model.newsfeed.applicationjson

import com.fasterxml.jackson.annotation.JsonProperty

class HasPart(
    @JsonProperty("@id") val id: String? = null,
    @JsonProperty("@type") val type: String? = null,
    val name: String? = null,
    val startOffset: Double? = null,
    val endOffset: Double? = null,
    val url: String? = null
)