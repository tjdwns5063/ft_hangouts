package com.example.ft_hangouts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.contact_database.ContactDatabaseDAO
import com.example.ft_hangouts.data.contact_database.ContactHelper
import com.example.ft_hangouts.data.sms_database.SmsDatabaseDAO
import com.example.ft_hangouts.data.sms_database.SmsInfo
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.sms.ContactSmsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@Suppress("NonAsciiCharacters")
@RunWith(RobolectricTestRunner::class)
class SmsViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var contactHelper: ContactHelper
    private lateinit var contactDatabaseDAO: ContactDatabaseDAO
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var context: Context
    private lateinit var testScope: TestScope
    private lateinit var viewModel: ContactSmsViewModel
    private lateinit var smsDatabaseDAO: SmsDatabaseDAO


    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().context
        testScope = TestScope(StandardTestDispatcher())
        contactHelper = ContactHelper.createDatabase(context)
        contactDatabaseDAO = ContactDatabaseDAO(contactHelper)
        smsDatabaseDAO = SmsDatabaseDAO(context.contentResolver)
        baseViewModel = BaseViewModel(testScope)
        viewModel = ContactSmsViewModel(contactDatabaseDAO, 1, testScope, baseViewModel, smsDatabaseDAO)
    }

    @After
    fun after() {
        contactHelper.close()
    }

    @Test
    fun `문자 추가 테스트`() {
        val msg = SmsInfo("hello world", System.currentTimeMillis(), 2)

        viewModel.addMessage(msg)

        Assert.assertEquals(listOf(msg), viewModel.messageList.value)
    }
}