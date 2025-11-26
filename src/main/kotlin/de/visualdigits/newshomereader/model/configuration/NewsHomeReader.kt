package de.visualdigits.newshomereader.model.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.io.File
import java.nio.file.Paths

@Configuration
@ConfigurationProperties(prefix = "newshomereader")
@ConfigurationPropertiesScan
class NewsHomeReader(
    var theme: String = "default",
    var siteTitle: String? = null,
) {

    companion object {

        private val log = LoggerFactory.getLogger(NewsHomeReader::class.java)

        private val mapper = jacksonMapperBuilder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()

        val rootDirectory: File = File(System.getProperty("user.home"), ".newshomereader")

        fun getRelativeResourcePath(resource: File): String? {
            return try {
                rootDirectory.toPath()
                    .relativize(Paths.get(resource.canonicalPath))
                    .toString()
                    .replace("\\", "/")
            } catch (e: Exception) {
                log.error("Could not determine relative path for resource '$resource'", e)
                null
            }
        }
    }

    @Autowired
    private lateinit var envvironment: Environment

    var newsFeedsConfiguration: NewsFeedsConfiguration? = null

    @PostConstruct
    fun initialize() {
        newsFeedsConfiguration = mapper.readValue(Paths.get(rootDirectory.canonicalPath, "resources", "newsfeeds.json").toFile(), NewsFeedsConfiguration::class.java)
    }

    fun isProfileActive(profile: String): Boolean {
        return envvironment.activeProfiles.contains(profile)
    }
}