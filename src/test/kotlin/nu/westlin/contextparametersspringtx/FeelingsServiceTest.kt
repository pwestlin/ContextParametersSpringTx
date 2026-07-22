package nu.westlin.contextparametersspringtx

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.exposed.v1.core.Transaction
import org.junit.jupiter.api.Test

class FeelingsServiceTest {
    private val dummyTransaction = mockk<Transaction>(relaxed = true)

    private val repository: FeelingsRepository = mockk()
    private val txRunner = FakeExposedTransactionRunner(dummyTransaction)

    private val service = FeelingsService(repository = repository, txRunner = txRunner)

    @Test
    fun `getById - ingen hittas ska ge null`() = txRunner {
        val id = FeelingId.random()
        every { repository.getFeelingById(id) } returns null

        assertThat(service.getById(id)).isNull()
    }

    @Test
    fun create() = txRunner<Unit> {
        val feeling = Feeling.new(Feeling.Status.Boozed)
        val dto = feeling.toDTO()

        every { repository.create(dto) } returns feeling

        assertThat(service.create(dto)).isEqualTo(feeling)
    }

    @Test
    fun `delete - finns inte`() = txRunner {
        val id = FeelingId.random()
        every { repository.delete(id) } returns false

        assertThat(service.delete(id)).isFalse

        verify { repository.delete(id) }
        confirmVerified(repository)
    }

    @Test
    fun `delete - finns`() = txRunner {
        val id = FeelingId.random()
        every { repository.delete(id) } returns true

        assertThat(service.delete(id)).isTrue

        verify { repository.delete(id) }
        confirmVerified(repository)
    }

    @Test
    fun `replace - finns inte`() = txRunner {
        val id = FeelingId.random()
        every { repository.delete(id) } returns false

        assertThatThrownBy { service.replace(id, CreateFeelingDTO.example()) }
            .isExactlyInstanceOf<IllegalArgumentException>()
            .hasMessage("Feeling med id $id finns inte och kan således inte bytas ut")

        verify { repository.delete(id) }
        confirmVerified(repository)
    }

    @Test
    fun `replace - finns`() = txRunner {
        val id = FeelingId.random()
        every { repository.delete(id) } returns true

        val createdFeeling = Feeling.example()
        val dto = createdFeeling.toDTO()
        every { repository.create(dto) } returns createdFeeling

        assertThat(
            service.replace(id, dto),
        ).isEqualTo(createdFeeling)

        verify { repository.delete(id) }
        verify { repository.create(dto) }
        confirmVerified(repository)
    }
}
