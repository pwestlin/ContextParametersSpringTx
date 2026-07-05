package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ColumnType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.javatime.timestamp

object FeelingsTable : IdTable<FeelingId>("feeling") {

    override val id: Column<EntityID<FeelingId>> = registerColumn("id", FeelingIdColumnType())
        .autoIncrement()
        .entityId()

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

class FeelingIdColumnType : ColumnType<FeelingId>() {
    override fun sqlType(): String = "INT"

    override fun valueFromDB(value: Any): FeelingId = when (value) {
        is FeelingId -> value
        is Int -> FeelingId(value)
        is Number -> FeelingId(value.toInt())
        else -> error("Oväntad typ för FeelingId: ${value::class.qualifiedName}")
    }

    // Nu med rätt signatur matchande ColumnType<FeelingId>
    override fun notNullValueToDB(value: FeelingId): Any {
        return value.value
    }
}