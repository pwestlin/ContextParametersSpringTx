package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.core.Transaction

class FakeExposedTransactionRunner(private val dummyTransaction: Transaction) : TransactionRunner {

    override fun <T> runInTransaction(block: context(Transaction) () -> T): T {
        // Vi etablerar den fejkade Exposed-transaktionen i scopet
        return with(dummyTransaction) {
            block()
        }
    }
}