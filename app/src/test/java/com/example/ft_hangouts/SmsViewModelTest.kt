package com.example.ft_hangouts

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.data.sms_database.SmsDatabaseDAO
import com.example.ft_hangouts.data.sms_database.SmsInfo
import com.example.ft_hangouts.ui.base.BaseViewModel
import com.example.ft_hangouts.ui.sms.ContactSmsViewModel
import com.example.ft_hangouts.ui.sms.SmsViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("NonAsciiCharacters")
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.DEFAULT_MANIFEST_NAME)
class SmsViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var contactDAO: ContactDAO
    private lateinit var contactDatabase: ContactDatabase
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var context: Context
    private lateinit var testScope: TestScope
    private lateinit var viewModel: ContactSmsViewModel
    @Mock
    private lateinit var smsDatabaseDAO: SmsDatabaseDAO
    private lateinit var viewModelFactory: SmsViewModelFactory

    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().context
        testScope = TestScope(StandardTestDispatcher())
        contactDatabase = createContactDatabase(context)
        contactDAO = contactDatabase.contactDao()
        MockitoAnnotations.openMocks(this)
        baseViewModel = BaseViewModel(testScope)
        viewModelFactory = SmsViewModelFactory(contactDatabase, 1, smsDatabaseDAO, baseViewModel)
        viewModel = viewModelFactory.create(ContactSmsViewModel::class.java)
    }

    @After
    fun after() {
        contactDatabase.close()
    }

    @Test
    fun `문자 추가 테스트`() {
        val msg = SmsInfo("hello world", System.currentTimeMillis(), 2)

        viewModel.addMessage(msg)

        Assert.assertEquals(listOf(msg), viewModel.messageList.value)
    }
}