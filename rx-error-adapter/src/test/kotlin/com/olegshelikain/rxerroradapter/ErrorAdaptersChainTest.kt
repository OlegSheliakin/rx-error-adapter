package com.olegshelikain.rxerroradapter

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.Exception

/**
 * Created by Oleg Sheliakin on 02.04.2019.
 * Contact me by email - olegsheliakin@gmail.com
 */
class ErrorAdaptersChainTest {

    private lateinit var mainErrorAdapter: FakeErrorAdapter<TestException>

    private lateinit var detailErrorAdapter: FakeErrorAdapter<TestException>

    private lateinit var thirdErrorAdapter: FakeErrorAdapter<TestException>

    @Before
    fun setUp() {
        mainErrorAdapter = FakeErrorAdapter(Identity)

        detailErrorAdapter = FakeErrorAdapter {
            if (it is TestException.Second) {
                it
            } else {
                null
            }
        }

        thirdErrorAdapter = FakeErrorAdapter {
            if (it is TestException.Third) {
                it
            } else {
                null
            }
        }
    }

    @Test
    fun shouldInterceptThird() {
        val subject = mainErrorAdapter.asChain().apply {
            addNext(detailErrorAdapter)
            addNext(thirdErrorAdapter)
        }

        subject.invoke(TestException.Third)

        Assert.assertTrue(thirdErrorAdapter.isAdapted)
        Assert.assertFalse(mainErrorAdapter.isAdapted)
        Assert.assertFalse(detailErrorAdapter.isAdapted)
    }

    @Test
    fun shouldInterceptMain() {
        val subject = mainErrorAdapter.asChain().apply {
            addNext(detailErrorAdapter)
            addNext(thirdErrorAdapter)
        }

        subject.invoke(TestException.First)

        Assert.assertFalse(thirdErrorAdapter.isAdapted)
        Assert.assertTrue(mainErrorAdapter.isAdapted)
        Assert.assertFalse(detailErrorAdapter.isAdapted)
    }

    sealed class TestException : Exception() {
        object First : TestException()
        object Second : TestException()
        object Third : TestException()
    }
}