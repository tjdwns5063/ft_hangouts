package com.example.ft_hangouts

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ft_hangouts.data.ImageDAO
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.data.contact_database.ContactDAO
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import com.example.ft_hangouts.error.DatabaseSuccessHandler
import com.example.ft_hangouts.ui.add.AddViewModelFactory
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
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var addViewModel: ContactAddViewModel
    private lateinit var contactDatabase: ContactDatabase
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var contactDAO: ContactDAO
    private lateinit var imageDAO: ImageDAO
    private lateinit var context: Context
    private lateinit var testScope: TestScope
    private lateinit var viewModelFactory: AddViewModelFactory

    @Before
    @ExperimentalCoroutinesApi
    fun setUpViewModel() {
        context = InstrumentationRegistry.getInstrumentation().context
        contactDatabase = Room.inMemoryDatabaseBuilder(context, ContactDatabase::class.java).build()
        contactDAO = contactDatabase.contactDao()
        imageDAO = ImageDAO(context)
        testScope = TestScope(mainDispatcherRule.testDispatcher)
        baseViewModel = BaseViewModel(testScope)
        viewModelFactory = AddViewModelFactory(contactDatabase, imageDAO, baseViewModel)
        addViewModel = viewModelFactory.create(ContactAddViewModel::class.java)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `연락처 추가 테스트`() = runTest {
        //given
        val expect = Contact(1, "seongjki", "01012345678", "abc@def.com", "mam", "friend")
        addViewModel.addContact("seongjki", "01012345678", "abc@def.com", "friend", "mam")

        //when
        val actual = CoroutineScope(Dispatchers.IO).async {
            contactDAO.getAllItems()
        }.await()

        //then
        assertEquals(expect, actual[0])
        assertEquals(baseViewModel.errorHandler.value is DatabaseSuccessHandler, true)
    }
    @After
    fun closeDb() {
        contactDatabase.close()
    }
}