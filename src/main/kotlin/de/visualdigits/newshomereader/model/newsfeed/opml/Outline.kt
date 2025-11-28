package de.visualdigits.newshomereader.model.newsfeed.opml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.newshomereader.model.page.Page


class Outline(
    @field:JacksonXmlProperty(isAttribute = true) val title: String? = null,
    @field:JacksonXmlProperty(isAttribute = true) val text: String? = null,
    @field:JacksonXmlProperty(isAttribute = true) val type: String? = null,

    @field:JacksonXmlProperty(isAttribute = true) val notify: String? = null,
    @field:JacksonXmlProperty(isAttribute = true) val imageUrl: String? = null,
    @field:JacksonXmlProperty(isAttribute = true) val fullTextByDefault: String? = null,
    @field:JacksonXmlProperty(isAttribute = true) val openArticlesWith: String? = null,
    @field:JacksonXmlProperty(isAttribute = true) val alternateId: String? = null,
    @field:JacksonXmlProperty(isAttribute = true) val xmlUrl: String? = null,

    @field:JacksonXmlElementWrapper(useWrapping = false) @field:JacksonXmlProperty(localName = "outline") val childoutlines: List<Outline> = listOf()
) : BaseNode<Outline>() {

    fun toPage(): Page {
        return Page(
            name = title?:error("No title"),
            children = childoutlines.map { co -> co.toPage() },
            url = xmlUrl
        )
    }
}
