package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.core.Transaction

@Suppress("RedundantWith")
class FakeExposedTransactionRunner(override val exposedTx: Transaction) :
    TransactionRunner,
    TransactionRunner.WriteTx {

    @Suppress("REDUNDANT_WITH")
    override fun <T> readOnly(block: context(TransactionRunner.ReadTx) () -> T): T {
        // Vi skickar in 'this' (faken själv) som kontext
        return with(this) { block() }
    }

    @Suppress("REDUNDANT_WITH")
    override fun <T> write(block: context(TransactionRunner.WriteTx) () -> T): T {
        // Vi skickar in 'this' (faken själv) som kontext
        return with(this) { block() }
    }
}

@Suppress("RedundantWith")
operator fun <T> FakeExposedTransactionRunner.invoke(block: context(FakeExposedTransactionRunner) () -> T) =
    with(this) { block() }