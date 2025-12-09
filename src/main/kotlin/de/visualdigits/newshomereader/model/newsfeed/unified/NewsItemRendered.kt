package de.visualdigits.newshomereader.model.newsfeed.unified

class NewsItemRendered(
    val itemClass: String,
    val feedName: String?,
    val title: String?,
    val path: String,
    val updated: String?,
    val isFree: Boolean,

    val imageTitle: String?,
    val imageCaption: String?,
    val imageUrl: String?,

    val audioUrl: String?,
    val videoUrl: String?,

    val discussionUrl: String?,
    val commentCount: Int?,

    val summary: String?,
    val html: String?

)