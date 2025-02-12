package co.uk.e4s.technical.test.integration

import co.uk.e4s.technical.test.model.repo.MemoryRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
abstract class IntegrationTestSetup() {

    @Autowired
    private lateinit var memoryRepository : MemoryRepository

    @BeforeEach
    fun setup() {
        memoryRepository.clear()
    }
}