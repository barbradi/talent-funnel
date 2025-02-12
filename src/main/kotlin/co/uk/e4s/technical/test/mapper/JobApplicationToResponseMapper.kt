package co.uk.e4s.technical.test.mapper

import co.uk.e4s.technical.test.model.application.JobApplication
import co.uk.e4s.technical.test.model.responses.ApplicationInfoResponse
import org.springframework.stereotype.Component

@Component
class JobApplicationToResponseMapper : GenericMapper<JobApplication, ApplicationInfoResponse> {
     override fun apply(ja: JobApplication) =
        ApplicationInfoResponse(
            id = ja.id,
            candidateName = ja.candidateName,
            position = ja.position,
            email = ja.email,
            status = ja.status
        )
}