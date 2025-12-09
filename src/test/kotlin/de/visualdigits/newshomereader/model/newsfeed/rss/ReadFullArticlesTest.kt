package de.visualdigits.newshomereader.model.newsfeed.rss

import de.visualdigits.newshomereader.service.cache.NewsItemCache
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsFeed
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.net.URI

@SpringBootTest
@ActiveProfiles("test")
class ReadFullArticlesTest @Autowired constructor(
    private val newsItemCache: NewsItemCache
) {

    @Test
    fun readTagesschau() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "Tagesschau", URI("https://www.tagesschau.de/infoservices/alle-meldungen-100~rss2.xml"))
        println(newsFeed.items.joinToString("\n============================\n") { ni ->
            ni.readFullArticle()
            ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() }?:""
        })
    }

    @Test
    fun readNtv() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "NTV", URI("https://www.n-tv.de/rss"))
        println(newsFeed.items.joinToString("\n============================\n") { ni ->
            ni.readFullArticle()
            ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() }?:""
        })
    }

    @Test
    fun readNdr() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "NDR", URI("https://www.ndr.de/nachrichten/hamburg/index~rdf.xml"))
        println(newsFeed.items.joinToString("\n============================\n") { ni ->
            ni.readFullArticle()
            ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() }?:""
        })
    }

    @Test
    fun readWdr() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "WDR", URI("https://www1.wdr.de/nachrichten/ruhrgebiet/uebersicht-ruhrgebiet-100.feed"))
        println(newsFeed.items.joinToString("\n============================\n") { ni ->
            ni.readFullArticle()
            ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() }?:""
        })
    }

    @Test
    fun readHeise() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "Heise", URI("https://www.heise.de/rss/heise-atom.xml"))
        println(newsFeed.items.joinToString("\n============================\n") { ni ->
            ni.readFullArticle()
            ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() }?:""
        })
    }

    @Test
    fun readT3n() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "t3n", URI("https://t3n.de/rss.xml"))
        newsFeed.items.forEach { ni ->
            ni.readFullArticle()
            println(ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() })
        }
    }
}
