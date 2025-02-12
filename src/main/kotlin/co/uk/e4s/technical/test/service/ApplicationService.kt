package co.uk.e4s.technical.test.service

import co.uk.e4s.technical.test.exceptions.ApplicationNotFoundException
import co.uk.e4s.technical.test.model.application.JobApplication
import co.uk.e4s.technical.test.model.enums.ApplicationStatus
import co.uk.e4s.technical.test.model.repo.ApplicationRepository
import co.uk.e4s.technical.test.model.requests.CreateApplicationRequest
import co.uk.e4s.technical.test.model.requests.UpdateStatusRequest
import org.springframework.stereotype.Service


@Service
class ApplicationService(
    private val repository: ApplicationRepository
) {

    fun getApplications(): List<JobApplication> =
        repository.findAll()

    fun findById(id: String): JobApplication =
        repository.findById(id)
            ?: throw ApplicationNotFoundException("Application not found")

    fun findByPosition(position: String): List<JobApplication> =
        repository.findByPosition(position)

    fun submitApplication(request: CreateApplicationRequest): JobApplication {
        return repository.save(
            JobApplication(
                candidateName = request.candidateName,
                email = request.email,
                position = request.position,
                status = ApplicationStatus.NEW
            )
        )
    }

    fun updateApplicationStatus(id: String, statusRequest: UpdateStatusRequest): JobApplication =
        repository.findById(id)
            ?.let { it.copy(status = statusRequest.status) }
            ?.let { repository.save(it) }
            ?: throw ApplicationNotFoundException("Application not found")
}