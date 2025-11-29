package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class Author(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val name: String? = null,
    val url: String? = null
) : BaseNode<Author>() {

    constructor(name: String? = null): this(id = null, name = name)
}

