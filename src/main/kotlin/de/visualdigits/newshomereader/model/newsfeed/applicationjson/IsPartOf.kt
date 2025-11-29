package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class IsPartOf(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: List<String> = listOf(),
    val name: String? = null,
    val productID: String? = null,
) : BaseNode<IsPartOf>()