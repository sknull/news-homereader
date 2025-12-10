package de.visualdigits.newshomereader.service.cache

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.newshomereader.model.cache.ImageInfo
import de.visualdigits.newshomereader.model.configuration.NewsHomeReader
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.time.OffsetDateTime
import kotlin.io.path.relativeTo

@Service
class ImageProxy(
    private val newsHomeReader: NewsHomeReader
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private val mapper: JsonMapper = jacksonMapperBuilder()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .addModule(JavaTimeModule())
        .build()

    private val imageCache: MutableMap<ImageInfo, Path?> = mutableMapOf()

    private val imageDirectory = Paths.get(NewsHomeReader.rootDirectory.canonicalPath, "resources", "cache", "images").toFile()

    @PostConstruct
    fun initialize() {
        imageDirectory
            .listFiles { file -> file.isFile && file.extension == "json" }
            ?.forEach { file ->
                val imageInfo = mapper.readValue(File(imageDirectory, "${file.nameWithoutExtension}.json").readText(), ImageInfo::class.java)
                if (imageInfo.available) {
                    imageCache[imageInfo] = imageInfo.path?.let { p -> Paths.get(p) }
                } else {
                    imageCache[imageInfo] = null
                }
            }
    }

    fun getImage(newItemHashCode: UInt, isThumbnail: Boolean, uri: String): String? {
        var imageInfo: ImageInfo? = ImageInfo(newItemHashCode, isThumbnail, uri, OffsetDateTime.now(), "", null, false)
        if (!imageCache.contains(imageInfo)) {
            imageInfo = downloadImage(newItemHashCode, isThumbnail, uri)
        }

        return imageInfo?.let { ii -> imageCache[ii]?.relativeTo(NewsHomeReader.rootDirectory.toPath())?.toString()?.let { path -> "/$path" } }
    }

    fun clearCache() {
        imageCache.keys.toList()
            .forEach { imageInfo ->
                if (!File(
                        NewsHomeReader.rootDirectory,
                        "${imageInfo.newItemHashCode}.${imageInfo.extension}"
                    ).delete()) log.warn("Could not delete cached image file for id '${imageInfo.hashCode()}'")
                if (!File(NewsHomeReader.rootDirectory, "${imageInfo.newItemHashCode}.json").delete()) log.warn("Could not delete cached json file for id '${imageInfo.hashCode()}'")
                imageCache.remove(imageInfo)
            }
    }

    fun cleanCache() {
        if (imageCache.size > newsHomeReader.maxImagesInCache) {
            imageCache.keys
                .sortedBy { imageInfo -> imageInfo.downloaded.toInstant().toEpochMilli() }
                .dropLast(newsHomeReader.maxImagesInCache)
                .forEach { imageInfo -> deleteImage(imageInfo.newItemHashCode) }
        }
    }

    fun deleteImage(newItemHashCode: UInt): Boolean {
        return NewsHomeReader.rootDirectory
            .listFiles { file -> file.nameWithoutExtension.startsWith(newItemHashCode.toString()) }
            ?.map { file -> file.delete() }
            ?.any { e -> !e }
            ?:true
    }

    fun downloadImage(newItemHashCode: UInt, isThumbnail: Boolean, uri: String): ImageInfo? {
        val url = URI(uri.replace(" ", "+")).toURL()
        val file = File(url.path.replace("+", " "))
        val baseName = "${newItemHashCode}${if (isThumbnail) "_thumbnail" else ""}"
        var attempt = 0
        var imageInfo: ImageInfo? = null
        loop@ while (attempt++ < newsHomeReader.maxDownloadRetries) {
            val imageFile = File(imageDirectory, "$baseName.${file.extension}")
            if (!imageFile.exists()) {
                try {
                    url.openStream().use { ins -> imageFile.outputStream().use { outs -> ins.transferTo(outs) } }
                    imageInfo = ImageInfo(
                        newItemHashCode,
                        isThumbnail,
                        uri,
                        OffsetDateTime.now(),
                        imageFile.canonicalPath,
                        file.extension,
                        true
                    )
                    File(imageDirectory, "$baseName.json").writeText(mapper.writeValueAsString(imageInfo))
                    imageCache[imageInfo] = imageFile.toPath()
                    break@loop
                } catch (e: Exception) {
                    log.warn("Could not download image '$uri' with reason '${e.message}' - retrying")
                    Thread.sleep(newsHomeReader.downloadRetryDelay)
                }
            }
        }
        if (attempt > newsHomeReader.maxDownloadRetries) {
            log.error("Downloading image '$uri' finally failed - marking as unavailable")
            imageInfo = ImageInfo(newItemHashCode, isThumbnail, uri, OffsetDateTime.now(), null, null, false)
            imageCache[imageInfo] = null // set key to null to avoid future retry attempts
            File(imageDirectory, "$baseName.json").writeText(mapper.writeValueAsString(imageInfo))
        }

        return imageInfo
    }
}