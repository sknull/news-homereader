package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class CopyrightHolder(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
val name: String? = null
) : BaseNode<CopyrightHolder>()