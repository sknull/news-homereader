package de.visualdigits.newshomereader.model.newsfeed.unified

import java.time.OffsetDateTime

class ThumbnailItem(
    val url: String? = null,
    val description: String? = null,
    val author: String? = null,
    val datePublished: OffsetDateTime? = null,
    val width: Int? = null,
    val height: Int? = null
)