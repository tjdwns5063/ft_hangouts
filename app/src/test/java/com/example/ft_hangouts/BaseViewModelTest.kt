package com.example.ft_hangouts

import com.example.ft_hangouts.error.DatabaseCreateErrorHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class BaseViewModelTest {
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var testScope: TestScope

    @Before
    fun setUpViewModel() {
        testScope = TestScope(StandardTestDispatcher())
        baseViewModel = BaseViewModel(testScope)
    }

    @Test
    @OptIn(InternalCoroutinesApi::class)
    @ExperimentalCoroutinesApi
    fun `given add contact failed then check error state expect change state properly`() = runTest {
        //then
        val errorHandler = DatabaseCreateErrorHandler()

        backgroundScope.launch {
            withContext(this.coroutineContext) {
                baseViewModel.submitHandler(errorHandler)
            }

            baseViewModel.errorHandler.collect(FlowCollector {
                Assert.assertEquals(it, errorHandler)
            })
        }
    }

    @Test
    @OptIn(InternalCoroutinesApi::class)
    @ExperimentalCoroutinesApi
    fun `given set error state then initiate error state expect state is null`() =  runTest {
        val errorHandler = DatabaseCreateErrorHandler()

        backgroundScope.launch {
            withContext(this.coroutineContext) {
                baseViewModel.submitHandler(errorHandler)
                baseViewModel.initiateError()
            }

            baseViewModel.errorHandler.collect(FlowCollector {
                Assert.assertEquals(it, null)
            })
        }
    }
}