package de.visualdigits.newshomereader.model.newsfeed.rss

import de.visualdigits.hybridxml.model.BaseNode

class Comment(
    val submitted: String? = null,
    val title: String? = null,
    val content: String? = null
) : BaseNode<Comment>()
