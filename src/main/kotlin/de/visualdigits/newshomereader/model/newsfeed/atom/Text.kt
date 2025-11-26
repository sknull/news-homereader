package de.visualdigits.newshomereader.model.newsfeed.atom


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import de.visualdigits.hybridxml.model.BaseNode

class Text(
    @JacksonXmlProperty(isAttribute = true) val type: String? = null,
) : BaseNode<Text>() {

    @JacksonXmlText
    var text: String? = null
        set(value) {
            field = value?.trim()
        }
}
