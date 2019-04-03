package com.olegshelikain.rxerroradapter

/**
 * Created by olegshelyakin on 01/04/2019.
 * Contact me by email - olegsheliakin@gmail.com
 */
typealias ErrorAdapter<T> = (T) -> Throwable?

fun <T : Throwable> ErrorAdapter<T>.asChain() = ErrorAdapterChain.create(this)

class ErrorAdapterChain<T : Throwable> private constructor(
    private val errorAdapter: ErrorAdapter<T>
) : ErrorAdapter<T> {

    private var next: ErrorAdapter<T>? = null

    companion object {
        fun <T : Throwable> create(errorAdapter: ErrorAdapter<T>) = ErrorAdapterChain<T>(errorAdapter)
    }

    fun addNext(errorAdapter: ErrorAdapter<T>): ErrorAdapterChain<T> {
        this.next = errorAdapter
        return ErrorAdapterChain(errorAdapter)
    }

    override fun invoke(t: T): Throwable? {
        return errorAdapter.invoke(t) ?: next?.invoke(t)
    }

}


