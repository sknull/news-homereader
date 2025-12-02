package de.visualdigits.newshomereader.controller

import de.visualdigits.newshomereader.HtmlUtil.getRequestUri
import de.visualdigits.newshomereader.service.PageService
import de.visualdigits.newshomereader.service.ResourceService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller("PageController")
class PageController(
    private val pageService: PageService,
    private val resourceService: ResourceService,
) {

    @GetMapping(value = ["/**"], produces = ["application/xhtml+xml"])
    fun dispatch(
        @RequestParam(name = "hashCode", required = false) hashCode: String? = null,
        @CookieValue(name = "hideRead", required = false) hideRead: Boolean = false,
        @CookieValue(name = "readItems", required = false) readItems: String = "",
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String? {
        val requestUri = request.getRequestUri()
        return if (requestUri.startsWith("/resources")) {
            resourceService.getResource(request, response)
            null
        } else if (requestUri.startsWith("/news/")) {
            pageService.renderPage(
                hashCode = hashCode?.toUInt(),
                hideRead = hideRead,
                readItems = readItems.toMutableSet(),
                requestUri = request.getRequestUri().removePrefix("/news/"),
                response = response,
                model = model
            )
        } else {
            pageService.renderPage(
                hashCode = hashCode?.toUInt(),
                hideRead = hideRead,
                readItems = readItems.toMutableSet(),
                requestUri = request.getRequestUri(),
                response = response,
                model = model
            )
        }
    }

    @PostMapping(value = ["/formHideRead/**"], produces = ["application/xhtml+xml"])
    fun formHideRead(
        @CookieValue(name = "readItems", required = false) readItems: String = "",
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        return pageService.formHideRead(
            readItems = readItems.toMutableSet(),
            request = request,
            response = response,
            model = model
        )
    }

    @PostMapping(value = ["/formMarkAllRead/**"], produces = ["application/xhtml+xml"])
    fun formMarkAllRead(
        @CookieValue(name = "readItems", required = false) readItems: String = "",
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        return pageService.formMarkAllRead(
            readItems = readItems.toMutableSet(),
            request = request,
            response = response,
            model = model
        )
    }
}

private fun String.toMutableSet(): MutableSet<UInt> {
    return this.split("/").mapNotNull { it.trim().let { n -> if (n.isNotEmpty()) n.toUInt() else null } }.toMutableSet()
}
