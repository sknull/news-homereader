package de.visualdigits.newshomereader.model.newsfeed.applicationjson


import com.fasterxml.jackson.annotation.JsonProperty
import de.visualdigits.hybridxml.model.BaseNode

class Item(
    @field:JsonProperty("@id") val id: String? = null,
    @field:JsonProperty("@type") val type: String? = null,
    val additionalType: String? = null,
    val abstract: String? = null,
    val branding: String? = null,
    val containerId: String? = null,
    val headline: String? = null,
    val image: Image? = null,
    val isAccessibleForFree: Boolean? = null,
    val isAlert: Boolean? = null,
    val isFamilyFriendly: Boolean? = null,
    val isLive: Boolean? = null,
    val isUpdate: Boolean? = null,
    val kicker: String? = null,
    val name: String? = null,
    val sourceOrganization: SourceOrganization? = null,
    val url: String? = null
) : BaseNode<Item>() {

    constructor(url: String? = null): this(id = null, url = url)
}