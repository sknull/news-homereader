package de.visualdigits.newshomereader.model.cache.newsitem

import de.visualdigits.newshomereader.model.configuration.NewsHomeReader
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsItem
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class NewsItemCache(
    private val newsHomeReader: NewsHomeReader
) {

    private val itemCache: MutableMap<NewsItemCacheKey, NewsItem> = mutableMapOf()

    fun getNewsItem(
        hashCode: UInt,
        updated: OffsetDateTime? = null
    ): NewsItem? {
        return updated
            ?.let { u -> itemCache[NewsItemCacheKey(hashCode, u)] }
            ?:let { itemCache.keys // case when updated is unknown and therefore null
                .find { existingKey -> existingKey.newsItemHashCode == hashCode }
                ?.let { existingKey -> itemCache[existingKey] }
            }
    }

    fun getNewsItemHashCodes(): Set<UInt> = itemCache.keys.map { k -> k.newsItemHashCode }.toSet()

    fun cacheNewsItem(newsItem: NewsItem): NewsItem {
        val newKey = newsItem.cacheKey()
        itemCache.putIfAbsent(newKey, newsItem)
        cleanupCache()
        return itemCache[newKey] ?: error("Item with key '$newKey' was not put ot the cache")
    }

    /**
     * Cleans up the cache:
     * - removes all older new items where a newer one exists
     * - uses the given newsFeed (if any) to get rid of entries in the cache which are now longf
     */
    fun cleanupCache() {
        // remove older duplicates
        itemCache.keys
            .groupBy { key -> key.newsItemHashCode }
            .flatMap { (_, keys) -> keys.sortedBy { key -> key.updated?.toInstant()?.toEpochMilli() ?: 0 }.dropLast(1) }
            .forEach { key -> itemCache.remove(key) }

        // remove old items exceeding max item number
        if (itemCache.size > newsHomeReader.maxItemsInCache) {
            itemCache.keys
                .sortedBy { key -> key.updated }
                .dropLast(newsHomeReader.maxItemsInCache)
                .forEach { key -> itemCache.remove((key)) }
        }
    }
}