package de.visualdigits.newshomereader.service.cache

import de.visualdigits.newshomereader.model.cache.NewsItemCacheKey
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

        getNewsItem(newsItem.newsItemHashCode)
            ?.also { oldItem ->
                val cacheKey = oldItem.cacheKey()
//                readItemsService.remove(cacheKey.newsItemHashCode)
                itemCache.remove(cacheKey)
            }

        itemCache.putIfAbsent(newKey, newsItem)

        // remove old items exceeding max item number
        if (itemCache.size > newsHomeReader.maxItemsInCache) {
            itemCache.keys
                .sortedBy { key -> key.updated }
                .dropLast(newsHomeReader.maxItemsInCache)
                .forEach { key -> itemCache.remove((key)) }
        }

        return itemCache[newKey] ?: error("Item with key '$newKey' was not put ot the cache")
    }
}