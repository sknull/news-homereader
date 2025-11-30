package de.visualdigits.newshomereader.model.cache.images

import java.time.OffsetDateTime

class ImageInfo(
    val newItemHashCode: UInt,
    val uri: String,
    val downloaded: OffsetDateTime,
    val path: String?,
    val extension: String?,
    val available: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageInfo

        return uri == other.uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }
}