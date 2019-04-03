package com.olegshelikain.rxerroradapter


/**
 * Created by olegshelyakin on 03/04/2019.
 * Contact me by email - olegsheliakin@gmail.com
 */
class FakeErrorAdapter<T>(private val errorAdapter: ErrorAdapter<T>) : ErrorAdapter<T> {
    var isAdapted = false

    override fun invoke(p1: T): Throwable? {
        val res = errorAdapter.invoke(p1)
        if (res != null) {
            isAdapted = true
        }
        return res
    }
}