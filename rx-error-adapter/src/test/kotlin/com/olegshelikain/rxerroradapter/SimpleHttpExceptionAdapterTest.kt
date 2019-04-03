package com.olegshelikain.rxerroradapter

import com.olegshelikain.rxerroradapter.utils.EmptyStackThrowable
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

/**
 * Created by olegshelyakin on 03/04/2019.
 * Contact me by email - olegsheliakin@gmail.com
 */
class SimpleHttpExceptionAdapterTest {

    private val keySelector: KeySelector = {
        IntKey(it.httpCode)
    }

    private val defAdapter = FakeErrorAdapter<HttpResponseError> {
        DefaultError(
            httpCode = it.httpCode,
            message = it.message
        )
    }

    private val badRequestAdapter = FakeErrorAdapter<HttpResponseError> {
        BadRequestError(
            message = it.message
        )
    }

    private val subject = SimpleHttpExceptionAdapter.create(keySelector) {
        setDefaultAdapter(defAdapter)
        register(400, badRequestAdapter)
    }

    @Test
    fun testBadRequestAdapter() {
        val response: Response<String> = Response.error(400, ResponseBody.create(null, ""))
        subject.invoke(HttpException(response))

        Assert.assertTrue(badRequestAdapter.isAdapted)
    }

    @Test
    fun testDefAdapter() {
        val response: Response<String> = Response.error(404, ResponseBody.create(null, ""))
        subject.invoke(HttpException(response))

        Assert.assertFalse(badRequestAdapter.isAdapted)
        Assert.assertTrue(defAdapter.isAdapted)
    }

    data class DefaultError(
        val httpCode: Int,
        override val message: String
    ) : EmptyStackThrowable()

    data class BadRequestError(
        override val message: String
    ) : EmptyStackThrowable()

}