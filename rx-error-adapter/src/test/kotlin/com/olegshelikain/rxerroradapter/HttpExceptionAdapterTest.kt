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

private const val SERVER_ERROR_STRING_KEY = "server_error"

class HttpExceptionAdapterTest {

    class IntRangeKey(val intRange: IntRange) : Key {
        override fun generateKey(): String {
           return intRange.toString()
        }

    }

    private val keySelector: KeySelector = {
        if(it.httpCode in 500..599) {
            StringKey(SERVER_ERROR_STRING_KEY)
        } else {
            IntKey(it.httpCode)
        }
    }


    private val defAdapter = FakeErrorAdapter<HttpResponseError> {
        DefaultError(
            httpCode = it.httpCode,
            message = it.message
        )
    }

    private val serverErrorAdapter = FakeErrorAdapter<HttpResponseError> {
        ServerError(
            httpCode = it.httpCode,
            message = it.message
        )
    }

    private val badRequestAdapter = FakeErrorAdapter<HttpResponseError> {
        BadRequestError(
            message = it.message
        )
    }

    private val subject = HttpExceptionAdapter.create(keySelector) {
        setDefaultAdapter(defAdapter)
        register(SERVER_ERROR_STRING_KEY, serverErrorAdapter)
        register(400, badRequestAdapter)
    }

    @Test
    fun testBadRequestAdapter() {
        val response: Response<String> = Response.error(400, ResponseBody.create(null, ""))
        val res = subject.invoke(HttpException(response))

        Assert.assertTrue(res is BadRequestError)
    }

    @Test
    fun testDefAdapter() {
        val response: Response<String> = Response.error(404, ResponseBody.create(null, ""))
        val res = subject.invoke(HttpException(response))

        Assert.assertTrue(res is DefaultError)
    }

    @Test
    fun testServerErrorAdapter() {
        val response: Response<String> = Response.error(500, ResponseBody.create(null, ""))
        val res = subject.invoke(HttpException(response))

        Assert.assertTrue(res is ServerError)
    }

    data class DefaultError(
        val httpCode: Int,
        override val message: String
    ) : EmptyStackThrowable()

    data class BadRequestError(
        override val message: String
    ) : EmptyStackThrowable()

    data class ServerError(
        val httpCode: Int,
        override val message: String
    ) : EmptyStackThrowable()

}