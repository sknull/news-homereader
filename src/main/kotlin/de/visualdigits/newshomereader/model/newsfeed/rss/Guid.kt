package de.visualdigits.newshomereader.model.newsfeed.rss

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.hybridxml.model.hybrid.HybridTextNode

class Guid(
    @field:JacksonXmlProperty(isAttribute = true) val isPermaLink: Boolean? = null
) : HybridTextNode()
