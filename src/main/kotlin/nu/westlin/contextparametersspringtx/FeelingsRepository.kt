package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.temporal.ChronoUnit

@Repository
class FeelingsRepository {

    context(_: TransactionRunner.WriteTx)
    fun create(feeling: CreateFeelingDTO): Feeling {
        val createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS)
        val generatedId = FeelingsTable.insertAndGetId { row ->
            // id hoppas över här eftersom databasen genererar det automatiskt
            row[status] = feeling.status
            row[this.createdAt] = createdAt
            row[comment] = feeling.comment
        }

        // Returnerar en ny instans med det korrekta ID:t från databasen
        return Feeling(
            id = generatedId.value,
            status = feeling.status,
            createdAt = createdAt,
            comment = feeling.comment
        )
    }

    context(_: TransactionRunner.ReadTx)
    fun getFeelingById(feelingId: FeelingId): Feeling? {
        return FeelingsTable
            .selectAll()
            .where { FeelingsTable.id eq feelingId }
            .singleOrNull() // Returnerar ResultRow? (eller null om id inte finns)
            ?.toFeeling()   // Använder extension-funktionen på ResultRow om den inte är null
    }

    fun delete(feelingId: FeelingId): Boolean {
        return FeelingsTable
            .deleteWhere { FeelingsTable.id eq feelingId } == 1
    }
}