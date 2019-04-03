package com.olegshelikain.rxerroradapter

/**
 * Created by olegshelyakin on 03/04/2019.
 * Contact me by email - olegsheliakin@gmail.com
 */

typealias KeySelector = (HttpResponseError) -> Key?

interface Key {
    fun generateKey(): String
}

data class IntKey(val code: Int) : Key {
    override fun generateKey(): String = code.toString()
}

data class StringKey(val code: String) : Key {
    override fun generateKey(): String = code
}
