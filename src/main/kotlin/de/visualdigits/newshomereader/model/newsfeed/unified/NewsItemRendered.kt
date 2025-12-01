package de.visualdigits.newshomereader.model.newsfeed.unified

class NewsItemRendered(
    val hasImage: Boolean,
    val hasAudio: Boolean,
    val hasVideo: Boolean,

    val itemClass: String,
    val feedName: String?,
    val title: String?,
    val path: String,
    val updated: String?,

    val imageTitle: String?,
    val imageCaption: String?,
    val imageUrl: String?,

    val audioUrl: String?,
    val videoUrl: String?,

    val summary: String?,
    val html: String?
)