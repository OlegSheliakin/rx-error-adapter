package com.olegshelikain.rxerroradapter

import kotlin.reflect.KClass

/**
 * Created by Oleg Sheliakin on 01.04.2019.
 * Contact me by email - olegsheliakin@gmail.com
 */
class ErrorAdapterProvider private constructor() {

    private val mapOfAdapters: MutableMap<KClass<out Throwable>, ErrorAdapter<Throwable>> by lazy {
        return@lazy mutableMapOf<KClass<out Throwable>, ErrorAdapter<Throwable>>()
    }

    companion object {
        fun create(builder: ErrorAdapterProvider.() -> ErrorAdapterProvider) : ErrorAdapterProvider {
            return builder.invoke(ErrorAdapterProvider())
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Throwable> register(klazz: KClass<T>, adapter: ErrorAdapter<T>) : ErrorAdapterProvider {
        mapOfAdapters[klazz] = adapter as ErrorAdapter<Throwable>
        return this
    }

    internal fun provide(throwable: Throwable): ErrorAdapter<Throwable>? {
        return mapOfAdapters[throwable::class]
    }

}