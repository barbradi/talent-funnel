package co.uk.e4s.technical.test.model.repo

import co.uk.e4s.technical.test.model.application.JobApplication

interface ApplicationRepository {
    fun save(application: JobApplication): JobApplication
    fun findById(id: String): JobApplication?
    fun findAll(): List<JobApplication>
    fun findByPosition(position: String): List<JobApplication>
    fun clear()
}