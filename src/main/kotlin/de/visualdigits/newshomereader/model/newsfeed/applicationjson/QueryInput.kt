package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class QueryInput(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val valueRequired: Boolean? = null,
    val valueName: String? = null,
    val inLanguage: String? = null,
    val url: String? = null,
    val contentUrl: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val caption: String? = null
) : BaseNode<QueryInput>()