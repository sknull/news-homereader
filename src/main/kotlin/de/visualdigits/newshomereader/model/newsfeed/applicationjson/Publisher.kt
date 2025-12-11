package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class Publisher(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val name: String? = null,
    val url: String? = null,
    val alternateName: String? = null,
    val correctionsPolicy: String? = null,
    val diversityPolicy: String? = null,
    val sameAs: List<String> = listOf(),
    val logo: Logo? = null
) : BaseNode<Publisher>()