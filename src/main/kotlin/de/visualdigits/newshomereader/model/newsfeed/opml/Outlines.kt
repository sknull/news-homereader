package de.visualdigits.newshomereader.model.newsfeed.opml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.BaseNode


class Outlines(
    @field:JacksonXmlProperty(isAttribute = true) val title: String? = null,
    @field:JacksonXmlProperty(isAttribute = true) val text: String? = null,
    @field:JacksonXmlElementWrapper(useWrapping = false) @field:JacksonXmlProperty(localName = "outline") val outlines: List<Outline> = listOf()
) : BaseNode<Outlines>()
