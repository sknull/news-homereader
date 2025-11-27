package de.visualdigits.newshomereader.model.newsfeed.opml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.BaseNode


@JsonIgnoreProperties("settingsMap", "blockedList")
class Settings(
    @field:JacksonXmlElementWrapper(useWrapping = false) @field:JacksonXmlProperty(localName = "setting") val settings: List<Setting> = listOf(),
    @field:JacksonXmlElementWrapper(useWrapping = false) @field:JacksonXmlProperty(localName = "blocked") val blocked: List<Blocked> = listOf()
) : BaseNode<Settings>() {

    val settingsMap: Map<String, String> = settings.associate { setting -> Pair(setting.key!!, setting.value!!) }
    val blockedList: List<String> = blocked.map { blocked -> blocked.pattern!! }
}