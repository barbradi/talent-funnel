package co.uk.e4s.technical.test.model.responses

import co.uk.e4s.technical.test.model.enums.ApplicationStatus

data class ApplicationInfoResponse(
    val id: String,
    val candidateName: String,
    val email: String,
    val position: String,
    val status: ApplicationStatus
)