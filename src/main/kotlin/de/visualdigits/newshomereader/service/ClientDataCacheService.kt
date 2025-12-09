package de.visualdigits.newshomereader.service

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.newshomereader.service.cache.NewsItemCache
import de.visualdigits.newshomereader.model.clientdata.ClientData
import de.visualdigits.newshomereader.model.clientdata.ClientDataCache
import de.visualdigits.newshomereader.model.configuration.NewsHomeReader
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Paths
import java.util.UUID

@Service
@OptIn(ExperimentalUnsignedTypes::class)
class ClientDataCacheService(
    private val newsItemCache: NewsItemCache
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private var clientDataCache: ClientDataCache = ClientDataCache()

    private val cacheFile = Paths.get(NewsHomeReader.rootDirectory.canonicalPath, "resources", "clientDataCache.json").toFile()

    private val jsonMapper = jacksonMapperBuilder().enable(SerializationFeature.INDENT_OUTPUT).build()

    @PostConstruct
    fun initialize() {
        log.info("Initializing clientDataCache")
        if (cacheFile.exists()) {
            clientDataCache = jsonMapper.readValue(cacheFile, ClientDataCache::class.java)
        }
    }

    @PreDestroy
    fun tearDown() {
        log.info("Shutting down clientDataCache")
        jsonMapper.writeValue(cacheFile, clientDataCache)
    }

    fun getClientData(clientCode: UUID): ClientData {
        return clientDataCache.getClientData(clientCode)
    }

    fun setHideRead(clientCode: UUID, hideRead: Boolean) {
        getClientData(clientCode).hideRead = hideRead
    }

    fun isHideRead(clientCode: UUID): Boolean = getClientData(clientCode).hideRead

    fun getReadItems(clientCode: UUID): Set<UInt> = getClientData(clientCode).readItems.toSet()

    fun addReadItem(
        clientCode: UUID,
        hashCode: UInt
    ) {
        getClientData(clientCode).readItems.add(hashCode)
    }

    fun addReadItems(
        clientCode: UUID,
        hashCodes: Collection<UInt>?
    ) {
        hashCodes?.also { hc ->
            getClientData(clientCode).readItems.addAll(hc)
        }
    }

    fun removeReeadItem(
        clientCode: UUID,
        hashCode: UInt
    ) {
        getClientData(clientCode).readItems.remove(hashCode)
    }

    fun removeReadItems(
        clientCode: UUID,
        hashCodes: Collection<UInt>?
    ) {
        hashCodes?.also { hc ->
            getClientData(clientCode).readItems.removeAll(hc)
        }
    }

    fun cleanupOrphanedReadItems(clientCode: UUID) {
        val unknownHashes = getReadItems(clientCode).toMutableSet()
        unknownHashes.removeAll(newsItemCache.getNewsItemHashCodes())
        removeReadItems(clientCode, unknownHashes)
    }
}