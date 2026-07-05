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
    fun create() = txRunner {
        val feeling = Feeling.new(Feeling.Status.Boozed)
        val createdFeeling = feeling.copy(id = FeelingId.random())

        every { repository.create(feeling) } returns createdFeeling

        assertThat(service.create(feeling)).isEqualTo(createdFeeling)
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

        assertThatThrownBy { service.replace(id, Feeling.new(Feeling.Status.entries.random())) }
            .isExactlyInstanceOf<IllegalArgumentException>()
            .hasMessage("Feeling med id $id finns inte och kan således inte bytas ut")

        verify { repository.delete(id) }
        confirmVerified(repository)
    }

    @Test
    fun `replace - finns`() = txRunner {
        val id = FeelingId.random()
        every { repository.delete(id) } returns true

        val feeling = Feeling.new(Feeling.Status.Boozed)
        val createdFeeling = feeling.copy(id = feeling.id + 1)
        every { repository.create(feeling) } returns createdFeeling

        assertThat(
            service.replace(id, feeling)
        ).isEqualTo(createdFeeling)

        verify { repository.delete(id) }
        verify { repository.create(feeling) }
        confirmVerified(repository)
    }
}
