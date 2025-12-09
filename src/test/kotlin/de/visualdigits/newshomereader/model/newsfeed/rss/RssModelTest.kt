package de.visualdigits.newshomereader.model.newsfeed.rss

import de.visualdigits.newshomereader.service.cache.NewsItemCache
import de.visualdigits.newshomereader.model.newsfeed.unified.NewsFeed
import io.github.cdimascio.essence.Essence
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.util.zip.GZIPInputStream

@SpringBootTest
@ActiveProfiles("test")
class RssModelTest @Autowired constructor(
    private val newsItemCache: NewsItemCache
)  {

    fun URI.get(): ByteArray {
        val connection = createConnection()
        val response =
            (if (connection.contentEncoding == "gzip") GZIPInputStream(connection.inputStream) else connection.inputStream)
                .use { ins ->
                    ins.readAllBytes()
                }
        return response
    }

    private fun URI.createConnection(): HttpURLConnection {
        val connection = (toURL().openConnection() as HttpURLConnection)
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
//        mapOf(
//
//        ).forEach { (key, value) -> connection.setRequestProperty(key, value) }
        return connection
    }

    @Test
    fun readTagesschau() {
//        val newsFeed = NewsFeed.readValue(URI("https://www.tagesschau.de/infoservices/alle-meldungen-100~rss2.xml"))
        val newsFeed = NewsFeed.readValue(newsItemCache, "Tagesschau", File(ClassLoader.getSystemResource("rdf/tagesschau.xml").toURI()))
        val expected = File(ClassLoader.getSystemResource("rdf/tagesschau_expected-json.txt").toURI()).readText()
        val actual = newsFeed.writeValueAsJsonString()
        assertEquals(expected, actual)
    }

    @Test
    fun readNtv() {
//        val newsFeed = NewsFeed.readValue(URI("https://www.n-tv.de/rss"))
        val newsFeed = NewsFeed.readValue(newsItemCache, "NTV", File(ClassLoader.getSystemResource("rdf/ntv.xml").toURI()))
        val expected = File(ClassLoader.getSystemResource("rdf/ntv_expected-json.txt").toURI()).readText()
        val actual = newsFeed.writeValueAsJsonString()
        assertEquals(expected, actual)
    }

    @Test
    fun readNdr() {
//        val newsFeed = NewsFeed.readValue(URI("https://www.ndr.de/nachrichten/hamburg/index~rdf.xml"))
        val newsFeed = NewsFeed.readValue(newsItemCache, "NDR", File(ClassLoader.getSystemResource("rdf/ndr.xml").toURI()))
        val expected = File(ClassLoader.getSystemResource("rdf/ndr_expected-json.txt").toURI()).readText()
        val actual = newsFeed.writeValueAsJsonString()
        assertEquals(expected, actual)
    }

    @Test
    fun readWdr() {
//        val newsFeed = NewsFeed.readValue(URI("https://www1.wdr.de/nachrichten/ruhrgebiet/uebersicht-ruhrgebiet-100.feed"))
        val newsFeed = NewsFeed.readValue(newsItemCache, "WDR", File(ClassLoader.getSystemResource("rdf/wdr.xml").toURI()))
        val expected = File(ClassLoader.getSystemResource("rdf/wdr_expected-json.txt").toURI()).readText()
        val actual = newsFeed.writeValueAsJsonString()
        assertEquals(expected, actual)
    }

    @Test
    fun readHeise() {
//        val newsFeed = NewsFeed.readValue(URI("https://www.heise.de/rss/heise-atom.xml"))
        val newsFeed = NewsFeed.readValue(newsItemCache, "Heise", File(ClassLoader.getSystemResource("rdf/heise.xml").toURI()))
        val expected = File(ClassLoader.getSystemResource("rdf/heise_expected-json.txt").toURI()).readText()
        val actual = newsFeed.writeValueAsJsonString()
        assertEquals(expected, actual)
    }

    @Test
    fun scrapeHeise() {
//        val newsFeed = NewsFeed.readValue(URI("view-source:https://www.heise.de/news/Proaktive-IT-Security-mit-Pentesting-Ethical-Hacking-fuer-Admins-11070849.html?wt_mc=rss.red.ho.ho.atom.beitrag.beitrag"))
        val html = File(ClassLoader.getSystemResource("rdf/heise-story.html").toURI()).readText()
        val expected = File(ClassLoader.getSystemResource("rdf/heise_expected-html.txt").toURI()).readText()
        val actual = Essence.extract(html).html
        assertEquals(expected, actual)
    }

    @Test
    fun readT3n() {
//        val newsFeed = NewsFeed.readValue(URI("https://t3n.de/rss.xml"))
        val newsFeed = NewsFeed.readValue(newsItemCache, "t3n", File(ClassLoader.getSystemResource("rdf/t3n.xml").toURI()))
        val expected = File(ClassLoader.getSystemResource("rdf/t3n_expected-json.txt").toURI()).readText()
        val actual = newsFeed.writeValueAsJsonString()
        assertEquals(expected, actual)
    }
}
