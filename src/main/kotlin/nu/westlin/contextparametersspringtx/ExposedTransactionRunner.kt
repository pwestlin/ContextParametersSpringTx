package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition

@Component
class ExposedTransactionRunner(private val transactionManager: PlatformTransactionManager): TransactionRunner {

    @Suppress("RedundantWith")
    override fun <T> runInTransaction(block: context(Transaction) () -> T): T {
        // 1. Starta transaktionen via Springs manager
        val status = transactionManager.getTransaction(DefaultTransactionDefinition())

        try {
            // 2. Hämta den Exposed-transaktion som Spring just skapade på tråden
            val currentExposedTx = TransactionManager.current()

            // 3. Exekvera blocket. Här behålls typen T intakt (oavsett om den är nullable eller ej)
            val result = with(currentExposedTx) {
                block()
            }

            // 4. Om allt gick bra, commitar vi mot Postgres
            transactionManager.commit(status)
            return result

        } catch (ex: Throwable) {
            // 5. Om något smällde rullar vi tillbaka automatiskt
            if (!status.isCompleted) {
                transactionManager.rollback(status)
            }
            throw ex
        }
    }
}