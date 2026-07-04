package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.spring.boot4.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

// Måste ha proxyBeanMethods = false om man har object isf class.
@TestConfiguration(proxyBeanMethods = false)
@ImportAutoConfiguration(
    // Tvinga Spring att köra Exposeds auto-konfiguration. Den körs inte automatiskt av @JdbcTest.
    classes = [ExposedAutoConfiguration::class]
)
object SharedTestcontainersConfiguration {

    // Genom att initiera den i ett object startar containern en gång
    // när klassen laddas första gången.
    private val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine")).apply {
        val reuse = System.getenv("testcontainers.reuse.enable").toBoolean()
        withReuse(reuse)
        start()
    }

    @Bean
    @ServiceConnection
    fun postgresContainerBean(): PostgreSQLContainer {
        return postgresContainer
    }
}