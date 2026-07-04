package nu.westlin.contextparametersspringtx

import java.time.Instant
import java.time.temporal.ChronoUnit

data class Feeling(
    val id: Int = 0,
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
    id = 0,
    status = status,
    // Postgres klarar "bara" mikrosekunder
    createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS),
    comment = comment
)