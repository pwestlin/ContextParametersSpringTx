package nu.westlin.contextparametersspringtx

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
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
    fun `get - ingen finns`() = exposedWriteTestBlock {
        assertThat(repository.getFeelingById(FeelingId.random())).isNull()
    }

    @Test
    fun `get - en finns`() = exposedWriteTestBlock {
        val feeling = Feeling.example()
        val createdFeeling = repository.create(feeling)
        assertThat(createdFeeling).isEqualTo(feeling.copy(id = createdFeeling.id))

        assertThat(repository.getFeelingById(createdFeeling.id)).isEqualTo(feeling.copy(id = createdFeeling.id))
    }

    @Test
    fun `delete - ingen finns ska returnera false`() {
        assertThat(repository.delete(FeelingId.random())).isFalse
    }

    @Test
    fun `delete - en finns ska returnera true`() = exposedWriteTestBlock {
        val feeling = Feeling.new(status = Feeling.Status.Crazy)
        val createdFeeling = repository.create(feeling)

        assertThat(repository.delete(createdFeeling.id)).isTrue
        val exist: Boolean = !FeelingsTable
            .selectAll()
            .limit(1)
            .where { FeelingsTable.id eq createdFeeling.id }
            .empty()
        assertThat(exist).isFalse
    }
}

// 1. För att testa metoder som bara kräver ReadTx
@Suppress("REDUNDANT_WITH", "unused", "RedundantWith")
fun <T> exposedReadTestBlock(block: context(TransactionRunner.ReadTx) () -> T) {
    val ctx = object : TransactionRunner.ReadTx {
        override val exposedTx = TransactionManager.current()
    }
    return with(ctx) { block() }
}

// 2. För att testa metoder som kräver WriteTx
@Suppress("REDUNDANT_WITH", "RedundantWith")
fun <T> exposedWriteTestBlock(block: context(TransactionRunner.WriteTx) () -> T) {
    val ctx = object : TransactionRunner.WriteTx {
        override val exposedTx = TransactionManager.current()
    }
    return with(ctx) { block() }
}