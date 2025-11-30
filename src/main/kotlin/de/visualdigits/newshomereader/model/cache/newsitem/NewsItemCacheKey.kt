package de.visualdigits.newshomereader.model.cache.newsitem

import java.time.OffsetDateTime

data class NewsItemCacheKey(
    val newsItemHashCode: UInt,
    val updated: OffsetDateTime? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsItemCacheKey

        if (newsItemHashCode != other.newsItemHashCode) return false
        if (updated?.toInstant()?.toEpochMilli() != other.updated?.toInstant()?.toEpochMilli()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = newsItemHashCode.toInt()
        result = 31 * result + (updated?.toInstant()?.toEpochMilli()?.hashCode()?:0)
        return result
    }
}