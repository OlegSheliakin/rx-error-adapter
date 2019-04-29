package com.olegshelikain.rxerroradapter

import retrofit2.HttpException

/**
 * It can be used to adapt only HttpException.
 */
class HttpExceptionAdapter private constructor(
    private val keySelector: KeySelector
) : ErrorAdapter<HttpException> {

    /**
     * It adapts throwable if there is not any specific adapter.
     */
    private var defaultErrorAdapter: ErrorAdapter<HttpResponseError>? = null

    /**
     * Map of specific adapters. It uses to get adapter by Key.
     */
    private val specificAdapters: MutableMap<String, ErrorAdapter<HttpResponseError>> by lazy {
        return@lazy mutableMapOf<String, ErrorAdapter<HttpResponseError>>()
    }

    companion object {

        fun create(
            keySelector: KeySelector = HttpCodeKeySelector(),
            builder: HttpExceptionAdapter.() -> Unit
        ): HttpExceptionAdapter {
            return HttpExceptionAdapter(keySelector).apply(builder)
        }

    }

    /**
     * Tries to adapt throwable.
     * @return adapted throwable or null if there is no any adapter.
     */
    override fun invoke(error: HttpException): Throwable? {
        val errorBodyString = error.response().errorBody()?.string()
        val responseError = HttpResponseError(
            httpCode = error.code(),
            message = error.message(),
            errorBodyString = errorBodyString
        )

       return keySelector.invoke(responseError)?.let {
            specificAdapters[it.generateKey()]?.invoke(responseError)
        } ?: defaultErrorAdapter?.invoke(responseError)
    }

    /**
     * Sets default error adapter.
     */
    fun setDefaultAdapter(adapter: ErrorAdapter<HttpResponseError>): HttpExceptionAdapter {
        this.defaultErrorAdapter = adapter
        return this
    }

    /**
     * Registers specific error adapter by Key.
     */
    fun register(code: Key, adapter: ErrorAdapter<HttpResponseError>): HttpExceptionAdapter {
        specificAdapters[code.generateKey()] = adapter
        return this
    }

    /**
     * Registers specific error adapter by IntKey.
     */
    fun register(code: Int, adapter: ErrorAdapter<HttpResponseError>): HttpExceptionAdapter {
        specificAdapters[IntKey(code).generateKey()] = adapter
        return this
    }

    /**
     * Registers specific error adapter by StringKey.
     */
    fun register(code: String, adapter: ErrorAdapter<HttpResponseError>): HttpExceptionAdapter {
        specificAdapters[StringKey(code).generateKey()] = adapter
        return this
    }
}
