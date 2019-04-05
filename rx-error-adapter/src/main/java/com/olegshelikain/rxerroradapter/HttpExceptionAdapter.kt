package com.olegshelikain.rxerroradapter

import retrofit2.HttpException

/**
 * It adapts HttpException with status code only in range 400..599, otherwise it doesn't perform any adaptation
 */
class HttpExceptionAdapter private constructor(
    private val keySelector: KeySelector
) : ErrorAdapter<HttpException> {

    private var defaultErrorAdapter: ErrorAdapter<HttpResponseError>? = null

    private val specificAdapters: MutableMap<Key, ErrorAdapter<HttpResponseError>> by lazy {
        return@lazy mutableMapOf<Key, ErrorAdapter<HttpResponseError>>()
    }

    companion object {

        fun create(
            keySelector: KeySelector = HttpCodeKeySelector(),
            builder: HttpExceptionAdapter.() -> Unit
        ): HttpExceptionAdapter {
            return HttpExceptionAdapter(keySelector).apply(builder)
        }

    }

    override fun invoke(error: HttpException): Throwable? {
        val errorBodyString = error.response().errorBody()?.string()
        val responseError = HttpResponseError(
            httpCode = error.code(),
            message = error.message(),
            errorBodyString = errorBodyString
        )

        if (error.code() in 500..599) {
            return ServerError(
                httpCode = responseError.httpCode,
                message = responseError.message
            )
        }

        if (error.code() in 400..499) {
            return keySelector.invoke(responseError)?.let {
                specificAdapters[it]?.invoke(responseError)
            } ?: defaultErrorAdapter?.invoke(responseError)
        }

        return UnexpectedError(responseError)
    }

    /**
     * Sets default error adapter.
     * It is called if there is not any specific adapter
     */
    fun setDefaultAdapter(adapter: ErrorAdapter<HttpResponseError>): HttpExceptionAdapter {
        this.defaultErrorAdapter = adapter
        return this
    }

    /**
     * Registers specific error adapter by Key
     */
    fun register(code: Key, adapter: ErrorAdapter<HttpResponseError>): HttpExceptionAdapter {
        specificAdapters[code] = adapter
        return this
    }

    /**
     * Registers specific error adapter by IntKey
     */
    fun register(code: Int, adapter: ErrorAdapter<HttpResponseError>): HttpExceptionAdapter {
        specificAdapters[IntKey(code)] = adapter
        return this
    }

    /**
     * Registers specific error adapter by StringKey
     */
    fun register(code: String, adapter: ErrorAdapter<HttpResponseError>): HttpExceptionAdapter {
        specificAdapters[StringKey(code)] = adapter
        return this
    }
}

