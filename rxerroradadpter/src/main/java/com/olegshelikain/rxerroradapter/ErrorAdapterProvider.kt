package com.olegshelikain.rxerroradapter

import kotlin.reflect.KClass

class ErrorAdapterProvider private constructor() {

    private val mapOfAdapters: MutableMap<KClass<out Throwable>, ErrorAdapter<Throwable>> by lazy {
        return@lazy mutableMapOf<KClass<out Throwable>, ErrorAdapter<Throwable>>()
    }

    companion object {
        fun create() = ErrorAdapterProvider()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Throwable> register(klazz: KClass<T>, adapter: ErrorAdapter<T>) {
        mapOfAdapters[klazz] = adapter as ErrorAdapter<Throwable>
    }

    fun provide(throwable: Throwable): ErrorAdapter<Throwable>? {
        return mapOfAdapters[throwable::class]
    }

}