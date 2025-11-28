package de.visualdigits.newshomereader.controller

import de.visualdigits.newshomereader.service.ResourceService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController(
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
}