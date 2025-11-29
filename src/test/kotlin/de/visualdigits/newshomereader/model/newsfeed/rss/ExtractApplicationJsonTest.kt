package de.visualdigits.newshomereader.model.newsfeed.rss

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.newshomereader.model.newsfeed.applicationjson.AppJson
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import java.io.File

class ExtractApplicationJsonTest {

    private val mapper = jacksonMapperBuilder()
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ISODate
//        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, JsonInclude.Include.NON_EMPTY))
        .addModule(JavaTimeModule())
        .build()

    @Test
    fun testNdr() {
        val html = File(ClassLoader.getSystemResource("rdf/ndr-story.html").toURI()).readText()
        val document = Jsoup.parse(html)
        val scripts = document.select("script[type=application/ld+json]").map { elem -> mapper.readValue(elem.data(), AppJson::class.java) }
        val audioItems = scripts.filter { script -> script.type == "AudioObject" }
        val videoItems = scripts
            .filter { script -> script.type == "VideoObject" }
        println(scripts.joinToString("\n---------------\n") { elem -> elem.writeValueAsJsonString() })
    }

    @Test
    fun testWdr() {
        val html = File(ClassLoader.getSystemResource("rdf/wdr-story.html").toURI()).readText()
        val document = Jsoup.parse(html)
//        val scripts = document.select("script[type=application/ld+json]").map { elem -> elem.data() }
//        println(scripts.joinToString("\n---------------\n"))
        val scripts = document.select("script[type=application/ld+json]").map { elem -> mapper.readValue(elem.data(), AppJson::class.java) }
        println(scripts.joinToString("\n---------------\n") { elem -> elem.writeValueAsJsonString() })
    }

    @Test
    fun testTagesschau() {
        val html = File(ClassLoader.getSystemResource("rdf/tagesschau-story.html").toURI()).readText()
        val document = Jsoup.parse(html)
        val scripts = document.select("script[type=application/ld+json]").map { elem -> mapper.readValue(elem.data(), AppJson::class.java) }
        println(scripts.joinToString("\n---------------\n") { elem -> elem.writeValueAsJsonString() })
    }

    @Test
    fun testNtv() {
        val html = File(ClassLoader.getSystemResource("rdf/ntv-story.html").toURI()).readText()
        val document = Jsoup.parse(html)
        val scripts = document.select("script[type=application/ld+json]").map { elem -> mapper.readValue(elem.data(), AppJson::class.java) }
        println(scripts.joinToString("\n---------------\n") { elem -> elem.writeValueAsJsonString() })
    }

    @Test
    fun testHeise() {
        val html = File(ClassLoader.getSystemResource("rdf/heise-story.html").toURI()).readText()
        val document = Jsoup.parse(html)
//        val scripts = document.select("script[type=application/ld+json]").map { elem -> elem.data() }
//        println(scripts.joinToString("\n---------------\n"))
        val scripts = document.select("script[type=application/ld+json]").map { elem -> mapper.readValue(elem.data(), AppJson::class.java) }
        println(scripts.joinToString("\n---------------\n") { elem -> elem.writeValueAsJsonString() })
    }

    @Test
    fun testT3n() {
        val html = File(ClassLoader.getSystemResource("rdf/t3n-story.html").toURI()).readText()
        val document = Jsoup.parse(html)
        val scripts = document.select("script[type=application/ld+json]").map { elem -> mapper.readValue(elem.data(), AppJson::class.java) }
        println(scripts.joinToString("\n---------------\n") { elem -> elem.writeValueAsJsonString() })
    }

    @Test
    fun testT3nJson() {
        val scripts = File(ClassLoader.getSystemResource("rdf/t3n.json").toURI()).readText()
        val json = mapper.readValue(scripts, AppJson::class.java)
        println(json.writeValueAsJsonString())
    }
}