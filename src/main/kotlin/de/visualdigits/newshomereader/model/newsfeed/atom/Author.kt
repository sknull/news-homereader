package de.visualdigits.newshomereader.model.newsfeed.atom


import de.visualdigits.hybridxml.model.BaseNode

class Author(
    val name: String? = null,
    val uri: String? = null
) : BaseNode<Author>()