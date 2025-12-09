package de.visualdigits.newshomereader.model.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.file.Paths

@Configuration
@ConfigurationProperties(prefix = "newshomereader")
@ConfigurationPropertiesScan
class NewsHomeReader(
    var theme: String = "default",
    var siteTitle: String? = null,
    var maxItemsInCache: Int = 0,
    var maxImagesInCache: Int = 0,
    var maxDownloadRetries: Int = 0,
    var downloadRetryDelay: Long = 0
) {

    companion object {

        private val mapper = jacksonMapperBuilder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()

        val rootDirectory: File = File(System.getProperty("user.home"), ".newshomereader")
    }

    fun newsFeedsConfiguration(): NewsFeedsConfiguration? = mapper.readValue(Paths.get(rootDirectory.canonicalPath, "resources", "newsfeeds.json").toFile(), NewsFeedsConfiguration::class.java)
}