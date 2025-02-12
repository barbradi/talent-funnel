package co.uk.e4s.technical.test.model.application

import co.uk.e4s.technical.test.model.enums.ApplicationStatus
import java.util.*

data class JobApplication(
    val id: String = UUID.randomUUID().toString(),
    val candidateName: String,
    val email: String,
    val position: String,
    val status: ApplicationStatus = ApplicationStatus.NEW
)