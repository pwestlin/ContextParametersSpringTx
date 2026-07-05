package nu.westlin.contextparametersspringtx

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.Instant
import java.time.temporal.ChronoUnit

data class Feeling(
    val id: FeelingId = FeelingId.Zero,
    val status: Status,
    val createdAt: Instant = Instant.now(),
    val comment: String? = null
) {
    enum class Status {
        Happy,
        Sad,
        Tired,
        Crazy,
        Boozed;
    }

    companion object
}

fun Feeling.Companion.new(status: Feeling.Status, comment: String? = null): Feeling = Feeling(
    id = FeelingId.Zero,
    status = status,
    // Postgres klarar "bara" mikrosekunder
    createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS),
    comment = comment
)

@JvmInline
value class FeelingId private constructor(val value: Int) : Comparable<FeelingId> {

    override fun compareTo(other: FeelingId): Int = value.compareTo(other.value)

    override fun toString(): String {
        return value.toString()
    }

    operator fun plus(other: FeelingId): FeelingId = FeelingId(this.value + other.value)

    operator fun plus(value: Int): FeelingId = FeelingId(this.value + value)

    companion object {
        val Zero = FeelingId(0)

        @JsonCreator
        @JvmStatic
        operator fun invoke(id: Int): FeelingId {
            require(id > 0) { "id måste vara > 0 men var $id" }
            return FeelingId(id)
        }
    }
}

