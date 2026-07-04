package nu.westlin.contextparametersspringtx

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest
import org.springframework.context.annotation.Import

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(FeelingsRepository::class, SharedTestcontainersConfiguration::class)
class FeelingsRepositoryTest @Autowired constructor(
    private val repository: FeelingsRepository
) {

    @Test
    fun `get - ingen finns`() = exposedTestBlock {
        assertThat(repository.getFeelingById(42)).isNull()
    }

    @Test
    fun `get - en finns`() = exposedTestBlock {
        val feeling = Feeling.new(status = Feeling.Status.Crazy)
        val createdFeeling = repository.create(feeling)
        assertThat(createdFeeling).isEqualTo(feeling.copy(id = createdFeeling.id))

        assertThat(repository.getFeelingById(createdFeeling.id)).isEqualTo(feeling.copy(id = createdFeeling.id))
    }
}

// Hjälpfunktion för att exekvera testkod inuti den aktiva Exposed-transaktionen
fun <T> exposedTestBlock(block: context(Transaction) () -> T) {
    return with(TransactionManager.current()) {
        block()
    }
}