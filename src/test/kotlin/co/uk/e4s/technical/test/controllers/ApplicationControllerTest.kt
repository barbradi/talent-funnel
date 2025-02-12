package co.uk.e4s.technical.test.controllers

import co.uk.e4s.technical.test.mapper.GenericMapper
import co.uk.e4s.technical.test.mapper.JobApplicationToResponseMapper
import co.uk.e4s.technical.test.model.application.JobApplication
import co.uk.e4s.technical.test.model.enums.ApplicationStatus
import co.uk.e4s.technical.test.model.enums.ApplicationStatus.ACCEPTED
import co.uk.e4s.technical.test.model.enums.ApplicationStatus.NEW
import co.uk.e4s.technical.test.model.requests.UpdateStatusRequest
import co.uk.e4s.technical.test.model.responses.ApplicationInfoResponse
import co.uk.e4s.technical.test.service.ApplicationService
import co.uk.e4s.technical.test.testFixtures.TestData
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.client.MockMvcWebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.server.ResponseStatusException

@WebMvcTest(controllers = [ApplicationController::class])
class ApplicationControllerTest  @Autowired constructor(
    mockMvc: MockMvc
) {

    @MockkBean
    private lateinit var applicationService: ApplicationService

    @MockkBean
    private lateinit var jobApplicationToResponseMapper : GenericMapper<JobApplication, ApplicationInfoResponse>

    private val webTestClient : WebTestClient = MockMvcWebTestClient.bindTo(mockMvc).build()
    private val mapper = JobApplicationToResponseMapper()

    @BeforeEach
    fun setup() {
        // use the real mapper jobApplication -> ApplicationInfoResponse
        every { jobApplicationToResponseMapper.apply(any()) } answers { mapper.apply(firstArg()) }
    }

    @Test
    fun `should return all applications`() {
        // Given
        val id = "id1"
        every { applicationService.getApplications() } returns
                listOf(getJobApplication("id1"), getJobApplication("id2"))

        // When
        val resultJson = webTestClient.get()
            .uri("/api/applications")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        // Then
        assertThatJson(resultJson).isEqualTo("""
            [
              {
                "id": "id1",
                "candidateName": "name",
                "email": "email",
                "position": "position",
                "status": "NEW"
              },
              {
                "id": "id2",
                "candidateName": "name",
                "email": "email",
                "position": "position",
                "status": "NEW"
              }
            ]
        """.trimIndent())
    }

    @Test
    fun `should find application by id`() {
        // Given
        val id = "id1"
        every { applicationService.findById(id) } returns getJobApplication(id)

        // When
        val resultJson = webTestClient.get()
            .uri("/api/applications/$id")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        // Then
        assertThatJson(resultJson).isEqualTo("""
            {
              "id": "$id",
              "candidateName": "name",
              "email": "email",
              "position": "position",
              "status": "NEW"
            }
        """.trimIndent())
    }

    @Test
    fun `should find application by position`() {
        // Given
        val position = "aPosition"
        every { applicationService.findByPosition(position) } returns
                listOf(
                    getJobApplication(id = "id1", position = position),
                    getJobApplication(id = "id2", position = position)
                )

        // When
        val resultJson = webTestClient.get()
            .uri("/api/applications/position/$position")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        // Then
        assertThatJson(resultJson).isEqualTo("""
            [
              {
                "id": "id1",
                "candidateName": "name",
                "email": "email",
                "position": "$position",
                "status": "NEW"
              },
              {
                "id": "id2",
                "candidateName": "name",
                "email": "email",
                "position": "$position",
                "status": "NEW"
              }
            ]
        """.trimIndent())
    }



    @Test
    fun `should create application`() {
        // Given
        val id = "id1"
        val applicationRequest = TestData.createApplicationRequest()
        every { applicationService.submitApplication(applicationRequest) } returns getJobApplication(id)

        // When
        val resultJson = webTestClient.post()
            .uri("/api/applications")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(applicationRequest))
            .exchange()
            .expectStatus().isCreated
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        // Then
        assertThatJson(resultJson).isEqualTo("""
            {
              "id": "$id",
              "candidateName": "name",
              "email": "email",
              "position": "position",
              "status": "NEW"
            }
        """.trimIndent())
    }


    @Test
    fun `should update status`() {
        // Given
        val id = "id1"
        val updateStatusRequest = UpdateStatusRequest(ACCEPTED)
        every { applicationService.updateApplicationStatus(id, updateStatusRequest) }returns
                getJobApplication(id, ACCEPTED)

        // When
        val resultJson = webTestClient.patch()
            .uri("/api/applications/$id/status")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(updateStatusRequest))
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        // Then
        assertThatJson(resultJson).isEqualTo("""
            {
              "id": "$id",
              "candidateName": "name",
              "email": "email",
              "position": "position",
              "status": "ACCEPTED"
            }
        """.trimIndent())
    }

    private fun getJobApplication(
        id: String,
        status: ApplicationStatus = NEW,
        position: String = "position")
    : JobApplication {
        val expectedJobApplication = JobApplication(
            id = id,
            candidateName = "name",
            email = "email",
            position = position,
            status = status
        )
        return expectedJobApplication
    }

}