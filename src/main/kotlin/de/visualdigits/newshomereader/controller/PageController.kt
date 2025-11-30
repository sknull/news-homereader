package de.visualdigits.newshomereader.controller

import de.visualdigits.newshomereader.service.PageService
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
) {

    @GetMapping(value = ["/news/**"], produces = ["application/xhtml+xml"])
    fun page(
        @RequestParam(name = "hashCode", required = false) hashCode: String? = null,
        @CookieValue(name = "hideRead", required = false) hideRead: Boolean = false,
        @CookieValue(name = "readItems", required = false) readItems: String = "",
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String? {
        return pageService.renderPage(
            hashCode = hashCode?.toUInt(),
            hideRead = hideRead,
            readItems = readItems.toMutableSet(),
            model = model,
            request = request,
            response = response
        )
    }

    @PostMapping(value = ["/formHideRead/**"], produces = ["application/xhtml+xml"])
    fun formHideRead(
        @CookieValue(name = "readItems", required = false) readItems: String = "",
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        return pageService.formHideRead(
            readItems = readItems.toMutableSet(),
            model = model,
            request = request,
            response = response
        )
    }

    @PostMapping(value = ["/formMarkAllRead/**"], produces = ["application/xhtml+xml"])
    fun formMarkAllRead(
        @CookieValue(name = "readItems", required = false) readItems: String = "",
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        return pageService.formMarkAllRead(
            readItems = readItems.toMutableSet(),
            model = model,
            request = request,
            response = response
        )
    }
}

private fun String.toMutableSet(): MutableSet<UInt> {
    return this.split("/").mapNotNull { it.trim().let { n -> if (n.isNotEmpty()) n.toUInt() else null } }.toMutableSet()
}
