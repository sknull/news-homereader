package de.visualdigits.newshomereader.model.newsfeed.applicationjson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OffsetDateTimeHeuristicDeserializer() : JsonDeserializer<OffsetDateTime?>() {

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): OffsetDateTime? {
        val text = ctxt.readValue(p, String::class.java)
        return parseDateOnly(text)
            ?: parseOffsetDateTimeWithMillis(text)
            ?: parseOffsetDateTimeWithoutMillis(text)
    }

    private fun parseOffsetDateTimeWithMillis(text: String): OffsetDateTime? {
        return try {
            OffsetDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
        } catch (_: Exception) {
            null // by means
        }
    }

    private fun parseOffsetDateTimeWithoutMillis(text: String): OffsetDateTime? {
        return try {
            OffsetDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"))
        } catch (_: Exception) {
            null // by means
        }
    }

    private fun parseDateOnly(text: String): OffsetDateTime? {
        return try {
            OffsetDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (_: Exception) {
            null // by means
        }
    }
}