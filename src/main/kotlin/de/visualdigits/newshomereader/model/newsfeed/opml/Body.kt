package de.visualdigits.newshomereader.model.newsfeed.opml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.newshomereader.model.page.Page


class Body(
    @field:JacksonXmlElementWrapper(useWrapping = false) @field:JacksonXmlProperty(localName = "outline") val outlines: List<Outline> = listOf(),
    val settings: Settings? = null
) : BaseNode<Body>() {

    fun toPage(): Page {
        return Page(name = "root", children = outlines.map { ol -> ol.toPage() })
    }
}