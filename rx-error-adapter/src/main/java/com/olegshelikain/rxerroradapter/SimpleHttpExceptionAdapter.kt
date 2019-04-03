package com.olegshelikain.rxerroradapter

import retrofit2.HttpException

/**
 * It adapts HttpException with status code only in range 400..599, otherwise it doesn't perform any adaptation
 */
class SimpleHttpExceptionAdapter private constructor(
    private val keySelector: KeySelector
) : HttpExceptionAdapter {

    private var defaultErrorAdapter: ErrorAdapter<HttpResponseError>? = null

    private val specificAdapters: MutableMap<Key, ErrorAdapter<HttpResponseError>> by lazy {
        return@lazy mutableMapOf<Key, ErrorAdapter<HttpResponseError>>()
    }

    companion object {

        fun create(keySelector: KeySelector, builder: SimpleHttpExceptionAdapter.() -> Unit): SimpleHttpExceptionAdapter {
            return SimpleHttpExceptionAdapter(keySelector).apply(builder)
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
            return keySelector.select(responseError)?.let {
                specificAdapters[it]?.invoke(responseError)
            } ?: defaultErrorAdapter?.invoke(responseError)
        }

        return UnexpectedError(responseError)
    }

    /**
     * Sets default error adapter.
     * It is called if there is not any specific adapter
     */
    fun setDefaultAdapter(httpExceptionAdapter: ErrorAdapter<HttpResponseError>): SimpleHttpExceptionAdapter {
        this.defaultErrorAdapter = httpExceptionAdapter
        return this
    }

    /**
     * Registers specific error adapter by Key
     */
    fun register(code: Key, httpExceptionAdapter: ErrorAdapter<HttpResponseError>): SimpleHttpExceptionAdapter {
        specificAdapters[code] = httpExceptionAdapter
        return this
    }

    /**
     * Registers specific error adapter by IntKey
     */
    fun register(code: Int, httpExceptionAdapter: ErrorAdapter<HttpResponseError>): SimpleHttpExceptionAdapter {
        specificAdapters[IntKey(code)] = httpExceptionAdapter
        return this
    }

    /**
     * Registers specific error adapter by StringKey
     */
    fun register(code: String, httpExceptionAdapter: ErrorAdapter<HttpResponseError>): SimpleHttpExceptionAdapter {
        specificAdapters[StringKey(code)] = httpExceptionAdapter
        return this
    }
}

