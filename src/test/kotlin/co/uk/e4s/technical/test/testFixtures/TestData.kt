package co.uk.e4s.technical.test.testFixtures

import co.uk.e4s.technical.test.model.requests.CreateApplicationRequest

class TestData {
    companion object {
        fun createApplicationRequest(position: String = "position"): CreateApplicationRequest {
            val applicationRequest = CreateApplicationRequest(
                candidateName = "name",
                email = "valid@email.com",
                position = position
            )
            return applicationRequest
        }
    }

}