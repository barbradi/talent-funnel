package co.uk.e4s.technical.test.model.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank


data class CreateApplicationRequest(

    @field:NotBlank
    val candidateName: String,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank(message = "position is mandatory")
    val position: String
)