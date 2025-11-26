package de.visualdigits.newshomereader.model.newsfeed.rss

import de.visualdigits.hybridxml.model.BaseNode
import de.visualdigits.hybridxml.model.polymorphic.PolymorphicNode

class HtmlContent(
    var html: PolymorphicNode<*>? = null
) : BaseNode<HtmlContent>()
