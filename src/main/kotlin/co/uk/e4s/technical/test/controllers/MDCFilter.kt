package co.uk.e4s.technical.test.controllers

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import mu.withLoggingContext
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class MDCFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        withLoggingContext(
            "userAgent" to request.getHeader("User-Agent"),
            "ipAddress" to request.getHeader("X-Forwarded-For")
        ) {
            log.info { "request filter " + request.getHeader("User-Agent")}
            filterChain.doFilter(request, response)
        }
    }

    companion object {
        val log = KotlinLogging.logger { }
    }
}