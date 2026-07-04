package nu.westlin.contextparametersspringtx

import nu.westlin.contextparametersspringtx.TransactionRunner.WriteTx
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition

@Component
class ExposedTransactionRunner(private val transactionManager: PlatformTransactionManager) : TransactionRunner {

    // 1. Skapar en ren lästransaktion - tillåter null som returvärde (T kan vara nullable)
    @Suppress("RedundantWith")
    override fun <T> readOnly(block: context(TransactionRunner.ReadTx) () -> T): T {
        val definition = DefaultTransactionDefinition().apply {
            isReadOnly = true
        }
        val status = transactionManager.getTransaction(definition)

        try {
            val ctx = object : TransactionRunner.ReadTx {
                override val exposedTx = TransactionManager.current()
            }

            // Exekvera blocket. Om det blir null, sparas det i 'result'
            val result = with(ctx) { block() }

            transactionManager.commit(status)
            return result
        } catch (ex: Throwable) {
            if (!status.isCompleted) transactionManager.rollback(status)
            throw ex
        }
    }

    // 2. Skapar en skrivtransaktion - tillåter också null som returvärde
    @Suppress("RedundantWith")
    override fun <T> write(block: context(WriteTx) () -> T): T {
        val definition = DefaultTransactionDefinition().apply {
            isReadOnly = false
        }
        val status = transactionManager.getTransaction(definition)

        try {
            val ctx = object : WriteTx {
                override val exposedTx = TransactionManager.current()
            }

            val result = with(ctx) { block() }

            transactionManager.commit(status)
            return result
        } catch (ex: Throwable) {
            if (!status.isCompleted) transactionManager.rollback(status)
            throw ex
        }
    }
}