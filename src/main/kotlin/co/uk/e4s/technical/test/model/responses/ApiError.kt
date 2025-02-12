package co.uk.e4s.technical.test.model.responses

import org.springframework.http.HttpStatus

data class ApiError (
    val status: HttpStatus,
    val message: String,
    val errors: String
)