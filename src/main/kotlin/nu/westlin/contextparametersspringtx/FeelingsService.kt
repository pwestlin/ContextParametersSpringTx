package nu.westlin.contextparametersspringtx

import org.springframework.stereotype.Service

@Service
class FeelingsService(
    private val repository: FeelingsRepository,
    private val txRunner: TransactionRunner
) {

    @TxRead
    fun getById(id: Int): Feeling? = txRunner.readOnly {
        repository.getFeelingById(id)
    }

    @TxWrite
    fun create(feeling: Feeling): Feeling = txRunner.write { repository.create(feeling) }
}