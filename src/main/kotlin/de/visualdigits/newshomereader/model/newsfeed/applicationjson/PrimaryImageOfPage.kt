package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class PrimaryImageOfPage(
    @field:JsonProperty("@id") val id: String? = null
) : BaseNode<PrimaryImageOfPage>()