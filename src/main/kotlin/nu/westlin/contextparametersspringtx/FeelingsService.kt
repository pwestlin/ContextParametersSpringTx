package nu.westlin.contextparametersspringtx

import org.springframework.stereotype.Service

@Service
class FeelingsService(
    private val repository: FeelingsRepository,
    private val txRunner: TransactionRunner
) {

    fun getById(id: Int): Feeling? {
        return txRunner.write {
            repository.getFeelingById(id)
        }
    }

    fun create(feeling: Feeling): Feeling {
        return txRunner.write { repository.create(feeling) }
    }
}