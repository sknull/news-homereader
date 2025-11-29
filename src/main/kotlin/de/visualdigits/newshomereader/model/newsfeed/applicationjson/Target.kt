package de.visualdigits.newshomereader.model.newsfeed.applicationjson

import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class Target(
    @field:JsonProperty("@type") val type: String? = null,
    @field:JsonProperty("urlTemplate") val urlTemplate: String? = null
): BaseNode<Target>() {

    constructor(
        urlTemplate: String? = null
    ): this(null, urlTemplate)
}