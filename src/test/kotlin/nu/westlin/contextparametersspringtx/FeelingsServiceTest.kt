package nu.westlin.contextparametersspringtx

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.v1.core.Transaction
import org.junit.jupiter.api.Test

class FeelingsServiceTest {
    private val dummyTransaction = mockk<Transaction>(relaxed = true)

    private val repository: FeelingsRepository = mockk()
    private val txRunner = FakeExposedTransactionRunner(dummyTransaction)

    private val service = FeelingsService(repository = repository, txRunner = txRunner)

    @Test
    fun `getById - ingen hittas ska ge null`() {

        val id = 42
        with(txRunner) {
            every { repository.getFeelingById(id) } returns null
        }

        assertThat(service.getById(id)).isNull()
    }

    @Test
    fun create() {
        val feeling = Feeling.new(Feeling.Status.Boozed)
        val createdFeeling = feeling.copy(id = 42)

        with(txRunner) {
            every { repository.create(feeling) } returns createdFeeling
        }

        assertThat(service.create(feeling)).isEqualTo(createdFeeling)
    }
}