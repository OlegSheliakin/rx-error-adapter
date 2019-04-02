package com.olegshelikain.rxerroradapter

/**
 * Created by Oleg Sheliakin on 01.04.2019.
 * Contact me by email - olegsheliakin@gmail.com
 */
typealias ErrorAdapter<T> = (T) -> Throwable?

object Identity : ErrorAdapter<Throwable> {
    override fun invoke(error: Throwable): Throwable? = error
}