package co.uk.e4s.technical.test.integration

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.extracting
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import co.uk.e4s.technical.test.model.enums.ApplicationStatus
import co.uk.e4s.technical.test.model.enums.ApplicationStatus.ACCEPTED
import co.uk.e4s.technical.test.model.enums.ApplicationStatus.NEW
import co.uk.e4s.technical.test.model.repo.MemoryRepository
import co.uk.e4s.technical.test.model.requests.CreateApplicationRequest
import co.uk.e4s.technical.test.model.requests.UpdateStatusRequest
import co.uk.e4s.technical.test.model.responses.ApplicationInfoResponse
import co.uk.e4s.technical.test.testFixtures.TestData
import co.uk.e4s.technical.test.testFixtures.TestData.Companion.createApplicationRequest
import io.mockk.every
import net.javacrumbs.jsonunit.assertj.JsonAssertions
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.server.ResponseStatusException


class ApplicationIntegrationTest @Autowired constructor(
	private val webTestClient: WebTestClient,
) : IntegrationTestSetup() {

	@Test
	fun `should return all applications`() {
		// Given
		listOf(
			createApplicationRequest("position1"),
			createApplicationRequest("position2"),
		).forEach { createApplication(it) }

		// When
		val applicationsFound = webTestClient.get()
			.uri("/api/applications")
			.exchange()
			.expectStatus().isOk
			.expectBodyList(ApplicationInfoResponse::class.java)
			.returnResult()
			.responseBody!!

		// Then
		assertThat(applicationsFound)
			.extracting(ApplicationInfoResponse::position)
			.containsExactlyInAnyOrder("position1", "position2")
	}

	@Test
	fun `should find application by id`() {
		// Given
        val createdApplication = createApplication(createApplicationRequest("position"))

		// When
		val applicationFound = webTestClient.get()
			.uri("/api/applications/${createdApplication.id}")
			.exchange()
			.expectStatus().isOk
			.expectBody(ApplicationInfoResponse::class.java)
			.returnResult()
			.responseBody!!

		// Then
		assertThat(applicationFound.candidateName).isEqualTo(createdApplication.candidateName)
		assertThat(applicationFound.status).isEqualTo(NEW)
	}

	@Test
	fun `should find application by position`() {
		// Given
		listOf(
			createApplicationRequest("position"),
			createApplicationRequest("position"),
			createApplicationRequest("positionBad")
			).forEach { createApplication(it) }

		// When
		val applicationsFound = webTestClient.get()
			.uri("/api/applications/position/position")
			.exchange()
			.expectStatus().isOk
			.expectBodyList(ApplicationInfoResponse::class.java)
			.returnResult()
			.responseBody!!

		// Then
		assertThat(applicationsFound)
			.extracting(ApplicationInfoResponse::position)
			.containsExactlyInAnyOrder("position", "position")
	}

	@Test
	fun `should handle not found by id`() {
		// Given
		val id = "idnotexisting"

		// When
		val errorResponse = webTestClient.get()
			.uri("/api/applications/$id")
			.exchange()
			.expectStatus().isNotFound
			.expectBody(String::class.java)
			.returnResult()
			.responseBody

		// Then
		assertThatJson(errorResponse).isEqualTo("""
			{
			  "errors": "Application not found",
			  "message": "Application not found",
			  "status": "NOT_FOUND"
			}
        """.trimIndent())
	}

	@Test
	fun `should create application`() {
		// Given
		val applicationRequest = createApplicationRequest("position")

		// When
		val applicationInfoResponse = createApplication(applicationRequest)

		// Then
		assertThat(applicationInfoResponse.candidateName).isEqualTo(applicationRequest.candidateName)
		assertThat(applicationInfoResponse.status).isEqualTo(NEW)
	}


	@Test
	fun `should handle invalid email`() {
		// Given
		val applicationRequest =  CreateApplicationRequest(
				candidateName = "name",
				email = "", // empty
				position = "position"
			)

		// When
		val errorResponse = webTestClient.post()
			.uri("/api/applications")
			.accept(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(applicationRequest))
			.exchange()
			.expectStatus().isBadRequest
			.expectBody(String::class.java)
			.returnResult()
			.responseBody

		// Then
		assertThatJson(errorResponse).isEqualTo("""
			{
			  "errors": "Validation failed for argument [0] in public co.uk.e4s.technical.test.model.responses.ApplicationInfoResponse co.uk.e4s.technical.test.controllers.ApplicationController.createApplication(co.uk.e4s.technical.test.model.requests.CreateApplicationRequest): [Field error in object 'createApplicationRequest' on field 'email': rejected value []; codes [NotBlank.createApplicationRequest.email,NotBlank.email,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [createApplicationRequest.email,email]; arguments []; default message [email]]; default message [must not be blank]] ",
			  "message": "Validation failed for argument [0] in public co.uk.e4s.technical.test.model.responses.ApplicationInfoResponse co.uk.e4s.technical.test.controllers.ApplicationController.createApplication(co.uk.e4s.technical.test.model.requests.CreateApplicationRequest): [Field error in object 'createApplicationRequest' on field 'email': rejected value []; codes [NotBlank.createApplicationRequest.email,NotBlank.email,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [createApplicationRequest.email,email]; arguments []; default message [email]]; default message [must not be blank]] ",
			  "status": "BAD_REQUEST"
			}
        """.trimIndent())
	}



	@Test
	fun `should update status`() {
		// Given
		val createdApplication = createApplication(createApplicationRequest("position"))
		val updateStatusRequest = UpdateStatusRequest(ACCEPTED)

		// When "/{id}/status"
		val applicationInfoResponse = webTestClient.patch()
			.uri("/api/applications/${createdApplication.id}/status")
			.accept(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(updateStatusRequest))
			.exchange()
			.expectStatus().isOk
			.expectBody(ApplicationInfoResponse::class.java)
			.returnResult()
			.responseBody!!

		// Then
		assertThat(applicationInfoResponse.candidateName).isEqualTo(createdApplication.candidateName)
		assertThat(applicationInfoResponse.status).isEqualTo(ACCEPTED)
	}

	private fun createApplication(applicationRequest: CreateApplicationRequest): ApplicationInfoResponse {
		val applicationInfoResponse = webTestClient.post()
			.uri("/api/applications")
			.accept(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(applicationRequest))
			.exchange()
			.expectStatus().isCreated
			.expectBody(ApplicationInfoResponse::class.java)
			.returnResult()
			.responseBody

		return applicationInfoResponse!!

	}

}
