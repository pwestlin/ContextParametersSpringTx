package nu.westlin.contextparametersspringtx

import org.springframework.stereotype.Service

@Service
class FeelingsService(
    private val repository: FeelingsRepository,
    private val txRunner: TransactionRunner
) {

    @TxRead
    fun getById(id: FeelingId): Feeling? = txRunner.readOnly {
        repository.getFeelingById(id)
    }

    @TxWrite
    fun create(feeling: CreateFeelingDTO): Feeling = txRunner.write { repository.create(feeling) }

    @TxWrite
    fun delete(feelingId: FeelingId): Boolean = txRunner.write { repository.delete(feelingId) }

    @TxWrite
    fun replace(feelingId: FeelingId, replacer: CreateFeelingDTO): Feeling = txRunner.write {
        // Här kan jag faktiskt göra interna bönanrop med bibehållen transaktionshantering eftersom Springs proxy inte används för transaktioner - woohoo! :)
        require(delete(feelingId)) { "Feeling med id $feelingId finns inte och kan således inte bytas ut" }

        create(replacer)
    }
}