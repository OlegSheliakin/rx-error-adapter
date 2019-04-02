package com.olegshelikain.rxerroradapter

/**
 * Created by Oleg Sheliakin on 02.04.2019.
 * Contact me by email - olegsheliakin@gmail.com
 */
class ErrorAdaptersChain<T : Throwable> private constructor() : ErrorAdapter<T> {

    private val list: MutableList<ErrorAdapter<T>> = mutableListOf()

    companion object {
        fun <T : Throwable> create(builder: ErrorAdaptersChain<T>.() -> Unit): ErrorAdaptersChain<T> {
            return ErrorAdaptersChain<T>().apply {
                builder.invoke(this)
            }
        }
    }

    fun addNext(errorAdapter: ErrorAdapter<T>): ErrorAdaptersChain<T> {
        list.add(errorAdapter)
        return this
    }

    override fun invoke(throwable: T): Throwable? {
        for (adapter in list.asReversed()) {
            val res = adapter.invoke(throwable)
            if (res != null) {
                return res
            }
        }
        return null
    }
}
