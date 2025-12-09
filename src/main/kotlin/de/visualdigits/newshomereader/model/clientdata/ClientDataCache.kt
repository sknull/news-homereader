package de.visualdigits.newshomereader.model.clientdata

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap



class ClientDataCache(
    val clientDataCache: ConcurrentHashMap<UUID, ClientData> = ConcurrentHashMap()
) {

    fun getClientData(clientCode: UUID): ClientData {
        return clientDataCache.computeIfAbsent(clientCode) { ClientData() }
    }
}