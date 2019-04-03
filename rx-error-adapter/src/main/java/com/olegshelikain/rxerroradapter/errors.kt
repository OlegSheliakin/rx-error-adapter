package com.olegshelikain.rxerroradapter

import com.olegshelikain.rxerroradapter.utils.EmptyStackThrowable

data class HttpResponseError(
    val httpCode: Int,
    override val message: String,
    val errorBodyString: String?
) : EmptyStackThrowable()

data class ServerError(
    val httpCode: Int,
    override val message: String
) : EmptyStackThrowable()

data class UnexpectedError(
    val exception: HttpResponseError
) : EmptyStackThrowable()
