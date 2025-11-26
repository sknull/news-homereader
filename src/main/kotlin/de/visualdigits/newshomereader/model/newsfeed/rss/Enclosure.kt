package de.visualdigits.newshomereader.model.newsfeed.rss

import de.visualdigits.hybridxml.model.BaseNode

class Enclosure(
    val `type`: String? = null,
    val length: Int? = null,
    val url: String? = null
) : BaseNode<Enclosure>()
