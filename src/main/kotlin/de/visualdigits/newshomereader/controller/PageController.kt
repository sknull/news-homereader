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
import java.util.UUID


@Controller("PageController")
class PageController(
    private val pageService: PageService,
    private val resourceService: ResourceService,
) {

    @GetMapping(value = ["/**"], produces = ["application/xhtml+xml"])
    fun dispatch(
        @RequestParam(name = "hashCode", required = false) hashCode: String? = null,
        @CookieValue(name = "clientCode", required = false) clientCode: UUID? = null,
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
                clientCode = clientCode,
                requestUri = request.getRequestUri().removePrefix("/news/"),
                response = response,
                model = model
            )
        } else {
            pageService.renderPage(
                hashCode = hashCode?.toUInt(),
                clientCode = clientCode,
                requestUri = request.getRequestUri(),
                response = response,
                model = model
            )
        }
    }

    @PostMapping(value = ["/formHideRead/**"], produces = ["application/xhtml+xml"])
    fun formHideRead(
        @CookieValue(name = "clientCode", required = true) clientCode: UUID,
        request: HttpServletRequest,
        model: Model
    ): String {
        return pageService.formHideRead(
            clientCode = clientCode,
            requestUri = request.getRequestUri().removePrefix("/formHideRead/"),
            request = request,
            model = model
        )
    }

    @PostMapping(value = ["/formMarkAllRead/**"], produces = ["application/xhtml+xml"])
    fun formMarkAllRead(
        @CookieValue(name = "clientCode", required = true) clientCode: UUID,
        request: HttpServletRequest,
        model: Model
    ): String {
        return pageService.formMarkAllRead(
            clientCode = clientCode,
            requestUri = request.getRequestUri().removePrefix("/formMarkAllRead/"),
            request = request,
            model = model
        )
    }
}
