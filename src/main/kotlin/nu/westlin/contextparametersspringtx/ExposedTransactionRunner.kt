package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate

@Component
class ExposedTransactionRunner(
    private val transactionTemplate: TransactionTemplate
): TransactionRunner {

    // Vi kräver nu Exposeds 'Transaction' som kontext i blocket
    override fun <T> runInTransaction(block: context(Transaction) () -> T): T {
        return transactionTemplate.execute { _ ->
            // Hämta den transaktion som Springs TransactionManager just skapat för tråden
            val currentExposedTx = TransactionManager.current()

            // Etablera den som Context Parameter för kodblocket
            with(currentExposedTx) {
                block()
            }
        } ?: throw IllegalStateException("Transaktionen misslyckades och returnerade null")
    }
}