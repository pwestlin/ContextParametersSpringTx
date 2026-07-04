package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.core.Transaction

interface TransactionRunner {
    fun <T> write(block: context(WriteTx) () -> T): T
    fun <T> readOnly(block: context(ReadTx) () -> T): T

    interface ReadTx {
        val exposedTx: Transaction
    }

    interface WriteTx : ReadTx
}

