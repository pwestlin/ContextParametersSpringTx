package nu.westlin.contextparametersspringtx

import nu.westlin.contextparametersspringtx.Feeling.Status
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random

fun FeelingId.Companion.random(): FeelingId = FeelingId(Random.nextInt(1, Int.MAX_VALUE))

fun Feeling.Companion.example(
    id: FeelingId = FeelingId.Zero,
    status: Status = Status.entries.random(),
    createdAt: Instant = Instant.now().truncatedTo(ChronoUnit.MICROS),
    comment: String? = null
): Feeling = Feeling(
    id = id,
    status = status,
    createdAt = createdAt,
    comment = comment
)

fun CreateFeelingDTO.Companion.example(
    status: Status = Status.entries.random(),
    comment: String? = null
): CreateFeelingDTO = CreateFeelingDTO(
    status = status,
    comment = comment
)

fun Feeling.toDTO(): CreateFeelingDTO = CreateFeelingDTO(
    status = status,
    comment = comment
)
