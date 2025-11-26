package de.visualdigits.newshomereader.controller

import de.visualdigits.newshomereader.service.PageService
import de.visualdigits.newshomereader.service.ResourceService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller("PageController")
class PageController(
    private val pageService: PageService,
    private val resourceService: ResourceService
) {

    @GetMapping(value = ["/"])
    fun root(
        response: HttpServletResponse
    ) {
        response.sendRedirect("/news")
    }


    @GetMapping(value = ["/resources/**"])
    fun resource(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        resourceService.getResource(request, response)
    }

    @PostMapping(value = ["/formHideRead/**"], produces = ["application/xhtml+xml"])
    fun formHideRead(
        model: Model,
        request: HttpServletRequest
    ): String {
        return pageService.formHideRead(
            model = model,
            request = request
        )
    }

    @PostMapping(value = ["/formMarkAllRead/**"], produces = ["application/xhtml+xml"])
    fun formMarkAllRead(
        model: Model,
        request: HttpServletRequest
    ): String {
        return pageService.formMarkAllRead(
            model = model,
            request = request
        )
    }

    @GetMapping(value = ["/news/**"], produces = ["application/xhtml+xml"])
    fun page(
        @RequestParam(name = "feedName", required = false, defaultValue = "") feedName: String = "",
        @RequestParam(name = "identifier", required = false, defaultValue = "") identifier: String = "",
        @RequestParam(name = "hideRead", required = false, defaultValue = "false") hideRead: Boolean = false,
        model: Model,
        request: HttpServletRequest
    ): String? {
        return pageService.renderPage(
            feedName = feedName,
            identifier = identifier,
            hideRead = hideRead,
            model = model,
            request = request
        )
    }
}

