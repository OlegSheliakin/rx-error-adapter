package com.olegshelikain.rxerroradapter

/**
 * Created by Oleg Sheliakin on 01.04.2019.
 * Contact me by email - olegsheliakin@gmail.com
 */
typealias ErrorAdapter<T> = (T) -> Throwable?

fun <T : Throwable> ErrorAdapter<T>.asChain() : ErrorAdaptersChain<T> = ErrorAdaptersChain.create<T> {
    addNext(this@asChain)
}

object Identity : ErrorAdapter<Throwable> {
    override fun invoke(error: Throwable): Throwable? = error
}