package de.visualdigits.newshomereader.model.newsfeed.rss

import de.visualdigits.hybridxml.model.BaseNode
import io.github.cdimascio.essence.Essence
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.GZIPInputStream


class ScratchTest {

    @Test
    fun crawlTest() {
        val document = Jsoup.parse(URI("https://www.ndr.de/kultur/musik/klassik/entwurf-der-bjarke-ingels-group-gewinnt-wettbewerb-fuer-kuehne-oper,opernhaushamburg-102.html").toURL(), 5000)
        println(document.body().wholeText())
    }

    @Test
    fun extractTextTest() {
        val html = URI("https://t3n.de/news/renten-rechenzentren-zukunft-regierung-1716889/?utm_source=rss&utm_medium=newsFeed&utm_campaign=newsFeed").toURL().readText()
//        val html = File(ClassLoader.getSystemResource("rdf/t3n-item.html").toURI()).readText()
        val result = Essence.extract(html)
        println(result)
    }

    @Test
    fun readXmlTest() {
        val rss = BaseNode.readValue<Rss>(File(ClassLoader.getSystemResource("rdf/heise.xml").toURI()))
        val actual = rss.writeValueAsString()
        val expected = File(ClassLoader.getSystemResource("rdf/heise_expected.xml.txt").toURI()).readText()
        assertEquals(expected, actual)
    }

    @Test
    fun testParseDate() {
        val text = "Thu, 11 Dec 2025 14:55:16 GMT"
        val zonedDateTime = ZonedDateTime.parse(text, DateTimeFormatter.RFC_1123_DATE_TIME).toOffsetDateTime()
        println(zonedDateTime)
    }

    @Test
    fun readUrl() {
        val rss = URI("https://www.ndr.de/nachrichten/hamburg/index~rdf.xml").get(
            headers = mapOf(
                "Accept" to "application/xml",
                "Accept-Encoding" to "gzip"
            )
        )
        println(rss)
    }
}

fun URI.get(
    headers: Map<String, String> = mapOf()
): String {
    val connection = createConnection("GET", headers)
    val response =
        (if (connection.contentEncoding == "gzip") GZIPInputStream(connection.inputStream) else connection.inputStream)
            .use { ins ->
                ins.readAllBytes()
            }
    return String(response)
}

private fun URI.createConnection(
    method: String,
    headers: Map<String, String>,
    doOutput: Boolean = false
): HttpURLConnection {
    val connection = (toURL().openConnection() as HttpURLConnection)
    connection.requestMethod = method
    connection.connectTimeout = 5000
    headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }
    if (doOutput) {
        connection.doOutput = true
    }
    return connection
}
