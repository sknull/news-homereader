package de.visualdigits.newshomereader.model.newsfeed.unified

import java.time.OffsetDateTime

class MediaItem(
    val url: String? = null,
    val headline: String? = null,
    val description: String? = null,
    val datePublished: OffsetDateTime? = null,
    val dateModified: OffsetDateTime? = null,
    val uploadDate: OffsetDateTime? = null,
    val expires: OffsetDateTime? = null,
    val keywords: List<String> = listOf(),
    val thumbnails: List<ThumbnailItem> = listOf()
)