package nu.westlin.contextparametersspringtx

import org.jetbrains.exposed.v1.core.Transaction

interface TransactionRunner {
    fun <T> runInTransaction(block: context(Transaction) () -> T): T
}