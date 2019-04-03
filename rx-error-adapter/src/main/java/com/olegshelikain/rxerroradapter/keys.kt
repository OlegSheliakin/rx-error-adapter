package com.olegshelikain.rxerroradapter

/**
 * Created by olegshelyakin on 03/04/2019.
 * Contact me by email - olegsheliakin@gmail.com
 */
data class IntKey(val code: Int) : Key {
    override fun generateKey(): String = code.toString()
}

data class StringKey(val code: String) : Key {
    override fun generateKey(): String = code
}

interface Key {
    fun generateKey(): String
}

interface KeySelector {
    fun select(errorResponse: HttpResponseError): Key?
}