package co.uk.e4s.technical.test.controllers

import co.uk.e4s.technical.test.mapper.GenericMapper
import co.uk.e4s.technical.test.model.application.JobApplication
import co.uk.e4s.technical.test.model.requests.CreateApplicationRequest
import co.uk.e4s.technical.test.model.requests.UpdateStatusRequest
import co.uk.e4s.technical.test.model.responses.ApplicationInfoResponse
import co.uk.e4s.technical.test.service.ApplicationService
import jakarta.validation.Valid
import mu.KotlinLogging
import mu.withLoggingContext
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/applications")
class ApplicationController(
    private val service: ApplicationService,
    private val jobApplicationToResponseMapper: GenericMapper<JobApplication, ApplicationInfoResponse>

) {

    @GetMapping
    fun getApplications() : List<ApplicationInfoResponse> {
        log.info { "getting all applications" }
        return service.getApplications().map { jobApplicationToResponseMapper.apply(it) }
    }

    @GetMapping("/{id}")
    fun getApplicationById(@PathVariable id: String) : ApplicationInfoResponse
    {
        withLoggingContext("applicationId" to "$id") {
            log.info { "getting by id: $id" }
            return jobApplicationToResponseMapper.apply(service.findById(id))
        }
    }

    @GetMapping("/position/{position}")
    fun findApplicationByPosition(@PathVariable position: String,) : List<ApplicationInfoResponse> {
        log.info { "getting by position: $position" }
        return service.findByPosition(position).map { jobApplicationToResponseMapper.apply(it) }
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    fun createApplication(@Valid @RequestBody request: CreateApplicationRequest) : ApplicationInfoResponse {
        log.info { "create application" }
        return jobApplicationToResponseMapper.apply(service.submitApplication(request))
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(code = HttpStatus.CREATED)
    fun updateStatus(
        @PathVariable id: String,
        @Valid @RequestBody updateStatusRequest: UpdateStatusRequest
    ) : ApplicationInfoResponse {
        withLoggingContext("applicationId" to "$id") {
            log.info { "patching status id: $id to status: ${updateStatusRequest.status}" }
            return jobApplicationToResponseMapper.apply(service.updateApplicationStatus(id, updateStatusRequest))
        }
    }

    companion object {
        val log = KotlinLogging.logger { }
    }
}