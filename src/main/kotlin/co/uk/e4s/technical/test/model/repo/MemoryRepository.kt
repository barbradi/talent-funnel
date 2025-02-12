package co.uk.e4s.technical.test.model.repo

import co.uk.e4s.technical.test.model.application.JobApplication
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Repository
class MemoryRepository : ApplicationRepository {

    val memoryRepository: MutableMap<String, JobApplication> = ConcurrentHashMap()

    override fun save(application: JobApplication): JobApplication {
        memoryRepository[application.id] = application
        return application
    }

    override fun findById(id: String): JobApplication? =
        memoryRepository[id]

    override fun findAll(): List<JobApplication> =
        memoryRepository.values.toList()

    override fun findByPosition(position: String): List<JobApplication> =
        memoryRepository.values.filter { ja -> ja.position == position }

    override fun clear() {
        memoryRepository.clear()
    }

}