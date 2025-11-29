package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class About(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val name: String? = null,
    val sameAs: String? = null
) : BaseNode<About>()