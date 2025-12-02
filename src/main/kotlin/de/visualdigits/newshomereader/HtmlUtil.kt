package de.visualdigits.newshomereader

import jakarta.servlet.http.HttpServletRequest
import org.jsoup.Jsoup
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLDecoder

object HtmlUtil {

    fun extractImage(content: String): Triple<String?, String?, String?> {
        val document = Jsoup.parse(content)
        val image = document.select("img").firstOrNull()
        val url = image?.attr("src")
        val title = image?.attr("title")
        var caption = image?.attr("alt")
        if (caption?.isEmpty() == true) {
            caption = document.select("body").firstOrNull()?.wholeText()?.trim()
            if (caption?.isEmpty() == true) {
                caption = url?.let { u -> File(URI(u).path).nameWithoutExtension.replace("-", " ") }
            }
        }

        return Triple(url, title, caption)
    }

    fun HttpServletRequest.getRequestUri(): String {
        var uri = ""
        try {
            uri = URLDecoder.decode(this.requestURI, this.characterEncoding)
        } catch (_: UnsupportedEncodingException) {
            // ignore
        }
        return uri
    }
}