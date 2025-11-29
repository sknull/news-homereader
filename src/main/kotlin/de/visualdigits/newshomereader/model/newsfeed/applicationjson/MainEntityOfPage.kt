package de.visualdigits.newshomereader.model.newsfeed.applicationjson

import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class MainEntityOfPage(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val url: String? = null
) : BaseNode<MainEntityOfPage>() {

    constructor(url: String? = null): this(id = null, url = url)
}