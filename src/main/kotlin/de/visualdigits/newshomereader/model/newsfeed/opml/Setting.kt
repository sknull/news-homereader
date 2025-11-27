package de.visualdigits.newshomereader.model.newsfeed.opml

import de.visualdigits.hybridxml.model.BaseNode


class Setting(
    val key: String? = null,
    val value: String? = null
) : BaseNode<Setting>()