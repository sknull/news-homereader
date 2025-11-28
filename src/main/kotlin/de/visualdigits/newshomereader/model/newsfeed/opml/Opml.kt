package de.visualdigits.newshomereader.model.newsfeed.opml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.newshomereader.model.configuration.NewsFeedsConfiguration


@JacksonXmlRootElement
class Opml(
    @field:JacksonXmlProperty(isAttribute = true) val version: String? = null,
    val head: Head? = null,
    val body: Body? = null
) : BaseNode<Opml>() {

    fun toNewsFeedsConfiguration(): NewsFeedsConfiguration {
        return NewsFeedsConfiguration(
            naviMain = (body?:error("No body")).toPage()
        )
    }
}