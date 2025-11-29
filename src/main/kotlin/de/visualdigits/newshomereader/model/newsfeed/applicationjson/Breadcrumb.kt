package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class Breadcrumb(
    @field:JsonProperty("@context") val context: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val itemListElement: List<ItemElement> = listOf()
) : BaseNode<Breadcrumb>()