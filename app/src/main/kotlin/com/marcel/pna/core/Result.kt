package com.marcel.pna.core

import kotlinx.coroutines.delay

sealed class Result<out F, out S> {
    data class Failure<F>(val reason: F) : Result<F, Nothing>()
    data class Success<out S>(val value: S) : Result<Nothing, S>()

    fun isSuccess(): Boolean = this is Success<S>
    fun isFailure(): Boolean = this is Failure<F>

    inline fun <T> fold(
        onFailure: (F) -> T,
        onSuccess: (S) -> T,
    ): T = when (this) {
        is Failure -> onFailure(reason)
        is Success -> onSuccess(value)
    }

    inline fun <T> map(transform: (S) -> T): Result<F, T> = when (this) {
        is Failure -> this
        is Success -> Success(transform(value))
    }

    inline fun <T> mapFailure(transform: (F) -> T): Result<T, S> = when (this) {
        is Failure -> Failure(transform(reason))
        is Success -> this
    }

    companion object {
        /**
         * Runs a suspending [block], returning [Success] if it succeeds or [Failure] if an exception is thrown.
         * Retries the [block] if [shouldRetryProvider] returns true, passing the current attempt count and the thrown exception.
         *
         * @param delayMillisProvider A function to determine delay before retry, based on current attempt (starting from 1).
         * @param shouldRetryProvider A predicate to decide whether to retry, based on attempt count and caught exception.
         */
        suspend inline fun <S> catching(
            noinline delayMillisProvider: (attempt: Int) -> Long = { 0 },
            noinline shouldRetryProvider: (attempt: Int, Throwable) -> Boolean = { _, _ -> false },
            crossinline block: suspend () -> S
        ): Result<Throwable, S> {
            var attempt = 1
            while (true) {
                try {
                    return Success(block())
                } catch (e: Throwable) {
                    if (!shouldRetryProvider(attempt, e)) return Failure(e)
                    delay(delayMillisProvider(attempt))
                    attempt++
                }
            }
        }
    }
}
