package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.timestamp

object FeelingsTable : IntIdTable("feeling") {
    // Lagrar enumen som en sträng i databasen (t.ex. "Happy", "Sad")
    val status = enumerationByName<Feeling.Status>("status", length = 20)

    // Mappar direkt mot java.time.Instant via Exposeds javatime-tillägg
    val createdAt = timestamp("created_at")

    // nullable() hanterar String?
    val comment = varchar("comment", length = 255).nullable()
}

/**
 * Extension-funktion för att enkelt mappa en databasrad till din domänmodell.
 */
fun ResultRow.toFeeling() = Feeling(
    id = this[FeelingsTable.id].value, // .value behövs då IntIdTable använder EntityID<Int>
    status = this[FeelingsTable.status],
    createdAt = this[FeelingsTable.createdAt],
    comment = this[FeelingsTable.comment]
)