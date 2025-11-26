package de.visualdigits.newshomereader.model.configuration.newsfeeds

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.newshomereader.model.page.Page
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class NewsFeedsTest {

    private val mapper = jacksonMapperBuilder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build()

    @Test
    fun newsFeedsTest() {
        val pages = mapper.readValue(Paths.get(System.getProperty("user.home"), ".newshomereader", "resources", "newsfeeds.json").toFile(), Page::class.java)
//        val page1 = page.getPage(listOf("News", "Welt", "Tagesschau"))
//        println(page1?.path())
        println(mapper.writeValueAsString(pages))
    }
}