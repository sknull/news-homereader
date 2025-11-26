package de.visualdigits.newshomereader.model.newsfeed.atom


import de.visualdigits.hybridxml.model.BaseNode

class Link(
    val rel: String? = null,
    val type: String? = null,
    val href: String? = null
) : BaseNode<Link>()