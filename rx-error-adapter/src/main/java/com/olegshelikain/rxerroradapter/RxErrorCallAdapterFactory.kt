package com.olegshelikain.rxerroradapter

import io.reactivex.Scheduler
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type

/**
 * Created by olegshelyakin on 01/04/2019.
 * Contact me by email - olegsheliakin@gmail.com
 */
class RxErrorCallAdapterFactory private constructor(
    private val delegate: RxJava2CallAdapterFactory,
    private val errorAdapterProvider: ErrorAdapterProvider
) : CallAdapter.Factory() {

    companion object {

        fun createHttpErrorAdapter(
            withScheduler: Scheduler? = null,
            httpExceptionAdapter: HttpExceptionAdapter
        ): RxErrorCallAdapterFactory {
            val errorAdapterProvider = ErrorAdapterProvider.create {
                register(HttpException::class, httpExceptionAdapter)
            }

            return RxErrorCallAdapterFactory(
                createRxCallAdapter(withScheduler),
                errorAdapterProvider
            )
        }

        fun create(
            withScheduler: Scheduler? = null,
            errorAdapterProvider: ErrorAdapterProvider
        ): RxErrorCallAdapterFactory {
            return RxErrorCallAdapterFactory(
                createRxCallAdapter(withScheduler),
                errorAdapterProvider
            )
        }

        private fun createRxCallAdapter(withScheduler: Scheduler?): RxJava2CallAdapterFactory {
            return if (withScheduler != null) {
                RxJava2CallAdapterFactory.createWithScheduler(withScheduler)
            } else {
                RxJava2CallAdapterFactory.create()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<Any, Any>? {
        val callAdapter = delegate.get(returnType, annotations, retrofit) as? CallAdapter<in Any, out Any>
        return if (callAdapter == null) {
            null
        } else RxErrorCallAdapter(
            callAdapter,
            errorAdapterProvider
        )
    }

}
