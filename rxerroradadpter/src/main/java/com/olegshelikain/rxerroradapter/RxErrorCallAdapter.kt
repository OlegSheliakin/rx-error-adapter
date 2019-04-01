package com.olegshelikain.rxerroradapter

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class RxErrorCallAdapter constructor(
    private val wrapped: CallAdapter<in Any, out Any>,
    private val errorAdapterProvider: ErrorAdapterProvider
) : CallAdapter<Any, Any> {

    override fun responseType(): Type {
        return wrapped.responseType()
    }

    override fun adapt(call: Call<Any>): Any {
        val adaptedCall = wrapped.adapt(call)

        if (adaptedCall is Completable) {
            return adaptedCall.onErrorResumeNext { throwable: Throwable -> Completable.error(adapt(throwable)) }
        }

        if (adaptedCall is Single<*>) {
            return adaptedCall.onErrorResumeNext { throwable: Throwable -> Single.error(adapt(throwable)) }
        }

        if (adaptedCall is Observable<*>) {
            return adaptedCall.onErrorResumeNext { throwable: Throwable -> Observable.error(adapt(throwable)) }
        }

        if (adaptedCall is Flowable<*>) {
            return adaptedCall.onErrorResumeNext { throwable: Throwable -> Flowable.error(adapt(throwable)) }
        }

        throw RuntimeException("Type ${adaptedCall.javaClass.simpleName} is not supported")
    }

    private fun adapt(throwable: Throwable): Throwable {
        val adapter = errorAdapterProvider.provide(throwable)
        return adapter?.invoke(throwable) ?: throwable
    }

}