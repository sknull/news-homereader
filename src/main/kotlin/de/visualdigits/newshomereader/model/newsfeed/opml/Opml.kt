package de.visualdigits.newshomereader.model.newsfeed.opml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.BaseNode


class Opml(
    @field:JacksonXmlProperty(isAttribute = true) val version: String? = null,
    val head: Head? = null,
    val body: Body? = null
) : BaseNode<Opml>()