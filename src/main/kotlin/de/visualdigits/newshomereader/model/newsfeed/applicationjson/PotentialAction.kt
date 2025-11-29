package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class PotentialAction(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val name: String? = null,
    val target: List<Target> = listOf(),
    @field:JsonProperty("startOffset-input") val startOffsetInput: String? = null,
    @field:JsonProperty("query-input") val queryInput: QueryInput? = null
) : BaseNode<PotentialAction>()