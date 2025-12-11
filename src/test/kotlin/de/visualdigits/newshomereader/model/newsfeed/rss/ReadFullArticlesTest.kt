package de.visualdigits.newshomereader.model.newsfeed.rss

import de.visualdigits.newshomereader.model.newsfeed.applicationjson.AppJson
import de.visualdigits.newshomereader.service.cache.NewsItemCache
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsFeed
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsItem.Companion.jsonMapper
import io.github.cdimascio.essence.Essence
import org.jsoup.Jsoup
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.io.File
import java.net.URI

@Disabled("Only for local testing")
@SpringBootTest
@ActiveProfiles("test")
class ReadFullArticlesTest @Autowired constructor(
    private val newsItemCache: NewsItemCache
) {

    @Test
    fun readTagesschau() {
//        val newsFeed = NewsFeed.readValue(newsItemCache, "Tagesschau", URI("https://www.tagesschau.de/infoservices/alle-meldungen-100~rss2.xml"))
        val newsFeed = NewsFeed.readValue(newsItemCache, "Tagesschau", File(ClassLoader.getSystemResource("rdf/tagesschau2a.xml").toURI()))
        println(newsFeed.items.joinToString("\n============================\n") { ni ->
            println("### ${ni.identifier}")
            ni.readFullArticle("/News/Welt/Tagesschau")
            ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() }?:""
        })
    }

    @Test
    fun readTagesschau2() {
        val rawHtml = File(ClassLoader.getSystemResource("rdf/tagesschau-story2-script.json").toURI()).readText()
        val applicationJson = Jsoup.parse(rawHtml)
            .select("script[type=application/ld+json]")
            .map { script ->
                println("#### $script")
                val appJson = jsonMapper.readValue(script.data(), AppJson::class.java)
                appJson.clazz = script.attr("class")
                appJson
            }
    }

    @Test
    fun readNtv() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "NTV", URI("https://www.n-tv.de/rss"))
        println(newsFeed.items.joinToString("\n============================\n") { ni ->
            ni.readFullArticle("/News/Welt/NTV")
            ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() }?:""
        })
    }

    @Test
    fun readNdr() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "NDR", URI("https://www.ndr.de/nachrichten/hamburg/index~rdf.xml"))
        println(newsFeed.items.joinToString("\n============================\n") { ni ->
            ni.readFullArticle("/News/Lokal/NDR")
            ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() }?:""
        })
    }

    @Test
    fun readWdr() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "WDR", URI("https://www1.wdr.de/nachrichten/ruhrgebiet/uebersicht-ruhrgebiet-100.feed"))
        println(newsFeed.items.joinToString("\n============================\n") { ni ->
            ni.readFullArticle("/News/Lokal/WDR")
            ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() }?:""
        })
    }

    @Test
    fun readHeise() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "Heise", URI("https://www.heise.de/rss/heise-atom.xml"))
        println(newsFeed.items.joinToString("\n============================\n") { ni ->
            ni.readFullArticle("/IT/Heise")
            ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() }?:""
        })
    }

    @Test
    fun readT3n() {
        val newsFeed = NewsFeed.readValue(newsItemCache, "t3n", URI("https://t3n.de/rss.xml"))
        newsFeed.items.forEach { ni ->
            ni.readFullArticle("IT/t3n")
            println(ni.applicationJson?.joinToString("\n----------------------------\n") { it.writeValueAsJsonString() })
        }
    }
}
