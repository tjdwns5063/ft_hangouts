package com.example.ft_hangouts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.data.image_database.ImageDatabaseDAO
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.add.ContactAddViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class AddViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var addViewModel: ContactAddViewModel
    private lateinit var dao: ContactDatabaseDAO
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var imageDao: ImageDatabaseDAO
    private lateinit var dbHelper: ContactHelper
    private lateinit var context: Context
    private lateinit var testScope: TestScope

    @Before
    @ExperimentalCoroutinesApi
    fun setUpViewModel() {
        context = InstrumentationRegistry.getInstrumentation().context
        dbHelper = ContactHelper(context)
        dao = ContactDatabaseDAO(dbHelper)
        imageDao = ImageDatabaseDAO(context)
        testScope = TestScope(mainDispatcherRule.testDispatcher)
        baseViewModel = BaseViewModel(testScope)
        addViewModel = ContactAddViewModel(dao, testScope, baseViewModel, imageDao)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `연락처 추가 테스트`() = runTest {
        val expect = Contact(1, "seongjki", "01012345678", "abc@def.com", "mam", "friend")

        addViewModel.addContact("seongjki", "01012345678", "abc@def.com", "friend", "mam").join()

        val actual = dao.getItemById(1)

        assertEquals(expect, actual)
        assertEquals(baseViewModel.errorHandler.value is DatabaseSuccessHandler, true)
    }
    @After
    fun closeDb() {
        dbHelper.close()
    }
}