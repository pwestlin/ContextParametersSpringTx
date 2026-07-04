package nu.westlin.contextparametersspringtx

import org.springframework.stereotype.Service

@Service
class FeelingsService(
    private val repository: FeelingsRepository,
    private val txRunner: ExposedTransactionRunner
) {

    fun getById(id: Int): Feeling? {
        return txRunner.runInTransaction {
            repository.getFeelingById(id)
        }
    }

    fun create(feeling: Feeling): Feeling {
        return txRunner.runInTransaction { repository.create(feeling) }
    }
}