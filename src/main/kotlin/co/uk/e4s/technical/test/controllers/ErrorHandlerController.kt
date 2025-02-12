package co.uk.e4s.technical.test.controllers

import co.uk.e4s.technical.test.exceptions.ApplicationNotFoundException
import co.uk.e4s.technical.test.model.responses.ApiError
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ErrorHandlerController {

    @ExceptionHandler(value = [(ApplicationNotFoundException::class)])
    fun handleUserAlreadyExists(ex: ApplicationNotFoundException): ResponseEntity<ApiError> {
        return ResponseEntity(
            ApiError(HttpStatus.NOT_FOUND, ex.message ?: "", ex.message ?: "" ),
            HttpStatus.NOT_FOUND
        )
            .also { log.error { "bad request" } }
    }

    @ExceptionHandler(value = [(MethodArgumentNotValidException::class)])
    fun handleArgumentsExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        return ResponseEntity(
            ApiError(HttpStatus.BAD_REQUEST, ex.message, ex.message),
            HttpStatus.BAD_REQUEST
        )
            .also { log.error { "bad request" } }
    }

    companion object {
        val log = KotlinLogging.logger { }
    }
}