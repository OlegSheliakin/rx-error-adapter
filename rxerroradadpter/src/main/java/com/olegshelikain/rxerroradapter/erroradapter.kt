package com.olegshelikain.rxerroradapter

typealias ErrorAdapter<T> = (T) -> Throwable?

object Identity : ErrorAdapter<Throwable> {
    override fun invoke(error: Throwable): Throwable? = error
}