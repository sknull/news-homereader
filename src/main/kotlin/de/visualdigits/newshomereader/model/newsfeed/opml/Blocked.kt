package de.visualdigits.newshomereader.model.newsfeed.opml

import de.visualdigits.hybridxml.model.BaseNode


class Blocked(
    val pattern: String? = null
) : BaseNode<Blocked>()