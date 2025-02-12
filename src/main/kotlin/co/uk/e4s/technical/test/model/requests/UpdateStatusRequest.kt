package co.uk.e4s.technical.test.model.requests

import co.uk.e4s.technical.test.model.enums.ApplicationStatus

data class UpdateStatusRequest(
    val status: ApplicationStatus
)