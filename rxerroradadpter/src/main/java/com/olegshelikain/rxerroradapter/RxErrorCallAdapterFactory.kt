package com.olegshelikain.rxerroradapter

import io.reactivex.Scheduler
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type

class RxErrorCallAdapterFactory private constructor(
    private val delegate: RxJava2CallAdapterFactory,
    private val errorAdapterProvider: ErrorAdapterProvider
) : CallAdapter.Factory() {

    companion object {
        fun create(
            withScheduler: Scheduler? = null,
            httpExceptionAdapter: HttpExceptionAdapter
        ): RxErrorCallAdapterFactory {

            val errorAdapterProvider = ErrorAdapterProvider.create().apply {
                register(HttpException::class, httpExceptionAdapter)
            }

            return if (withScheduler != null) {
                RxErrorCallAdapterFactory(
                    RxJava2CallAdapterFactory.createWithScheduler(
                        withScheduler
                    ), errorAdapterProvider
                )
            } else {
                RxErrorCallAdapterFactory(
                    RxJava2CallAdapterFactory.create(),
                    errorAdapterProvider
                )
            }
        }

        fun create(
            withScheduler: Scheduler? = null,
            errorAdapterProvider: ErrorAdapterProvider
        ): RxErrorCallAdapterFactory {
            return if (withScheduler != null) {
                RxErrorCallAdapterFactory(
                    RxJava2CallAdapterFactory.createWithScheduler(
                        withScheduler
                    ), errorAdapterProvider
                )
            } else {
                RxErrorCallAdapterFactory(
                    RxJava2CallAdapterFactory.create(),
                    errorAdapterProvider
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<Any, Any>? {
        val callAdapter = delegate.get(returnType, annotations, retrofit) as? CallAdapter<in Any, out Any>
        return if (callAdapter == null) null else RxErrorCallAdapter(
            callAdapter,
            errorAdapterProvider
        )
    }

}
