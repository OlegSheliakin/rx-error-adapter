package com.olegshelikain.rxerroradapter

import com.olegshelikain.rxerroradapter.utils.EmptyStackThrowable

interface HttpError

data class HttpResponseError(
    val httpCode: Int,
    override val message: String,
    val errorBodyString: String?
) : EmptyStackThrowable(), HttpError
