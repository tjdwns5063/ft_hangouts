package com.example.ft_hangouts

import com.example.ft_hangouts.error.DatabaseCreateErrorHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class BaseViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()
    private lateinit var baseViewModel: BaseViewModel

    @Before
    fun setUpViewModel() = runTest {
        baseViewModel = BaseViewModel(TestScope(mainDispatcher.testDispatcher))
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `given add contact failed then check error state expect change state properly`() = runTest {
        val errorHandler = DatabaseCreateErrorHandler()

        baseViewModel.submitHandler(errorHandler)

        Assert.assertEquals(baseViewModel.errorHandler.value, errorHandler)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `given set error state then initiate error state expect state is null`() =  runTest {
        val errorHandler = DatabaseCreateErrorHandler()

        //given
        baseViewModel.submitHandler(errorHandler)

        //then
        baseViewModel.initiateError()

        //expect
        Assert.assertEquals(baseViewModel.errorHandler.value, null)
    }
}