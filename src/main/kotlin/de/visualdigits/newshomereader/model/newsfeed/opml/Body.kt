package de.visualdigits.newshomereader.model.newsfeed.opml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.BaseNode


class Body(
    @field:JacksonXmlElementWrapper(useWrapping = false) @field:JacksonXmlProperty(localName = "outline") val outlines: List<Outlines> = listOf(),
    val settings: Settings? = null
) : BaseNode<Body>()