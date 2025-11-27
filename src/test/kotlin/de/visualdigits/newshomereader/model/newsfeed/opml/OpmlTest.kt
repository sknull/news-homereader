package de.visualdigits.newshomereader.model.newsfeed.opml

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.junit.jupiter.api.Test
import java.io.File

class OpmlTest {

    @Test
    fun testReadModel() {
        val mapper = XmlMapper.builder().addModule(kotlinModule()).build()
        val file = File(ClassLoader.getSystemResource("opml/feeder-export-2025-11-26-65586.opml").toURI())
        val opml = mapper.readValue(file, Opml::class.java)
        println(opml.writeValueAsString())
    }
}