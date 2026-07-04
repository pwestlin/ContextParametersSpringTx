package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository

@Repository
class FeelingsRepository {

    context(_: TransactionRunner.WriteTx)
    fun create(feeling: Feeling): Feeling {
        val generatedId = Feelings.insertAndGetId { row ->
            // id hoppas över här eftersom databasen genererar det automatiskt
            row[status] = feeling.status
            row[createdAt] = feeling.createdAt
            row[comment] = feeling.comment
        }

        // Returnerar en ny instans med det korrekta ID:t från databasen
        return feeling.copy(id = generatedId.value)
    }

    // TODO pevest: readOnlye
    context(_: TransactionRunner.ReadTx)
    fun getFeelingById(feelingId: Int): Feeling? {
        return Feelings
            .selectAll()
            .where { Feelings.id eq feelingId }
            .singleOrNull() // Returnerar ResultRow? (eller null om id inte finns)
            ?.toFeeling()   // Använder extension-funktionen på ResultRow om den inte är null
    }
}